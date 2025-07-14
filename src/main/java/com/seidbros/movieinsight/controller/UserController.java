package com.seidbros.movieinsight.controller;


import com.seidbros.movieinsight.dto.LoginDto;
import com.seidbros.movieinsight.dto.UserInDto;
import com.seidbros.movieinsight.dto.base.Base;
import com.seidbros.movieinsight.service.BaseService;
import com.seidbros.movieinsight.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {
private final UserService userService;
private final BaseService baseService;


    @PostMapping("signup")
    public ResponseEntity<?> createUser(@RequestBody UserInDto userInDto) {
        Base<?> base = userService.signUp(userInDto);
        return baseService.rest(base);
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        Base<?> base = userService.login(loginDto);
        return baseService.rest(base);
    }
}
