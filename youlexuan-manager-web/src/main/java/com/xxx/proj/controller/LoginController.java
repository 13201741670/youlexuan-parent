package com.xxx.proj.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.Map;

/**
 * @program: youlexuan-parent
 * @description: 登录校验
 * @author: 刘博
 * @create: 2020-06-27 14:17
 **/
@RestController
@RequestMapping("/login")
public class LoginController {
    @RequestMapping("/name")
    public Map name(){
    String mame = SecurityContextHolder.getContext().getAuthentication().getName();
    Map map = new HashMap();
    map.put("loginName",mame);
    return map;
    }
}
