package com.member.model;

import java.sql.Date;

import lombok.Data;

@Data
public class Review {
	
	private int r_num;
	private String r_writer;
	private String r_content;
	private String r_imgpath;
	private Date r_date;
	private int r_pronum;
	private double r_star;
}
