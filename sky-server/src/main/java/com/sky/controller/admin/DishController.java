package com.sky.controller.admin;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 功能：
 * 作者：ljs
 * 日期：2024/4/15 0:10
 */
@RestController("adminDishController")
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品管理")
public class DishController {
    @Autowired
    public DishService dishService;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private RedisTemplate redisTemplate;

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

        //清理缓存数据
        String key = "dish_" + dishDTO.getCategoryId();
        redisTemplate.delete(key);
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

        //将所有菜品缓存清理
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 起售停售菜品
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用菜品")
    public Result<String> startOrstop(@PathVariable Integer status,Long id){
        log.info("起售停售菜品，{}",id);
        dishService.startOrstop(status,id);

        //将所有菜品缓存清理
        cleanCache("dish_*");
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
        //将所有菜品缓存清理
        cleanCache("dish_*");

        return Result.success();

    }
    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }


    /**
     * 清理缓存的方法
     * @param pattern
     */
    private void cleanCache(String pattern){
        //将所有菜品缓存清理
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}