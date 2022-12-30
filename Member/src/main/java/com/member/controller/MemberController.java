package com.member.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.member.model.Member;
import com.member.service.EmailService;
import com.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {
	@Autowired //필요한 의존 객체의 “타입"에 해당하는 빈을 찾아 주입한다, 생성자,Setter,필드 3가지
	private final MemberService mService; // < 필드에 주입 
	@Autowired
	private final EmailService eService;

	@PostMapping("/member/add")
	public String MemberAdd(final Member m) {
		mService.MemberAdd(m);
		return "login";
	}

	@PostMapping("/login")
	public String login(String id, String pwd, HttpServletRequest request, HttpSession session) {

		Member m = mService.getMember(id);
		if (m != null && m.getPwd().equals(pwd)) {
			session.setAttribute("id", id);
			session.setAttribute("name", m.getName());
			session.setAttribute("type", m.getType());
			return "redirect:/index";
		} else {
			return "/login";
		}
	}

	@ResponseBody
	@GetMapping("/idchk")
	public String idchk(String id) { 
		String chk = "no";
		Member m = new Member();
		m = mService.getMember(id); // 같은 아이디가 존재한다면 m의 값이 Null이 아니게됨, 그래서 no를 반환
		if (m == null) {
			chk = "yes";
		}

		return chk;
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.removeAttribute("m");
		session.invalidate();
		return "redirect:/index";
	}

	@GetMapping("/mypage")
	public String mypage(String id, Model model) {
		Member m = mService.getMember(id);
		model.addAttribute("Member", m);
		return "mypage";
	}

	@GetMapping("/myup")
	public String updatepage(String myChk, HttpServletRequest request, Model model, HttpSession session) {
		Member m = mService.getMember((String) session.getAttribute("id"));
		model.addAttribute("myChk", myChk); // update페이지인지 구분하기 위한 변수, update라면 mypage에서										
		model.addAttribute("Member", m);	// update를 하기위한  값을 입력할 input을 보여주게 됨
		return "mypage";
	}

	@PostMapping("/myup")
	public String update(final Member m, Model model) {
		mService.editMember(m);
		return "redirect:/myup";
	}

	@PostMapping("/mydel")
	public String delete(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String id = (String) session.getAttribute("id");
		mService.delMember(id);
		session.removeAttribute("id");
		session.invalidate();
		return "redirect:/index";
	}

	@ResponseBody
	@GetMapping("/email")
	public String email(String email) {  // 이메일인증의 시작점, 아이디 찾기에서 이메일 입력시 이곳으로 넘어와서
		ArrayList<Member> m = mService.AllMember(); //email입력값이 DB에 존재하는 이메일인지 체크, 업다면 NULL
		String chk = null;							// 있다면 SUCCESS를 반환
		for (int i = 0; i < m.size(); i++) {
			if (m.get(i).getEmail().equals(email)) {
				chk ="SUCCESS";
			}
		}
		return chk;
	}
	 							//이메일의 존재유무를 체크한뒤 submit을 실행해 이곳으로 오게됨,
	@PostMapping("/findid")		//이메일 인증을 위한 인증번호를 보내주는 메서드 
	public String findid(String email,Model model) throws Exception {
		String code = eService.sendSimpleMessage(email); // EmailService의 메일을 보내는 메서드를 작동.(리턴값으로 인증번호를 받아옴)
		mService.EmailNum(code, email);		// Email에 해당하는 DB의 컬럼에 Wish에 인증번호를 저장시켜준다.
		model.addAttribute("email", email); // 인증번호를 보낸 email의 값을 인증 체크시 사용하기 위해 View로 전송 (Hidden으로 값을 저장해둠)
		return "findid";
	}

	@PostMapping("/EmailChk")
	public String EmailChk(@RequestParam("email")String email,@RequestParam("chknum") String chknum,Model model) throws Exception {
		String findid=null; // hidden처리된 email의 값을 Param으로 받아오고 , 인증번호 입력란에 입력한 값을 받아옴,			
		findid=mService.EmailCode(chknum, email); // 받아온 Code와 email로 SELECT하여 값이 존재한다면 id를리턴,아니라면 NULL을 리턴한다 
		model.addAttribute("findid",findid);		
	
		return "findid";
	}
}
