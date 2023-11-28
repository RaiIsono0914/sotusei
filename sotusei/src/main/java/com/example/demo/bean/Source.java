package com.example.demo.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Source {
	private String type;
	private String userId;

	// デフォルトコンストラクタ
	public Source() {
	}

	// getters and setters
}