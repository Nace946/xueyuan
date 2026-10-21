package com.aixueyuan.rent.controller.open;

import com.aixueyuan.rent.common.OpenPageResult;
import com.aixueyuan.rent.common.R;
import com.aixueyuan.rent.dto.JobQuery;
import com.aixueyuan.rent.entity.Job;
import com.aixueyuan.rent.service.JobService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序端招聘接口（无需登录）
 */
@RestController
@RequestMapping("/api/open/job")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/list")
    public R<OpenPageResult<Job>> list(JobQuery query) {
        return R.ok(jobService.openPage(query));
    }

    @GetMapping("/{id}")
    public R<Job> detail(@PathVariable("id") Long id) {
        Job job = jobService.openDetail(id);
        if (job == null) {
            return R.fail(404, "职位不存在或已下架");
        }
        return R.ok(job);
    }
}
