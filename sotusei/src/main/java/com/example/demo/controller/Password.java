package com.example.demo.controller;

import java.time.LocalTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class Password {
    @Autowired
    JdbcTemplate jdbcTemplate;


    @Scheduled(cron = "0 0 8 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
    public void generateAndSavePassword() {
        LocalTime currentTime = LocalTime.now();
        int hour = currentTime.getHour();

        if (hour == 10) {
            // ランダムなパスワード生成
            String generatedPassword = generateRandomPassword(4);

            int first = Integer.parseInt(generatedPassword);
            int second = Integer.parseInt(generatedPassword);
            int third = Integer.parseInt(generatedPassword);
            int fourth = Integer.parseInt(generatedPassword);

            // ここでデータベースにパスワードを保存するなどの処理を実行できます
            jdbcTemplate.update("INSERT INTO password (first, second, third,fourth) VALUES (?, ?, ?,?)", first, second, third,fourth);
        }
    }



    public String generateRandomPassword(int length) {
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
