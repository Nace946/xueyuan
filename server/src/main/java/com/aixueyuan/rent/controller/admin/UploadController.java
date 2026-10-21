package com.aixueyuan.rent.controller.admin;

import com.aixueyuan.rent.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * 后台 - 图片上传
 * 保存目录由 upload.path 配置，访问路径前缀固定为 /uploads/
 */
@RestController
@RequestMapping("/api/admin/upload")
public class UploadController {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.allow-ext}")
    private String allowExt;

    @Value("${upload.max-size}")
    private long maxSize;

    private static final DateTimeFormatter DIR_FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @PostMapping("/image")
    public R<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return R.fail("请选择要上传的图片");
        }
        if (file.getSize() > maxSize) {
            return R.fail("图片大小不能超过 " + (maxSize / 1024 / 1024) + "MB");
        }

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        }
        if (ext.isEmpty() || !("," + allowExt.toLowerCase(Locale.ROOT) + ",").contains("," + ext + ",")) {
            return R.fail("不支持的图片格式，仅支持：" + allowExt);
        }

        String dateDir = LocalDate.now().format(DIR_FMT);

        // 关键：统一转成绝对路径。
        // MultipartFile.transferTo 传相对路径时，Tomcat 会相对其临时工作目录
        // （%TEMP%/tomcat.8080.xxx/work/Tomcat/localhost/ROOT/）解析，
        // 导致 java.io.FileNotFoundException（目录跑到 temp 下且不存在）。
        File baseDir;
        try {
            baseDir = new File(uploadPath).getCanonicalFile();
        } catch (IOException e) {
            baseDir = new File(uploadPath).getAbsoluteFile();
        }

        File dir = new File(baseDir, dateDir);
        if (!dir.exists() && !dir.mkdirs()) {
            return R.fail("创建上传目录失败：" + dir.getAbsolutePath());
        }

        String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        File dest = new File(dir, filename);
        try {
            // 双保险：确保 transferTo 拿到的一定是绝对路径
            file.transferTo(dest.getAbsoluteFile());
        } catch (IOException e) {
            return R.fail("图片保存失败：" + e.getMessage());
        }

        String url = "/uploads/" + dateDir + "/" + filename;
        Map<String, String> data = new HashMap<>(2);
        data.put("url", url);
        data.put("filename", filename);
        return R.ok(data);
    }
}
