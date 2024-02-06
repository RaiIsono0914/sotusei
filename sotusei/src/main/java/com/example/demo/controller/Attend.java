package com.example.demo.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class Attend {
	@Autowired //DB接続
	private JdbcTemplate jdbcTemplate;

	///1限のアラーム////
	@Scheduled(cron = "0 35 9 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void FirstMessage() {

		//初期化
		List<Map<String, Object>> resultList;
		Object attendObject = null;
		String attend = null;
		//DBから出席情報を取得
		resultList = jdbcTemplate.queryForList("select class1 FROM user");//attendテーブルのclass1（一限の出席情報）取得
		Map<String, Object> firstRow = resultList.get(0);//以下３行は取得したリストを文字列にする
		attendObject = firstRow.get("class1");
		attend = attendObject.toString();
		if (attend.equals("0")) {//出席状況が未入力なら
			jdbcTemplate.update("UPDATE user SET class1 = ?;", 4);//出席状況を遅刻にする
			System.out.println("一限目の出席情報を遅刻予定にしました");
		}
		System.out.println("正常終了");
	}

	///2限のアラーム////
	@Scheduled(cron = "0 20 11 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void SecondMessage() {
		//初期化
		List<Map<String, Object>> resultList;
		Object attendObject = null;
		String attend = null;
		resultList = jdbcTemplate.queryForList("select class2 FROM user");//attendテーブルのclass1（一限の出席情報）取得
		Map<String, Object> firstRow = resultList.get(0);//以下３行は取得したリストを文字列にする
		attendObject = firstRow.get("class2");
		attend = attendObject.toString();
		if (attend.equals("0")) {
			jdbcTemplate.update("UPDATE user SET class2 = ?;", 4);//出席状況を遅刻にする
			System.out.println("二限目の出席情報を遅刻予定にしました");
		}
		System.out.println("正常終了");
	}

	///3限のアラーム////
	@Scheduled(cron = "0 50 13 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void ThirdMessage() {
		//初期化
		List<Map<String, Object>> resultList;
		Object attendObject = null;
		String attend = null;
		resultList = jdbcTemplate.queryForList("select class3 FROM user");
		Map<String, Object> firstRow = resultList.get(0);
		attendObject = firstRow.get("class3");
		attend = attendObject.toString();
		if (attend.equals("0")) {
			jdbcTemplate.update("UPDATE user SET class3 = ?;", 4);
			System.out.println("三限目の出席情報を遅刻予定にしました");
		}
		System.out.println("正常終了");
	}

	///4限のアラーム////
	@Scheduled(cron = "0 35 15 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void FourthMessage() {
		//初期化
		List<Map<String, Object>> resultList;
		Object attendObject = null;
		String attend = null;
		resultList = jdbcTemplate.queryForList("select class4 FROM user");
		Map<String, Object> firstRow = resultList.get(0);
		attendObject = firstRow.get("class4");
		attend = attendObject.toString();
		if (attend.equals("0")) {
			jdbcTemplate.update("UPDATE user SET class4 = ?;", 4);
			System.out.println("四限目の出席情報を遅刻予定にしました");
		}
		System.out.println("正常終了");
	}

	///欠席ゾーン

	//	///1限のアラーム////
	@Scheduled(cron = "0 15 10 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void AbsenceFirstMessage() {
		//初期化
		List<Map<String, Object>> resultList;
		Object attendObject = null;
		String attend = null;
		//DBから出席情報を取得
		resultList = jdbcTemplate.queryForList("select class1 FROM user");//attendテーブルのclass1（一限の出席情報）取得
		Map<String, Object> firstRow = resultList.get(0);//以下３行は取得したリストを文字列にする
		attendObject = firstRow.get("class1");
		attend = attendObject.toString();
		if (attend.equals("4")) {//出席状況が未入力なら
			jdbcTemplate.update("UPDATE user SET class1 = ?;", 3);//出席状況を遅刻にする
			System.out.println("一限目の出席情報を欠席にしました");
		}
		System.out.println("正常終了");
	}

	///2限のアラーム////
	@Scheduled(cron = "0 0 12 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void AbsenceSecondMessage() {

		//初期化
		List<Map<String, Object>> resultList;
		Object attendObject = null;
		String attend = null;
		resultList = jdbcTemplate.queryForList("select class2 FROM user");//attendテーブルのclass1（一限の出席情報）取得
		Map<String, Object> firstRow = resultList.get(0);//以下３行は取得したリストを文字列にする
		attendObject = firstRow.get("class2");
		attend = attendObject.toString();
		if (attend.equals("4")) {
			jdbcTemplate.update("UPDATE user SET class2 = ?;", 3);//出席状況を遅刻にする
			System.out.println("二限目の出席情報を欠席にしました");
		}
		System.out.println("正常終了");
	}

	///3限のアラーム////
	@Scheduled(cron = "0 30 14 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void AbsenceThirdMessage() {

		//初期化
		List<Map<String, Object>> resultList;
		Object attendObject = null;
		String attend = null;
		resultList = jdbcTemplate.queryForList("select class3 FROM user");
		Map<String, Object> firstRow = resultList.get(0);
		attendObject = firstRow.get("class3");
		attend = attendObject.toString();
		if (attend.equals("4")) {
			jdbcTemplate.update("UPDATE user SET class3 = ?;", 3);
			System.out.println("三限目の出席情報を欠席にしました");
		}
		System.out.println("正常終了");
	}

	///4限のアラーム////
	@Scheduled(cron = "0 15 16 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void AbsenceFourthMessage() {

		//初期化
		List<Map<String, Object>> resultList;
		Object attendObject = null;
		String attend = null;
		resultList = jdbcTemplate.queryForList("select class4 FROM user");
		Map<String, Object> firstRow = resultList.get(0);
		attendObject = firstRow.get("class4");
		attend = attendObject.toString();
		if (attend.equals("4")) {
			jdbcTemplate.update("UPDATE user SET class4 = ?;", 3);
			System.out.println("四限目の出席情報を欠席にしました");
		}
		System.out.println("正常終了");
	}

	//////////授業がない時間の対応//////////
	@Scheduled(cron = "0 0 8 * * MON-FRI")
	public void NoClass() {
		LocalDate currentDate = LocalDate.now();
		DayOfWeek dayOfWeek = currentDate.getDayOfWeek();

		// 曜日名を取得（小文字）
		String dayOfWeekString = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase();
		System.out.println(dayOfWeekString);
		NoClassSet(dayOfWeekString);
		System.out.println("正常終了");
	}

	public void NoClassSet(String dayofweek) {
		List<Map<String, Object>> resultList;
		resultList = jdbcTemplate.queryForList("SELECT * FROM classroom");
		String save = "";

		for (Map<String, Object> row : resultList) {
			// monday1に1が入っていた場合、saveに「class1」を保存
			if (row.get(dayofweek + "1") != null && (Integer) row.get(dayofweek + "1") == 1) {
				save += "class1=7,";
			}

			// monday2に1が入っていた場合、saveに「class2」を結合
			if (row.get(dayofweek + "2") != null && (Integer) row.get(dayofweek + "2") == 1) {
				save += "class2=7,";
			}

			// monday3に1が入っていた場合、saveに「class3」を結合
			if (row.get(dayofweek + "3") != null && (Integer) row.get(dayofweek + "3") == 1) {
				save += "class3=7,";
			}

			// monday4に1が入っていた場合、saveに「class4」を結合
			if (row.get(dayofweek + "4") != null && (Integer) row.get(dayofweek + "4") == 1) {
				save += "class4=7,";
			}

			// gradeとclassroomの情報を抽出
			String grade = (String) row.get("grade");
			String classroom = (String) row.get("classroom");

			// 余分なカンマがある場合は削除
			if (save.endsWith(",")) {
				save = save.substring(0, save.length() - 1);
			}

			// 抽出されたデータを利用した処理を実行
			if (!save.isEmpty()) { // saveが空でない場合のみSQLを生成
				String sql = "UPDATE user SET " + save + " WHERE user_classroom = ? AND user_grade=?";
				System.out.println("Generated SQL: " + sql);
				// ここで生成したSQLを実行する処理を追加する
				jdbcTemplate.update("UPDATE user SET " + save + " WHERE user_classroom = ? AND user_grade=?", classroom,
						grade);
				// 次の繰り返しのためにsaveをリセット
				save = "";
			}
		}
		System.out.println("自動的に空き授業を出席情報に記録しました。");
	}

	//データ保存
	@Scheduled(cron = "0 0 18 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void SeveData() {
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList("select * FROM user");

		for (Map<String, Object> result : resultList) {

			// 取得したデータから必要な情報を取り出す
			String user_name = (String) result.get("user_name");
			String user_classroom = (String) result.get("user_classroom");
			String user_grade = (String) result.get("user_grade");
			int class1 = (int) result.get("class1");
			int class2 = (int) result.get("class2");
			int class3 = (int) result.get("class3");
			int class4 = (int) result.get("class4");
			String class1time = (String) result.get("class1time");
			String class2time = (String) result.get("class2time");
			String class3time = (String) result.get("class3time");
			String class4time = (String) result.get("class4time");
			String pc = (String) result.get("user_pc");
			String Exittime = (String) result.get("Exittime");
			String Entertime = (String) result.get("Entertime");
			// attendlogテーブルにデータを挿入
			String insertSql = "INSERT INTO attendlog (user_name,user_classroom,user_grade,class1, class2, class3, class4,class1time,class2time,class3time,class4time,user_pc,Exittime,Entertime) VALUES (?,?,?,?,?,?, ?,?,?,?,?,?,?,?)";
			jdbcTemplate.update(insertSql, user_name, user_classroom, user_grade, class1, class2, class3, class4,
					class1time, class2time,
					class3time, class4time, pc, Exittime, Entertime);

		}
		System.out.println("今日の出席情報をDBに保存しました");
		System.out.println("正常終了");
	}

	@Scheduled(cron = "0 00 18 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void DeleAttend() {
		jdbcTemplate.update(
				"UPDATE user SET class1 = 0, class2 = 0, class3 = 0,class4 = 0,class1time = null, class2time =null, class3time = null,class4time = null,Exittime = null,Entertime = null;");
		System.out.println("今日の出席情報を初期化しました");
		System.out.println("正常終了");
	}

	//毎朝8時
	@Scheduled(cron = "0 0 8 * * MON-FRI") //(cron = "秒　分　時　日　月　曜日"）
	public void DelePc() {
		jdbcTemplate.update("UPDATE user SET user_pc = null WHERE user_pc IS NOT NULL");
		System.out.println("正常終了");

	}
}