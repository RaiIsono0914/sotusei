package com.example.demo.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
	private String type;
	private String id;
	private String text;

	// デフォルトコンストラクタ
	public Message() {
	}

	// getters and setters
}