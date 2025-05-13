package org.mason.bupt_note.controller;

import jakarta.annotation.Resource;
import org.mason.bupt_note.entity.User;
import org.mason.bupt_note.repository.UserRepository;
import org.mason.bupt_note.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
//返回数据
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> requestBody) {
        String studentId = requestBody.get("studentId");
        String password = requestBody.get("password");
        // 查找用户
        User user = userRepository.findByStudentId(studentId);
        if (user == null) {
            return ResponseEntity.status(401).body("用户不存在");
        }
        // 使用 BCryptPasswordEncoder 来验证用户输入的密码
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // 使用 matches 方法来验证输入的密码是否与存储的加密密码匹配
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body("学号或密码错误");
        }
        // 如果密码正确，生成 JWT Token 并返回
        String token = JwtUtil.generateToken(studentId);
        return ResponseEntity.ok(token);
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
}