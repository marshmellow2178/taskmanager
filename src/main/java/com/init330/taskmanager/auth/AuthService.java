package com.init330.taskmanager.auth;

import com.init330.taskmanager.user.User;
import com.init330.taskmanager.user.UserRepository;
import com.init330.taskmanager.user.UserRequestDTO;
import com.init330.taskmanager.user.UserResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UserResponseDTO signup(UserRequestDTO userRequestDTO) {
        String encoded = passwordEncoder.encode(userRequestDTO.password());
        User user = User.of(userRequestDTO.username(), userRequestDTO.email(), encoded);
        userRepository.save(user);
        return UserResponseDTO.from(user);
    }

    public TokenResponseDTO login(LoginRequestDTO loginRequestDTO) {
        String id = loginRequestDTO.usernameOrEmail();
        Optional<User> userOpt = userRepository.findByUsername(id);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(id);
        }

        User user = userOpt.orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(loginRequestDTO.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtTokenProvider.createAccessToken(user.getId(), user.getUsername());
        return TokenResponseDTO.bearer(token);
    }
}

