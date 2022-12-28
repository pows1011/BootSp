package com.member.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.member.model.Product;
import com.member.model.Review;
import com.member.model.SearchDto;

@Mapper
public interface ProductMapper {

	void addProduct(Product p);

	Product getProduct(int p_num);

	void DelProduct(int p_num);

	void EditimgPro(Product p);

	void EditProduct(Product p);

	ArrayList<Product> Productstar();

	ArrayList<Product> Productdate();

	List<Product> Search(Map<String, Object> map);

	List<Product> SearchTotal(Map<String, Object> map);

	void addReview(Review r);

	void editStar(Product p);

	List<Review> reviewChk(int r_pronum);

	List<Review> AllReview(Map<String, Object> map);

	List<Product> Category(Map<String, Object> map);

	List<Product> PagingList(SearchDto params);

	List<Product> List_Sel(Map<String, Object> map);

	int count();

	int category_count(String params);

	int Review_count(int r_pronum);

	int SearchCount(@Param("keyword") String keyword, @Param("search") String search);

	int SearchTotCount(@Param("total")String total);
}
