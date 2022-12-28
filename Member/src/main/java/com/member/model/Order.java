package com.member.model;

import java.sql.Date;

import lombok.Data;
@Data
public class Order {
	private String o_name,o_img,o_id,o_color,o_size,o_seller,o_favorite;
	private int o_num,o_ostate,o_dstate,o_quantity,o_price;
	private Date o_date;
}
