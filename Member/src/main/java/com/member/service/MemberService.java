package com.member.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.member.mapper.MemberMapper;
import com.member.model.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	
	
	@Autowired
	private final MemberMapper mMapper;

	@Transactional
	public void MemberAdd(final Member m) {
		mMapper.insert(m);
	}

	public Member getMember(String id) {

		return mMapper.getMember(id);
	}

	@Transactional
	public void editMember(final Member m) {
		mMapper.editMember(m);
	}

	@Transactional
	public void delMember(final String id) {
		mMapper.delMember(id);
	}
	@Transactional
	public void EmailNum(String wish,String email) {
		mMapper.EmailNum(wish, email);
	}
	
	public String EmailCode(String wish,String email) {
		return mMapper.EmailCode(wish, email);
	}
	
	
	
	public ArrayList<Member> AllMember(){
		
		return mMapper.AllMember();
	}

  
 
   
}
