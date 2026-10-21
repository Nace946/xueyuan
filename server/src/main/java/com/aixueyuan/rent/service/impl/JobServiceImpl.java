package com.aixueyuan.rent.service.impl;

import com.aixueyuan.rent.common.OpenPageResult;
import com.aixueyuan.rent.dto.JobQuery;
import com.aixueyuan.rent.entity.Job;
import com.aixueyuan.rent.mapper.JobMapper;
import com.aixueyuan.rent.service.JobService;
import com.aixueyuan.rent.util.ConvertUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    @Override
    public OpenPageResult<Job> openPage(JobQuery query) {
        long pageNum = query.getPageNum() <= 0 ? 1 : query.getPageNum();
        long pageSize = query.getPageSize() <= 0 ? 10 : Math.min(query.getPageSize(), 50);

        LambdaQueryWrapper<Job> qw = new LambdaQueryWrapper<>();
        qw.eq(Job::getStatus, 1);

        if (StringUtils.hasText(query.getKeyword())) {
            String kw = query.getKeyword().trim();
            qw.and(w -> w.like(Job::getTitle, kw)
                    .or().like(Job::getCompanyName, kw)
                    .or().like(Job::getDistrict, kw));
        }
        if (StringUtils.hasText(query.getDistrict())) {
            qw.eq(Job::getDistrict, query.getDistrict().trim());
        }
        if (StringUtils.hasText(query.getJobType())) {
            qw.eq(Job::getJobType, query.getJobType().trim());
        }

        if ("salary_desc".equals(query.getSort())) {
            qw.orderByDesc(Job::getSalaryMax);
        } else {
            qw.orderByDesc(Job::getSort).orderByDesc(Job::getCreateTime);
        }

        Page<Job> page = new Page<>(pageNum, pageSize);
        page.addOrder(OrderItem.desc("id"));
        IPage<Job> result = this.page(page, qw);

        List<Job> voList = new ArrayList<>();
        for (Job j : result.getRecords()) {
            voList.add(toOpenVo(j));
        }

        return new OpenPageResult<>(voList, result.getTotal(), pageNum, pageSize,
                this.baseMapper.selectDistricts());
    }

    @Override
    public Job openDetail(Long id) {
        if (id == null) {
            return null;
        }
        Job job = this.getById(id);
        if (job == null || job.getStatus() == null || job.getStatus() != 1) {
            return null;
        }
        try {
            this.baseMapper.incrViewCount(id);
        } catch (Exception e) {
            // ignore
        }
        return toOpenVo(job);
    }

    @Override
    public IPage<Job> adminPage(long pageNum, long pageSize, String keyword, Integer status) {
        LambdaQueryWrapper<Job> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            qw.and(w -> w.like(Job::getTitle, kw)
                    .or().like(Job::getCompanyName, kw)
                    .or().like(Job::getDistrict, kw));
        }
        if (status != null) {
            qw.eq(Job::getStatus, status);
        }
        qw.orderByDesc(Job::getSort).orderByDesc(Job::getId);
        return this.page(new Page<>(pageNum, pageSize), qw);
    }

    @Override
    public void normalize(Job job) {
        job.setTags(ConvertUtil.toStoreValue(job.getTags()));
        job.setWelfare(ConvertUtil.toStoreValue(job.getWelfare()));
        if (job.getStatus() == null) {
            job.setStatus(1);
        }
        if (job.getSort() == null) {
            job.setSort(0);
        }
        if (job.getRecommend() == null) {
            job.setRecommend(0);
        }
        if (job.getSalaryMin() == null) {
            job.setSalaryMin(0);
        }
        if (job.getSalaryMax() == null) {
            job.setSalaryMax(0);
        }
    }

    private Job toOpenVo(Job job) {
        Job vo = new Job();
        BeanUtils.copyProperties(job, vo);
        vo.setTags(ConvertUtil.toJsonArray(job.getTags()));
        vo.setWelfare(ConvertUtil.toJsonArray(job.getWelfare()));
        return vo;
    }
}
