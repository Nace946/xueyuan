package com.aixueyuan.rent.controller.admin;

import com.aixueyuan.rent.common.R;
import com.aixueyuan.rent.entity.House;
import com.aixueyuan.rent.service.HouseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 后台 - 房源管理
 */
@RestController
@RequestMapping("/api/admin/house")
public class AdminHouseController {

    private final HouseService houseService;

    public AdminHouseController(HouseService houseService) {
        this.houseService = houseService;
    }

    @GetMapping("/page")
    public R<Map<String, Object>> page(@RequestParam(defaultValue = "1") long pageNum,
                                       @RequestParam(defaultValue = "10") long pageSize,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) Integer status) {
        IPage<House> page = houseService.adminPage(pageNum, pageSize, keyword, status);
        Map<String, Object> data = new HashMap<>(4);
        data.put("list", page.getRecords());
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.ok(data);
    }

    @GetMapping("/{id}")
    public R<House> get(@PathVariable("id") Long id) {
        return R.ok(houseService.getById(id));
    }

    @PostMapping
    public R<Long> save(@RequestBody House house) {
        house.setId(null);
        houseService.normalize(house);
        houseService.save(house);
        return R.ok(house.getId());
    }

    @PutMapping("/{id}")
    public R<Boolean> update(@PathVariable("id") Long id, @RequestBody House house) {
        house.setId(id);
        houseService.normalize(house);
        return R.ok(houseService.updateById(house));
    }

    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable("id") Long id) {
        return R.ok(houseService.removeById(id));
    }

    /**
     * 上架 / 下架
     */
    @PostMapping("/{id}/status")
    public R<Boolean> updateStatus(@PathVariable("id") Long id, @RequestParam Integer status) {
        House house = new House();
        house.setId(id);
        house.setStatus(status);
        return R.ok(houseService.updateById(house));
    }
}
