package com.init330.taskmanager;

import com.init330.taskmanager.todo.Todo;
import com.init330.taskmanager.todo.TodoRepository;
import com.init330.taskmanager.user.User;
import com.init330.taskmanager.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@EnableJpaAuditing
@SpringBootApplication
public class TaskmanagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskmanagerApplication.class, args);
    }

    @Bean
    @Profile("dev")
    public CommandLineRunner initData(
            UserRepository userRepository,
            TodoRepository todoRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            User user = userRepository.findByUsername("testUser")
                    .orElseGet(() -> userRepository.save(
                            User.of("testUser", "test@email.com", passwordEncoder.encode("12345"))
                    ));

            if (todoRepository.count() == 0) {
                Todo todo1 = Todo.of("제목", LocalDateTime.now().plusDays(1), user);
                Todo todo2 = Todo.of("두번째", LocalDateTime.now().plusDays(2), user);
                todoRepository.save(todo1);
                todoRepository.save(todo2);
            }

            System.out.println(">>> 개발용 테스트 데이터 seed 완료 (dev profile)");
        };
    }
}
