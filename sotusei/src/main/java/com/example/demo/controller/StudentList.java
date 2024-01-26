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
public class StudentList {

	//DBへつなぐために必要
	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(path = "/studentlist", method = RequestMethod.GET)
	public String getDisplay_StudentList(@RequestParam(name = "search", required = false) String searchValue, @RequestParam(name = "kinds", required = false) String searchKinds,
			Model model) {

		// SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> resultList = null;

		// 検索条件が指定されている場合は、名前を含むデータを検索
		if (searchValue != null && !searchValue.isEmpty()) {
			if ("name".equals(searchKinds)) {
				resultList = jdbcTemplate.queryForList("select * from user where user_name like ?",
						"%" + searchValue + "%");
			} else if ("grade".equals(searchKinds)) {
				resultList = jdbcTemplate.queryForList("select * from user where user_grade like ?",
						"%" + searchValue + "%");
			} else if ("classroom".equals(searchKinds)) {
				resultList = jdbcTemplate.queryForList("select * from user where user_classroom like ?",
						"%" + searchValue + "%");
			}
		} else {
			resultList = jdbcTemplate.queryForList("select * from user");
		}
		//実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("selectResult", resultList);

		return "studentlist";
	}

}
