package com.init330.taskmanager.todo;

import com.init330.taskmanager.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    Page<Todo> findByUser(User user, Pageable pageable);
}
