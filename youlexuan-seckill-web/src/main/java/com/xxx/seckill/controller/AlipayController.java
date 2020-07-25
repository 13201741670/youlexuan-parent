package com.xxx.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xxx.proj.pojo.TbSeckillOrder;
import com.xxx.seckill.service.AliPayService;
import com.xxx.proj.dto.Result;
import com.xxx.seckill.service.SeckillOrderService;
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
   private SeckillOrderService seckillOrderService;

    @RequestMapping("/createNative")
    public Map createNative() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder tbSeckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
        if (tbSeckillOrder != null) {
            Long fen = (long) (tbSeckillOrder.getMoney().doubleValue()*100);
            return aliPayService.createNative(tbSeckillOrder.getId()+"",fen);
        }
        return new HashMap();
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        int x = 0;

        while (x++ < 100) {
            Map map = aliPayService.queryPayStatus(out_trade_no);
            String trade_no =  map.get("trade_no")+"";
            if ("TRADE_SUCCESS".equals(map.get("trade_status"))) {
                seckillOrderService.saveOrderFromRedisToDb(userId,Long.valueOf(out_trade_no),trade_no);
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
            seckillOrderService.deleteOrderFromRedis(userId,Long.valueOf(out_trade_no));
        return new Result(false, "二维码超时");
    }
}
