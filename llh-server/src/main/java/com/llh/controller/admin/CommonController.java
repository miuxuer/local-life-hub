package com.llh.controller.admin;

import com.llh.constant.MessageConstant;
import com.llh.result.Result;
import com.llh.utils.AliOssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/admin/common")
@Tag(name = "通用接口")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @Operation(summary = "文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {

        log.info("文件上传:{}", file);

        try {
            // 原始文件名传给工具类，目录与文件名由它内部按 年/月 + UUID 生成
            String filePath = aliOssUtil.upload(file.getBytes(), file.getOriginalFilename());
            return Result.success(filePath);
        } catch (Exception e) {
            log.error("文件上传失败：{}", e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);

    }

}
