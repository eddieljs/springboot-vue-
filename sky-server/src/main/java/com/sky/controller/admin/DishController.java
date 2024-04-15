package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 功能：
 * 作者：ljs
 * 日期：2024/4/15 0:10
 */
@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品管理")
public class DishController {
    @Autowired
    public DishService dishService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }
}