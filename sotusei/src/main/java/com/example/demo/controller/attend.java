package com.example.demo.controller;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class attend {
	@Autowired //DB接続
	private JdbcTemplate jdbcTemplate;

	///1限のアラーム////
	@Scheduled(cron = "0 35 9 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void FirstMessage() {
		LocalTime currentTime = LocalTime.now();//時間を取得するためのやつ
		int hour = currentTime.getHour();//時を取得
		int min = currentTime.getMinute();//分を取得
		//初期化
		List<Map<String, Object>> resultList;
		Object attendObject = null;
		String attend = null;
		//DBから出席情報を取得
		resultList = jdbcTemplate.queryForList("select class1 FROM attend");//attendテーブルのclass1（一限の出席情報）取得
		Map<String, Object> firstRow = resultList.get(0);//以下３行は取得したリストを文字列にする
		attendObject = firstRow.get("class1");
		attend = attendObject.toString();
		if (attend.equals("0")) {//出席状況が未入力なら
			jdbcTemplate.update("UPDATE attend SET class1 = ?;", 4);//出席状況を遅刻にする
			System.out.println("一限目の出席情報を遅刻予定にしました");
		}
	}

	///2限のアラーム////
	@Scheduled(cron = "0 20 11 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void SecondMessage() {
		LocalTime currentTime = LocalTime.now();//時間を取得するためのやつ
		int hour = currentTime.getHour();//時を取得
		int min = currentTime.getMinute();//分を取得
		//初期化
		List<Map<String, Object>> resultList;
		Object attendObject = null;
		String attend = null;
		resultList = jdbcTemplate.queryForList("select class2 FROM attend");//attendテーブルのclass1（一限の出席情報）取得
		Map<String, Object> firstRow = resultList.get(0);//以下３行は取得したリストを文字列にする
		attendObject = firstRow.get("class2");
		attend = attendObject.toString();
		if (attend.equals("0")) {
			jdbcTemplate.update("UPDATE attend SET class2 = ?;", 4);//出席状況を遅刻にする
			System.out.println("二限目の出席情報を遅刻予定にしました");
		}
	}

	///3限のアラーム////
	@Scheduled(cron = "0 50 13 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void ThirdMessage() {
		LocalTime currentTime = LocalTime.now();//時間を取得するためのやつ
		int hour = currentTime.getHour();//時を取得
		int min = currentTime.getMinute();//分を取得
		//初期化
		List<Map<String, Object>> resultList;
		Object attendObject = null;
		String attend = null;
		resultList = jdbcTemplate.queryForList("select class3 FROM attend");
		Map<String, Object> firstRow = resultList.get(0);
		attendObject = firstRow.get("class3");
		attend = attendObject.toString();
		if (attend.equals("0")) {
			jdbcTemplate.update("UPDATE attend SET class3 = ?;", 4);
			System.out.println("三限目の出席情報を遅刻予定にしました");
		}
	}
	///欠席ゾーン

//	///1限のアラーム////
	@Scheduled(cron = "0 15 10 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void absenceFirstMessage() {
		LocalTime currentTime = LocalTime.now();//時間を取得するためのやつ
		int hour = currentTime.getHour();//時を取得
		int min = currentTime.getMinute();//分を取得
		//初期化
		List<Map<String, Object>> resultList;
		Object attendObject = null;
		String attend = null;
		//DBから出席情報を取得
		resultList = jdbcTemplate.queryForList("select class1 FROM attend");//attendテーブルのclass1（一限の出席情報）取得
		Map<String, Object> firstRow = resultList.get(0);//以下３行は取得したリストを文字列にする
		attendObject = firstRow.get("class1");
		attend = attendObject.toString();
		if (attend.equals("4")) {//出席状況が未入力なら
			jdbcTemplate.update("UPDATE attend SET class1 = ?;", 3);//出席状況を遅刻にする
			System.out.println("一限目の出席情報を欠席にしました");
		}
	}

	///2限のアラーム////
	@Scheduled(cron = "0 0 12 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void absenceSecondMessage() {
		LocalTime currentTime = LocalTime.now();//時間を取得するためのやつ
		int hour = currentTime.getHour();//時を取得
		int min = currentTime.getMinute();//分を取得
		//初期化
		List<Map<String, Object>> resultList;
		Object attendObject = null;
		String attend = null;
		resultList = jdbcTemplate.queryForList("select class2 FROM attend");//attendテーブルのclass1（一限の出席情報）取得
		Map<String, Object> firstRow = resultList.get(0);//以下３行は取得したリストを文字列にする
		attendObject = firstRow.get("class2");
		attend = attendObject.toString();
		if (attend.equals("4")) {
			jdbcTemplate.update("UPDATE attend SET class2 = ?;", 3);//出席状況を遅刻にする
			System.out.println("二限目の出席情報を欠席にしました");
		}
	}

	///3限のアラーム////
	@Scheduled(cron = "0 30 14 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void AbsenceMessage() {
		LocalTime currentTime = LocalTime.now();//時間を取得するためのやつ
		int hour = currentTime.getHour();//時を取得
		int min = currentTime.getMinute();//分を取得
		//初期化
		List<Map<String, Object>> resultList;
		Object attendObject = null;
		String attend = null;
		resultList = jdbcTemplate.queryForList("select class3 FROM attend");
		Map<String, Object> firstRow = resultList.get(0);
		attendObject = firstRow.get("class3");
		attend = attendObject.toString();
		if (attend.equals("4")) {
			jdbcTemplate.update("UPDATE attend SET class3 = ?;", 3);
			System.out.println("三限目の出席情報を欠席にしました");
		}
	}


	//データ保存
	@Scheduled(cron = "0 0 17 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void sevedata(){
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList("select * FROM attend");

		for (Map<String, Object> result : resultList) {

			// 取得したデータから必要な情報を取り出す
			String user_id = (String) result.get("user_id");
			int class1 = (int) result.get("class1");
			int class2 = (int) result.get("class2");
			int class3 = (int) result.get("class3");

			// attendlogテーブルにデータを挿入
			String insertSql = "INSERT INTO attendlog (user_id,class1, class2, class3) VALUES (?, ?, ?,?)";
			jdbcTemplate.update(insertSql, user_id,class1, class2, class3);
			System.out.println("今日の出席情報をDBに保存しました");
		}
	}

	@Scheduled(cron = "0 0 17 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void dele(){
		jdbcTemplate.update("UPDATE attend SET class1 = 0, class2 = 0, class3 = 0;");
		System.out.println("今日の出席情報を初期化しました");
		}
	}