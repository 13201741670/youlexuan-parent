package com.xxx.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xxx.content.service.ContentService;
import com.xxx.proj.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: youlexuan-parent
 * @description: 广告模块
 * @author: 刘博
 * @create: 2020-07-02 11:02
 **/
@RestController
@RequestMapping("content")
public class ContentController {
    @Reference
    private ContentService contentService;
    @RequestMapping("/findByCategoryId")
    public List<TbContent> findByCategoryId(Long categoryId){
       return contentService.findByCategoryId(categoryId);
    }
}
