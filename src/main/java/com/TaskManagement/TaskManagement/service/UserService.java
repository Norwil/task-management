package com.TaskManagement.TaskManagement.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.TaskManagement.TaskManagement.entity.Task;
import com.TaskManagement.TaskManagement.entity.User;
import com.TaskManagement.TaskManagement.repository.TaskRepository;
import com.TaskManagement.TaskManagement.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public UserService(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Retrieves all users with pagination
     * @param pageable pagination information
     * @return page of users
     */
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    /**
     * Retrieves all users as a list (without pagination)
     * @return list of all users
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by ID
     * @param id the user ID
     * @return Optional containing the user, or empty if not found
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Saves a user, check for duplicate username or email.
     * @param user the user to save
     * @return the saved user
     * @throws IllegalArgumentException if username or email is taken
     */
    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if  (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already taken");
        }
        return userRepository.save(user);
    }

    /**
     * Delete a user by ID
     * @param id the user ID
     * @throws NoSuchElementException if user not found
     */
    public void deleteById(Long id) {
        if (!userRepository.findById(id).isPresent()) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Finds a user by username
     * @param username the username
     * @return Optional containing the user, or empty if not found
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Finds a user by email
     * @param email the email
     * @return Optional containing the user, or empty if not found
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Finds all tasks by user id
     * @param id the user id
     * @return List of tasks
     * @throws NoSuchElementException if user not found
     */
    public List<Task> findByUserId(Long id) {
        if (!userRepository.findById(id).isPresent()) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        return taskRepository.findByUserId(id);
    }

}
