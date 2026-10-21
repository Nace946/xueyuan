package com.aixueyuan.rent.service.impl;

import com.aixueyuan.rent.common.OpenPageResult;
import com.aixueyuan.rent.dto.HouseQuery;
import com.aixueyuan.rent.entity.House;
import com.aixueyuan.rent.mapper.HouseMapper;
import com.aixueyuan.rent.service.HouseService;
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
public class HouseServiceImpl extends ServiceImpl<HouseMapper, House> implements HouseService {

    @Override
    public OpenPageResult<House> openPage(HouseQuery query) {
        long pageNum = query.getPageNum() <= 0 ? 1 : query.getPageNum();
        long pageSize = query.getPageSize() <= 0 ? 10 : Math.min(query.getPageSize(), 50);

        LambdaQueryWrapper<House> qw = new LambdaQueryWrapper<>();
        qw.eq(House::getStatus, 1);

        if (StringUtils.hasText(query.getKeyword())) {
            String kw = query.getKeyword().trim();
            qw.and(w -> w.like(House::getTitle, kw)
                    .or().like(House::getAddress, kw)
                    .or().like(House::getDistrict, kw));
        }
        if (StringUtils.hasText(query.getDistrict())) {
            qw.eq(House::getDistrict, query.getDistrict().trim());
        }
        if (query.getMinPrice() != null) {
            qw.ge(House::getPrice, query.getMinPrice());
        }
        if (query.getMaxPrice() != null) {
            qw.le(House::getPrice, query.getMaxPrice());
        }

        String sort = query.getSort();
        if ("price_asc".equals(sort)) {
            qw.orderByAsc(House::getPrice);
        } else if ("price_desc".equals(sort)) {
            qw.orderByDesc(House::getPrice);
        } else {
            qw.orderByDesc(House::getSort).orderByDesc(House::getCreateTime);
        }

        Page<House> page = new Page<>(pageNum, pageSize);
        page.addOrder(OrderItem.desc("id"));
        IPage<House> result = this.page(page, qw);

        List<House> voList = new ArrayList<>();
        for (House h : result.getRecords()) {
            voList.add(toOpenVo(h));
        }

        return new OpenPageResult<>(voList, result.getTotal(), pageNum, pageSize,
                this.baseMapper.selectDistricts());
    }

    @Override
    public House openDetail(Long id) {
        if (id == null) {
            return null;
        }
        House house = this.getById(id);
        if (house == null || house.getStatus() == null || house.getStatus() != 1) {
            return null;
        }
        // 浏览量自增（失败不影响主流程）
        try {
            this.baseMapper.incrViewCount(id);
        } catch (Exception e) {
            // ignore
        }
        return toOpenVo(house);
    }

    @Override
    public IPage<House> adminPage(long pageNum, long pageSize, String keyword, Integer status) {
        LambdaQueryWrapper<House> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            qw.and(w -> w.like(House::getTitle, kw)
                    .or().like(House::getAddress, kw)
                    .or().like(House::getDistrict, kw));
        }
        if (status != null) {
            qw.eq(House::getStatus, status);
        }
        qw.orderByDesc(House::getSort).orderByDesc(House::getId);
        return this.page(new Page<>(pageNum, pageSize), qw);
    }

    @Override
    public void normalize(House house) {
        house.setTags(ConvertUtil.toStoreValue(house.getTags()));
        house.setFacilities(ConvertUtil.toStoreValue(house.getFacilities()));
        house.setImages(ConvertUtil.normalizeImages(house.getImages()));
        if (!StringUtils.hasText(house.getCover())) {
            house.setCover(ConvertUtil.firstImage(house.getImages()));
        }
        if (house.getStatus() == null) {
            house.setStatus(1);
        }
        if (house.getSort() == null) {
            house.setSort(0);
        }
        if (house.getRecommend() == null) {
            house.setRecommend(0);
        }
    }

    /**
     * 实体转小程序端展示对象：标签类字段输出为 JSON 数组字符串
     */
    private House toOpenVo(House house) {
        House vo = new House();
        BeanUtils.copyProperties(house, vo);
        vo.setTags(ConvertUtil.toJsonArray(house.getTags()));
        vo.setFacilities(ConvertUtil.toJsonArray(house.getFacilities()));
        vo.setImages(ConvertUtil.normalizeImages(house.getImages()));
        if (!StringUtils.hasText(vo.getCover())) {
            vo.setCover(ConvertUtil.firstImage(vo.getImages()));
        }
        return vo;
    }
}
