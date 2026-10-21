package com.aixueyuan.rent.init;

import com.aixueyuan.rent.entity.SysUser;
import com.aixueyuan.rent.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 首次启动初始化默认管理员（仅当 sys_user 表为空时执行）
 * 密码使用 BCrypt 加密，不落明文
 */
@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final SysUserMapper sysUserMapper;

    @Value("${app.admin.default-username}")
    private String defaultUsername;

    @Value("${app.admin.default-password}")
    private String defaultPassword;

    public AdminInitializer(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public void run(String... args) {
        Long count = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>());
        if (count != null && count > 0) {
            return;
        }
        SysUser user = new SysUser();
        user.setUsername(defaultUsername);
        user.setPassword(new BCryptPasswordEncoder().encode(defaultPassword));
        user.setNickname("超级管理员");
        user.setRole("admin");
        user.setStatus(1);
        sysUserMapper.insert(user);

        log.info("=================================================");
        log.info("已创建默认管理员账号：{} ，初始密码：{}", defaultUsername, defaultPassword);
        log.info("请登录后台后及时修改密码（当前版本可在数据库中更新 sys_user.password）");
        log.info("=================================================");
    }
}
