package com.sky.controller.admin;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    /**
     * 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询，{}",dishPageQueryDTO);
        //select * from category limit 0,10
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品批量删除
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result delete(@RequestParam List<Long> ids){//todo 该注解作用：将数据分隔，存入集合
        log.info("删除菜品id：{}",ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 起手停售菜品
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用菜品")
    public Result<String> startOrstop(@PathVariable Integer status,Long id){
        log.info("起售停售菜品，{}",id);
        dishService.startOrstop(status,id);
        return Result.success();
    }

    /**
     *根据id查菜品
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查菜品，{}",id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品，{}",dishDTO);
        dishService.update(dishDTO);
        return Result.success();

    }
}