package com.member.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.member.mapper.OrderMapper;
import com.member.model.Order;
import com.member.model.Pagination;
import com.member.model.PagingResponse;
import com.member.model.Product;
import com.member.model.SearchDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderMapper oMapper;

	@Transactional
	public boolean AddCart(Order o) {

		return oMapper.AddCart(o);
	};

	@Transactional
	public boolean AddDiOrder(Order o) {
		return oMapper.AddDiOrder(o);
	}

	@Transactional
	public boolean Favorite(Order o) {
		return oMapper.Favorite(o);
	}

	public ArrayList<Order> FindCart(String o_id) {
		return oMapper.FindCart(o_id);
	}

	public ArrayList<Order> FindCartSeller(String o_name) {
		return oMapper.FindCartSeller(o_name);
	}

	@Transactional
	public void AddOrder(Order o) {
		oMapper.AddOrder(o);
	}

	public PagingResponse<Order> FindOrder(SearchDto params, String id, String type) {
		int count = oMapper.FindOrder_count(id, type);
		if (count < 1) {
			return new PagingResponse<>(Collections.emptyList(), null);
		}
		System.out.println(id);
		System.out.println(type);
		Pagination pagination = new Pagination(count, params);
		params.setPagination(pagination);
		Map<String, Object> map = new HashMap<>();
		map.put("name", id);
		map.put("type", type);
		map.put("limitstart", params.getPagination().getLimitStart());
		map.put("recordsize", params.getRecordSize());
		List<Order> list = oMapper.FindOrder(map);
		return new PagingResponse<>(list, pagination);

	}

	@Transactional
	public boolean DelOrder(String o_id, int o_num) {
		Map<String, Object> map = new HashMap<>();
		map.put("o_id", o_id);
		map.put("o_num", o_num);

		return oMapper.DelOrder(map);

	}

}
