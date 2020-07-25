package com.xxx.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xxx.proj.dto.PageResult;
import com.xxx.proj.mapper.TbSeckillGoodsMapper;
import com.xxx.proj.mapper.TbSeckillOrderMapper;
import com.xxx.proj.pojo.TbSeckillGoods;
import com.xxx.proj.pojo.TbSeckillOrder;
import com.xxx.proj.pojo.TbSeckillOrderExample;
import com.xxx.proj.pojo.TbSeckillOrderExample.Criteria;
import com.xxx.seckill.service.SeckillOrderService;
import com.xxx.util.IdWorker;
import com.xxx.util.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	@Autowired
	private TbSeckillGoodsMapper tbSeckillGoodsMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.selectByExample(null);
	}

	@Override
	public TbSeckillOrder searchOrderFromRedisByUserId(String userId) {
		TbSeckillOrder tbSeckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("tbSeckillOrder").get(userId);
		return tbSeckillOrder;
	}

	@Override
	public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
		TbSeckillOrder tbSeckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("tbSeckillOrder").get(userId);
		if (tbSeckillOrder == null){
			throw new RuntimeException("订单不存在");
		}
		if (tbSeckillOrder.getId().longValue() != orderId.longValue()){
			throw  new RuntimeException("订单不符合");
		}
		tbSeckillOrder.setStatus("1");
		tbSeckillOrder.setPayTime(new Date());
		tbSeckillOrder.setTransactionId(transactionId);
		seckillOrderMapper.insert(tbSeckillOrder);
		redisTemplate.boundHashOps("tbSeckillOrder").delete(userId);
	}

	@Override
	public void deleteOrderFromRedis(String userId, Long orderId) {
		TbSeckillOrder tbSeckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("tbSeckillOrder").get(userId);
		if (tbSeckillOrder != null || tbSeckillOrder.getId().longValue() == orderId.longValue()){
			redisTemplate.boundHashOps("tbSeckillOrder").delete(userId);
		}
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(tbSeckillOrder.getSeckillId());
		if (seckillGoods != null){
			seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
			redisTemplate.boundHashOps("seckillGoods").put(tbSeckillOrder.getSeckillId(),seckillGoods);
		}
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insert(seckillOrder);		
	}
	@Autowired
	private IdWorker idWorker;
	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private RedisLock redisLock;
	@Override
	public void submitOrder(Long seckillId, String userId) {
		String lockey = "createSecKillOrder";
		long le = 1000;
		String locvalue = String.valueOf(System.currentTimeMillis() + le);
		boolean lock = redisLock.lock(lockey,locvalue);
		if (lock) {
			TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
			if (seckillGoods == null) {
				throw new RuntimeException("商品不存在");
			}
			if (seckillGoods.getStockCount() <= 0) {
				throw new RuntimeException("商品被秒杀完了");
			}
			seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
			redisTemplate.boundHashOps("seckillGoods").put(seckillId, seckillGoods);
			if (seckillGoods.getStockCount() == 0) {
				tbSeckillGoodsMapper.updateByPrimaryKey(seckillGoods);
				redisTemplate.boundHashOps("seckillGoods").delete(seckillId);
			}
			TbSeckillOrder tbSeckillOrder = new TbSeckillOrder();
			tbSeckillOrder.setId(idWorker.nextId());
			tbSeckillOrder.setSeckillId(seckillId);
			tbSeckillOrder.setMoney(seckillGoods.getCostPrice());
			tbSeckillOrder.setUserId(userId);
			tbSeckillOrder.setSellerId(seckillGoods.getSellerId());
			tbSeckillOrder.setCreateTime(new Date());
			tbSeckillOrder.setStatus("0");
			redisTemplate.boundHashOps("tbSeckillOrder").put(userId, tbSeckillOrder);
		}
		redisLock.unlock(lockey,locvalue);
	}


	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		seckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillOrderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}			if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}			if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}			if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}			if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}			if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}			if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}	
		}
		
		Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)seckillOrderMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
