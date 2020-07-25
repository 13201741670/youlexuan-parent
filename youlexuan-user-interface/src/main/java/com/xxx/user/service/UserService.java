package com.xxx.user.service;

import com.xxx.proj.dto.Result;
import com.xxx.proj.pojo.TbUser;

/**
 * @program: youlexuan-parent
 * @description: 用户模块
 * @author: 刘博
 * @create: 2020-07-10 17:06
 **/
public interface UserService {
    /**
     * @param phone
	 * @param code [phone, code]
     * @return boolean
     * @author 刘博
     * @describe:校验短信
     * @date 2020/7/10 17:07
     */
    public boolean checkSmsCode(String phone,String code);
    /**
     * @param phone
     * @return
     * @author 刘博
     * @describe:发送验证吗
     * @date 2020/7/10 17:08
     */
    public boolean createSmsCode(String phone);
    /**
     * @param tbUser
     * @return
     * @author 刘博
     * @describe:
     * @date 2020/7/10 17:09
     */
    public void add(TbUser tbUser);
}
