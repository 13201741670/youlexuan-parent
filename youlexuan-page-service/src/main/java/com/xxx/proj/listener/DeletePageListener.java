package com.xxx.proj.listener;

import com.alibaba.fastjson.JSONArray;
import com.xxx.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.List;

/**
 * @program: youlexuan-parent
 * @description:
 * @author: 刘博
 * @create: 2020-07-09 18:52
 **/
@Component
public class DeletePageListener implements MessageListener {
   @Autowired
   private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
       ObjectMessage objectMessage = (ObjectMessage) message;
        try {
          Long[] ids = (Long[])  objectMessage.getObject();
            for (Long id:ids){
                itemPageService.deleteHtml(id);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        };
    }
}
