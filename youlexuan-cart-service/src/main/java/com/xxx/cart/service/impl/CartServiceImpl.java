package com.xxx.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.xxx.cart.service.CartService;
import com.xxx.proj.mapper.TbItemMapper;
import com.xxx.proj.pojo.Cart;
import com.xxx.proj.pojo.TbItem;
import com.xxx.proj.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: youlexuan-parent
 * @description:
 * @author: 刘博
 * @create: 2020-07-13 20:37
 **/
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        if (tbItem == null){
            throw new RuntimeException("商品部存在");
        }
        if (!tbItem.getStatus().equals("1")){
            throw new RuntimeException("商品状态不存在");
        }
        String sellerId = tbItem.getSellerId();
        Cart cart = searchCartBySellerId(cartList, sellerId);
        if (cart == null){
       cart = new Cart();
        cart.setSellerId(sellerId);
        cart.setSellerName(tbItem.getSeller());
        TbOrderItem orderItem = createOrderItem(tbItem, num);
        List list = new ArrayList();
        list.add(orderItem);
        cart.setTbOrderItems(list);
        cartList.add(cart);
        }else {
            TbOrderItem tbOrderItem = searchCartBySellerId(cart.getTbOrderItems(), itemId);
            if (tbOrderItem == null) {
                tbOrderItem = createOrderItem(tbItem, num);
                cart.getTbOrderItems().add(tbOrderItem);

            } else {
                tbOrderItem.setNum(tbOrderItem.getNum() + num);
                tbOrderItem.setTotalFee(new BigDecimal(tbOrderItem.getNum() * tbOrderItem.getPrice().doubleValue()));

                if (tbOrderItem.getNum() <= 0) {
                    cart.getTbOrderItems().remove(tbOrderItem);

                }
                if (cart.getTbOrderItems().size() == 0) {
                    cartList.remove(cart);
                }
            }
        }
        return  cartList;
    }



    public Cart searchCartBySellerId(List<Cart> cartList,String sellerID){
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerID)){
            return cart;
            }
        }
            return  null;
    }
    public TbOrderItem searchCartBySellerId(List<TbOrderItem> orderItemList,Long itemId){
        for (TbOrderItem tbOrderItem : orderItemList) {
            if (tbOrderItem.getItemId().longValue() == itemId.longValue()){
                return  tbOrderItem;
            }
        }
        return null;
    }
    public TbOrderItem createOrderItem(TbItem tbItem,Integer num){
        if (num <= 0){
        throw new RuntimeException("数量非法");
        }
        TbOrderItem tbOrderItem = new TbOrderItem();
        tbOrderItem.setGoodsId(tbItem.getGoodsId());
        tbOrderItem.setItemId(tbItem.getId());
        tbOrderItem.setNum(num);
        tbOrderItem.setPrice(tbItem.getPrice());
        tbOrderItem.setPicPath(tbItem.getImage());
        tbOrderItem.setSellerId(tbItem.getSellerId());
        tbOrderItem.setTitle(tbItem.getTitle());
        tbOrderItem.setTotalFee(tbItem.getPrice().multiply(new BigDecimal(num)));
        return tbOrderItem;
    }

    @Override
    public List<Cart> findCartListFromRedis(String name) {
        List<Cart> cartList =(List<Cart>) redisTemplate.boundHashOps("cartList").get(name);
        if (cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String name, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(name,cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList2) {
            for (TbOrderItem tbOrderItem:cart.getTbOrderItems()){
                cartList1=addGoodsToCartList(cartList1,tbOrderItem.getItemId(),tbOrderItem.getNum());
            }
        }
        return cartList1;
    }
}
