package com.example.demo.controller;

import java.util.ArrayList;
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

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(path = "/studentlist", method = RequestMethod.GET)
	public String getDisplayStudentList(@RequestParam(name = "search", required = false) String searchValue, Model model) {

		List<Map<String, Object>> resultList = new ArrayList<>();
		if (searchValue != null && !searchValue.isEmpty()) {
            // スペースでキーワードを分割する
            String[] keywords = searchValue.trim().split("\\s+");
            // WHERE句を組み立てる
            String whereClause = "";
            List<Object> params = new ArrayList<>();
            for (String keyword : keywords) {
                if (!whereClause.isEmpty()) {
                    whereClause += " AND ";
                }
                whereClause += "(user_name LIKE ? OR user_classroom LIKE ? OR user_grade LIKE ?)";
                params.add("%" + keyword + "%");
                params.add("%" + keyword + "%");
                params.add("%" + keyword + "%");

            }

            // SQL文を実行（プレースホルダーを使用）
            String sqlQuery = "SELECT * FROM user WHERE " + whereClause;
            resultList = jdbcTemplate.queryForList(sqlQuery, params.toArray());
        } else {
            resultList = jdbcTemplate.queryForList("SELECT * FROM user");
        }

		model.addAttribute("selectResult", resultList);

		return "studentlist";
	}
	
	@RequestMapping(path = "/studentdele", method = RequestMethod.GET)
	public String getStudentDele(@RequestParam("user_id") String userId, Model model) {
		jdbcTemplate.update("delete from user where user_id = ?", userId);
		return "redirect:/studentlist";
	}
}
