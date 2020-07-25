package com.xxx.proj.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xxx.proj.pojo.TbSeller;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: youlexuan-parent
 * @description: 认证
 * @author: 刘博
 * @create: 2020-06-28 14:23
 **/
public class UserDetailsServiceImpl implements UserDetailsService {
    @Reference
    private SellerService sellerService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        TbSeller tbSeller = sellerService.findOne(username);
        if (tbSeller != null){
            if (tbSeller.getStatus().equals("1")){
                return new User(username,tbSeller.getPassword(),list);
            }else {
                return null;
            }
        }else {
            return  null;
        }
    }
}
