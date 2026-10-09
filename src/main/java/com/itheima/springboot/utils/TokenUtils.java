package com.itheima.springboot.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.server.HttpServerRequest;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.itheima.springboot.entity.User;
import com.itheima.springboot.service.IUserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.util.Date;

/**
 * @Author YuanZG
 * @Date 2026/10/3
 * @Description TokenUtils
 */

@RestController
@Controller("/")
@Component
public class TokenUtils {

    private static IUserService staticUserService;
    @Autowired
    private IUserService userService;

    @PostConstruct
    public void setUserService() {
        staticUserService = this.userService;
    }

    public static String genToken(String userID, String sign) {

        //获取jwt生成器
        JWTCreator.Builder jwtBuilder = JWT.create();
        String token = jwtBuilder.withAudience(userID)
                //token失效时间，这里为一天后失效
                //.withExpiresAt(DateUnit.offsetHour(new Date(), 1, DateUnit.DAY))
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2))
                //设置该jwt的发行时间，一般为当前系统时间
                .withIssuedAt(new Date(System.currentTimeMillis()))
                //token的发行者（可自定义）
                .withIssuer("YuanZG")
                //进行签名，选择加密算法，以一个字符串密钥为参数
                .sign(Algorithm.HMAC256(sign));
        //token生成完毕，可以发送给客户端了，前端可以使用
        //localStorage.setItem("your_token", token)进行存储，在
        //下次请求时携带发送给服务器端进行验证
        System.out.println(token);
        return token;
    }

    public static User getCurrentUser() {
        try {
            HttpServletRequest requestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = requestAttributes.getHeader("token");
            if (StrUtil.isNotBlank(token)) {
                String userID = JWT.decode(token).getAudience().get(0);
                return staticUserService.getById(Integer.valueOf(userID));
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }
}

