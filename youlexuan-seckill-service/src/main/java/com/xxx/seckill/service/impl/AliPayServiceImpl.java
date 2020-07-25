package com.xxx.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.xxx.seckill.service.AliPayService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: youlexuan-parent
 * @description:
 * @author: 刘博
 * @create: 2020-07-16 14:11
 **/
@Service
public class AliPayServiceImpl implements AliPayService {
    @Autowired
    private AlipayClient alipayClient;
    @Override
    public Map createNative(String out_trade_no, Long total_fee) {
        AlipayTradePrecreateRequest payRequest = new AlipayTradePrecreateRequest();
        Map map = new HashMap();
        map.put("out_trade_no",out_trade_no);
        map.put("total_amount",(total_fee));
        map.put("subject","有了选");
        map.put("store_id", "xa_001");
        map.put("timeout_express", "90m");
        payRequest.setBizContent(JSON.toJSONString(map));
        try {
            AlipayTradePrecreateResponse alipayTradePrecreateResponse = alipayClient.execute(payRequest);
            String code = alipayTradePrecreateResponse.getCode();
            if (code.equals("10000")) {
                map.put("qrcode", alipayTradePrecreateResponse.getQrCode());
            }
               else throw new RuntimeException("下单失败");

        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        AlipayTradeQueryRequest payRequest = new AlipayTradeQueryRequest();
        Map map = new HashMap();
        map.put("out_trade_no",out_trade_no);
        map.put("trade_no","");
        payRequest.setBizContent(JSON.toJSONString(map));
        try {
            AlipayTradeQueryResponse alipayTradePrecreateResponse = alipayClient.execute(payRequest);
            String code = alipayTradePrecreateResponse.getCode();
            if ("10000".equals(code)){
                map.put("trade_no",alipayTradePrecreateResponse.getTradeNo());
                map.put("trade_status",alipayTradePrecreateResponse.getTradeStatus());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }

}
