package org.mason.bupt_note.controller;

import org.mason.bupt_note.util.JwtUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    private final StringRedisTemplate redisTemplate;

    public SmsController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendSmsCode(@RequestHeader("Authorization") String token) {
        // 从JWT中解析用户ID
        String userId = JwtUtil.validateToken(token);
        System.out.println("用户ID: " + userId);
        // 生成6位随机验证码
        String code = String.format("%06d", new Random().nextInt(1000000));
        System.out.println("发送的验证码: " + code);

        // 将验证码存入Redis，设置3分钟有效期
        redisTemplate.opsForValue().set("SMS_CODE_" + userId, code, 20, TimeUnit.MINUTES);

        return ResponseEntity.ok("验证码已发送");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifySmsCode(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> requestBody) {
        // 从JWT中解析用户ID
        String userId = JwtUtil.validateToken(token);
        String code = requestBody.get("code");
        // 从Redis中获取验证码
        String redisCode = redisTemplate.opsForValue().get("SMS_CODE_" + userId);

        System.out.println("Redis中的验证码: " + redisCode);
        System.out.println("用户输入的验证码: " + code);
        if (redisCode == null) {
            return ResponseEntity.status(400).body("验证码已过期");
        }

        if (!redisCode.equals(code)) {
            return ResponseEntity.status(400).body("验证码错误");
        }

        // 验证成功后删除Redis中的验证码
        redisTemplate.delete("SMS_CODE_" + userId);

        return ResponseEntity.ok("验证码验证成功");
    }
}