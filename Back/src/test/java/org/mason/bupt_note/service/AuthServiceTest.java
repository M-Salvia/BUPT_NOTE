package org.mason.bupt_note.service;

import org.junit.jupiter.api.Test;
import org.mason.bupt_note.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    public void testGetUserByUserIdCaching() {
        // 第一次调用，应该触发数据库查询
        User result1 = authService.getUserByUserId("1");
        assertNotNull(result1);

        // 第二次调用，应该从缓存中获取
        User result2 = authService.getUserByUserId("1");
        assertNotNull(result2);

        // 验证缓存中是否存在
        User cachedUser = (User) cacheManager.getCache("loggedInUsers").get("1").get();
        assertNotNull(cachedUser);
    }
}