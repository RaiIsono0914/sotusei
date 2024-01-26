package com.example.demo.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AttendLogEdit {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/attendlogedit")
    public String getDisplay_Attend_Log_Edit(@RequestParam("user_id") String userId,
                            @RequestParam("user_name") String user_name,
                            @RequestParam("user_grade") String user_grade,
                            @RequestParam("user_classroom") String user_classroom,
                            @RequestParam("user_pc") String user_pc,
                            @RequestParam("class1") String class1,
                            @RequestParam("class1time") String class1time,
                            @RequestParam("class2") String class2,
                            @RequestParam("class2time") String class2time,
                            @RequestParam("class3") String class3,
                            @RequestParam("class3time") String class3time,
                            @RequestParam("class4") String class4,
                            @RequestParam("class4time") String class4time,
                            @RequestParam("Exittime") String exittime,
                            @RequestParam("Entertime") String entertime,
                            @RequestParam("day") String day,
                            Model model) {

        // パラメーターをモデルにセット
        model.addAttribute("user_id", userId);
        model.addAttribute("user_name", user_name);
        model.addAttribute("user_grade", user_grade);
        model.addAttribute("user_classroom", user_classroom);
        model.addAttribute("user_pc", user_pc);
        model.addAttribute("class1", class1);
        model.addAttribute("class1time", class1time);
        model.addAttribute("class2", class2);
        model.addAttribute("class2time", class2time);
        model.addAttribute("class3", class3);
        model.addAttribute("class3time", class3time);
        model.addAttribute("class4", class4);
        model.addAttribute("class4time", class4time);
        model.addAttribute("Exittime", exittime);
        model.addAttribute("Entertime", entertime);
        model.addAttribute("day", day);

        return "attendedit";
    }

    @PostMapping("/attendlogedit")
    public String postDisplay_Attend_Log_Edit(@RequestParam("user_id") String userId,
                                  @RequestParam("class1") String class1,
                                  @RequestParam("class1time") String class1time,
                                  @RequestParam("class2") String class2,
                                  @RequestParam("class2time") String class2time,
                                  @RequestParam("class3") String class3,
                                  @RequestParam("class3time") String class3time,
                                  @RequestParam("class4") String class4,
                                  @RequestParam("class4time") String class4time,
                                  @RequestParam("user_pc") String user_pc,
                                  @RequestParam("Exittime") String Exittime,
                                  @RequestParam("Entertime") String Entertime,
                                  @RequestParam("day") String day
                                  ) throws IOException {

        String sql = "UPDATE user SET class1 = ?, class1time = ?, class2 = ?, class2time = ?, " +
                     "class3 = ?, class3time = ?, class4 = ?, class4time = ?, " +
                     "user_pc = ?, Exittime = ?, Entertime = ? WHERE user_id = ? AND day=?";

        jdbcTemplate.update(sql, class1, class1time, class2, class2time,
                            class3, class3time, class4, class4time,
                            user_pc, Exittime, Entertime, userId,day);

        return "redirect:/attend_list";
    }
}
