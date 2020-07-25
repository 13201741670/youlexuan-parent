package com.xxx.proj.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xxx.proj.dto.PageResult;
import com.xxx.proj.mapper.*;

import com.xxx.proj.pojo.*;
import com.xxx.proj.pojo.TbGoodsExample.Criteria;
import com.xxx.proj.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper tbGoodsDescMapper;
	@Autowired
	private TbBrandMapper tbBrandMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbSellerMapper tbSellerMapper;
	@Autowired
	private TbItemCatMapper tbItemCatMapper;
	@Autowired
	private SolrTemplate solrTemplate;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbGoods goods) {
		goods.getTbGoods().setAuditStatus("0");
		goodsMapper.insert(goods.getTbGoods());
		System.out.println(goods.getTbGoodsDesc());
		goods.getTbGoodsDesc().setGoodsId(goods.getTbGoods().getId());
		tbGoodsDescMapper.insert(goods.getTbGoodsDesc());
		saveItemList(goods);
		saveItemList(goods);

	}

		private  void setItemValus(TbGoods goods,TbItem tbItem) {
		tbItem.setGoodsId(goods.getTbGoods().getId());
		tbItem.setSellerId(goods.getTbGoods().getSellerId());
		tbItem.setCategoryid(goods.getTbGoods().getCategory3Id());
		tbItem.setCreateTime(new Date());
		tbItem.setUpdateTime(new Date());
		TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
		tbItem.setBrand(tbBrand.getName());
		TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
		tbItem.setCategory(tbItemCat.getName());
		TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
		tbItem.setSeller(tbSeller.getNickName());
		List<Map> maps = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(),Map.class);
		if (maps.size() >0){
			tbItem.setImage((String)maps.get(0).get("url"));
		}
	}
	
	/**
	 * 修改
	 */
	@Override
	public void update(TbGoods goods){
		goods.getTbGoods().setAuditStatus("0");
		goodsMapper.updateByPrimaryKey(goods.getTbGoods());
		tbGoodsDescMapper.updateByPrimaryKey(goods.getTbGoodsDesc());
		TbItemExample tbItemExample = new TbItemExample();
		TbItemExample.Criteria criteria = tbItemExample.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getTbGoods().getId());
		itemMapper.deleteByExample(tbItemExample);
		saveItemList(goods);
	}
	private void saveItemList(TbGoods goods){
		if("1".equals(goods.getTbGoods().getIsEnableSpec())) {
			for (TbItem tbItem : goods.getTbItems()) {
				String goodsName = goods.getTbGoods().getGoodsName();
				Map<String, Object> specMap = JSON.parseObject(tbItem.getSpec());
				for (String key : specMap.keySet()) {
					goodsName += "" + specMap.get(key);
				}
				tbItem.setTitle(goodsName);
				setItemValus(goods, tbItem);
				itemMapper.insert(tbItem);
			}
		}else {
			TbItem tbItem=new TbItem();
			tbItem.setTitle(goods.getTbGoods().getGoodsName());//商品KPU+规格描述串作为SKU名称
			tbItem.setPrice( goods.getTbGoods().getPrice());//价格
			tbItem.setStatus("1");//状态
			tbItem.setIsDefault("1");//是否默认
			tbItem.setNum(99999);//库存数量
			tbItem.setSpec("{}");
			setItemValus(goods, tbItem);
			itemMapper.insert(tbItem);
		}
	}
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbGoods findOne(Long id){
		TbGoods tbGoods = new TbGoods();
		TbGoods tbGoods1 = goodsMapper.selectByPrimaryKey(id);
		tbGoods.setTbGoods(tbGoods1);
		TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(id);
		tbGoods.setTbGoodsDesc(tbGoodsDesc);


		TbItemExample tbItemExample = new TbItemExample();
		TbItemExample.Criteria criteria = tbItemExample.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);
		tbGoods.setTbItems(tbItems);
		List<TbItem> tbItems1 = tbGoods.getTbItems();
		System.out.println(tbItems1);
		return tbGoods ;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			goodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		for(Long id:ids){
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
			TbItemExample tbItemExample = new TbItemExample();
			TbItemExample.Criteria criteria = tbItemExample.createCriteria();
			criteria.andGoodsIdEqualTo(id);
			List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);
			for (TbItem tbItem:tbItems){
				tbItem.setStatus(status);
				itemMapper.updateByPrimaryKey(tbItem);
				/*Map map= JSON.parseObject(tbItem.getSpec());
				tbItem.setSpecMap(map);*/
			}
			/*if ("1".equals(status)){
			*//*	System.out.println(tbItems);
				solrTemplate.saveBeans(tbItems);
				solrTemplate.commit();*//*
			}else {
				org.springframework.data.solr.core.query.Criteria criteria1 = new org.springframework.data.solr.core.query.Criteria("item_goodsid").is(id);
				Query query = new SimpleQuery("*:*");
				query.addCriteria(criteria1);
				solrTemplate.delete(query);
				solrTemplate.commit();
			}*/
		}
	}

}
