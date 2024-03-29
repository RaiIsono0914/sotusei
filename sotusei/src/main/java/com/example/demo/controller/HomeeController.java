package com.example.demo.controller;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeeController {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(path = "/home", method = RequestMethod.GET)
	public String getDisplay_Home(Model model, HttpSession session) {

		LocalTime currentTime = LocalTime.now();//時間を取得
        int hour = currentTime.getHour();//時を取得
        int min = currentTime.getMinute();//分を取得

        // 時と分を2桁の文字列に変換
        String time_hour = String.format("%02d", hour);
        String time_min = String.format("%02d", min);

        // 時と分を結合して時間の文字列を作成
        String time = time_hour + time_min;

        // 文字列を整数に変換
        int inttime = Integer.parseInt(time);

		System.out.println(inttime);
		String sql = "";
		String pass = "";

		if (inttime <= 1100) {
			sql = "select first from password";
		} else if (inttime <= 1245) {
			sql = "select second from password";
		} else if (inttime <= 1515) {
			sql = "select third from password";
		}else {
			sql = "select fourth from password";
		}

		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> resultList;

		//SELECT文の実行
		resultList = jdbcTemplate.queryForList(sql);

		for (Map<String, Object> result : resultList) {
			pass =result.values().toArray()[0].toString();
		}

		System.out.println(pass);
		//実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("pass", pass);

		return "homee";

	}
}
