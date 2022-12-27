package com.member.model;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class Product {
	private int p_num,p_price,p_quantity,p_reviewcnt;
	private String p_id,p_name,p_img,p_content,p_category,p_color,p_size,p_originimg;
	private LocalDateTime p_date,p_ndate;
	private double p_reviewstar;
}
