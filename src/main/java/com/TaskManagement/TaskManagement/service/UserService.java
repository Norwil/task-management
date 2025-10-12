package com.TaskManagement.TaskManagement.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.TaskManagement.TaskManagement.dto.request.RoleUpdateRequest;
import com.TaskManagement.TaskManagement.dto.request.UserRequest;
import com.TaskManagement.TaskManagement.dto.response.UserResponse;
import com.TaskManagement.TaskManagement.exception.UserNotFoundException;
import com.TaskManagement.TaskManagement.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            return user;
    }

    /**
     * Register a new user
     */
    @Transactional
    public UserResponse registerUser(UserRequest request) {
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return userMapper.toResponseDTO(user);
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

    /**
     * Get all users
     * @param pageable pagination and sorting parameters
     * @return a page of users
     * @throws IllegalArgumentException if pageable is null
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        Page<User> userPage = userRepository.findAll(pageable);

        return userPage.map(userMapper::toResponseDTO);
    }

    /**
     * Get a user by id
     * @param id
     * @return user
     * @throws UserNotFoundException if id is invalid
     */
    @Transactional(readOnly = true)
    public UserResponse findUserById(Long id) {
        User entity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toResponseDTO(entity);
    }

    /**
     * Update User details
     * @params id, request
     * @return the final DTO
     * @throws UserNotFoundException if id is invalid
     */
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User entity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        entity.setUsername(request.getUsername());
        entity.setEmail(request.getEmail());

        User savedUser = userRepository.save(entity);

        return userMapper.toResponseDTO(savedUser);
    }

    /**
     * Modify the role of the user by id
     * @param id, request
     * @return map and return the final UserResponse DTO
     */
    @Transactional
    public UserResponse updateRole(Long id, RoleUpdateRequest request) {
        User entity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        entity.setRole(request.getRole());

        User updatedRole = userRepository.save(entity);

        return userMapper.toResponseDTO(updatedRole);
    }




    /**
     * Deletes a user from database by id
     * @param id the id of the user to delete
     * @throws UserNotFoundException if id is invalid
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.deleteById(id);
    }
}
