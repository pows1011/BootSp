package com.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.member.model.Member;
import com.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {
	@Autowired
	private final MemberService mService;	

	

	@PostMapping("/member/add")
	public String MemberAdd(final Member m) {
		mService.MemberAdd(m);
		return "login";
	}
	@PostMapping("/login")
	public String login(String id,String pwd,HttpServletRequest request) {
		Member m=mService.getMember(id);		
		if(m!=null && m.getPwd().equals(pwd)) {
			HttpSession session=request.getSession();			
			session.setAttribute("id",id);
			session.setAttribute("name",m.getName());
			session.setAttribute("type",m.getType());
			return "redirect:/index";
		}else {
			return "/login";
		}
	}
	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		HttpSession session=request.getSession();
		session.removeAttribute("m");
		session.invalidate();
		return "redirect:/index";
	}
	@GetMapping("/mypage")
	public String mypage(String id,Model model) {		
		Member m=mService.getMember(id);		
		model.addAttribute("Member", m);
		return "mypage";
	}
	
	@GetMapping("/myup")
	public String updatepage(String myChk,HttpServletRequest request,Model model) {		
		HttpSession session=request.getSession();
		Member m=mService.getMember((String)session.getAttribute("id"));
		model.addAttribute("myChk",myChk);
		model.addAttribute("Member",m);
		return "mypage";
	}
	@PostMapping("/myup")
	public String update(final Member m,Model model) {
		mService.editMember(m);		
		return "redirect:/myup";
	}
	@PostMapping("/mydel")
	public String delete(HttpServletRequest request){
		HttpSession session=request.getSession();
		String id=(String)session.getAttribute("id");
		mService.delMember(id);
		session.removeAttribute("id");
		session.invalidate();		
		return "redirect:/index";
	}
}



