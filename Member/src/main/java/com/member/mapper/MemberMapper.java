package com.member.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.member.model.Member;

@Mapper
public interface MemberMapper {
	void insert(Member m);
	Member getMember(@Param("id") String id);
	void editMember(Member m);
	void delMember(String id);
	void EmailNum(@Param("wish")String wish,@Param("email")String email);
	String EmailCode(@Param("wish")String wish,@Param("email")String email);
	ArrayList<Member> AllMember();
}
