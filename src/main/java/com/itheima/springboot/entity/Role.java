package com.itheima.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author YuanZG
 * @since 2026-10-05
 * @Description Role实体类
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "sys_role")
@ToString
@Getter
@Setter
public class Role implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private String description;
    private String flag;
}
