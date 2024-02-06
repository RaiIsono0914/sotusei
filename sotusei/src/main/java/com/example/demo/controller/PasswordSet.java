package com.example.demo.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PasswordSet {
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Scheduled(cron = "00 00 8 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void getDisplay_PassSet() {

		// ランダムなパスワード生成
		String generatedPassword = "";
		generatedPassword = GenerateRandomPassword(4);
		int first = Integer.parseInt(generatedPassword);
		generatedPassword = GenerateRandomPassword(4);
		int second = Integer.parseInt(generatedPassword);
		generatedPassword = GenerateRandomPassword(4);
		int third = Integer.parseInt(generatedPassword);
		generatedPassword = GenerateRandomPassword(4);
		int fourth = Integer.parseInt(generatedPassword);

		// ここでデータベースにパスワードを保存するなどの処理を実行でき
		jdbcTemplate.update("UPDATE password SET first = ? ,second = ?, third = ?, fourth = ?;", first, second, third,
				fourth);
		System.out.println("パスワード更新");
	}

	public String GenerateRandomPassword(int length) {
		String digits = "0123456789";
		StringBuilder password = new StringBuilder();

		Random random = new Random();

		for (int i = 0; i < length; i++) {
			int index = random.nextInt(digits.length());
			char randomDigit = digits.charAt(index);
			password.append(randomDigit);
		}

		return password.toString();
	}
}
