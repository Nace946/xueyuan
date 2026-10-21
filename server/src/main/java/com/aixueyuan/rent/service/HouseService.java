package com.aixueyuan.rent.service;

import com.aixueyuan.rent.common.OpenPageResult;
import com.aixueyuan.rent.dto.HouseQuery;
import com.aixueyuan.rent.entity.House;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 房源服务
 */
public interface HouseService extends IService<House> {

    /**
     * 小程序端分页列表
     */
    OpenPageResult<House> openPage(HouseQuery query);

    /**
     * 小程序端详情（仅上架数据，并累加浏览量）
     */
    House openDetail(Long id);

    /**
     * 后台管理分页
     */
    IPage<House> adminPage(long pageNum, long pageSize, String keyword, Integer status);

    /**
     * 新增 / 编辑时的数据清洗
     */
    void normalize(House house);
}
