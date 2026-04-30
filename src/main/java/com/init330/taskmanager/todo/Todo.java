package com.init330.taskmanager.todo;

import com.init330.taskmanager.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 'new' 생성 막기
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private boolean completed;
    private LocalDateTime dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // 팩토리 메서드: static으로 선언해서 어디서든 Todo.of()로 호출 가능하게!
    public static Todo of(String title, LocalDateTime dueDate, User user) {
        Todo newTodo = new Todo();
        newTodo.title = title;
        newTodo.dueDate = dueDate;
        newTodo.completed = false;
        newTodo.user = user;
        return newTodo;
    }

    public void setCompletedTrue(){
        this.completed = true;
    }

    public void setCompletedFalse(){
        this.completed = false;
    }

    public void updateTodo(String title, LocalDateTime dueDate) {
        this.title = title;
        this.dueDate = dueDate;
    }
}
