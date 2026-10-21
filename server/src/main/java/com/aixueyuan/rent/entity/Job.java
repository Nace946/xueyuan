package com.aixueyuan.rent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 招聘信息
 */
@Data
@TableName("job")
public class Job implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 职位名称 */
    private String title;

    private String companyName;
    private String companyLogo;
    private String companyType;

    /** 全职/兼职/实习/临时 */
    private String jobType;

    /** 薪资下限（元/月） */
    private Integer salaryMin;

    /** 薪资上限（元/月） */
    private Integer salaryMax;

    /** 薪资文案（优先展示，如 150元/天） */
    private String salaryText;

    /** 结算方式：日结 / 周结 / 月结 / 完工结 */
    private String payType;

    private String city;
    private String district;
    private String address;

    /** 工作时段，如：早班 9:00-14:00 */
    private String workTime;

    /** 经验要求 */
    private String experience;

    /** 学历要求（社区临工场景一般不限） */
    private String education;

    /** 招聘人数 */
    private String recruitCount;

    /** 福利（逗号分隔） */
    private String welfare;

    /** 标签（逗号分隔） */
    private String tags;

    /** 职位描述 */
    private String description;

    private String contactName;
    private String contactPhone;
    private String contactWechat;

    private Integer recommend;

    /** 状态：0 下架 1 上架 */
    private Integer status;

    private Integer sort;

    private Integer viewCount;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
