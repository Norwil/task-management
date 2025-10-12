package com.TaskManagement.TaskManagement.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.TaskManagement.TaskManagement.dto.request.UserRequest;
import com.TaskManagement.TaskManagement.dto.response.UserResponse;
import com.TaskManagement.TaskManagement.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.TaskManagement.TaskManagement.entity.Task;
import com.TaskManagement.TaskManagement.entity.User;
import com.TaskManagement.TaskManagement.repository.TaskRepository;
import com.TaskManagement.TaskManagement.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            return user;
    }

    /**
     * To find user by username
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return userMapper.toResponseDTO(entity);
    }
}
