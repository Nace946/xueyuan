package com.aixueyuan.rent.service;

import com.aixueyuan.rent.entity.Banner;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 首页轮播服务
 */
public interface BannerService extends IService<Banner> {

    /**
     * 按排序值返回全部轮播（含禁用项，供后台管理）
     */
    List<Banner> listAll();
}
