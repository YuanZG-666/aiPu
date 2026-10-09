package com.itheima.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.springboot.common.Constants;
import com.itheima.springboot.controller.dto.UserDto;
import com.itheima.springboot.entity.Menu;
import com.itheima.springboot.entity.RoleMenu;
import com.itheima.springboot.entity.User;
import com.itheima.springboot.exception.ServiceException;
import com.itheima.springboot.mapper.RoleMapper;
import com.itheima.springboot.mapper.RoleMenuMapper;
import com.itheima.springboot.mapper.UserMapper;
import com.itheima.springboot.service.IMenuService;
import com.itheima.springboot.service.IUserService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.itheima.springboot.utils.TokenUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author YuanZG
 * @since 2026-10-01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Log log = Log.get();
    @Autowired
    private RoleMapper roleMapper;
    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService menuService;

    @Override
    public UserDto login(UserDto userDto) {
        User one = getUserInfo(userDto);
        if (one != null) {
            BeanUtil.copyProperties(one, userDto, true);//从数据库查询用户信息
            //设置token
            String token = TokenUtils.genToken(one.getId().toString(), one.getPassword());
            userDto.setToken(token);

            String role = one.getRole();
            List<Menu> roleMenus = getRoleMenus(role);
            //设置用户菜单列表
            userDto.setMenus(roleMenus);
            return userDto;
        } else {
            throw new ServiceException(Constants.CODE_600, "用户名或密码错误");
        }

//        List<User> list = list(queryWrapper);
//        return list.size() > 0;
    }

    @Override
    public User register(UserDto userDto) {
        User one = getUserInfo(userDto);
        if (one == null) {
            one = new User();
            BeanUtil.copyProperties(userDto, one, true);
            save(one);
        } else {
            throw new ServiceException(Constants.CODE_600, "用户名已存在");
        }
        return one;
    }

    private User getUserInfo(UserDto userDto) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userDto.getUsername());
        queryWrapper.eq("password", userDto.getPassword());
        User one;
        try {
            one = getOne(queryWrapper);
        } catch (Exception e) {
            log.error("登录失败", e);
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        return one;
    }

    /*
     * 获取当前用户的菜单列表
     * */
    private List<Menu> getRoleMenus(String roleFlag) {
        Integer roleId = roleMapper.selectByFlag(roleFlag);
        //当前角色的所有菜单id集合
        List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);
        //查出系统所以的菜单
        List<Menu> menus = menuService.findMenus("");
        //new一个最后刷选完成的list
        List<Menu> roleMenus = new ArrayList<>();
        //筛选当前用户菜单
        for (Menu menu : menus) {
            if (menuIds.contains(menu.getId())) {
                roleMenus.add(menu);
            }
            List<Menu> children = menu.getChildren();
            //移除children里面不在menuIds里面的元素
            children.removeIf(child -> !menuIds.contains(child.getId()));
        }

        return roleMenus;
    }
}
