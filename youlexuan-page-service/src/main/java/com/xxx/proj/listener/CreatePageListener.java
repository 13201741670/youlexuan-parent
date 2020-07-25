package com.xxx.proj.listener;


import com.xxx.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**
 * @program: youlexuan-parent
 * @description:
 * @author: 刘博
 * @create: 2020-07-09 17:32
 **/
@Component
public class CreatePageListener implements MessageListener {
   @Autowired
   private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Long[] ids = (Long[])  objectMessage.getObject();
            for (Long id:ids){
                itemPageService.getItemHtnl(id);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
