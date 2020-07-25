package com.xxx.page.service.impl;

import com.xxx.page.service.ItemPageService;
import com.xxx.proj.mapper.TbGoodsDescMapper;
import com.xxx.proj.mapper.TbGoodsMapper;
import com.xxx.proj.mapper.TbItemCatMapper;
import com.xxx.proj.mapper.TbItemMapper;
import com.xxx.proj.pojo.TbGoods;
import com.xxx.proj.pojo.TbGoodsDesc;
import com.xxx.proj.pojo.TbItem;
import com.xxx.proj.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;


import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: youlexuan-parent
 * @description: 商品
 * @author: 刘博
 * @create: 2020-07-07 21:10
 **/
@com.alibaba.dubbo.config.annotation.Service
@Service
public class ItemPageServiceImpl implements ItemPageService {
    @Value("${pagedir}")
    private String pagedir;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private TbGoodsMapper tbGoodsMapper;
    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;
    @Autowired
    private TbItemCatMapper tbItemCatMapper;
    @Autowired
    private TbItemMapper tbItemMapper;
    @Override
    public boolean getItemHtnl(Long goodsId) {

        try {
            Configuration configuration =freeMarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            Map map = new HashMap();
            TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);
            TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);
            String category1Id = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String category2Id = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String category3Id = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
            //读取SKU
            TbItemExample tbItemExample = new TbItemExample();
            TbItemExample.Criteria criteria = tbItemExample.createCriteria();
            criteria.andStatusEqualTo("1");
            criteria.andGoodsIdEqualTo(goodsId);
            tbItemExample.setOrderByClause("is_default desc");
            List<TbItem> tbItems = tbItemMapper.selectByExample(tbItemExample);
            map.put("tbItems",tbItems);

            map.put("category1Id",category1Id);
            map.put("category2Id",category2Id);
            map.put("category3Id",category3Id);
            map.put("tbGoods",tbGoods);
            map.put("tbGoodsDesc",tbGoodsDesc);
            Writer out = new FileWriter(pagedir+goodsId+".html");
            template.process(map,out);
            out.flush();
            out.close();
            return true;
        }catch (Exception e){
        e.printStackTrace();
        return false;
        }

    }
    @Override
    public boolean deleteHtml(Long goodsId) {
        File file = new File(pagedir + goodsId + ".html");
        return file.delete();
    }
}
