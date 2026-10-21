package com.aixueyuan.rent.mapper;

import com.aixueyuan.rent.entity.House;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface HouseMapper extends BaseMapper<House> {

    /**
     * 上架房源的可用区域列表（自定义 SQL，逻辑删除条件需手写）
     */
    @Select("SELECT DISTINCT district FROM house " +
            "WHERE deleted = 0 AND status = 1 AND district IS NOT NULL AND district <> '' " +
            "ORDER BY district")
    List<String> selectDistricts();

    @Update("UPDATE house SET view_count = view_count + 1 WHERE id = #{id}")
    int incrViewCount(@Param("id") Long id);
}
