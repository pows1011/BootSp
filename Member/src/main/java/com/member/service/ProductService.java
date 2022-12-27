package com.member.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.member.mapper.ProductMapper;
import com.member.model.Pagination;
import com.member.model.PagingResponse;
import com.member.model.Product;
import com.member.model.Review;
import com.member.model.SearchDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductMapper pMapper;

	@Transactional
	public void addProduct(Product p) {
		pMapper.addProduct(p);
	}

	public Product getProduct(int p_num) {
		return pMapper.getProduct(p_num);
	}

	@Transactional
	public void DelProduct(int p_num) {
		pMapper.DelProduct(p_num);
	}

	@Transactional
	public void EditProduct(Product p) {

		pMapper.EditProduct(p);

	}

	@Transactional
	public void EditimgPro(Product p) {
		pMapper.EditimgPro(p);
	}

	public ArrayList<Product> Productstar() {
		return pMapper.Productstar();
	}

	public ArrayList<Product> Productdate() {
		return pMapper.Productdate();
	}

	/*********************************************************************************************/

	@Transactional
	public void addReview(Review r) {
		pMapper.addReview(r);
	}

	@Transactional
	public void editStar(Product p) {

		pMapper.editStar(p);
	}

	public List<Review> reviewChk(int r_pronum) {

		return pMapper.reviewChk(r_pronum);
	}

	/*********************************************************************************************/
	public PagingResponse<Review> AllReview(SearchDto params, int r_pronum) {
		int count = pMapper.Review_count(r_pronum);
		if (count < 1) {
			return new PagingResponse<>(Collections.emptyList(), null);
		}
		Pagination pagination = new Pagination(count, params);
		params.setPagination(pagination);

		Map<String, Object> map = new HashMap<>();
		map.put("r_pronum", r_pronum);
		map.put("limitstart", params.getPagination().getLimitStart());
		map.put("recordsize", params.getRecordSize());
		List<Review> list = pMapper.AllReview(map);
		return new PagingResponse<>(list, pagination);

	}

	public PagingResponse<Product> Category(SearchDto params, String p_category) {

		int count = pMapper.category_count(p_category);
		if (count < 1) {
			return new PagingResponse<>(Collections.emptyList(), null);
		}
		Pagination pagination = new Pagination(count, params);
		params.setPagination(pagination);
		Map<String, Object> map = new HashMap<>();
		map.put("p_category", p_category);
		map.put("limitstart", params.getPagination().getLimitStart());
		map.put("recordsize", params.getRecordSize());
		List<Product> list = pMapper.Category(map);
		return new PagingResponse<>(list, pagination);
	}

	public PagingResponse<Product> PagingList(SearchDto params) {
		int count = pMapper.count();
		if (count < 1) {
			return new PagingResponse<>(Collections.emptyList(), null);
		}

		Pagination pagination = new Pagination(count, params);
		params.setPagination(pagination);
		List<Product> list = pMapper.PagingList(params);
		return new PagingResponse<>(list, pagination);

	}

	public PagingResponse<Product> Search(SearchDto params, String keyword, String search) {

		int count;
		if (keyword.equals("p_name") || keyword.equals("p_content")) {
			count = pMapper.SearchCount(keyword, search);

		} else {
			count = pMapper.SearchTotCount(search);
		}		

		if (count < 1) {
			return new PagingResponse<>(Collections.emptyList(), null);
		}

		Pagination pagination = new Pagination(count, params);
		params.setPagination(pagination);
		Map<String, Object> map = new HashMap<>();
		map.put("search", search);
		map.put("keyword", keyword);
		map.put("limitstart", params.getPagination().getLimitStart());
		map.put("recordsize", params.getRecordSize());

		List<Product> list = null;
		if (keyword.equals("p_name") || keyword.equals("p_content")) {
			list = pMapper.Search(map);
		} else {
			list = pMapper.SearchTotal(map);
		}
		return new PagingResponse<>(list, pagination);

	}

	public PagingResponse<Product> List_Sel(SearchDto params, String selector, String selectorOrder) {
		int count = pMapper.count();
		if (count < 1) {
			return new PagingResponse<>(Collections.emptyList(), null);
		}

		Pagination pagination = new Pagination(count, params);
		params.setPagination(pagination);
		Map<String, Object> map = new HashMap<>();	
		map.put("selector", selector);
		map.put("selectorOrder", selectorOrder);
		map.put("limitstart", params.getPagination().getLimitStart());
		map.put("recordsize", params.getRecordSize());
		List<Product> list = pMapper.List_Sel(map);
		return new PagingResponse<>(list, pagination);

	}
}
