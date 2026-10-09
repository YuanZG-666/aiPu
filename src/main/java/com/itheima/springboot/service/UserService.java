package com.itheima.springboot.service;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.itheima.springboot.controller.dto.UserDto;
import com.itheima.springboot.entity.User;
import com.itheima.springboot.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * @Author YuanZG
 * @Date 2026/9/29
 * @Description UserService
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User>{
    public boolean saveUser(User user) {
/*        if (user.getId()==null){
            return save(user);
        }else{
           return updateById(user);
        }*/

        return saveOrUpdate(user);
    }



/*    @Autowired
    private UserMapper userMapper;
    public int save(User user){
        if (user.getId()==null){
            System.out.println("插入");
           return userMapper.insert(user);
        }else {
            System.out.println("更新");
           return userMapper.update(user);
        }
    }*/
}
