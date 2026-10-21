package com.aixueyuan.rent.common;

import java.io.Serializable;
import java.util.List;

/**
 * 小程序端列表返回：分页数据 + 筛选项（区域列表）
 */
public class OpenPageResult<T> extends PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 可选区域，用于列表页筛选栏 */
    private List<String> districts;

    public OpenPageResult() {
    }

    public OpenPageResult(List<T> list, long total, long pageNum, long pageSize, List<String> districts) {
        super(list, total, pageNum, pageSize);
        this.districts = districts;
    }

    public List<String> getDistricts() {
        return districts;
    }

    public void setDistricts(List<String> districts) {
        this.districts = districts;
    }
}
