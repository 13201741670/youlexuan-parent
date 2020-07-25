package com.xxx.seckill.service;

import java.util.Map;

/**
 * @program: youlexuan-parent
 * @description: 支付模块
 * @author: 刘博
 * @create: 2020-07-16 14:09
 **/
public interface AliPayService {
    public Map createNative(String out_trade_no, Long total_fee);
    public Map queryPayStatus(String out_trade_no);
}
