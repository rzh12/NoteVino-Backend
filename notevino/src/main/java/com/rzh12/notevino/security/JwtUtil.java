package com.rzh12.notevino.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    // 從外部配置檔案讀取 base64 編碼的密鑰
    public JwtUtil(@Value("${jwt.secret.key}") String secretKeyString) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKeyString);
        this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }

    // 生成 JWT token，允許存儲多個用戶屬性
    public String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)  // 設置 claims
                .setSubject(subject)  // 設置 subject，例如用戶 email
                .setIssuedAt(new Date())  // 設置發行時間
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 3600))  // 1小時過期
                .signWith(secretKey, SignatureAlgorithm.HS256)  // 使用密鑰進行簽名
                .compact();
    }

    // 從 token 中提取 claims
    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)  // 使用密鑰進行簽名驗證
                    .build()
                    .parseClaimsJws(token)  // 解析並驗證 token
                    .getBody();  // 提取 token 中的 Claims
        } catch (JwtException e) {
            throw new JwtException("Token is expired or invalid", e);
        }
    }
}

