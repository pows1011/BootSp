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

import com.member.model.Order;
import com.member.model.PagingResponse;
import com.member.model.Product;
import com.member.model.Review;
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
public class ProductController {

	@Value("${file.path}")
	private String path;

	@Value("${file.imgpath}")
	private String reviewfile;

	@Autowired
	private final ProductService pService;

	@Autowired
	private final OrderService oService;

	/*------------------------------------공통 처리 메서드-------------------------------------------------*/
	public void delimg(Product p) { // 이미지를 전체삭제시 사용하는 공통 메서드
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

	public void updelimg(String del) { // 이미지의 부분삭제를 위한 공통메서드
		try {
			String delpath = path + del;
			File file1 = new File(delpath);
			file1.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String imgadd(MultipartFile[] file, String type) throws Exception { // 여러개의 이미지를 등록시 사용
		String imgpath = "";

		
			for (int i = 0; i < file.length; i++) {
				if (!file[i].isEmpty()) {
					String origName = file[i].getOriginalFilename(); // 입력한 원본 파일의 이름
					String uuid = UUID.randomUUID().toString(); //문자+숫자의 랜덤한 파일명으로 변경
					String extension = origName.substring(origName.lastIndexOf("."));  // 원본파일의 파일확장자
					String savedName = uuid + extension; //랜덤이름 + 확장자 
					if (type == "a") {	//리뷰 이미지 등록 , 상품 이미지 등록을 나누기 위한 type a= 상품이미지등록 , b = 리뷰 이미지 등록
						File converFile = new File(path, savedName); // path = 상품 이미지 파일의 저장 경로가 들어있는 프로퍼티 설정값
						file[i].transferTo(converFile); // --- 실제로 저장을 시켜주는 부분 , 해당 경로에 접근할 수 있는 권한이 없으면 에러 발생
					} else {   
						File converFile = new File(reviewfile, savedName); //reviewfile= 리뷰이미지 파일의 저장 경로가 들어있는 프로퍼티 설정값
						file[i].transferTo(converFile); // --- 실제로 저장을 시켜주는 부분 , 해당 경로에 접근할 수 있는 권한이 없으면 에러 발생
					}
					imgpath += savedName + "/"; // 등록시킨 이미지 파일의 이름을 ~~/~~~/~~~/ < 이런식으로 저장 ( img 불러올때 나누기 위함 )
				}		
		}
		return imgpath;

	}

	public PagingResponse<Product> imgsplit(PagingResponse<Product> list){		
		if (!list.getList().isEmpty()) { // 나누기위해 저장시켜놓은 이미지 파일의 명을 잘라내기 위한 메서드. 페이징처리된 List에서 getImg값을 가져옴
			for (int i = 0; i < list.getList().size(); i++) {
				String[] img = list.getList().get(i).getP_img().split("/");
				String imgtxt = img[0];
				list.getList().get(i).setP_img(imgtxt);
			}
		}
		return list;
	}

	public Map<String, Object> colsizefor(String[] arraycol, String[] arraysize) { // 저장시켜놓은 color,size값을 나누기위함 
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

	
	@PostMapping("/addproduct") //Multipart[]배열로 Input file전부를 배열에 저장,
	public String addProduct(@RequestParam MultipartFile[] file, Product p, HttpServletRequest request)
			throws Exception {
		String origName = "";
		for (int i = 0; i < file.length; i++) {
			if (!file[i].isEmpty()) { //파일의 원본명을 저장시키기위해 추출 / update시 사진의 중복 체크 , 원본 이미지의 변경에 대한 값을 체크하기 위해 사용
				origName += file[i].getOriginalFilename() + "/";
			}
		}

		String[] arraycol = request.getParameterValues("p_color"); 
		String[] arraysize = request.getParameterValues("p_size");
		Map<String, Object> map = colsizefor(arraycol, arraysize);	// color.size값을 배열에 담아서 공통메서드에 파라미터로 전달, 
		
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
	public String getProduct(int p_num, Model model, HttpSession session) {
		Product p = pService.getProduct(p_num);			
		String id = (String) session.getAttribute("id");
		Order o = new Order(); // FindFavorite의 매개변수로 넘겨주기 위해서 생성 ( MAP으로 대체 가능 )
		o.setO_id(id);
		o.setO_num(p_num);
		Order favorite = oService.FindFavorite(o);    // 찜하기 여부를 확인하기 위해서 id와 제품번호로 해당하는 제품의 Order정보를 가져온다.
		String[] color = p.getP_color().split("/");
		String[] size = p.getP_size().split("/");
		String[] img = p.getP_img().split("/");
		p.setP_reviewstar(Math.round(p.getP_reviewstar() * 100) / 100.0);  //소수점 2자리까지 끊어내기 위해 작성
		model.addAttribute("img", img);
		model.addAttribute("p", p);
		if (favorite != null) {
			model.addAttribute("favorite", favorite.getO_ostate());// 찜하기가 눌려져 있을경우만 작동
		}
		model.addAttribute("color", color);
		model.addAttribute("size", size);
		return "productdetail";
	}

	@PostMapping("/prodel.do")
	public String delProduct(@RequestParam("p_num") int p_num) {
		try {

			Product p = pService.getProduct(p_num); // 삭제할 제품의 정보를 받아옴
			delimg(p); // 이미지를 삭제 시켜주는 공통메서드
			pService.DelProduct(p_num); // DB의 값을 삭제
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/prolist.do";
	}

	@PostMapping("/proupdate.do")
	public String editProduct(@RequestParam MultipartFile[] file, @RequestParam("topview") MultipartFile topview,
			Product p, HttpServletRequest request, @RequestParam("topname") String topname,@RequestParam(name="subname" ,required=false)String[] subname) throws Exception {
		String[] arraycol = request.getParameterValues("p_color"); 		//required = 값이 필수값이 아닐경우 체크해주면 NULL에러가 방지됨, 기본값은 true로 클라이언트에서 subname이없으면 에러가 발생한다.
		String[] arraysize = request.getParameterValues("p_size");
		Map<String, Object> map = colsizefor(arraycol, arraysize); // color,size 공통메서드 처리
		String p_color = (String) map.get("p_color");
		String p_size = (String) map.get("p_size");
		p.setP_color(p_color);
		p.setP_size(p_size);
		Product b = pService.getProduct(p.getP_num()); //제품번호로 제품의 정보를 다 가져옴.
		String[] img = b.getP_img().split("/"); 		// img 값을 얻어내기위해서 split로 잘라서 배열선언,
		String[] orig = b.getP_originimg().split("/");	// 원본 파일명을 얻기위해 split로 잘라서 배열선언,
		String subnoimg = "";	 //대표이미지를 제외한 이미지파일의 값을 저장하기 위해서 생성
		for (int i = 1; i < img.length; i++) {   // img[0]은 항상 대표이미지가 위치하기 때문에 제외하고 1부터 리스트의 길이만큼 반복
			subnoimg += img[i] + "/";  // 서브이미지들의 값을 저장 
		}

		String newimgpath = ""; // 새롭게 추가되는 이미지
		String firstpath = ""; // 대표 이미지
		String imgd = ""; // 기존에 존재하던 이미지 파일값을 넣기위해 생성 or 기존 이미지가 수정됬을때 추가
		String delimg = ""; // 기존파일이 수정되었을때 그 기존파일을 삭제시키기 위해서 이름값을 저장시키기 위한 변수,
		String original = ""; // 기존파일+수정된 파일의 원본 파일명을 저장하기 위해서 
		int cnt = 0; // Multipartfile 5개 모두가 EMPTY인지 확인 하기위해 증감시켜주는 변수
		if (!topview.isEmpty()) { // 대표 이미지의 수정 유무를 파악하기 위함. 값이 존재한다면 input file에서 값이 넘어온것으로 수정 한다는 뜻.
			String origName = topview.getOriginalFilename(); 
			String uuid = UUID.randomUUID().toString();
			String extension = origName.substring(origName.lastIndexOf("."));
			String savedName = uuid + extension;
			File converFile = new File(path, savedName);
			topview.transferTo(converFile);
			firstpath += savedName + "/";  // firstpath는 대표이미지의 값이 저장되는 변수 , 새로운 이미지의 변환된 이름을 저장시킨다
			original += origName + "/";		 // 파일의 원본 이름을 저장하기 위해original에 지금 원본파일의 명을 저장한다. 
			updelimg(img[0]); // 기존 파일을 삭제하고 새로운 UUID로 파일을 재생성
		} else { // 새로운 파일이 없으므로 기존의 파일을 그대로 유지한다
			firstpath += img[0] + "/"; // 넘어온 input file의 값이 없을때는 기존에 있던 대표파일의 값을 입력
			original += orig[0] + "/";	 // 마찬가지로 대표파일의 원본명을입력 [0]은 항상 대표파일의 배열값
		}

		for (int i = 0; i < file.length; i++) {
			if (!file[i].isEmpty()) { // 현재 배열[i]의 값이 빈공간,Null이 아니라면 작동
				String origName = file[i].getOriginalFilename(); //원본파일명 가져오기
				if (subname != null) { // 기존에 파일의 존재 유무
					if (i + 1 > subname.length) {// input file의 값이 존재하는 위치가 subname의 길이와 같거나 작다면 기존 이미지를 수정했다 라는 것, 
						String uuid = UUID.randomUUID().toString();
						String extension = origName.substring(origName.lastIndexOf("."));
						String savedName = uuid + extension;
						File converFile = new File(path, savedName); 
						file[i].transferTo(converFile); 
						newimgpath += savedName + "/"; // 기존이미지를 수정 했기 때문에 newimgpath에 등록 (기존이미지보다 뒤로 보내기 위함)
						original += origName + "/";
					} else { // subname이 존재하고, length보다 작다는것 = 기존이미지를수정했다는 것 
						updelimg(img[i + 1]); //img[0]은 대표 이미지의 값, 그래서 +1시켜준값을 delimg로 이미지삭제,
						String uuid = UUID.randomUUID().toString();
						String extension = origName.substring(origName.lastIndexOf("."));
						String savedName = uuid + extension;
						File converFile = new File(path, savedName);
						file[i].transferTo(converFile);
						imgd += savedName + "/";	// 기존 존재했던 파일의 위치에 저장시켰기 때문에 그 값의 위치를 대체하기 위해서 newimg가아닌 다른 변수명으로 저장
						original += origName + "/";
						delimg += img[i + 1] + "/"; //이미지는 삭제 했지만 DB값의 수정이 안되었기 때문에 뒤에 DB수정을 위해서 삭제된 img값을 저장
						cnt += 10;

					}
				} else { // 기존의 파일이 존재하지 않지만 file의 값이 존재하는 경우에 실행
					String uuid = UUID.randomUUID().toString();
					String extension = origName.substring(origName.lastIndexOf("."));
					String savedName = uuid + extension;
					File converFile = new File(path, savedName);
					file[i].transferTo(converFile);
					newimgpath += savedName + "/"; // 새로운 이미지값이기 때문에 newimgpath에 저장
					original += origName + "/";
				}

			} else {
				cnt++; // cnt는 그저 file이 수정,등록된 유무를 학인하기 위한것, 값은 의미가 없음, file의 길이보다 길면 어떠한 변화가 일어났다 라는것,
			}
		}
		if (subname != null && cnt == file.length) { //기존의 파일이 존재하지만 새로운 파일의 추가가 없었을 경우,
			imgd = subnoimg;						// img[0]을 제외한 img의 파일명을 저장한 subnoimg값을 기존 파일명을 저장하는 imgd에 저장시킨다.
			for (int i = 1; i < orig.length; i++) { // original값도 마찬가지로 이전에 존재하던 값을 그대로 입력,
				original += orig[i] + "/";
			}
		} else if (subname != null) {	 //기존의 파일이 존재하고 기존파일or새로운 파일의 변화가 있었을 경우,
			for (int i = 1; i < img.length; i++) {
				String[] j = delimg.split("/"); //기존 파일에서 수정이 된 파일의 원본명이 들어있는 변수를 split로 분리,
				for (int e = 0; e < j.length; e++) { // img에서 delimg값에 들어있지 않는 (수정되지 않은)값들만 추출해서 기존이미지 저장하는 imgd에 다시 저장.
					if (!img[i].equals(j[e])) {
						imgd += img[i] + "/";
						original += orig[i] + "/";
					}
				}

			}
		}
		p.setP_img(firstpath + imgd + newimgpath); // 대표이미지 + 기존이미지(기존이미지에 수정을한 이미지) + 아예 새롭게 추가된 이미지
		p.setP_originimg(original); // 현재img의 원본 파일명들 저장
		pService.EditProduct(p);
		return "redirect:/prolist.do";

	}

	@ResponseBody // 리턴값을 자동 JSON처리,String 반환을해도 Template로 전송되지 않음,
	@GetMapping("/delimg.do") // 제품 수정페이지에서 사진의 삭제만을 요구할때 실행되는 메서드, Ajax로 실행됨 
	public int EditimgPro(String p_img, String p_num, String p_orig) {
		int num = Integer.parseInt(p_num);
		Product p = pService.getProduct(num);
		String[] img = p.getP_img().split("/");
		String[] ori = p.getP_originimg().split("/");
		String newimg = ""; // 제품 수정에서 삭제시킨 이미지값을 제외한 이미지 값을 저장하기 위해서 생성
		String orinewimg = "";	// 제품 수정에서 삭제시킨 파일의 원본명을 제외시킨 값을 저장,
		int cnt = 0;	 // 수정이 이뤄졌는지에 대한 체크를 하기 위해 그냥 생성한 값,,
		for (int i = 0; i < img.length; i++) {
			if (!img[i].equals(p_img)) {  // 파라미터로 넘어온 삭제한 이미지 파일의 이름과 동일하지 않다면 
				newimg += img[i] + "/"; // 계속 존재해야 하기때문에 newimg에 일치하지 않는 이미지의 값을저장,
				cnt++;
			}
		}
		for (int i = 0; i < ori.length; i++) { 
			if (!ori[i].equals(p_orig)) {	//파라미터로 넘어온 삭제한 파일의 원본명을 비교
				orinewimg += ori[i] + "/"; // 일치하지 않는 값들은 그대로 변수에 다시 저장
				cnt++;
			}
		}
		p.setP_img(newimg);  // img값을 저장 
		p.setP_originimg(orinewimg); // 원본파일명 저장
		pService.EditimgPro(p); // UPDATE
		

		return cnt;
	}

	@ResponseBody
	@GetMapping("/reviewlist") //Review의 리스트를 Ajax로 비동기 구현하기 위한 메소드,
	public Map<String, Object> reviewlist(SearchDto params, int r_pronum) { // 페이징 처리를 위한 SearchDTO를 가져옴,어떤 상품의 리뷰를 가져올지 체크하기 위한 제품번호
		PagingResponse<Review> prolist = pService.AllReview(params, r_pronum); 
		Map<String, Object> result = new HashMap<String, Object>(); // 리턴시켜야 하는 값의 자료형이 여러개이기 때문에 HashMap안에 담아서 KEY값으로 한번에 모아서 처리,

		result.put("params", params);
		result.put("prolist", prolist.getList());
		result.put("pagination", prolist.getPagination());

		return result;
	}

	@ResponseBody
	@PostMapping("/addreview")	//review를 생성하는 메서드, 이미지파일은 기본값이 아니기때문에 false설정,
	public Product reviewadd(@RequestParam(name = "p_img", required = false) MultipartFile[] file, Review r,
			HttpSession session, Model model) throws Exception {
		String writer = (String) session.getAttribute("id");
		String[] img = imgadd(file, "b").split("/"); // 공통처리메서드를 실행
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

	
	
	
	/*=================================페이징 처리 VIEW============================================*/
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
		
		
		PagingResponse<Product> prolist = pService.List_Sel(params, selector, selectorOrder);
		model.addAttribute("prolist", imgsplit(prolist));
		return "productlist";
	}
	/*=================================페이징 처리 VIEW============================================*/
}
