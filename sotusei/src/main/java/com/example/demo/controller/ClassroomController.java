package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClassroomController {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(path = "/classroom", method = RequestMethod.GET)
	public String edit() {
		return "classroom";
	}

	@RequestMapping(path = "/classroom", method = RequestMethod.POST)
	public String updateClassroom(@RequestParam Map<String, String> params) {
	    String[] days = { "monday", "tuesday", "wednesday", "thursday", "friday" };
	    String sqlColumns = "";
	    String sqlValues = "";

	    for (int i = 1; i <= 4; i++) {
	        for (String day : days) {
	            String value = params.get(day + i);

	            if ("1".equals(value)) {
	                sqlColumns += day + i + ",";
	                sqlValues += "1,";
	            }
	        }
	    }

	    String grade = params.get("grade");
	    String classroom = params.get("classroom");
	    List<Map<String, Object>> resultList = jdbcTemplate.queryForList("select * FROM classroom where grade=? AND classroom=?",grade,classroom);

		if(!resultList.isEmpty()) {
			jdbcTemplate.update("DELETE FROM classroom where grade=? AND classroom=?",grade,classroom);
		}

	    sqlColumns += "grade, classroom";
	    sqlValues += params.get("grade") + ", '" + params.get("classroom")+"'";

	    jdbcTemplate.update("INSERT INTO classroom (" + sqlColumns + ") VALUES (" + sqlValues + ");");

	    // 何かしらの結果を返す処理を追加する

	    return "redirect:/classroom"; // リダイレクト先を適切に設定する
	}



}
