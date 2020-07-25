package com.xxx.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.xxx.cart.service.CartService;
import com.xxx.proj.dto.Result;
import com.xxx.proj.pojo.Cart;
import com.xxx.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @program: youlexuan-parent
 * @description:
 * @author: 刘博
 * @create: 2020-07-13 21:18
 **/
@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference(timeout = 6000)
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse response;
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String cartList = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartList == null || cartList.equals("")){
            cartList="[]";
        }
        List<Cart> carts = JSON.parseArray(cartList, Cart.class);
        if (name.equals("anonymousUser")){
            System.out.println("从Cookie中获取");
            return carts;
        }else {
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(name);
            if (carts.size() >0){
             cartListFromRedis = cartService.mergeCartList(carts,cartListFromRedis);
             CookieUtil.deleteCookie(request,response,"cartList");
             cartService.saveCartListToRedis(name,carts);
                System.out.println("从Redis中获取");
            }
            return cartListFromRedis;
        }
    }
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9009",allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId,Integer num){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            List<Cart> cartList = findCartList();
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if (name.equals("anonymousUser")){
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "UTF-8");
                System.out.println("往Cookie存");
            }else{
                cartService.saveCartListToRedis(name,cartList);
                System.out.println("往Redis存");
            }
            return new Result(true,"添加成功");
        } catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }
    @RequestMapping("removeToCartList")
    public Result removeToCartList(){
        try {
            CookieUtil.deleteCookie(request,response,"cartList");
            return new Result(true,"清除成功");
        }catch (Exception e) {
            return new Result(false,"清楚失败");
        }
    }
}
