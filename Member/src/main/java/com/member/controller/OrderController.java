package com.member.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import com.member.model.Order;
import com.member.model.PagingResponse;
import com.member.model.Product;
import com.member.model.SearchDto;
import com.member.service.OrderService;
import com.member.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {

	@Autowired
	private final OrderService oService;
	@Autowired
	private final ProductService pService;

	public Order add(String p_color, String p_size, int o_quantity, String id, int p_num) {
		Product p = pService.getProduct(p_num);
		Order o = new Order();
		o.setO_name(p.getP_name()); // 판매자
		o.setO_num(p.getP_num());
		o.setO_size(p_size);
		o.setO_color(p_color);
		o.setO_id(id); // 구매자
		o.setO_seller(p.getP_id());
		String[] img = p.getP_img().split("/");
		o.setO_img(img[0]);
		o.setO_quantity(o_quantity);
		o.setO_price(p.getP_price());
		return o;
	}

	@ResponseBody
	@GetMapping("/addcart")
	public boolean addcart(int p_num, HttpSession session, String p_color, String p_size, int o_quantity) {
		String id = (String) session.getAttribute("id");
		Order o = add(p_color, p_size, o_quantity, id, p_num);

		return oService.AddCart(o);
	}

	@GetMapping("/cartlist")
	public String listcart(HttpSession session, Model model) {
		String id = (String) session.getAttribute("id");
		int type = (int) session.getAttribute("type");
		ArrayList<Product> pl = new ArrayList<>();
		if (type == 1) {
			ArrayList<Order> ol = oService.FindCart(id);

			for (int i = 0; i < ol.size(); i++) {
				Product p = pService.getProduct(ol.get(i).getO_num());
				pl.add(p);

			}
			model.addAttribute("ol", ol);
			model.addAttribute("pl", pl);

		} else if (type == 2) {
			ArrayList<Order> ol = oService.FindCartSeller(id);
			model.addAttribute("ol", ol);
		}
		return "cart";
	}

	@GetMapping("/addorder")
	public String addorder(Model model, Order o, HttpServletRequest request) {
		String[] o_num = request.getParameterValues("check");
		String[] o_quantity = request.getParameterValues("o_quantity");
		for (int i = 0; i < o_num.length; i++) {
			int num = Integer.parseInt(o_num[i]);
			int quantity = Integer.parseInt(o_quantity[i]);
			o.setO_num(num);
			o.setO_quantity(quantity);
			oService.AddOrder(o);
		}
		return "redirect:/index";
	}

	@ResponseBody
	@GetMapping("/DirectOrder")
	public boolean DirectOrder(String p_color, String p_size, int o_quantity, int p_num, HttpSession session) {
		boolean flag = false;
		String id = (String) session.getAttribute("id");
		Order o = add(p_color, p_size, o_quantity, id, p_num);
		flag = oService.AddDiOrder(o);
		return flag;
	}

	@GetMapping("/findorder")
	public String FindOrder(@ModelAttribute("params") SearchDto params, Model model, HttpSession session) {
		String id = (String) session.getAttribute("id");
		int typ = (int) session.getAttribute("type");
		String type = String.valueOf(typ);
		PagingResponse<Order> olist = oService.FindOrder(params, id, type);
		model.addAttribute("olist", olist);
		return "order";
	}

	@ResponseBody
	@GetMapping("/delorder")
	public boolean DelOrder(int o_num, HttpSession session) {
		boolean chk = false;
		String id = (String) session.getAttribute("id");
		chk = oService.DelOrder(id, o_num);
		return chk;
	}

	@ResponseBody
	@GetMapping("/favorite")
	public boolean Favorite(int o_num, HttpSession session, int type) {
		boolean chk = false;

		if (type == 1) {
			String id = (String) session.getAttribute("id");
			Product p = pService.getProduct(o_num);
			Order o = new Order();
			String[] img = p.getP_img().split("/");
			o.setO_id(id);
			o.setO_price(p.getP_price());
			o.setO_name(p.getP_name());
			o.setO_num(o_num);
			o.setO_img(img[0]);
			Order Favchk = oService.FavoriteOnChk(o);
			if (Favchk == null) {
				chk = oService.Favorite(o);
			} else if (Favchk.getO_ostate() == 0) {
				oService.FavoriteOn(o_num);
				chk = true;
			}
		} else {
			oService.FavoriteOff(o_num);
		}

		return chk;
	}

}
