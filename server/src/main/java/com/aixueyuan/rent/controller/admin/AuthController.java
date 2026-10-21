package com.aixueyuan.rent.controller.admin;

import com.aixueyuan.rent.common.R;
import com.aixueyuan.rent.entity.SysUser;
import com.aixueyuan.rent.util.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.aixueyuan.rent.mapper.SysUserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 后台 - 登录鉴权
 * 密码校验使用 BCrypt，数据库中不存储明文密码
 */
@RestController
@RequestMapping("/api/admin")
public class AuthController {

    private final SysUserMapper sysUserMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(SysUserMapper sysUserMapper, JwtUtil jwtUtil) {
        this.sysUserMapper = sysUserMapper;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return R.fail("请输入账号和密码");
        }

        LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery(SysUser.class)
                .eq(SysUser::getUsername, username.trim());
        SysUser user = sysUserMapper.selectOne(qw);

        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            return R.fail("账号不存在或已禁用");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return R.fail("账号或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        user.setLastLogin(LocalDateTime.now());
        sysUserMapper.updateById(user);

        Map<String, Object> data = new HashMap<>(4);
        data.put("token", token);
        Map<String, Object> userInfo = new HashMap<>(4);
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        data.put("user", userInfo);
        return R.ok(data);
    }

    /**
     * 校验 token 是否有效（前端刷新页面时调用）
     */
    @GetMapping("/profile")
    public R<Map<String, Object>> profile(@RequestHeader(value = "Authorization", required = false) String auth) {
        String username = null;
        if (auth != null && auth.startsWith("Bearer ")) {
            io.jsonwebtoken.Claims claims = jwtUtil.parse(auth.substring(7));
            if (claims != null) {
                username = claims.get("username", String.class);
            }
        }
        if (username == null) {
            return R.fail(401, "未登录");
        }
        Map<String, Object> data = new HashMap<>(2);
        data.put("username", username);
        return R.ok(data);
    }
}
