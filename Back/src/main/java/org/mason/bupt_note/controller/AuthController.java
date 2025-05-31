package org.mason.bupt_note.controller;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManagerFactory;
import org.mason.bupt_note.entity.User;
import org.mason.bupt_note.repository.UserRepository;
import org.mason.bupt_note.service.AuthService;
import org.mason.bupt_note.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
//返回数据
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private UserRepository userRepository;

    @Resource
    private AuthService authService;

    private final StringRedisTemplate redisTemplate;

    public AuthController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> requestBody) {
        String authProvider = requestBody.get("authProvider");
        String studentId = requestBody.get("studentId");
        String cachedToken = redisTemplate.opsForValue().get("loggedInUsers::" + studentId);
        //前端应该要跳转到登录成功的页面
        if (cachedToken != null) {
            return ResponseEntity.ok(cachedToken); // 返回已登录用户的 Token
        }
        if ("PASSWORD".equalsIgnoreCase(authProvider)) {
            String password = requestBody.get("password");
            // 处理密码登录
            User user = userRepository.findByStudentId(studentId);
            if (user == null) {
                return ResponseEntity.status(401).body("用户不存在");
            }

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.status(401).body("学号或密码错误");
            }
            authService.getUserByUserId(user.getUserId().toString());
            // 生成 JWT Token 并返回
            String token = JwtUtil.generateToken(user.getUserId().toString());

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("username", user.getName());

            return ResponseEntity.ok(result);
        }  else {
            return ResponseEntity.status(400).body("不支持的认证方式");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> requestBody) {
        String studentId = requestBody.get("studentId");
        String password = requestBody.get("password");
        String name = requestBody.get("name");
        // 检查用户是否已存在
        if (userRepository.findByStudentId(studentId) != null) {
            return ResponseEntity.status(400).body("用户已存在");
        }

        // 加密密码
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);

        // 创建新用户并保存
        User newUser = new User();
        newUser.setStudentId(studentId);
        newUser.setPassword(encodedPassword);
        newUser.setName(name);
        newUser.setRole(User.Role.valueOf("USER"));
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        newUser.setAuthProvider(User.AuthProvider.valueOf(User.AuthProvider.PASSWORD.name()));
        userRepository.save(newUser);
        return ResponseEntity.ok("注册成功");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        String userId = JwtUtil.validateToken(token);
        // 删除 Redis 中的缓存（如用户登录态、验证码等）
        redisTemplate.delete("loggedInUsers::" + userId);

        return ResponseEntity.ok("登出成功");
    }

}