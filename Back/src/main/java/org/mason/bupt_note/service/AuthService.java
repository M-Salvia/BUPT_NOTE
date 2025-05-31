package org.mason.bupt_note.service;

import org.mason.bupt_note.entity.User;
import org.mason.bupt_note.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //自动缓存写入
    @Cacheable(value = "loggedInUsers", key = "#userId")
    public User getUserByUserId(String userId) {
        // 模拟从数据库查询用户信息
        User user = findUserByUserId(userId);
        System.out.println("缓存键: " + userId + ", 缓存值: " + user);
        return user;
    }

    User findUserByUserId(String userId) {
        return userRepository.findById(Integer.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("用户未找到: " + userId));
    }
}