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
public class AttendList {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private static final Map<String, Integer> statusMappings = Map.of(
            "未入力", 0,
            "出席", 1,
            "遅刻", 2,
            "欠席", 3,
            "遅刻自動", 4,
            "早退", 5,
            "公欠", 6,
            "授業なし", 7
    );

	@RequestMapping(path = "/attend_list", method = RequestMethod.GET)
	public String getDisplay_Attend_List(@RequestParam(name = "search", required = false) String searchValue,Model model) {


		// SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> resultList = null;

		// 検索条件が指定されている場合は、名前を含むデータを検索
		if (searchValue != null && !searchValue.isEmpty()) {
            // 全角スペースを半角スペースに変換
            searchValue = searchValue.replaceAll("　", " ");
            // スペースでキーワードを分割する
            String[] keywords = searchValue.trim().split(" ");
            // WHERE句を組み立てる
            String whereClause = "";
            List<Object> params = new ArrayList<>();
            for (String keyword : keywords) {
                if (!whereClause.isEmpty()) {
                    whereClause += " AND ";
                }
                // 数値に対応するステータス文字列を検索キーワードとして受け取った場合、数値に変換する
                Integer status = statusMappings.get(keyword);
                if (status != null) {
                    whereClause += "(class1 = ? OR class2 = ? OR class3 = ? OR class4 = ?)";
                    params.add(status);
                    params.add(status);
                    params.add(status);
                    params.add(status);
                } else {
                    whereClause += "(user_name LIKE ? OR user_grade LIKE ? OR user_classroom LIKE ?)";
                    params.add("%" + keyword + "%");
                    params.add("%" + keyword + "%");
                    params.add("%" + keyword + "%");
                }
            }

            // SQL文を実行（プレースホルダーを使用）
            String sqlQuery = "SELECT * FROM user WHERE " + whereClause;
            resultList = jdbcTemplate.queryForList(sqlQuery, params.toArray());
        } else {
            resultList = jdbcTemplate.queryForList("SELECT * FROM user");
        }

		// リストの要素ごとに処理
		if (resultList != null) {
			for (Map<String, Object> result : resultList) {
				for (int i = 1; i <= 4; i++) {
					// "class1", "class2", "class3"の値がnullでないことを確認してから処理する
					Object classValue = result.get("class" + i);
					if (classValue != null) {
						int status = ((Number) classValue).intValue(); // キャストしてからintValue()を呼び出す
						String statusString = getStatusString(status);
						result.put("class" + i, statusString);
					} else {
						// "class1", "class2", "class3"がnullの場合の処理（デフォルト値を設定するなど）
						result.put("class" + i, "未入力");
					}
				}
			}
		}

		// 実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("selectResult", resultList);

		return "attend_list";
	}

	// ステータスを文字列に変換するメソッド
	private String getStatusString(int status) {
		switch (status) {
		case 0:
			return "未入力";
		case 1:
			return "出席";
		case 2:
			return "遅刻";
		case 3:
			return "欠席";
		case 4:
			return "遅刻自動";
		case 5:
			return "早退";
		case 6:
			return "公欠";
		case 7:
			return "授業なし";
		default:
			return "エラー";
		}
	}
}
