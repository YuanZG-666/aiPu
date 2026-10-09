package com.itheima.springboot.service;

import com.itheima.springboot.entity.Role;
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
public interface IRoleService extends IService<Role> {

    //先删除当前角色id所以的绑定关系
    void setRoleMenu(Integer roleId, List<Integer> menuIds);

    List<Integer> getRoleMenu(Integer roleId);
    //再把前端传过来的菜单id数组绑定到当前角色id
}
