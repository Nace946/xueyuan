package com.aixueyuan.rent.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具（HS256）
 * 注意：密钥必须配置在服务端，禁止下发到小程序端或写入前端代码
 */
public class JwtUtil {

    private final String secret;
    private final long expireHours;

    public JwtUtil(String secret, long expireHours) {
        this.secret = secret;
        this.expireHours = expireHours;
    }

    public String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>(4);
        claims.put("userId", userId);
        claims.put("username", username);
        long now = System.currentTimeMillis();
        Date expire = new Date(now + expireHours * 3600 * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(expire)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * 解析 token，失败返回 null
     */
    public Claims parse(String token) {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
