package com.aixueyuan.rent.config;

import com.aixueyuan.rent.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire-hours}")
    private long expireHours;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(secret, expireHours);
    }
}
