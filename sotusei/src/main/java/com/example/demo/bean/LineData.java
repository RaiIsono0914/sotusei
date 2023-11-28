package com.example.demo.bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LineData {
	private String destination;
	private List<Event> events;

	// デフォルトコンストラクタ
	public LineData() {
	}

	// getters and setters
}
