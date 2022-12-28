package com.member.model;

import java.sql.Date;

import lombok.Data;

@Data
public class Member {
	
	private String id;
	private String pwd;
	private String name;
	private String addr;
	private String tel;
	private String email;
	private Date birth;
	private String gender;
	private int type;
	private String wish;
}
