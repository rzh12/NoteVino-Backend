package com.rzh12.notevino.security;

import com.rzh12.notevino.dto.UserDetailDTO;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 獲取請求的 Authorization header
        String authorizationHeader = request.getHeader("Authorization");

        // 如果 Authorization header 不存在或不以 'Bearer ' 開頭，跳過過濾
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 從 header 中提取 JWT token
        String token = authorizationHeader.substring(7);

        try {
            // 驗證和解析 token
            Claims claims = jwtUtil.extractClaims(token);
            String email = claims.getSubject();  // 提取 email 作為 subject

            // 從 claims 中提取更多的用戶相關資訊
            Integer userId = (Integer) claims.get("userId");
            String username = (String) claims.get("username");
            String provider = (String) claims.get("provider");

            // 如果解析成功，並且 SecurityContext 中沒有設置用戶
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 創建 UserDetailDTO 並將用戶的詳細信息存入其中
                UserDetailDTO userDetailDTO = new UserDetailDTO(userId, username, email, provider);

                // 構建 Spring Security 的認證對象
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetailDTO, null, null);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 將認證對象設置到 Spring Security Context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {
            // 如果 token 無效或過期，返回 403 錯誤
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
            return;
        }

        // 繼續過濾器鏈
        filterChain.doFilter(request, response);
    }
}
