package com.itheima.springboot.service;

import com.itheima.springboot.entity.Menu;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author YuanZG
 * @since 2026-10-05
 */
public interface IMenuService extends IService<Menu> {


    List<Menu> findMenus(String name);
}
