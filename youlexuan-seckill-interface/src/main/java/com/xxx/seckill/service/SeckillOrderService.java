package com.xxx.seckill.service;

import com.xxx.proj.dto.PageResult;
import com.xxx.proj.pojo.TbSeckillOrder;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillOrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSeckillOrder> findAll();
	
	public TbSeckillOrder searchOrderFromRedisByUserId(String userId);
	public void saveOrderFromRedisToDb(String userId,Long orderId,String transactionId);
	public void deleteOrderFromRedis(String userId,Long orderId);
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);


	/**
	 * 增加
	*/
	public void add(TbSeckillOrder seckill_order);

	/**
	 * 立刻抢购
	 */
	public void submitOrder(Long seckillId,String userId);
	/**
	 * 修改
	 */
	public void update(TbSeckillOrder seckill_order);


	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSeckillOrder findOne(Long id);


	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param seckill_order
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSeckillOrder seckill_order, int pageNum, int pageSize);
	
}
