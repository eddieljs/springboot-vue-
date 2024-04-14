package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")//todo 文件上传
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){//返回值data为string 故泛型为string
        log.info("文件上传：{}",file);
        return null;
    }

}