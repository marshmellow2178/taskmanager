package com.init330.taskmanager.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponseDTO create(UserRequestDTO userDTO){
        User newUser = User.of(userDTO.username(),  userDTO.email(), userDTO.password());
        userRepository.save(newUser); //transactional 이라도 신규는 저장 필수
        return new UserResponseDTO(newUser.getId(), newUser.getUsername(), newUser.getEmail());
    }

    public UserResponseDTO findByUsername(String username){
        return userRepository.findByUsername(username)
                .map(value -> new UserResponseDTO(value.getId(), value.getUsername(), value.getEmail()))
                .orElse(null);
    }

    public UserResponseDTO findByEmail(String email){
        return userRepository.findByEmail(email)
                .map(value -> new UserResponseDTO(value.getId(), value.getUsername(), value.getEmail()))
                .orElse(null);
    }

    private User findById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    private void userCheck(User user) throws AccessDeniedException {
        User loginUser = findById(1L); //테스트용도
        if(user.getId().equals(loginUser.getId())){ //Long은 객체 타입이라 equals 비교가 필요하다
            throw new AccessDeniedException("Something gone wrong!");
        }
    }

    public UserResponseDTO searchById(Long id) {
        User user = findById(id);
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail());
    }

    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO userDTO) throws AccessDeniedException {
        User user = findById(id);
        userCheck(user);
        user.update(userDTO.username(),  userDTO.email(), userDTO.password());
        return new  UserResponseDTO(user.getId(), user.getUsername(), user.getEmail());
    }

    @Transactional
    public void deleteById(Long id) throws AccessDeniedException {
        User user = findById(id);
        userCheck(user);
        userRepository.deleteById(id);
    }
}
