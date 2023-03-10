package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

/**
 * icategory服务
 * <p>
 * 菜品及套餐分类 服务类
 * </p>
 *
 * @author ilovend
 * @date 2023/02/18
 * @since 2023-02-06
 */
public interface ICategoryService extends IService<Category> {
//    根据ID删除分类
    void removeById(Long id);

}
