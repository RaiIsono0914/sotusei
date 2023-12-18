package com.example.demo.controller;

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
public class LoginController {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(path = "/login", method = RequestMethod.GET)
	public String copGet() {
		return "login";
	}

	/////ログイン機能
	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public String copPost(String user_grade, String user_classroom, String user_name, Model model,
			HttpSession session) {
		
		//sessionに入力されたloginidを格納する。
		session.setAttribute("user_name", user_name);

		
		int length = user_grade.length();
		int length2 = user_classroom.length();
		if (length < 17 && length2 < 17) {

			// SQL文の修正
			List<Map<String, Object>> resultList = jdbcTemplate
					.queryForList("SELECT * FROM teacher WHERE user_grade=? AND user_classroom=? AND user_name=?",
							user_grade, user_classroom,
							user_name);

			model.addAttribute("user_grade", user_grade);
			model.addAttribute("user_classroom", user_classroom);
			model.addAttribute("user_name", user_name);

			int count = resultList.size();

			if (count > 0) {
				// ログイン成功時にセッションに情報を保存
				session.setAttribute("loginparam1", user_grade);
				session.setAttribute("loginparam2", user_classroom);
				session.setAttribute("loginparam3", user_name);

				return "redirect:/homee";
			} else {
				return "login_ng";
			}

		} else {
			return "login_ng2";
		}
	}

	////新しい先生の追加
	@RequestMapping(path = "/newuser", method = RequestMethod.GET)
	public String newGet() {
		return "newuser";
	}

	@RequestMapping(path = "/newuser", method = RequestMethod.POST)
	public String newPost(String user_grade, String user_classroom, String user_name, Model model) {

		if (user_grade.length() > 10 || user_classroom.length() > 10 || user_name.length() > 10) {
			model.addAttribute("message", "学年,クラス,名前は、10文字以内で入力してください。");
		} else {
			jdbcTemplate.update("INSERT INTO teacher (user_grade, user_classroom, user_name)"
					+ " VALUES (?, ?, ?)", user_grade, user_classroom, user_name);
			model.addAttribute("message", "登録が完了しました。");
		}

		return "newuser";
	}

	////ログアウト機能
	@RequestMapping(path = "/sessionlogout", method = RequestMethod.GET)
	public String third(HttpSession session) {

		//セッションを破棄する前に中身を確認。
		String x = (String) session.getAttribute("user_name");

		System.out.println(x);

		//セッションにしまっていたものを破棄する。
		session.removeAttribute("user_name");

		return "login";
	}

}
