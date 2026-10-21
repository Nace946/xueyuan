package com.aixueyuan.rent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房源信息
 */
@Data
@TableName("house")
public class House implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 房源标题 */
    private String title;

    /** 封面图 */
    private String cover;

    /** 图片 URL 数组（JSON 字符串） */
    private String images;

    /** 月租价格（元） */
    private BigDecimal price;

    /** 押金方式 */
    private String depositType;

    /** 户型 */
    private String layout;

    /** 面积 */
    private BigDecimal area;

    /** 所在楼层 */
    private String floor;

    /** 总楼层 */
    private String totalFloor;

    /** 朝向 */
    private String orientation;

    /** 装修 */
    private String decoration;

    /** 城市 */
    private String city;

    /** 区域/商圈 */
    private String district;

    /** 详细地址 */
    private String address;

    /** 标签（逗号分隔存储） */
    private String tags;

    /** 配套（逗号分隔存储） */
    private String facilities;

    /** 房源描述 */
    private String description;

    private String contactName;
    private String contactPhone;
    private String contactWechat;

    /** 是否推荐 */
    private Integer recommend;

    /** 状态：0 下架 1 上架 */
    private Integer status;

    /** 排序值 */
    private Integer sort;

    /** 浏览量 */
    private Integer viewCount;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
