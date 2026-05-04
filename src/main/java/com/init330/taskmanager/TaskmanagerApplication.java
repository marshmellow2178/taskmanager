package com.init330.taskmanager;

import com.init330.taskmanager.todo.Todo;
import com.init330.taskmanager.todo.TodoRepository;
import com.init330.taskmanager.user.User;
import com.init330.taskmanager.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;

@EnableJpaAuditing
@SpringBootApplication
public class TaskmanagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskmanagerApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, TodoRepository todoRepository) {
        return args -> {
            User user = User.of("testUser", "test@email.com", "12345");
            userRepository.save(user);

            Todo todo1 = Todo.of("제목", LocalDateTime.now().plusDays(1), user);
            Todo todo2 = Todo.of("두번째", LocalDateTime.now().plusDays(2), user);
            todoRepository.save(todo1);
            todoRepository.save(todo2);
            System.out.println(">>> 테스트 데이터 생성 완료");
        };
    }
}
