package com.itheima.springboot.utils;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;


import java.nio.file.Paths;
import java.util.Collections;

/**
 * @Author YuanZG
 * @Date 2026/9/30
 * @Description codeGenerator
 */
public class codeGenerator {
    public static void main(String[] args) {
        generate();
    }
    private static void generate(){
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/zhiguo?serverTimezone=GMT%2b8", "root", "123456")
                .globalConfig(builder -> {
                        builder.author("YuanZG")
                                .enableSwagger()//开启swagger模式，运行生成代码时，自动给 Entity 实体类加上 Swagger 的 API 文档注解
                        .outputDir(Paths.get(System.getProperty("user.dir")) + "/src/main/java")//代码生成器生成出来的 java 文件（entity、mapper、service、controller），全部输出到这个目录下，就是 SpringBoot 默认的 java 源码文件夹。
                        .commentDate("yyyy-MM-dd");
                })
                .packageConfig(builder -> builder
                        .parent("com.itheima.springboot")//设置父包名
                        .moduleName(null)//设置父包模块名
                        //.pathInfo(Collections.singletonMap(OutputFile.mapper, Paths.get(System.getProperty("user.dir")) + "/src/main/java"))
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .xml("mapper.xml")
                )
                .strategyConfig(builder -> builder
                        .addInclude("sys_menu")//设置需要生成的表名
                        .addTablePrefix("t_","sys_")//设置过滤表前缀
                        .entityBuilder()
                            .enableLombok()
                            .enableFileOverride()
//                           .enableTableName() // 自动生成 @TableName
                            .idType(IdType.AUTO)
                            .enableTableFieldAnnotation() // 开启字段注解，数据库字段和实体属性不一致自动生成@TableField
                        .mapperBuilder()
                            .enableFileOverride()  // mapper接口覆盖
                        .serviceBuilder()
                            .enableFileOverride()  // service覆盖
                        .controllerBuilder()
                            .enableFileOverride().enableHyphenStyle()//开启驼峰转连字符
                                .enableRestStyle()//开启RestController控制器
                )
//                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
