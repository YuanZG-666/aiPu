package com.itheima.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author YuanZG
 * @Description Menu实体类
 * @since 2026-10-05
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "sys_menu")
@ToString
@Getter
@Setter
public class Menu {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private String path;
    private String icon;
    private String description;

    @TableField(exist = false)
    private List<Menu> children;

    private Integer pid;

    @TableField(value = "page_path")
    private String pagePath;
}
