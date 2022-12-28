package com.member.mapper;

import java.util.ArrayList;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.member.model.Order;

@Mapper
public interface OrderMapper {

	public boolean AddCart(Order o);
	
	public boolean AddDiOrder(Order o);
	
	public boolean Favorite(Order o);
	
	public void FavoriteOff(int o_num);
	public void FavoriteOn(int o_num);
	public Order FindFavorite(Order o);
	public Order FavoriteOnChk(Order o);
	public ArrayList<Order> FindCart(String o_id);

	public ArrayList<Order> FindCartSeller(String o_name);

	public void AddOrder(Order o);

	public ArrayList<Order> FindOrder(Map<String, Object> map);
	
	
	public int FindOrder_count(@Param("name") String name,@Param("type")String type);
	
	public boolean DelOrder (Map<String,Object> map);
}