package com.aixueyuan.rent.service.impl;

import com.aixueyuan.rent.entity.Banner;
import com.aixueyuan.rent.mapper.BannerMapper;
import com.aixueyuan.rent.service.BannerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Override
    public List<Banner> listAll() {
        LambdaQueryWrapper<Banner> qw = Wrappers.lambdaQuery(Banner.class)
                .orderByAsc(Banner::getSort)
                .orderByDesc(Banner::getId);
        return this.list(qw);
    }
}
