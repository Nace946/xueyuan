package com.aixueyuan.rent.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串与数组互转工具
 * 数据库：tags / facilities / welfare 以「英文逗号」分隔字符串存储
 * 小程序端：统一以 JSON 数组字符串返回，便于 WXML 直接 wx:for
 */
public final class ConvertUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ConvertUtil() {
    }

    /**
     * 任意输入（JSON 数组、逗号/空格分隔字符串）归一化为逗号分隔字符串
     */
    public static String toStoreValue(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return "";
        }
        String v = raw.trim();
        if (v.startsWith("[")) {
            try {
                List<String> list = MAPPER.readValue(v, new TypeReference<List<String>>() {
                });
                return String.join(",", list);
            } catch (Exception e) {
                // 解析失败时按普通文本处理
            }
        }
        // 统一分隔符：中文逗号、空格、顿号
        String normalized = v.replace("，", ",").replace("、", ",").replace(" ", ",");
        List<String> parts = new ArrayList<>();
        for (String p : normalized.split(",")) {
            String t = p.trim();
            if (!t.isEmpty()) {
                parts.add(t);
            }
        }
        return String.join(",", parts);
    }

    /**
     * 逗号分隔字符串 → JSON 数组字符串（空值返回 "[]"）
     */
    public static String toJsonArray(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return "[]";
        }
        String[] parts = raw.split(",");
        List<String> list = new ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) {
                list.add(t);
            }
        }
        try {
            return MAPPER.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    /**
     * 图片数组 JSON 字符串校验，非法时返回 "[]"
     */
    public static String normalizeImages(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return "[]";
        }
        String v = raw.trim();
        if (!v.startsWith("[")) {
            return "[]";
        }
        try {
            MAPPER.readValue(v, new TypeReference<List<String>>() {
            });
            return v;
        } catch (Exception e) {
            return "[]";
        }
    }

    /**
     * 从图片 JSON 中取第一张作为封面
     */
    public static String firstImage(String imagesJson) {
        if (imagesJson == null || !imagesJson.trim().startsWith("[")) {
            return "";
        }
        try {
            List<String> list = MAPPER.readValue(imagesJson, new TypeReference<List<String>>() {
            });
            return list.isEmpty() ? "" : list.get(0);
        } catch (Exception e) {
            return "";
        }
    }
}
