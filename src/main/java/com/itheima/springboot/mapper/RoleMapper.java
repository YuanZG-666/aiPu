package com.itheima.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.springboot.entity.Role;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author YuanZG
 * @since 2026-10-05
 */
public interface RoleMapper extends BaseMapper<Role> {

    @Select("SELECT id FROM sys_role WHERE flag = #{flag}")
    Integer selectByFlag(@Param("flag") String flag);
}
