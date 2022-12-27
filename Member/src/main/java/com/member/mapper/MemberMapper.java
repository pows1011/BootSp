package com.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.member.model.Member;

@Mapper
public interface MemberMapper {
	void insert(Member m);
	Member getMember(@Param("id") String id);
	void editMember(Member m);
	void delMember(String id);
}
