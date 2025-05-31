package org.mason.bupt_note.controller;

import org.mason.bupt_note.entity.User;
import org.mason.bupt_note.repository.UserRepository;
import org.mason.bupt_note.service.AuthService;
import org.mason.bupt_note.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class OAuth2LoginController {

    private final StringRedisTemplate redisTemplate;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    public OAuth2LoginController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/github/success")
    public ResponseEntity<?> githubLoginSuccess() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
                OAuth2User oauth2User = oauth2Token.getPrincipal();
                String githubUsername = (String) oauth2User.getAttributes().get("login");

                // 查询数据库是否已有该 GitHub 用户
                User user = userRepository.findByGithubUsername(githubUsername);
                if (user == null) {
                    // 用户不存在，创建新用户
                    user = new User();
                    user.setAuthProvider(User.AuthProvider.GITHUB);
                    user.setName(githubUsername);
                    user.setGithubUsername(githubUsername);
                    user.setGithubId("1");  // 可替换为真实ID
                    user.setRole(User.Role.USER);
                    user.setStudentId("1");
                    user.setAvatarUrl("https://avatars.githubusercontent.com/" + githubUsername);
                    user.setBackgroundImageUrl("https://default.background.image/url.jpg");
                    user.setPassword("1");  // GitHub OAuth 不使用密码，设置占位
                    user.setCreatedAt(LocalDateTime.now());
                    user.setUpdatedAt(LocalDateTime.now());

                    userRepository.save(user);
                } // 如果存在，直接用数据库的 user
                // 这里可以调用 authService 做额外业务，比如缓存用户等
                
                authService.getUserByUserId(user.getUserId().toString());

                // 生成 JWT token
                String token = JwtUtil.generateToken(user.getUserId().toString());

                Map<String, Object> result = new HashMap<>();
                result.put("token", token);
                result.put("username", user.getName());

                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(401).body("用户未完成 OAuth2 登录");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("OAuth2 认证异常: " + e.getMessage());
        }
    }
}
