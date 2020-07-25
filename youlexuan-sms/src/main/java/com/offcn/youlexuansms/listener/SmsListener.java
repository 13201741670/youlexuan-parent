package com.offcn.youlexuansms.listener;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: youlexuan-parent
 * @description:
 * @author: 刘博
 * @create: 2020-07-10 15:16
 **/
@Component
public class SmsListener {
    @Value("${ali.accessKeyId}")
    private String accessKeyId;
    @Value("${ali.secret}")
    private String secret;
    @Value("${ali.regionId}")
    private String regionId;
    @Value("${ali.signName}")
    private String signName;
    @JmsListener(destination="sms")
    public void sms(Map<String,String> map) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, secret);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("PhoneNumbers", map.get("mobile"));
        request.putQueryParameter("TemplateCode", map.get("tempCode"));
        request.putQueryParameter("TemplateParam", map.get("param"));
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (
                ClientException e) {
            e.printStackTrace();
        }

}
}
