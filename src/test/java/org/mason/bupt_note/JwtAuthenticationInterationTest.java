package org.mason.bupt_note;

import org.junit.jupiter.api.Test;
import org.mason.bupt_note.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Test
    void testLoginSuccess() throws Exception {
        // 假设数据库中有用户学号为 "2022211331" 和密码为 "123456"
        mockMvc.perform(post("/api/auth/login")
                        .param("studentId", "2022211331")
                        .param("password", "123456"))
                .andExpect(status().isOk())  // 验证返回状态码为 200 OK
                .andExpect(content().string(org.hamcrest.Matchers.startsWith("eyJ")));  // 验证返回的 Token 以 "eyJ" 开头（JWT 格式）
    }
    @Test
    void testLoginAndAccessProtectedEndpoint() throws Exception {
        // 模拟登录，生成 JWT
        String userID = "1";
        String token = JwtUtil.generateToken(userID);

        // 使用生成的 JWT 访问受保护的端点
        mockMvc.perform(get("/api/protected-endpoint")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testAccessProtectedEndpointWithoutToken() throws Exception {
        // 不带 Token 访问受保护的端点
        mockMvc.perform(get("/api/protected-endpoint"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAccessProtectedEndpointWithInvalidToken() throws Exception {
        // 使用无效 Token 访问受保护的端点
        mockMvc.perform(get("/api/protected-endpoint")
                        .header("Authorization", "Bearer invalidToken"))
                .andExpect(status().isForbidden());
    }
}