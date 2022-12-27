package com.member.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.member.model.Product;
import com.member.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TemplController {
	@Autowired
	private final ProductService pService;
	@GetMapping("/")
	public String indexpage(Model model) {
		ArrayList<Product> starlist=pService.Productstar();
		ArrayList<Product> datelist=pService.Productdate();
		for(int i=0;i<starlist.size();i++) {
			String[] starimg = starlist.get(i).getP_img().split("/");
			String[] dateimg = datelist.get(i).getP_img().split("/");
			starlist.get(i).setP_img(starimg[0]);
			datelist.get(i).setP_img(dateimg[0]);
		}	
		model.addAttribute("starlist",starlist);
		model.addAttribute("datelist",datelist);
		
		
		return "index";
	}

	@GetMapping("/index")
	public String indexpage2(Model model) {
		ArrayList<Product> starlist=pService.Productstar();
		ArrayList<Product> datelist=pService.Productdate();
		for(int i=0;i<starlist.size();i++) {
			String[] starimg = starlist.get(i).getP_img().split("/");
			String[] dateimg = datelist.get(i).getP_img().split("/");
			starlist.get(i).setP_img(starimg[0]);
			datelist.get(i).setP_img(dateimg[0]);
		}	
		model.addAttribute("starlist",starlist);
		model.addAttribute("datelist",datelist);
		return "index";
	}

	@GetMapping("/login")
	public String loginpage() {
		return "login";
	}

	@GetMapping("/addmember")
	public String addpage() {
		return "addmember";
	}
	
	
	@PostMapping("/uppage")
	public String adduppage(@RequestParam("p_num")int p_num,Model model) {				
		Product p=pService.getProduct(p_num);
		String[] color = p.getP_color().split("/");
		String[] size = p.getP_size().split("/");
		String[] img =p.getP_img().split("/");
		String[] origimg =p.getP_originimg().split("/");
		model.addAttribute("img",img);
		model.addAttribute("origimg",origimg);		
		model.addAttribute("p",p);		
		model.addAttribute("color",color);
		model.addAttribute("size",size);
		model.addAttribute("detype",2);
		
		return "addproduct";
	}
	@GetMapping("/addproduct")
	public String addProduct() {
		
		return "addproduct";

	}

	@GetMapping("/productlist")
	public String listproduct() {
		return "productlist";
	}
	@GetMapping("/findid")
	public String findid() {
		return"findid";
	}
}
