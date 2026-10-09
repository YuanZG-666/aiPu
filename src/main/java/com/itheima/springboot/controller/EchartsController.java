package com.itheima.springboot.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Quarter;
import com.itheima.springboot.common.Result;
import com.itheima.springboot.entity.User;
import com.itheima.springboot.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author YuanZG
 * @Date 2026/10/5
 * @Description EchartsController
 */
@RestController
@RequestMapping("/echarts")
public class EchartsController {

    @Autowired
    private IUserService userService;
    @GetMapping("/example")
    public Result get(){
        Map<String,Object> map =new HashMap<>();
        map.put("x", CollUtil.newArrayList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
        map.put("y", CollUtil.newArrayList(120, 200, 150, 80, 70, 110, 130));
        return Result.success(map);
    }

    @GetMapping("/members")
    public Result getMembers(){
        List<User> list = userService.list();
        int q1=0;
        int q2=0;
        int q3=0;
        int q4=0;
        for (User user : list){
            Date createTime = user.getCreateTime();
            // Hutool DateUtil：根据createTime这个日期，得到它所属季度枚举
            Quarter quarter = DateUtil.quarterEnum(createTime);
            switch (quarter) {
                case Q1:
                    q1++;
                    break;
                case Q2:
                    q2++;
                    break;
                case Q3:
                    q3++;
                    break;
                case Q4:
                    q4++;
                    break;
                default:
                    break;
            }

        }
        return Result.success(CollUtil.newArrayList(q1, q2, q3, q4));
    }
}