package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 功能：
 * 作者：ljs
 * 日期：2024/4/14 22:50
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")//todo 文件上传
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){//返回值data为string 故泛型为string
        log.info("文件上传：{}",file);

        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始扩展名后缀xxx.jpg
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名
            String objectName = UUID.randomUUID().toString()+extension;

            //文件请求路径
            String filePath = aliOssUtil.upload(file.getBytes(),objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败，{}",e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

}