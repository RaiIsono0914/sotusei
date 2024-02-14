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
public class Soutai {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(path = "/soutai", method = RequestMethod.GET)
	public String getDisplay_Soutai(@RequestParam(name = "search", required = false) String searchValue,
			@RequestParam(name = "kinds", required = false) String searchKinds,
			Model model) {
		// SELECT文の結果をしまうためのリスト
		// SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> resultList = null;

		// 検索条件が指定されている場合は、名前を含むデータを検索
		if (searchValue != null && !searchValue.isEmpty()) {
			if ("name".equals(searchKinds)) {
				resultList = jdbcTemplate.queryForList("select * from soutai where student_name like ?",
						"%" + searchValue + "%");
			} else if ("grade".equals(searchKinds)) {
				resultList = jdbcTemplate.queryForList("select * from soutai where user_grade like ?",
						"%" + searchValue + "%");
			} else if ("classroom".equals(searchKinds)) {
				resultList = jdbcTemplate.queryForList("select * from soutai where user_classroom like ?",
						"%" + searchValue + "%");
			}
		} else {
			resultList = jdbcTemplate.queryForList("select * from soutai");
		}

		// リストの要素ごとに処理
		// リストの要素ごとに処理
		if (resultList != null) {
			for (Map<String, Object> result : resultList) {
				Object judgeValue = result.get("judge");

				if (judgeValue instanceof Number) {
					int status = ((Number) judgeValue).intValue();
					String statusString = getStatusString(status);
					result.put("judge", statusString);
				} else {
					// "judge"がNumber型でない場合の処理（デフォルト値を設定するなど）
					result.put("judge", "未入力");
				}
			}
		}

		// 実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("selectResult", resultList);

		return "soutai";

	}
	
	@RequestMapping(path = "/soutaidele", method = RequestMethod.GET)
	public String getDelete_Soutai(@RequestParam(name = "student_name", required = true) String studentName,
			@RequestParam(name = "day", required = true) String day) {
		
		jdbcTemplate.update("delete from soutai where student_name = ? and day = ?", studentName, day);
		
		return "redirect:/soutai";
		
	}
	
	private String getStatusString(int status) {
		switch (status) {
		case 0:
			return "未審査";
		case 1:
			return "許可";
		case 2:
			return "不許可";
		default:
			return "エラー";
		}
	}
}
