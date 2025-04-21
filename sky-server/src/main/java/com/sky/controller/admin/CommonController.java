package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<Object> upload(MultipartFile file) throws IOException {
        //获取文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        String fileName = UUID.randomUUID().toString() + extension;

        //上传到阿里云
        try {
            String filePath = aliOssUtil.upload(file.getBytes(), fileName);
            return Result.success(filePath);
        }catch (IOException e){
            log.error("上传文件失败", e.getMessage());
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
