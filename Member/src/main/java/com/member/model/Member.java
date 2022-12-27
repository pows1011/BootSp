package com.member.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
public class Member {
	
	private String id;
	private String pwd;
	private String name;
	private String addr;
	private String tel;
	private Date birth;
	private String gender;
	private int type;
}
