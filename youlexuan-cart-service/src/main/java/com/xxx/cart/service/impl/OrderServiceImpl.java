package com.xxx.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xxx.cart.service.OrderService;
import com.xxx.proj.dto.PageResult;
import com.xxx.proj.mapper.TbOrderItemMapper;
import com.xxx.proj.mapper.TbOrderMapper;
import com.xxx.proj.mapper.TbPayLogMapper;
import com.xxx.proj.pojo.*;
import com.xxx.proj.pojo.TbOrderExample.Criteria;
import com.xxx.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private IdWorker idWorker;
	@Autowired
	private TbOrderItemMapper tbOrderItemMapper;
	@Autowired
	private TbPayLogMapper tbPayLogMapper;
	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {
		List<Cart> carts = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
		List<String> orderList = new ArrayList<>();
		double total_fee= 0;
		for (Cart cart :carts){
			try {
				TbOrder clone =new TbOrder();
				long orworder = idWorker.nextId();
				clone.setOrderId(orworder);
				clone.setStatus("1");
				clone.setCreateTime(new Date());
				clone.setUpdateTime(new Date());
				clone.setSellerId(cart.getSellerId());
				clone.setUserId(order.getUserId());
				clone.setPaymentType(order.getPaymentType());
				clone.setReceiverMobile(order.getReceiverMobile());
				clone.setReceiverAreaName(order.getReceiverAreaName());
				clone.setReceiver(order.getReceiver());
				clone.setSourceType(order.getSourceType());
				double money = 0;
				for (TbOrderItem tbOrderItem :cart.getTbOrderItems() ){
					tbOrderItem.setId(idWorker.nextId());
					tbOrderItem.setOrderId(orworder);
					tbOrderItem.setSellerId(cart.getSellerId());
					money+=tbOrderItem.getTotalFee().doubleValue();
					tbOrderItemMapper.insert(tbOrderItem);
					orderList.add(idWorker.nextId()+"");
					total_fee+=money;
				}
				clone.setPayment(new BigDecimal(money));
				orderMapper.insert(clone);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ("1".equals(order.getPaymentType())){
			TbPayLog tbPayLog = new TbPayLog();
			tbPayLog.setOutTradeNo(idWorker.nextId()+"");
			tbPayLog.setCreateTime(new Date());
			tbPayLog.setTotalFee((long)total_fee);
			String str =orderList.toString().replace("[","").replace("]","").replace(","," ");
			tbPayLog.setOrderList(str);
			tbPayLog.setTradeState("0");
			tbPayLog.setUserId(order.getUserId());
			tbPayLog.setPayType("1");
			tbPayLogMapper.insert(tbPayLog);
			redisTemplate.boundHashOps("payLog").put(order.getUserId(),tbPayLog);
		}

		redisTemplate.boundHashOps("cartList").delete(order.getUserId());

	}
	@Override
	public TbPayLog searchPayLogFromRedis(String userId) {
		TbPayLog payLog =(TbPayLog)redisTemplate.boundHashOps("payLog").get(userId);
		return payLog;
	}

	@Override
	public void updateOrderStatus(String out_trade_no, String transaction_id) {
		TbPayLog tbPayLog =tbPayLogMapper.selectByPrimaryKey(out_trade_no);
		tbPayLog.setPayTime(new Date());
		tbPayLog.setTradeState("1");
		tbPayLog.setTransactionId(transaction_id);
		tbPayLogMapper.updateByPrimaryKey(tbPayLog);
		String orderList = tbPayLog.getOrderList();
		String [] order = orderList.split(",");
		for (String s : order) {
			TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.parseLong(s));
			if (tbOrder != null){
				tbOrder.setStatus("2");
				orderMapper.updateByPrimaryKey(tbOrder);
			}
		}
		redisTemplate.boundHashOps("payLog").delete(tbPayLog.getUserId());
	}


	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param orderId
	 * @return
	 */
	@Override
	public TbOrder findOne(Long orderId){
		return orderMapper.selectByPrimaryKey(orderId);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] orderIds) {
		for(Long orderId:orderIds){
			orderMapper.deleteByPrimaryKey(orderId);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
