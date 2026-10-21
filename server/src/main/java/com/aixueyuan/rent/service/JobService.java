package com.aixueyuan.rent.service;

import com.aixueyuan.rent.common.OpenPageResult;
import com.aixueyuan.rent.dto.JobQuery;
import com.aixueyuan.rent.entity.Job;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface JobService extends IService<Job> {

    OpenPageResult<Job> openPage(JobQuery query);

    Job openDetail(Long id);

    IPage<Job> adminPage(long pageNum, long pageSize, String keyword, Integer status);

    void normalize(Job job);
}
