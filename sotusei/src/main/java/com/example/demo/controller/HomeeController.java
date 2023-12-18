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

	@RequestMapping(path = "/homee", method = RequestMethod.GET)
	public String eidht(Model model, HttpSession session) {

		LocalTime currentTime = LocalTime.now();//時間を取得
		int hour = currentTime.getHour();//時を取得
		int min = currentTime.getMinute();//分を取得

		String time_hour = Integer.toString(hour);
		String time_min = Integer.toString(min);
		String time = time_hour + time_min;

		int inttime = Integer.parseInt(time);

		System.out.println(inttime);
		String sql = "";
		String pass = "";

		if (inttime <= 1100) {
			sql = "select first from password";
		} else if (inttime <= 1245) {
			sql = "select second from password";
		} else {
			sql = "select third from password";
		}

		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> resultList;

		//SELECT文の実行
		resultList = jdbcTemplate.queryForList(sql);

		for (Map<String, Object> result : resultList) {
			pass =result.toString();
		}

		System.out.println(pass);
		//実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("pass", pass);

		return null;

	}
}
