package com.xxx.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.xxx.proj.dto.Result;
import com.xxx.proj.mapper.TbUserMapper;
import com.xxx.proj.pojo.TbUser;
import com.xxx.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: youlexuan-parent
 * @description: 用户模块
 * @author: 刘博
 * @create: 2020-07-10 17:25
 **/
@Service
public class UserServiceImpl implements UserService {
    /**
     * @param phone
     * @param code [phone, code]
     * @return boolean
     * @author 刘博
     * @describe:校验短信
     * @date 2020/7/10 17:07
     */
    @Autowired
    private TbUserMapper tbUserMapper;
    @Override
    public boolean checkSmsCode(String phone, String code) {
        String smsCode =(String) redisTemplate.boundHashOps("smsCode").get(phone);
        if (smsCode == null){
            return false;
        }else if (!smsCode.equals(code)){
            return false;
        }else {
            return true;
        }
    }
    /**
     * @param phone
     * @return
     * @author 刘博
     * @describe:发送验证吗
     * @date 2020/7/10 17:08
     */
    @Autowired
    private RedisTemplate<String,Object> redisTemplate ;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Override
    public boolean createSmsCode(String phone) {
       final String code= (long)  (Math.random()*1000000)+"";
       redisTemplate.boundHashOps("smsCode").put(phone,code);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",code);
        Map<String,String> map = new HashMap<>();
        map.put("mobile",phone);
        map.put("tempCode","SMS_182669206");
        map.put("param",jsonObject.toString());
        jmsTemplate.convertAndSend("sms",map);
        return true;
    }
    /**
     * @param tbUser
     * @return
     * @author 刘博
     * @describe:
     * @date 2020/7/10 17:09
     */
    @Override
    public void add(TbUser tbUser) {
        tbUser.setCreated(new Date());
        tbUser.setUpdated(new Date());
        String newpasseord = DigestUtils.md5Hex(tbUser.getPassword());
        tbUser.setPassword(newpasseord);
      tbUserMapper.insert(tbUser);
    }
}
