package com.offcn.youlexuansms;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import java.util.HashMap;
import java.util.Map;


@SpringBootTest
class YoulexuanSmsApplicationTests {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Test
    void test1() throws JSONException {
      /*  JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","1234");
        Map<String,String> map = new HashMap<>();
        map.put("mobile","13201741670");
        map.put("tempCode","SMS_182669206");
        map.put("param",jsonObject.toString());
        jmsTemplate.convertAndSend("sms",map);*/
    }

}
