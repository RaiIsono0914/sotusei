package com.example.demo.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LinebotUpdata {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(path = "/linebotupdata", method = RequestMethod.GET)
	public String tenthGet(Model model) {

		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> resultList;

		//SELECT文の実行
		resultList = jdbcTemplate.queryForList("select * from user");

		//実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("selectResult", resultList);

		return "linebotupdata";
	}

	@RequestMapping(path = "/linebotupdata", method = RequestMethod.POST)
	public String tenthPostUpdate(String user_id, String user_name, String user_grade, String user_pc,
			String user_number, String user_classroom, Model model) throws IOException {

		System.out.println(user_classroom);

		//データ更新SQL実行
		jdbcTemplate.update("UPDATE user SET user_id=?,user_name=?,user_grade=?,"
				+ "user_pc=?,user_number=?,user_classroom=? WHERE user_id = ?;",
				user_id, user_name, user_grade, user_pc, user_number, user_classroom,user_id);

		return "redirect:/linebotupdata";
	}

	@RequestMapping(path = "/linebotdelete", method = RequestMethod.POST)
	public String tenthPostDelete(String user_id) throws IOException {

		System.out.println(user_id + "を削除しました");
		//データ削除SQL実行
		jdbcTemplate.update("DELETE FROM user WHERE user_id = ?", user_id);

		return "redirect:/linebotupdata";
	}
}