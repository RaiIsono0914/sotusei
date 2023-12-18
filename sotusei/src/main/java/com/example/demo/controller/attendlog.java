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
public class attendlog {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping(path = "/attendlog", method = RequestMethod.GET)
    public String eidht(@RequestParam(name = "search", required = false) String searchName, Model model) {
        // SELECT文の結果をしまうためのリスト
        List<Map<String, Object>> resultList;

        // 検索条件が指定されている場合は、名前を含むデータを検索
        if (searchName != null && !searchName.isEmpty()) {
            resultList = jdbcTemplate.queryForList("select * from attendlog where user_name like ?", "%" + searchName + "%");
        } else {
            // 検索条件が指定されていない場合は、全データを取得
            resultList = jdbcTemplate.queryForList("select * from attendlog");
        }

        // リストの要素ごとに処理
        for (Map<String, Object> result : resultList) {
            for (int i = 1; i <= 3; i++) {
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

        // 実行結果をmodelにしまってHTMLで出せるようにする。
        model.addAttribute("selectResult", resultList);
        model.addAttribute("searchName", searchName); // 検索条件を画面に表示するための設定

        return "attendlog";
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
            default:
                return "エラー";
        }
    }
}