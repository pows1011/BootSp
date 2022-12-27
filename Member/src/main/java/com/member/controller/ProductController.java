package com.member.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.member.model.PagingResponse;
import com.member.model.Product;
import com.member.model.Review;
import com.member.model.SearchDto;
import com.member.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProductController {

	@Value("${file.path}")
	private String path;

	@Value("${file.imgpath}")
	private String reviewfile;

	@Autowired
	private final ProductService pService;

	/*------------------------------------공통 처리 메서드-------------------------------------------------*/
	public void delimg(Product p) {
		try {
			String[] del = p.getP_img().split("/");
			for (int i = 0; i < del.length; i++) {
				String delpath = path + del[i];
				File file1 = new File(delpath);
				file1.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void updelimg(String del) {
		try {
			String delpath = path + del;
			File file1 = new File(delpath);
			file1.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String imgadd(MultipartFile[] file, String type) throws Exception {
		String imgpath = "";

		if (file.length != 0) { // ---파일이 없으면 true를 리턴. false일 경우에만 처리함.
			for (int i = 0; i < file.length; i++) {
				if (!file[i].isEmpty()) {
					String origName = file[i].getOriginalFilename();
					String uuid = UUID.randomUUID().toString();
					String extension = origName.substring(origName.lastIndexOf("."));
					String savedName = uuid + extension;
					if (type == "a") {
						File converFile = new File(path, savedName);
						file[i].transferTo(converFile);
					} else {
						File converFile = new File(reviewfile, savedName);
						file[i].transferTo(converFile); // --- 저장할 경로를 설정 해당 경로에 접근할 수 있는 권한이 없으면 에러 발생
					}
					imgpath += savedName + "/";
				}
			}
		}
		return imgpath;

	}

	public PagingResponse<Product> imgsplit(PagingResponse<Product> list) {
		if (!list.getList().isEmpty()) {
			for (int i = 0; i < list.getList().size(); i++) {
				String[] img = list.getList().get(i).getP_img().split("/");
				String imgtxt = img[0];
				list.getList().get(i).setP_img(imgtxt);
			}
		}
		return list;
	}

	public Map<String, Object> colsizefor(String[] arraycol, String[] arraysize) {
		String p_color = "";
		String p_size = "";
		for (int i = 0; i < arraycol.length; i++) {
			p_color += arraycol[i] + "/";
		}
		for (int i = 0; i < arraysize.length; i++) {
			p_size += arraysize[i] + "/";
		}
		Map<String, Object> map = new HashMap<>();
		map.put("p_color", p_color);
		map.put("p_size", p_size);

		return map;
	}

	/*------------------------------------공통 처리 메서드-------------------------------------------------*/
	@PostMapping("/addproduct")
	public String addProduct(@RequestParam MultipartFile[] file, Product p, HttpServletRequest request)
			throws Exception {
		String origName = "";
		for (int i = 0; i < file.length; i++) {
			if (!file[i].isEmpty()) {
				origName += file[i].getOriginalFilename() + "/";
			}
		}

		String[] arraycol = request.getParameterValues("p_color");
		String[] arraysize = request.getParameterValues("p_size");
		Map<String, Object> map = colsizefor(arraycol, arraysize);
		String p_color = (String) map.get("p_color");
		String p_size = (String) map.get("p_size");
		p.setP_color(p_color);
		p.setP_size(p_size);
		p.setP_originimg(origName);
		p.setP_img(imgadd(file, "a"));
		pService.addProduct(p);
		return "redirect:/prolist.do";
	}

	@GetMapping("/prodetail.do")
	public String getProduct(int p_num, Model model, String detype) {
		Product p = pService.getProduct(p_num);
		String[] color = p.getP_color().split("/");
		String[] size = p.getP_size().split("/");
		String[] img = p.getP_img().split("/");
		p.setP_reviewstar(Math.round(p.getP_reviewstar() * 100) / 100.0);
		model.addAttribute("img", img);
		model.addAttribute("p", p);
		model.addAttribute("color", color);
		model.addAttribute("size", size);
		if (detype != null && detype != "") {
			model.addAttribute("detype", "up");
		}

		return "productdetail";
	}

	@PostMapping("/prodel.do")
	public String delProduct(@RequestParam("p_num") int p_num) {
		try {

			Product p = pService.getProduct(p_num);
			delimg(p);
			pService.DelProduct(p_num);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/prolist.do";
	}

	@PostMapping("/proupdate.do")
	public String editProduct(@RequestParam MultipartFile[] file, @RequestParam("topview") MultipartFile topview,
			Product p, HttpServletRequest request, @RequestParam("topname") String topname) throws Exception {
		String[] subname = null;
		String sub = request.getParameter("sub");
		if (sub.equals("y")) {
			subname = request.getParameterValues("subname");
		}
		String[] arraycol = request.getParameterValues("p_color");
		String[] arraysize = request.getParameterValues("p_size");
		Map<String, Object> map = colsizefor(arraycol, arraysize);
		String p_color = (String) map.get("p_color");
		String p_size = (String) map.get("p_size");
		p.setP_color(p_color);
		p.setP_size(p_size);

		Product b = pService.getProduct(p.getP_num());
		String[] img = b.getP_img().split("/");
		String[] orig = b.getP_originimg().split("/");
		String subnoimg = "";
		for (int i = 1; i < img.length; i++) {
			subnoimg += img[i] + "/";
		}

		String newimgpath = ""; // 새롭게 추가되는 이미지
		String firstpath = ""; // 대표 이미지
		String imgd = ""; // 기존에 존재하던 이미지 파일값을 넣기위해 생성 or 기존 이미지의 수정
		String delimg = ""; // 기존파일의 이름을 삭제하기위해서 넘겨주는 변수
		String original = ""; // 원본 파일명을 저장하기 위해 생성
		int cnt = 0; // Multipartfile 5개 모두가 EMPTY인지 확인 하기위해 증감시켜주는 변수
		if (!topview.isEmpty()) { // 대표이미지의 새로운 파일 존재유무
			String origName = topview.getOriginalFilename();
			String uuid = UUID.randomUUID().toString();
			String extension = origName.substring(origName.lastIndexOf("."));
			String savedName = uuid + extension;
			File converFile = new File(path, savedName);
			topview.transferTo(converFile);
			firstpath += savedName + "/";
			original += origName + "/";
			updelimg(img[0]); // 기존 파일을 삭제하고 새로운 UUID로 파일을 재생성
		} else { // 새로운 파일이 없으므로 기존의 파일을 그대로 유지한다
			firstpath += img[0] + "/";
			original += orig[0] + "/";
		}

		for (int i = 0; i < file.length; i++) {
			if (!file[i].isEmpty()) {
				String origName = file[i].getOriginalFilename();
				if (subname != null) {
					if (i + 1 > subname.length) {// file[i]가 존재하는 이미지의 값을 수정 했는지 안했는지 여부 파악
						String uuid = UUID.randomUUID().toString();
						String extension = origName.substring(origName.lastIndexOf("."));
						String savedName = uuid + extension;
						File converFile = new File(path, savedName);
						file[i].transferTo(converFile);
						newimgpath += savedName + "/";
						original += origName + "/";
					} else {
						updelimg(img[i + 1]);
						String uuid = UUID.randomUUID().toString();
						String extension = origName.substring(origName.lastIndexOf("."));
						String savedName = uuid + extension;
						File converFile = new File(path, savedName);
						file[i].transferTo(converFile);
						imgd += savedName + "/";
						original += origName + "/";
						delimg += img[i + 1] + "/";
						cnt += 10;

					}
				} else {
					String uuid = UUID.randomUUID().toString();
					String extension = origName.substring(origName.lastIndexOf("."));
					String savedName = uuid + extension;
					File converFile = new File(path, savedName);
					file[i].transferTo(converFile);
					newimgpath += savedName + "/";
					original += origName + "/";
				}

			} else {
				cnt++;
			}
		}
		if (subname != null && cnt == file.length) {
			imgd = subnoimg;
			for (int i = 1; i < orig.length; i++) {
				original += orig[i] + "/";
			}
		} else if (subname != null) {
			for (int i = 1; i < img.length; i++) {
				String[] j = delimg.split("/");
				for (int e = 0; e < j.length; e++) {
					if (!img[i].equals(j[e])) {
						imgd += img[i] + "/";
						original += orig[i] + "/";
					}
				}

			}
		}
		p.setP_img(firstpath + imgd + newimgpath);
		p.setP_originimg(original);
		pService.EditProduct(p);
		return "redirect:/prolist.do";

	}

	@ResponseBody
	@GetMapping("/delimg.do")
	public int EditimgPro(String p_img, String p_num, String p_orig) {
		int num = Integer.parseInt(p_num);
		Product p = pService.getProduct(num);
		String[] img = p.getP_img().split("/");
		String[] ori = p.getP_originimg().split("/");

		String newimg = "";
		String orinewimg = "";
		int cnt = 0;
		for (int i = 0; i < img.length; i++) {
			if (!img[i].equals(p_img)) {
				newimg += img[i] + "/";
				cnt++;
			}
		}
		for (int i = 0; i < ori.length; i++) {
			if (!ori[i].equals(p_orig)) {
				orinewimg += ori[i] + "/";
				cnt++;
			}
		}
		p.setP_img(newimg);
		p.setP_originimg(orinewimg);
		pService.EditimgPro(p);

		return cnt;
	}

	@ResponseBody
	@GetMapping("/reviewlist")
	public Map<String, Object> reviewlist(SearchDto params, int r_pronum) {
		PagingResponse<Review> prolist = pService.AllReview(params, r_pronum);
		Map<String, Object> result = new HashMap<String, Object>();

		result.put("params", params);
		result.put("prolist", prolist.getList());
		result.put("pagination", prolist.getPagination());

		return result;
	}

	@ResponseBody
	@PostMapping("/addreview")
	public Product reviewadd(@RequestParam(name = "p_img", required = false) MultipartFile[] file, Review r,
			HttpSession session, Model model) throws Exception {
		String writer = (String) session.getAttribute("id");
		String[] img = imgadd(file, "b").split("/"); // 공통처리 add 에서 가져옴
		r.setR_imgpath(img[0]);
		r.setR_writer(writer);

		double r_reviewstar = 0;
		pService.addReview(r);
		List<Review> list = pService.reviewChk(r.getR_pronum());
		for (int i = 0; i < list.size(); i++) {
			r_reviewstar += list.get(i).getR_star();
			if (list.get(i).getR_imgpath().equals(r.getR_imgpath())) {
				r.setR_date(list.get(i).getR_date());
			}
		}

		Product p = new Product();
		p.setP_num(r.getR_pronum());
		p.setP_reviewstar(r_reviewstar / list.size());
		pService.editStar(p);

		Product pro = pService.getProduct(r.getR_pronum());
		pro.setP_reviewstar(Math.round(p.getP_reviewstar() * 100) / 100.0);
		return pro;
	}

	@GetMapping("/category")
	public String category(@ModelAttribute("params") SearchDto params, String p_category, Model model) {
		PagingResponse<Product> prolist = pService.Category(params, p_category);
		model.addAttribute("prolist", imgsplit(prolist));
		model.addAttribute("category", p_category);
		return "productlist";
	}

	@GetMapping("/prolist.do")
	public String PagingList(@ModelAttribute("params") SearchDto params, Model model) {
		PagingResponse<Product> prolist = pService.PagingList(params);

		model.addAttribute("prolist", imgsplit(prolist));

		return "productlist";
	}

	@GetMapping("/search.do")
	public String Search(@ModelAttribute("params") SearchDto params, Model model,
			@RequestParam("keyword") String keyword, @RequestParam("search") String search) {
		PagingResponse<Product> prolist = pService.Search(params, keyword, search);
		model.addAttribute("prolist", imgsplit(prolist));
		return "productlist";
	}

	@GetMapping("/listselect")
	public String List_Sel(@ModelAttribute("params") SearchDto params, Model model,
			@RequestParam("selector") String selector, @RequestParam("selectorOrder") String selectorOrder) {
		System.out.println(selector);
		System.out.println(selectorOrder);
		PagingResponse<Product> prolist = pService.List_Sel(params, selector, selectorOrder);
		model.addAttribute("prolist", imgsplit(prolist));
		return "productlist";
	}
}
