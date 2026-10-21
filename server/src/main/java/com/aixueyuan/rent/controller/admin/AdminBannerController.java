package com.aixueyuan.rent.controller.admin;

import com.aixueyuan.rent.common.R;
import com.aixueyuan.rent.entity.Banner;
import com.aixueyuan.rent.service.BannerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台 - 首页轮播管理
 */
@RestController
@RequestMapping("/api/admin/banner")
public class AdminBannerController {

    private final BannerService bannerService;

    public AdminBannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @GetMapping("/list")
    public R<List<Banner>> list() {
        return R.ok(bannerService.listAll());
    }

    @GetMapping("/{id}")
    public R<Banner> get(@PathVariable("id") Long id) {
        return R.ok(bannerService.getById(id));
    }

    @PostMapping
    public R<Long> save(@RequestBody Banner banner) {
        banner.setId(null);
        if (banner.getSort() == null) {
            banner.setSort(0);
        }
        if (banner.getStatus() == null) {
            banner.setStatus(1);
        }
        bannerService.save(banner);
        return R.ok(banner.getId());
    }

    @PutMapping("/{id}")
    public R<Boolean> update(@PathVariable("id") Long id, @RequestBody Banner banner) {
        banner.setId(id);
        return R.ok(bannerService.updateById(banner));
    }

    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable("id") Long id) {
        return R.ok(bannerService.removeById(id));
    }
}
