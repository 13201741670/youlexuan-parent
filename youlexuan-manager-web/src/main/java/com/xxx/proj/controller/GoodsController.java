package com.xxx.proj.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.page.service.ItemPageService;
import com.xxx.proj.dto.PageResult;
import com.xxx.proj.dto.Result;
import com.xxx.proj.pojo.TbGoods;
import com.xxx.proj.pojo.TbItem;
import com.xxx.proj.service.GoodsService;
import com.xxx.proj.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.lang.reflect.Array;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference()
	private GoodsService goodsService;
	@Reference(timeout = 4000)
	private ItemPageService pageService;
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){
		return goodsService.findAll();
	}


	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return goodsService.findPage(page, rows);
	}

	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbGoods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbGoods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}

	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbGoods findOne(Long id){
		return goodsService.findOne(id);
	}

	/**
	 * 批量删除
	 * @param  ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}

		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);
	}
	@Autowired
	private JmsTemplate jmsTemplate;
	@Qualifier("destination")
	@Autowired
	private Destination destination;
	@Qualifier("activeMQQueueDel")
	@Autowired
	private Destination activeMQQueueDel;
	@Qualifier("createPageQueue")
	@Autowired
	private Destination createPageQueue;
	@Qualifier("deletePageQueue")
	@Autowired
	private Destination deletePageQueue;
	@Reference
	private ItemService itemService;
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long [] ids,String status){
		try {
			goodsService.updateStatus(ids,status);
			List<TbItem> tbItemList = itemService.findItemListByGoodsIdAndStatus(ids,status);
			String tbItemStr = JSON.toJSONString(tbItemList);
			System.out.println(tbItemStr);
			if ("1".equals(status)){
				jmsTemplate.send(destination, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createTextMessage(tbItemStr);
					}
				});
				jmsTemplate.send(createPageQueue, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createObjectMessage(ids);
					}
				});
			}else {
				jmsTemplate.send(activeMQQueueDel, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createTextMessage(tbItemStr);
					}
				});
				jmsTemplate.send(deletePageQueue, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createObjectMessage(ids);
					}
				});
			}
			return new Result(true, "更新成功");

		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "更新失败");
		}

	}
	@RequestMapping("/genHtml")
	public void genHtml(Long goodsId){
		pageService.getItemHtnl(goodsId);
	}
}
