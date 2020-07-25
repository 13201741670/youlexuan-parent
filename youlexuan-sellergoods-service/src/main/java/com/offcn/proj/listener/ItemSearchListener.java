package com.offcn.proj.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xxx.proj.pojo.TbItem;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

/**
 * @program: youlexuan-parent
 * @description:
 * @author: 刘博
 * @create: 2020-07-09 17:32
 **/
public class ItemSearchListener implements MessageListener {
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
            for (TbItem tbItem :tbItemList){
                Map map= JSON.parseObject(tbItem.getSpec());
                tbItem.setSpecMap(map);
            }
            solrTemplate.saveBeans(tbItemList);
            solrTemplate.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
