package com.aixueyuan.rent.controller.open;

import com.aixueyuan.rent.common.R;
import com.aixueyuan.rent.entity.Banner;
import com.aixueyuan.rent.entity.House;
import com.aixueyuan.rent.entity.Job;
import com.aixueyuan.rent.service.HouseService;
import com.aixueyuan.rent.service.JobService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.aixueyuan.rent.mapper.BannerMapper;
import com.aixueyuan.rent.util.ConvertUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序首页聚合接口（一次请求返回首屏所需数据，减少请求数）
 */
@RestController
@RequestMapping("/api/open/home")
public class HomeController {

    private final HouseService houseService;
    private final JobService jobService;
    private final BannerMapper bannerMapper;

    public HomeController(HouseService houseService, JobService jobService, BannerMapper bannerMapper) {
        this.houseService = houseService;
        this.jobService = jobService;
        this.bannerMapper = bannerMapper;
    }

    @GetMapping("/index")
    public R<Map<String, Object>> index() {
        LambdaQueryWrapper<Banner> bq = Wrappers.lambdaQuery(Banner.class)
                .eq(Banner::getStatus, 1)
                .orderByAsc(Banner::getSort)
                .orderByDesc(Banner::getId)
                .last("LIMIT 5");
        List<Banner> banners = bannerMapper.selectList(bq);

        // 推荐房源
        List<House> houses = houseService.list(Wrappers.lambdaQuery(House.class)
                .eq(House::getStatus, 1)
                .orderByDesc(House::getRecommend)
                .orderByDesc(House::getSort)
                .orderByDesc(House::getCreateTime)
                .last("LIMIT 6"));

        List<Job> jobs = jobService.list(Wrappers.lambdaQuery(Job.class)
                .eq(Job::getStatus, 1)
                .orderByDesc(Job::getRecommend)
                .orderByDesc(Job::getSort)
                .orderByDesc(Job::getCreateTime)
                .last("LIMIT 6"));

        // 标签类字段转 JSON 数组（与列表接口保持一致）
        houses.forEach(h -> {
            h.setTags(ConvertUtil.toJsonArray(h.getTags()));
            h.setFacilities(ConvertUtil.toJsonArray(h.getFacilities()));
            h.setImages(ConvertUtil.normalizeImages(h.getImages()));
        });
        jobs.forEach(j -> {
            j.setTags(ConvertUtil.toJsonArray(j.getTags()));
            j.setWelfare(ConvertUtil.toJsonArray(j.getWelfare()));
        });

        long houseCount = houseService.count(Wrappers.lambdaQuery(House.class).eq(House::getStatus, 1));
        long jobCount = jobService.count(Wrappers.lambdaQuery(Job.class).eq(Job::getStatus, 1));

        Map<String, Object> data = new HashMap<>(8);
        data.put("banners", banners);
        data.put("houses", houses);
        data.put("jobs", jobs);
        Map<String, Object> stats = new HashMap<>(4);
        stats.put("houseCount", houseCount);
        stats.put("jobCount", jobCount);
        data.put("stats", stats);
        return R.ok(data);
    }
}
