package com.xxx.cart.service;

import com.xxx.proj.pojo.Cart;

import java.util.List;

/**
 * @program: youlexuan-parent
 * @description: 购物车
 * @author: 刘博
 * @create: 2020-07-13 20:33
 **/
public interface CartService {
    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);
    public List<Cart> findCartListFromRedis(String name);
    public void saveCartListToRedis(String name,List<Cart> cartList);
    public  List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
}
