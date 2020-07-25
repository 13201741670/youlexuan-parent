package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;

import com.xxx.proj.pojo.TbItem;
import com.xxx.search.service.ItemSearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @program: youlexuan-parent
 * @description:
 * @author: 刘博
 * @create: 2020-07-03 16:20
 **/
@Service(timeout=3000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String,Object> map =new HashMap<>();
        map.putAll(hiSearch(searchMap));
        List categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);
        String  category = (String) searchMap.get("category");
        if (!"".equals(category)){
            map.putAll(brandAndSpecListSearch(category));
        }else if (categoryList.size() > 0){
         map.putAll(brandAndSpecListSearch((String) categoryList.get(0)));
        }
        return map;
    }

    private Map hiSearch(Map searchMap) {
        Map map =new HashMap();
        HighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);
        String keywords = (String) searchMap.get("keywords");
        keywords = keywords.replace(" ","");
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);
        //商品
        if (!"".equals(searchMap.get("category"))){
            Criteria criterCat = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleQuery(criterCat);
            query.addFilterQuery(filterQuery);
        }
        if (!"".equals(searchMap.get("brand"))){
            Criteria criterBrand = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleQuery(criterBrand);
            query.addFilterQuery(filterQuery);
        }
        if (searchMap.get("spec") != null){
           Map<String,String> specs = (Map<String, String>) searchMap.get("spec");
           for (String key :specs.keySet()){
            Criteria criterSpec = new Criteria("item_spec_"+key).is(searchMap.get("spec"));
            FilterQuery filterQuery = new SimpleQuery(criterSpec);
            query.addFilterQuery(filterQuery);
           }
        }
        String prices =  searchMap.get("price")+"";
        if (!"".equals(prices)){
            String [] price = (searchMap.get("price")+"").split("-");
            if (!price[0].equals("0")){
                Criteria criterPrice = new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery filterQuery = new SimpleQuery(criterPrice);
                query.addFilterQuery(filterQuery);
            }
            if (!price[1].equals("*")){
                Criteria criterPrice = new Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery = new SimpleQuery(criterPrice);
                query.addFilterQuery(filterQuery);
            }
        }
        Integer pageNo = (Integer) searchMap.get("pageNo");
        Integer rows=(Integer) searchMap.get("pageSize");
        if (pageNo == null){
            pageNo = 1;
        }
        if (rows == null){
            rows = 20;
        }
        query.setOffset((pageNo-1)*rows);
        query.setRows(rows);
        //排序

      String sprt = searchMap.get("sort")+"";
      String sortField=searchMap.get("sortField")+"";
      if ("ASC".equals(sprt)){
          Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
          query.addSort(sort);
      }else if ("DESC".equals(sprt)){
          Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
          query.addSort(sort);
      }else {

      }

        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query,TbItem.class);
        for (HighlightEntry<TbItem> h: page.getHighlighted()){
             TbItem tbItem = h.getEntity();
             if (h.getHighlights().size() >0 && h.getHighlights().get(0).getSnipplets().size() > 0){
                 tbItem.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
             }
        }
        map.put("rows",page.getContent());
        map.put("totalPages",page.getTotalPages());
        map.put("total",page.getTotalElements());
        return map;
    }
    public List searchCategoryList(Map searchMap){
        List<String> list = new ArrayList<>();
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry:content){
            list.add(entry.getGroupValue());
        }
        return list;
    }
    public Map brandAndSpecListSearch(String category){
        Map map = new HashMap();
       Long id = (Long) redisTemplate.boundHashOps("itemCat").get(category);
       if (id != null){
           List brandList =(List) redisTemplate.boundHashOps("brandList").get(id);
           map.put("brandList",brandList);
          List specList =(List) redisTemplate.boundHashOps("specList").get(id);
           map.put("specList",specList);
       }
       return map;
    }
}
