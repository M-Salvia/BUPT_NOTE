package org.mason.bupt_note.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mason.bupt_note.util.JwtUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String studentId = JwtUtil.validateToken(token);
            if (studentId != null) {
                // 设置认证信息，表示用户已经通过认证
                SecurityContextHolder.getContext().setAuthentication(
                        new JwtAuthenticationToken(studentId, null, null)
                );
            }
        }
        filterChain.doFilter(request, response);
    }
}