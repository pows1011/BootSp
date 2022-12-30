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
		//Order를 생성할때 사용하는 값은 공통으로 적용되기 때문에 따로 메서드로 생성해서 불러오기하기 위함.
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
		// 위의 Add메서드에 view의 input에서 파라미터로 보내온 값을 대입하여 Order를 생성
		return oService.AddCart(o);
	}

	@GetMapping("/cartlist")
	public String listcart(HttpSession session, Model model) {		
		String id = (String) session.getAttribute("id");
		int memtype = (int) session.getAttribute("type");
		//type별로 (구매,판매자)에따라 다른 화면을 보여주기 위해 구분
		ArrayList<Product> pl = new ArrayList<>();
		if (memtype == 1) {
			ArrayList<Order> ol = oService.FindCart(id);
			// 구매자의 장바구니에 들어있는 제품들을 가져온다.o_ostate=0인 값
			for (int i = 0; i < ol.size(); i++) {
				Product p = pService.getProduct(ol.get(i).getO_num());
				pl.add(p);

			}
			model.addAttribute("ol", ol); // = 
			model.addAttribute("pl", pl); // = 제품별로 남은 수량을 계산하기 위해서 같이 보내줌

		} else if (memtype == 2) {
			ArrayList<Order> ol = oService.FindCartSeller(id);
			// 수정필요 , ( 판매자는 cart페이지가 필요 없음, 이부분은 Orderpage로 넘기고 Order에서 관리 )
			model.addAttribute("ol", ol);
		}
		return "cart";
	}

	@GetMapping("/addorder")
	public String addorder(Model model, Order o, HttpServletRequest request) {
		String[] o_num = request.getParameterValues("check");
		String[] o_quantity = request.getParameterValues("o_quantity");
		// checkbox로 체크해둔 상품들의 값들을 배열에 저장
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
	@GetMapping("/DirectOrder")//상세페이지에서 바로 구매할경우 URL
	public boolean DirectOrder(String p_color, String p_size, int o_quantity, int p_num, HttpSession session) {
		boolean flag = false;		
		String id = (String) session.getAttribute("id");
		Order o = add(p_color, p_size, o_quantity, id, p_num);
		flag = oService.AddDiOrder(o);
		return flag;
	}

	@GetMapping("/findorder") //주문/배송 페이지의 값을 뿌려주는곳
	public String FindOrder(@ModelAttribute("params") SearchDto params, Model model, HttpSession session) {
		String id = (String) session.getAttribute("id"); // 페이징 처리를 위해 SearchDTO를 받음,
		int memtype = (int) session.getAttribute("type"); //type별로 보이는 값이 다르게하기 위해 구분
		String type = String.valueOf(memtype);
		PagingResponse<Order> olist = oService.FindOrder(params, id, type);
		model.addAttribute("olist", olist);
		return "order";
	}

	@ResponseBody
	@GetMapping("/delorder") //주문/배송의 삭제
	public boolean DelOrder(int o_num, HttpSession session) {
		boolean chk = false;
		String id = (String) session.getAttribute("id");
		chk = oService.DelOrder(id, o_num);
		return chk;
	}

	@ResponseBody
	@GetMapping("/favorite")// 상세페이지에서 찜하기를 눌렷을경우
	public boolean Favorite(int o_num, HttpSession session, int favtype) {
		boolean chk = false;
			// 이 메서드의 favtype은 기존의 member의 type이 아닌 상세페이지 자체에서 현재 찜하기 상태의 확인을 하기 위해 만들어낸것,
			// 1일경우 찜하기 X 2일경우 찜하기 ON되있는 상태
		if (favtype == 1) {
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
