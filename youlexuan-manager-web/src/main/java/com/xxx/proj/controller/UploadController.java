package com.xxx.proj.controller;

import com.xxx.proj.dto.Result;
import com.xxx.util.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Properties;

/**
 * @program: youlexuan-parent
 * @description: 图片上传
 * @author: 刘博
 * @create: 2020-06-29 19:00
 **/
@RestController
public class UploadController {
    @Value("${fastdfs.FILE_SERVER_URL}")
    private String FILE_SERVER_URL;
    @Value("${fastdfs.tracker_server}")
    private String tracker_server;
    @RequestMapping("/upload")
    public Result upload(MultipartFile file){
        Result result = new Result();
        String originalFilename = file.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        Properties properties = new Properties();
        properties.put("fastdfs.tracker_servers",tracker_server);
        try {
            FastDFSClient fastDFSClient = new FastDFSClient(properties);
            String s = fastDFSClient.uploadFile(file.getBytes(), substring);
            String url = FILE_SERVER_URL+s;
            result.setSuccess(true);
            result.setMessage(url);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("上传失败");
            return result;
        }
    }
}
