package com.xxx.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xxx.cart.service.AliPayService;
import com.xxx.cart.service.OrderService;
import com.xxx.proj.dto.Result;
import com.xxx.proj.pojo.TbPayLog;
import com.xxx.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: youlexuan-parent
 * @description:
 * @author: 刘博
 * @create: 2020-07-16 14:36
 **/
@RestController
@RequestMapping("/pay")
public class AlipayController {
    @Reference(actives = 6000)
    private AliPayService aliPayService;
    @Reference
    private OrderService orderService;

    @RequestMapping("/createNative")
    public Map createNative() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog tbPayLog = orderService.searchPayLogFromRedis(name);
        if (tbPayLog != null) {
            return aliPayService.createNative(tbPayLog.getOutTradeNo(), tbPayLog.getTotalFee());
        }
        return new HashMap();
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        int x = 0;

        while (x++ < 100) {
            Map map = aliPayService.queryPayStatus(out_trade_no);
            String trade_no =  map.get("trade_no")+"";
            if ("TRADE_SUCCESS".equals(map.get("trade_status"))) {
                orderService.updateOrderStatus(out_trade_no, trade_no);
                return new Result(true, "支付成功");
            } else if ("TRADE_CLOSED".equals(map.get("trade_status"))) return new Result(false, "未付款交易超时关闭，或支付完成后全额退款");
            else if ("TRADE_FINISHED".equals(map.get("trade_status"))) return new Result(true, "交易结束，不可退款");
            else System.out.println("未付款");
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Result(false, "二维码超时");
    }
}
