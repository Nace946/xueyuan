package com.aixueyuan.rent.controller.open;

import com.aixueyuan.rent.common.OpenPageResult;
import com.aixueyuan.rent.common.R;
import com.aixueyuan.rent.dto.HouseQuery;
import com.aixueyuan.rent.entity.House;
import com.aixueyuan.rent.service.HouseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序端房源接口（无需登录）
 */
@RestController
@RequestMapping("/api/open/house")
public class HouseController {

    private final HouseService houseService;

    public HouseController(HouseService houseService) {
        this.houseService = houseService;
    }

    /**
     * 房源分页列表
     */
    @GetMapping("/list")
    public R<OpenPageResult<House>> list(HouseQuery query) {
        return R.ok(houseService.openPage(query));
    }

    /**
     * 房源详情
     */
    @GetMapping("/{id}")
    public R<House> detail(@PathVariable("id") Long id) {
        House house = houseService.openDetail(id);
        if (house == null) {
            return R.fail(404, "房源不存在或已下架");
        }
        return R.ok(house);
    }
}
