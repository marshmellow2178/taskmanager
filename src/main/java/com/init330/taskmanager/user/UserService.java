package com.init330.taskmanager.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponseDTO create(UserRequestDTO userDTO){
        log.info("UserService.create 요청됨");
        User newUser = User.of(userDTO.username(),  userDTO.email(), userDTO.password());
        userRepository.save(newUser); //transactional 이라도 신규는 저장 필수
        return UserResponseDTO.from(newUser);
    }

    public UserResponseDTO findByUsername(String username){
        log.info("UserService.findByUsername 요청됨");
        return userRepository.findByUsername(username)
                .map(UserResponseDTO::from)
                .orElse(null);
    }

    public UserResponseDTO findByEmail(String email){
        log.info("UserService.findByEmail 요청됨");
        return userRepository.findByEmail(email)
                .map(UserResponseDTO::from)
                .orElse(null);
    }

    public User findById(Long id){
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
        log.info("UserService.searchById 요청됨");
        User user = findById(id);
        return UserResponseDTO.from(user);
    }

    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO userDTO) throws AccessDeniedException {
        log.info("UserService.update 요청됨");
        User user = findById(id);
        userCheck(user);
        user.update(userDTO.username(),  userDTO.email(), userDTO.password());
        return UserResponseDTO.from(user);
    }

    @Transactional
    public void deleteById(Long id) throws AccessDeniedException {
        log.info("UserService.delete 요청됨");
        User user = findById(id);
        userCheck(user);
        userRepository.deleteById(id);
    }
}
