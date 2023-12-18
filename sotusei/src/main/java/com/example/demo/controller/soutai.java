package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class soutai {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(path = "/soutai", method = RequestMethod.GET)
	public String eidht(@RequestParam(name = "search", required = false) String searchName, Model model) {
		// SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> resultList;

		// 検索条件が指定されている場合は、名前を含むデータを検索
		if (searchName != null && !searchName.isEmpty()) {
			resultList = jdbcTemplate.queryForList("select * from soutai where student_name like ?",
					"%" + searchName + "%");
		} else {
			// 検索条件が指定されていない場合は、全データを取得
			resultList = jdbcTemplate.queryForList("select * from soutai");
		}

		// 実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("selectResult", resultList);
		model.addAttribute("searchName", searchName); // 検索条件を画面に表示するための設定

		return null;

	}
}
