package com.xxx.proj.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @program: youlexuan-parent
 * @description:
 * @author: 刘博
 * @create: 2020-07-13 20:29
 **/
public class Cart implements Serializable {
    private String sellerId;
    private String sellerName;
    private List<TbOrderItem> tbOrderItems;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TbOrderItem> getTbOrderItems() {
        return tbOrderItems;
    }

    public void setTbOrderItems(List<TbOrderItem> tbOrderItems) {
        this.tbOrderItems = tbOrderItems;
    }
}
