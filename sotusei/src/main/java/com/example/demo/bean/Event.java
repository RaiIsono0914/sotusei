package com.example.demo.bean;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Event {
	private String type;
	private Message message;
	private long timestamp;
	private Source source;
	private String replyToken;
	private String mode;
	private String webhookEventId;
	private DeliveryContext deliveryContext;

	// デフォルトコンストラクタ
	public Event() {
	}

	// getters and setters
}