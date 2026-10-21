package com.aixueyuan.rent.mapper;

import com.aixueyuan.rent.entity.Job;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface JobMapper extends BaseMapper<Job> {

    @Select("SELECT DISTINCT district FROM job " +
            "WHERE deleted = 0 AND status = 1 AND district IS NOT NULL AND district <> '' " +
            "ORDER BY district")
    List<String> selectDistricts();

    @Update("UPDATE job SET view_count = view_count + 1 WHERE id = #{id}")
    int incrViewCount(@Param("id") Long id);
}
