package com.aixueyuan.rent.controller.admin;

import com.aixueyuan.rent.common.R;
import com.aixueyuan.rent.entity.Job;
import com.aixueyuan.rent.service.JobService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 后台 - 招聘管理
 */
@RestController
@RequestMapping("/api/admin/job")
public class AdminJobController {

    private final JobService jobService;

    public AdminJobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/page")
    public R<Map<String, Object>> page(@RequestParam(defaultValue = "1") long pageNum,
                                       @RequestParam(defaultValue = "10") long pageSize,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) Integer status) {
        IPage<Job> page = jobService.adminPage(pageNum, pageSize, keyword, status);
        Map<String, Object> data = new HashMap<>(4);
        data.put("list", page.getRecords());
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.ok(data);
    }

    @GetMapping("/{id}")
    public R<Job> get(@PathVariable("id") Long id) {
        return R.ok(jobService.getById(id));
    }

    @PostMapping
    public R<Long> save(@RequestBody Job job) {
        job.setId(null);
        jobService.normalize(job);
        jobService.save(job);
        return R.ok(job.getId());
    }

    @PutMapping("/{id}")
    public R<Boolean> update(@PathVariable("id") Long id, @RequestBody Job job) {
        job.setId(id);
        jobService.normalize(job);
        return R.ok(jobService.updateById(job));
    }

    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable("id") Long id) {
        return R.ok(jobService.removeById(id));
    }

    @PostMapping("/{id}/status")
    public R<Boolean> updateStatus(@PathVariable("id") Long id, @RequestParam Integer status) {
        Job job = new Job();
        job.setId(id);
        job.setStatus(status);
        return R.ok(jobService.updateById(job));
    }
}
