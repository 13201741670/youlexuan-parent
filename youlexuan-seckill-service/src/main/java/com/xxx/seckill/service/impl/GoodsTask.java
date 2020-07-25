package com.xxx.seckill.service.impl;

import com.xxx.proj.mapper.TbSeckillGoodsMapper;
import com.xxx.proj.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: youlexuan-parent
 * @description:
 * @author: 刘博
 * @create: 2020-07-20 15:00
 **/
@Component
public class GoodsTask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbSeckillGoodsMapper tbSeckillGoodsMapper;
    @Scheduled(cron = "0/5 * * * * * ?")
    public void setRedisTemplate(){
        System.out.println("1111");
        List<TbSeckillGoods> list = redisTemplate.boundHashOps("seckillGoods").values();
        for (TbSeckillGoods goods :list){
            if (goods.getEndTime().getTime() < new Date().getTime()){
                tbSeckillGoodsMapper.updateByPrimaryKey(goods);
                redisTemplate.boundHashOps("seckillGoods").delete(goods.getId());
                System.out.println("移除过期产品"+goods.getTitle());
            }
        }
    }
}
