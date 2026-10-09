package com.itheima.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.springboot.entity.Role;
import com.itheima.springboot.entity.RoleMenu;
import com.itheima.springboot.mapper.RoleMapper;
import com.itheima.springboot.mapper.RoleMenuMapper;
import com.itheima.springboot.service.IRoleService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author YuanZG
 * @since 2026-10-05
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Resource
    private RoleMenuMapper roleMenuMapper;
    @Transactional
    @Override
    public void setRoleMenu(Integer roleId, List<Integer> menuIds) {
/*        QueryWrapper<Role> queryWrapper = new QueryWrapper<Role>();
        queryWrapper.eq("role_id", roleId);
        roleMapper.delete(queryWrapper);*/

        //方法二
        //先删除当前角色id所以的绑定关系
        roleMenuMapper.deleteByRoleId(roleId);
        //再把前端传过来的菜单id数组绑定到当前角色id
        for (Integer menuId : menuIds){
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(roleId);
            roleMenuMapper.insert(roleMenu);
        }
    }

    @Override
    public List<Integer> getRoleMenu(Integer roleId) {
        return roleMenuMapper.selectByRoleId(roleId);
    }
}
