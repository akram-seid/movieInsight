package com.seidbros.movieinsight.mongo.service;

import com.seidbros.movieinsight.mongo.dto.LoginDto;
import com.seidbros.movieinsight.mongo.dto.ChangePasswordDto;
import com.seidbros.movieinsight.mongo.dto.UserInDto;
import com.seidbros.movieinsight.common.dto.base.Base;
import com.seidbros.movieinsight.mongo.dto.UserOutDto;
import com.seidbros.movieinsight.mongo.enums.Role;
import com.seidbros.movieinsight.mongo.model.entity.User;
import com.seidbros.movieinsight.mongo.repository.UserRepository;
import com.seidbros.movieinsight.common.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BaseService baseService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public Base<?> signUp(UserInDto userInDto) {
        Optional<User> userByUsername = userRepository.findUserByUsername(userInDto.username().toLowerCase());
        Optional<User> userByEmail = userRepository.findUserByEmail(userInDto.username().toLowerCase());

        if (userByUsername.isPresent() ) {
            return baseService.error("Username  already in use");
        }else if(userByEmail.isPresent()){
            return baseService.error("Email already in use");
        }

        User user = User.builder()
                .userId(generateRandomCode())
                .username(userInDto.username())
                .email(userInDto.email())
                .password(passwordEncoder.encode(userInDto.password()))
                .role(Role.USER)
                .joinedAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
        return baseService.success("User registered successfully");
    }

    public Base<?> changePassword(ChangePasswordDto changePasswordDto) {
        Optional<User> userByUsername = userRepository.findUserByUsername(changePasswordDto.username().toLowerCase());

        if (userByUsername.isPresent()) {
            if (userByUsername.get().getPassword().equals(passwordEncoder.encode(changePasswordDto.oldPassword()))) {
                userByUsername.get().setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));
                userRepository.save(userByUsername.get());
            }
            return baseService.error("Incorrect old password");
        }
        return baseService.error("User not found");
    }
    public Base<?> resetPassword(String  email) {
        Optional<User> userByEmail = userRepository.findUserByEmail(email.toLowerCase());

        if (userByEmail.isPresent()) {
            String password = generateRandomCode();
            userByEmail.get().setPassword(passwordEncoder.encode(password));
            userRepository.save(userByEmail.get());
            return baseService.success(STR."Password successfully reset. \{password}");

        }

        return baseService.error("Username already in use");

    }

    public Base<?> login(LoginDto loginDto) {
        Optional<User> userByUsername = userRepository.findUserByUsername(loginDto.username());
        if (userByUsername.isPresent()) {
            User user = userByUsername.get();
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password()));
            if (auth != null) {
                String token = jwtUtil.generateToken((UserDetails) auth.getPrincipal());
                UserOutDto userOutDto = new UserOutDto(user.getUsername(), token, user.getUserId(), user.getRole());
                return baseService.success(userOutDto);
            }
            return baseService.error("Incorrect password");
        }
        return baseService.error("User not found");
    }

    String generateRandomCode(){
         Random random = new SecureRandom();
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
      int  length = 5;
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(alphanumeric.length());
            code.append(alphanumeric.charAt(randomIndex));
        }
        return code.toString();
    }
}
