package com.init330.taskmanager.todo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TodoRepositoryTest {
    @Autowired
    private TodoRepository todoRepository;

   /* @Test
    void todo_저장_테스트() {
        // 1. 팩토리 메서드로 객체 만들기 (딸깍)
        Todo todo = Todo.of("테스트하기", LocalDateTime.now());

        // 2. DB 저장
        Todo savedTodo = todoRepository.save(todo);

        // 3. 검증 (진짜 들어갔냐?)
        assertThat(savedTodo.getId()).isNotNull();
        assertThat(savedTodo.getTitle()).isEqualTo("테스트하기");
    }*/
}