package com.xxx.solrutil;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.promeg.pinyinhelper.Pinyin;
import com.xxx.proj.mapper.TbItemMapper;

import com.xxx.proj.pojo.TbItem;
import com.xxx.proj.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: youlexuan-parent
 * @description: solr框架
 * @author: 刘博
 * @create: 2020-07-03 09:46
 **/
@Component
public class SolrUtil {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private TbItemMapper itemMapper;
    public void importItemData(){
        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);
        System.out.println("=====商品列表=====");
        for (TbItem tbItem :tbItems){
            Map map= JSON.parseObject(tbItem.getSpec());
            tbItem.setSpecMap(map);
            System.out.println(tbItem);
        }
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
        System.out.println("结束");
    }
    public void importItemDele(){
        Query query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    public static void main(String[] args) {

            ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:applicationContext-*.xml");
           SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
          //solrUtil.importItemData();
        solrUtil.importItemDele();

    }


}
