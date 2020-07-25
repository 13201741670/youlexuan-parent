package com.offcn.proj.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xxx.proj.pojo.TbItem;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

/**
 * @program: youlexuan-parent
 * @description:
 * @author: 刘博
 * @create: 2020-07-09 18:52
 **/
public class ItemDeleteListener implements MessageListener {
    private SolrTemplate solrTemplate;

    public SolrTemplate getSolrTemplate() {
        return solrTemplate;
    }

    public void setSolrTemplate(SolrTemplate solrTemplate) {
        this.solrTemplate = solrTemplate;
    }

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            System.out.println(textMessage.getText());
            List<TbItem> tbItemList = JSONArray.parseArray(textMessage.getText(), TbItem.class);
            for (TbItem tbItem:tbItemList){
                org.springframework.data.solr.core.query.Criteria criteria1 = new org.springframework.data.solr.core.query.Criteria("item_goodsid").is(tbItem.getGoodsId());
                Query query = new SimpleQuery("*:*");
                query.addCriteria(criteria1);
                solrTemplate.delete(query);
                solrTemplate.commit();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        };
    }
}
