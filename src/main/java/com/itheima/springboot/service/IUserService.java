package com.itheima.springboot.service;

import com.itheima.springboot.controller.dto.UserDto;
import com.itheima.springboot.entity.User;
import com.baomidou.mybatisplus.spring.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author YuanZG
 * @since 2026-10-01
 */
public interface IUserService extends IService<User> {
    UserDto login(UserDto userDto);

    User register(UserDto userDto);
}
