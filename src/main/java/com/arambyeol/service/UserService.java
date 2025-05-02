package com.arambyeol.service;

import com.arambyeol.domain.User;
import com.arambyeol.dto.UserRequestDto;
import com.arambyeol.dto.PasswordResetDto;
import com.arambyeol.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import com.arambyeol.exception.UserNotFoundException;
import com.arambyeol.exception.DuplicateUsernameException;
import com.arambyeol.exception.InvalidPasswordException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(UserRequestDto requestDto) {
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new DuplicateUsernameException("이미 존재하는 사용자 이름입니다: " + requestDto.getUsername());
        }

        User user = new User();
        user.setUsername(requestDto.getUsername());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        
        log.info("New user registered: {}", requestDto.getUsername());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User login(UserRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername())
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다: " + requestDto.getUsername()));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다");
        }

        log.info("User logged in: {}", requestDto.getUsername());
        return user;
    }

    @Transactional(readOnly = true)
    public boolean checkUsernameDuplicate(String username) {
        boolean exists = userRepository.existsByUsername(username);
        log.info("Username duplicate check: {} - {}", username, exists);
        return exists;
    }

    @Transactional
    public void resetPassword(PasswordResetDto resetDto) {
        User user = userRepository.findByUsername(resetDto.getUsername())
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다: " + resetDto.getUsername()));

        user.setPassword(passwordEncoder.encode(resetDto.getNewPassword()));
        userRepository.save(user);
        log.info("Password reset for user: {}", resetDto.getUsername());
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
} 