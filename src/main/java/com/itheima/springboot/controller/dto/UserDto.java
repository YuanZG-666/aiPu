package com.itheima.springboot.controller.dto;

import com.itheima.springboot.entity.Menu;
import lombok.Data;

import java.util.List;

/**
 * @Author YuanZG
 * @Date 2026/10/2
 * @Description UserDto
 */
@Data
public class UserDto {
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String token;
    private List<Menu> menus;
    private String role;

}
