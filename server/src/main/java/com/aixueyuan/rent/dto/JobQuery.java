package com.aixueyuan.rent.dto;

import lombok.Data;

/**
 * 小程序端招聘列表查询条件
 */
@Data
public class JobQuery {

    private long pageNum = 1;
    private long pageSize = 10;

    /** 关键词：职位 / 公司 */
    private String keyword;

    private String district;

    /** 全职 / 兼职 / 实习 / 临时 */
    private String jobType;

    /** latest / salary_desc */
    private String sort;
}
