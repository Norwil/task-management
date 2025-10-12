package com.TaskManagement.TaskManagement.controller;

import java.net.URI;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TaskManagement.TaskManagement.dto.request.UserRequest;
import com.TaskManagement.TaskManagement.dto.response.TaskResponse;
import com.TaskManagement.TaskManagement.dto.response.UserResponse;
import com.TaskManagement.TaskManagement.entity.Task;
import com.TaskManagement.TaskManagement.entity.User;
import com.TaskManagement.TaskManagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


}
