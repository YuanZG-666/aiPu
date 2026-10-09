package com.itheima.springboot.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author YuanZG
 * @Date 2026/10/3
 * @Description Result  接口统一返回包装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private String code;
    private String msg;
    private Object data;

    public static Result success(){
        return new Result(Constants.CODE_200, "", null);
    }

    public static Result success(Object data){
        return new Result(Constants.CODE_200, "", data);
    }

    public static Result error(String code,String message){
        return new Result(code, message, null);
    }

    public static Result error(){
        return new Result(Constants.CODE_500, "系统错误", null);
    }
}
