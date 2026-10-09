package com.itheima.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Author YuanZG
 * @Date 2026/9/29
 * @Description User
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "sys_user")
@ToString
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private String address;
    @TableField(value = "create_time")
    private Date createTime;
    @TableField(value = "abatar_url")
    private String avatar;
    private String role;
}
