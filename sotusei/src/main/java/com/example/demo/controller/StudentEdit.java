package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StudentEdit {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(path = "/studentedit", method = RequestMethod.GET)
	public String getDisplay_StudentEdit(@RequestParam("user_id") String userId,
			@RequestParam("user_name") String user_name,
			@RequestParam("user_grade") String user_grade,
			@RequestParam("user_classroom") String user_classroom,
			@RequestParam("user_number") String user_number,

			Model model) {

		// パラメーターをモデルにセット
		model.addAttribute("user_id", userId);
		model.addAttribute("user_name", user_name);
		model.addAttribute("user_grade", user_grade);
		model.addAttribute("user_classroom", user_classroom);
		model.addAttribute("user_number", user_number);


		return "studentedit";
	}

	@RequestMapping(path = "/studentedit", method = RequestMethod.POST)
	public String postDisplay_StudentEdit(@RequestParam("user_id") String userId,
	        @RequestParam("user_name") String userName,
	        @RequestParam("user_grade") String userGrade,
	        @RequestParam("user_classroom") String userClassroom,
	        @RequestParam("user_number") String userNumber,
	        Model model) {
	    String sql = "UPDATE user SET user_name = ?, user_grade = ?, user_classroom = ?, " +
	            "user_number = ? WHERE user_id = ?";

	    jdbcTemplate.update(sql, userName, userGrade, userClassroom, userNumber, userId);

	    return "redirect:/studentlist";
	}
}