package com.aixueyuan.rent.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 小程序端房源列表查询条件
 */
@Data
public class HouseQuery {

    private long pageNum = 1;
    private long pageSize = 10;

    /** 关键词：标题 / 地址 / 区域 */
    private String keyword;

    /** 区域筛选 */
    private String district;

    /** 最低价（元） */
    private BigDecimal minPrice;

    /** 最高价（元） */
    private BigDecimal maxPrice;

    /** latest / price_asc / price_desc */
    private String sort;
}
