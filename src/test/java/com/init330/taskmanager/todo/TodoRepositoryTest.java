package com.init330.taskmanager.todo;

import com.init330.taskmanager.user.User;
import com.init330.taskmanager.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void todo_저장_테스트() {
        User user = userRepository.save(User.of("repoUser", "repo@test.com", "encoded"));
        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);

        Todo todo = Todo.of("테스트하기", dueDate, user);
        Todo savedTodo = todoRepository.save(todo);

        assertThat(savedTodo.getId()).isNotNull();
        assertThat(savedTodo.getTitle()).isEqualTo("테스트하기");
        assertThat(savedTodo.getDueDate()).isEqualTo(dueDate);
        assertThat(savedTodo.isCompleted()).isFalse();
        assertThat(savedTodo.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void findByUser_페이징() {
        User user = userRepository.save(User.of("pageUser", "page@test.com", "encoded"));
        todoRepository.save(Todo.of("첫 할 일", LocalDateTime.now().plusDays(1), user));
        todoRepository.save(Todo.of("둘째 할 일", LocalDateTime.now().plusDays(2), user));

        Page<Todo> page = todoRepository.findByUser(user, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting(Todo::getTitle).containsExactlyInAnyOrder("첫 할 일", "둘째 할 일");
    }
}
