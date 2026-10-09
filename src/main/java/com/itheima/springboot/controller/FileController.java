package com.itheima.springboot.controller;


import cn.hutool.core.io.FileUtil;


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.springboot.common.Result;
import com.itheima.springboot.entity.Files;
import com.itheima.springboot.entity.User;
import com.itheima.springboot.mapper.FileMapper;
import com.itheima.springboot.utils.TokenUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @Author YuanZG
 * @Date 2026/10/4
 * @Description FileController
 */
@RestController
@RequestMapping("/file")
public class FileController {
    @Value("${files.upload.path}")
    private String fileUploadPath;
    @Resource
    private FileMapper fileMapper;

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();
        //文件唯一标识码
        String uuid = IdUtil.fastSimpleUUID();
        String fileUUid = uuid + StrUtil.DOT + type;
        File uploadFile = new File(fileUploadPath, fileUUid);
        //存储到磁盘
        //File uploadParentFile = new File(fileUploadPath);
        if (!uploadFile.getParentFile().exists()) {
            uploadFile.getParentFile().mkdirs();
        }

//        String url = "http://localhost:9090/file/" + fileUUid;
        String url;
        String md5;
        //把获取到的文件存储到磁盘目录
        file.transferTo(uploadFile);

        md5 = SecureUtil.md5(uploadFile);
        Files dbFiles = getFileByMd5(md5);
        if (dbFiles!=null){
            url = dbFiles.getUrl();
            //由于文件存在，删除刚才的重复文件
            uploadFile.delete();
        }else {
            //数据库若不存在，不删除刚才上传的文件
            url = "http://localhost:9090/file/download/" + fileUUid;
        }
                /*if (uploadFile.exists()){
            //获取文件的md5
            md5 = SecureUtil.md5(uploadFile);
            Files dbFiles = getFileByMd5(md5);
            //获取文件url
            if (dbFiles!=null){
                url = dbFiles.getUrl();
            }else {
                //把获取到的文件存储到磁盘目录
                file.transferTo(uploadFile);
                url = "http://localhost:9090/file/" + fileUUid;
            }
        }else {
            md5 = SecureUtil.md5(uploadFile);
            file.transferTo(uploadFile);
            Files dbFiles = getFileByMd5(md5);
            if (dbFiles!=null){
                url = dbFiles.getUrl();
            }else {
                //把获取到的文件存储到磁盘目录
                file.transferTo(uploadFile);
                url = "http://localhost:9090/file/" + fileUUid;
            }
        }*/


/*
        //查询文件的md5是否存在
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);
        Files files = fileMapper.selectOne(queryWrapper);*/

        //存储数据库
        Files saveFile = new Files();
        saveFile.setName(originalFilename);
        saveFile.setSize(size / 1024);
        saveFile.setType(type);
        saveFile.setUrl(url);
        saveFile.setMd5(md5);
        fileMapper.insert(saveFile);


        return url;
    }



    /*
     * 文件下载接口 "http://localhost:9090/file/{fileUUid}";
     */
    @GetMapping("/download/{fileUUid}")
    public void download(@PathVariable String fileUUid, HttpServletResponse response) throws IOException {
        //根据文件的唯一标识码获取文件
        File uploadFile = new File(fileUploadPath, fileUUid);
        //设置输出流的格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUid, "UTF-8"));
        response.setContentType("application/octet-stream");

        //读取文件的字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }

    /**
     * 通过文件的md5获取文件
     */
    private Files getFileByMd5(String md5){
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);
        List<Files> filesList = fileMapper.selectList(queryWrapper);
        return filesList.size()==0?null:filesList.get(0);
    }

    //分页查询接口
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", false);
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)){
            queryWrapper.like("name", name);
        }
        return Result.success(fileMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper));
    }

    //删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        Files files = fileMapper.selectById(id);
        files.set_delete(true);
        fileMapper.updateById(files);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<Files> files = fileMapper.selectList(queryWrapper);
        for (Files file : files){
            file.set_delete(true);
            fileMapper.updateById(file);
        }

        return Result.success();
    }



    //新增或者更新
    @PostMapping("/update")
    public Result update(@RequestBody Files files) {
        return Result.success(fileMapper.updateById(files));
    }
}
