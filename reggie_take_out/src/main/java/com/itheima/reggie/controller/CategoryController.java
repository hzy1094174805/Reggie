package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.ICategoryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 类别控制器
 * <p>
 * 菜品及套餐分类 前端控制器
 * </p>
 *
 * @author ilovend
 * @date 2023/02/17
 * @since 2023-02-06
 */
@RestController
@RequestMapping("/category")
@Api(tags = "菜品及套餐分类")
@Slf4j
public class CategoryController {
    /**
     * 目录服务
     */
    @Autowired
    private ICategoryService categoryService;

    /**
     * 保存
     *
     * @param category 类别
     * @return {@link R}<{@link String}>
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("category:{}", category);
        categoryService.save(category);
        return R.success("添加成功");
    }

    /**
     * 页面
     *
     * @param page     页面
     * @param pageSize 页面大小
     * @return {@link R}<{@link Page}<{@link Category}>>
     */
    @GetMapping("/page")
    public R<Page<Category>> page(int page, int pageSize) {
        Page<Category> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo, lambdaQueryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 删除
     *
     * @param id id
     * @return {@link R}<{@link String}>
     */
    @DeleteMapping
    public R<String> delete(Long id) {
        log.info("删除分类，id:{}", id);
//        我重写了removeById方法，所以这里再调用一下
        categoryService.removeById(id);
        return R.success("分类信息删除成功");
    }

    /**
     * 更新
     *
     * @param category 类别
     * @return {@link R}<{@link String}>
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info("修改分类，category:{}", category);
        categoryService.updateById(category);
        return R.success("分类信息修改成功");
    }
}
