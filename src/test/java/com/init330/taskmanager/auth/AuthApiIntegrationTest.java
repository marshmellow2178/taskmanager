package com.init330.taskmanager.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 전체 애플리케이션 컨텍스트에서 HTTP 로그인·JWT·보호 API를 검증합니다.
 * <ul>
 *   <li>{@code @SpringBootTest} — 컨트롤러·시큐리티·JPA 등 실제 앱과 비슷하게 기동</li>
 *   <li>{@code @AutoConfigureMockMvc} — 서버 안 띄우고 HTTP만 흉내 내는 {@link MockMvc} 주입</li>
 *   <li>{@code mockMvc.perform(...)} — Postman으로 치는 요청 한 번과 같음</li>
 *   <li>{@code andExpect(...)} — 응답 상태코드·JSON 필드 검증</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthApiIntegrationTest {

    /** 가짜 HTTP 클라이언트 (GET/POST + 헤더·바디 조립) */
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProperties jwtProperties;

    /** 로그인 응답 JSON에서 accessToken 문자열만 꺼낼 때 사용 (스프링 빈 없이 테스트 전용 인스턴스) */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 인증 없이 보호 API 호출 → SecurityConfig의 401 JSON */
    @Test
    void getTodos_withoutToken_returns401Json() throws Exception {
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("unauthorized"));
    }

    /** 만료된 JWT로 보호 API 호출 시 401 + 동일 JSON (JwtAuthenticationFilter) */
    @Test
    void getTodos_withExpiredBearer_returns401Json() throws Exception {
        String expiredToken = Jwts.builder()
                .issuer(jwtProperties.issuer())
                .subject("1")
                .claim("username", "any")
                .issuedAt(Date.from(Instant.now().minusSeconds(7200)))
                .expiration(Date.from(Instant.now().minusSeconds(120)))
                .signWith(Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8)))
                .compact();

        mockMvc.perform(get("/api/todos").header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("unauthorized"));
    }

    /** 가입 후 비번 틀리면 로그인 실패(401). GlobalExceptionHandler의 BadCredentials 처리 */
    @Test
    void login_wrongPassword_returns401() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"w1","email":"w1@test.com","password":"correctPass1"}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"usernameOrEmail":"w1","password":"wrongPass"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    /** 회원가입 → 로그인 성공 시 응답에 tokenType·accessToken 있는지 */
    @Test
    void signup_thenLogin_returnsBearerAccessToken() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"s1","email":"s1@test.com","password":"pass1234"}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"usernameOrEmail":"s1","password":"pass1234"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").exists());
    }

    /**
     * Postman 시나리오 그대로: 가입 → 로그인 → 응답에서 토큰 추출 → Authorization 헤더로 Todo 목록.
     * jsonPath만으로 토큰을 다음 요청에 넣기 어려워서 {@link MvcResult}로 본문을 읽은 뒤 파싱합니다.
     */
    @Test
    void signup_login_thenGetTodosWithBearer_returnsOk() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"t1","email":"t1@test.com","password":"pass1234"}
                                """))
                .andExpect(status().isOk());

        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"usernameOrEmail":"t1","password":"pass1234"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        // 로그인 응답 본문(JSON) → accessToken 필드만 문자열로 추출
        JsonNode root = objectMapper.readTree(login.getResponse().getContentAsString());
        String accessToken = root.get("accessToken").asText();
        assertThat(accessToken).isNotBlank();

        // Postman에서 Bearer Token 넣는 것과 동일 (접두어 "Bearer " + 공백 주의)
        mockMvc.perform(get("/api/todos").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    /** Todo 생성 시 제목 검증 실패 → 400 */
    @Test
    void createTodo_withBlankTitle_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"v1","email":"v1@test.com","password":"pass1234"}
                                """))
                .andExpect(status().isOk());

        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"usernameOrEmail":"v1","password":"pass1234"}
                                """))
                .andExpect(status().isOk())
                .andReturn();
        String accessToken = objectMapper.readTree(login.getResponse().getContentAsString())
                .get("accessToken").asText();

        mockMvc.perform(post("/api/todos")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"   ","dueDate":"2030-01-01T12:00:00"}
                                """))
                .andExpect(status().isBadRequest());
    }
}
