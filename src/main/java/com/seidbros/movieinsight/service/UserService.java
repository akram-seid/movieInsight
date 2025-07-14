package com.seidbros.movieinsight.service;

import com.seidbros.movieinsight.dto.LoginDto;
import com.seidbros.movieinsight.dto.UserInDto;
import com.seidbros.movieinsight.dto.base.Base;
import com.seidbros.movieinsight.enums.Role;
import com.seidbros.movieinsight.model.User;
import com.seidbros.movieinsight.repository.UserRepository;
import com.seidbros.movieinsight.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BaseService baseService;
    private final PasswordEncoder passwordEncoder;
    private final MongoTemplate mongoTemplate;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public Base<?> signUp(UserInDto userInDto) {
        Optional<User> userByUsername = userRepository.findUserByUsername(userInDto.username());

        if (userByUsername.isPresent()) {
            return baseService.error("Username already in use");
        }
        GroupOperation group = Aggregation.group().max("user_id").as("maxUserId");
        Aggregation aggregation = Aggregation.newAggregation(group);

        AggregationResults<Map> result = mongoTemplate.aggregate(aggregation, "users", Map.class);
        Map resultMap = result.getUniqueMappedResult();
        Long maxUserId= 0L;
        if (resultMap !=null && resultMap.containsKey("maxUserId")) {
             maxUserId = Long.valueOf((String) resultMap.get("maxUserId"));
        }

        User user = User.builder()
                .userId(String.valueOf(maxUserId+1))
                .username(userInDto.username())
                .password(passwordEncoder.encode(userInDto.password()))
                .role(Role.USER)
                .joinedAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
        return baseService.success("User registered successfully");
    }

    public Base<?> login(LoginDto loginDto) {
        Optional<User> userByUsername = userRepository.findUserByUsername(loginDto.username());
        if (userByUsername.isPresent()) {
            User user = userByUsername.get();
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password()));
            if (auth != null) {
                String token = jwtUtil.generateToken((UserDetails) auth.getPrincipal());
                return baseService.success(token);
            }
            return baseService.error("Incorrect password");
        }
        return baseService.error("User not found");
    }
}
