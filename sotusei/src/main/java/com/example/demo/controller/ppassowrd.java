package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.stereotype.Controller; // この行を追加

@Controller
public class ppassowrd {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping(path = "/ppassword", method = RequestMethod.GET)
    public String eidht(Model model) {
        // SELECT文の結果をしまうためのリスト
        List<Map<String, Object>> resultList;

        // SELECT文の実行
        resultList = jdbcTemplate.queryForList("select * from password");

        // 実行結果をmodelにしまってHTMLで出せるようにする。
        model.addAttribute("selectResult", resultList);

        return "ppassword";
    }
}
