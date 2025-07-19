package com.seidbros.movieinsight.mongo.controller;


import com.seidbros.movieinsight.mongo.dto.ChangePasswordDto;
import com.seidbros.movieinsight.mongo.dto.LoginDto;
import com.seidbros.movieinsight.mongo.dto.UserInDto;
import com.seidbros.movieinsight.common.dto.base.Base;
import com.seidbros.movieinsight.mongo.service.BaseService;
import com.seidbros.movieinsight.mongo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("resetPassword")
    public ResponseEntity<?> reset(@RequestParam("email") String email) {
        Base<?> base = userService.resetPassword(email);
        return baseService.rest(base);
    }

    @PostMapping("changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        Base<?> base = userService.changePassword(changePasswordDto);
        return baseService.rest(base);
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        Base<?> base = userService.login(loginDto);
        return baseService.rest(base);
    }
}
