package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能：
 * 作者：ljs
 * 日期：2024/4/15 0:11
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    public DishMapper dishMapper;

    @Autowired
    public DishFlavorMapper dishFlavorMapper;

    @Autowired
    public SetmealDishMapper setmealDishMapper;

    @Autowired
    private SetmealMapper setmealMapper;
    /**
     * 新增菜品
     * @param dishDTO
     */
    @Override
    @Transient//todo 涉及到向两张表添加数据 添加此注解保证数据一致性
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();//dishDto有包括了口味字段，本方法不需要，故new新实体

        BeanUtils.copyProperties(dishDTO,dish);
        //菜品表插入一条数据
        dishMapper.insert(dish);
        //todo  获取动态SQL生成的ID
        Long dishId = dish.getId();
        //插入口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0){

            flavors.forEach(dishFlavor -> {//遍历每个口味，并为其id赋值
                dishFlavor.setDishId(dishId);
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        //下一条sql进行分页，自动加入limit关键字分页
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }



    /**
     * 菜品批量删除
     */
    @Override
    @Transient
    //todo 事务注解 保持多表操作时的一致性
    public void deleteBatch(List<Long> ids) {
        //判断菜品是否能够删除
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE){
                //菜品在销售中
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE  );
            }
        }

        //判断菜品是否和套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size()>0){
            //被套餐关联，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //可以删除--进行删除菜品数据
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            //删除菜品关联的口味数据
//            dishFlavorMapper.deleteById(id);
//        }
        //优化版删除，不用循环n次：根据菜品集合批量删除数据
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);



    }

    /**
     * 起售停售菜品
     * @param status
     * @param id
     */
    @Override
    public void startOrstop(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.update(dish);
    }

    /**
     * 根据id查找菜品
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //根据id查菜品
        Dish dish = dishMapper.getById(id);
        //根据菜品id查口味
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        //封装到vo
        DishVO dishVO =  new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    @Override
    public void update(DishDTO dishDTO) {
        //修改菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        //刪除口味
        List<Long> flavor = new ArrayList<>();
        flavor.add(dishDTO.getId());
        dishFlavorMapper.deleteByDishIds(flavor);
        //重新插入口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0){

            flavors.forEach(dishFlavor -> {//遍历每个口味，并为其id赋值
                dishFlavor.setDishId(dishDTO.getId());
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }
}