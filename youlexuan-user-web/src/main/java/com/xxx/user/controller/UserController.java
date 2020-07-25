package com.xxx.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xxx.proj.dto.Result;
import com.xxx.proj.pojo.TbUser;
import com.xxx.user.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户表controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;
	@RequestMapping("/sendCode")
	public Result sendCode(String phone ){
		try {
			userService.createSmsCode(phone);
			return new Result(true,"发送成功");
		}catch (Exception e){
			return new Result(false,"发送失败");
		}
	}
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser tbUser,String phone){
		boolean smsCode = userService.checkSmsCode(tbUser.getPhone(),phone);
		if (!smsCode){
			return new Result(false,"验证码发送失败");
		}
		try {
			userService.add(tbUser);
			return new Result(true,"添加成功");
		}catch (Exception e){
			return new Result(false,"添加失败");
		}

	}
}
