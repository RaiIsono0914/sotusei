package com.example.demo.controller;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.bean.Event;
import com.example.demo.bean.LineData;
import com.example.demo.service.UserStateService;

@RestController
public class MyLine {

	@Autowired
	JdbcTemplate jdbcTemplate;

	// ここにチャンネルアクセストークンを貼る！
	String channelAccessToken = "n+6eDF3xXCF90HIC94zGdKaMr7pkID8kyFQpvDf1WdUmNiGNKevUwNRWZuaUy+2MKGAj/72y1S/gZSRLYLmeufnTIVLnNymgj+7fmTEJp2uVZzUErN1gWgs1OPInPZ7P/C4F1ppC1Im8QYrWCuIT6gdB04t89/1O/w1cDnyilFU=";

	@Autowired
	private UserStateService userStateService;

	@PostMapping("/sotusei")
	@CrossOrigin(origins = "*")
	public void postApidata(@RequestBody LineData webhookData) {

		for (Event event : webhookData.getEvents()) {

			String replyToken = event.getReplyToken();
			String replyText = event.getMessage().getText();

			// ユーザIDを取得する。
			String userId = event.getSource().getUserId();

			if ("キャンセル".equals(replyText)) {
				userStateService.removeUserState(userId);
				String replyMessageText = "操作をキャンセルしました\nメニューから選択してください";
				replyMessage(replyToken, replyMessageText);
				///////////////////登録/////////////////
			} else if ("登録".equals(replyText)) {
				List<Map<String, Object>> resultList = jdbcTemplate
						.queryForList("SELECT user_id FROM user WHERE user_id = ?", userId);

				if (resultList.isEmpty()) {
					String replyMessageText = "学籍番号を半角で入力してください";
					replyMessage(replyToken, replyMessageText);
					userStateService.setUserState(userId, "number");//状態保存
				} else {
					String replyMessageText = "既に登録されています。";
					replyMessage(replyToken, replyMessageText);
					userStateService.removeUserState(userId);
				}

			} else if ("number".equals(userStateService.getUserState(userId))) {

				int length = replyText.length();

				if (length == 7) {

					String replyMessageText = "名前を入力して下さい";
					replyMessage(replyToken, replyMessageText);
					userStateService.setUserState(userId, "wait_name");
					userStateService.setUserNumber(userId, replyText);//学籍番号一時保存
				} else {
					String replyMessageText = "学籍番号が正しくありません\n最初からやり直してください";
					replyMessage(replyToken, replyMessageText);
					userStateService.removeUserState(userId);
				}

			} else if ("wait_name".equals(userStateService.getUserState(userId))) {
				String replyMessageText = "学年を半角で入力して下さい　例：2";
				replyMessage(replyToken, replyMessageText);
				userStateService.setUserState(userId, "wait_grade");
				userStateService.setUserName(userId, replyText);

			} else if ("wait_grade".equals(userStateService.getUserState(userId))) {
				System.out.println(replyText);
				if (replyText.trim().equals("1") || replyText.trim().equals("2") || replyText.trim().equals("3")
						|| replyText.trim().equals("4")) {
					// ここに処理を記述
					String replyMessageText = "クラスを半角大文字で入力して下さい　例：D";
					replyMessage(replyToken, replyMessageText);
					userStateService.setUserState(userId, "wait_classroom");
					userStateService.setUserGrade(userId, replyText);
				} else {
					String replyMessageText = "学年が正しくありません\n最初からやり直してください";
					replyMessage(replyToken, replyMessageText);
					userStateService.removeUserState(userId);
				}

			} else if ("wait_classroom".equals(userStateService.getUserState(userId))) {

				if (isUppercaseLetter(replyText)) {

					String replyMessageText = "登録完了しました";
					replyMessage(replyToken, replyMessageText);
					userStateService.removeUserState(userId);
					userStateService.setUserClassroom(userId, replyText);

					//DB保存

					int number = Integer.parseInt(userStateService.getUserNumber(userId));
					String name = userStateService.getUserName(userId);
					String grade = userStateService.getUserGrade(userId);
					String classroom = userStateService.getUserClassroom(userId);

					System.out.println(number + name + grade + classroom);//確認

					//登録情報入力DB
					jdbcTemplate.update(
							"INSERT INTO user ( user_id ,user_number ,user_name ,user_grade,user_classroom,class1 ,class2 ,class3 ) VALUES (?,?,?,?,?,?,?,?);",
							userId, number, name, grade, classroom, 0, 0, 0);
					System.out.println("登録完了");
				} else {
					String replyMessageText = "クラスが正しくありません\n最初からやり直してください";
					replyMessage(replyToken, replyMessageText);
					userStateService.removeUserState(userId);
				}

				////////////////////////////////////////////////////////////出席////////////////////////////////////////////////
			} else if ("出席".equals(replyText)) {
				LocalTime currentTime = LocalTime.now();//時間を取得
				int hour = currentTime.getHour();

				if (hour == 9 || hour == 10 || hour == 11 || hour == 13 || hour == 14) {//時間

					String replyMessageText = "パスワードを半角で入力してください";
					replyMessage(replyToken, replyMessageText);
					userStateService.setUserState(userId, "wait_pass");
				} else {
					String replyMessageText = "時間外です。";
					replyMessage(replyToken, replyMessageText);
					userStateService.removeUserState(userId);
				}

			} else if ("wait_pass".equals(userStateService.getUserState(userId))) {

				String password = replyText;//入力されたpass

				//////DBからpassを取得///////

				LocalTime currentTime = LocalTime.now();//時間を取得
				int hour = currentTime.getHour();

				System.out.println(hour + "時");

				//初期化
				List<Map<String, Object>> resultList;
				Object dbpassObject = null;
				String dbpass = null;
				Object attendObject = null;
				String attend = null;
				//

				switch (hour) {//時間ごとに出席をとる
				//////////////////////１限//////////////////////////////////////////////////////////////////////
				case 9, 10:
					System.out.println("case1");
					resultList = jdbcTemplate.queryForList("select first FROM password");
					Map<String, Object> firstRow = resultList.get(0);
					dbpassObject = firstRow.get("first");

					dbpass = dbpassObject.toString();
					System.out.println(dbpass);

					if (dbpass.equals(password)) {
						System.out.println("比較OK");
						resultList = jdbcTemplate.queryForList("select class1 FROM user where user_id = ?", userId);
						Map<String, Object> firstRow2 = resultList.get(0);
						attendObject = firstRow2.get("class1");
						attend = attendObject.toString();

						currentTime = LocalTime.now();//時間を取得するためのやつ
						hour = currentTime.getHour();//時を取得
						int min = currentTime.getMinute();//分を取得

						String time_hour = Integer.toString(hour);
						String time_min = Integer.toString(min);
						String time = time_hour + time_min;

						if (attend.equals("0")) {
							jdbcTemplate.update("UPDATE user SET class1 = ?,class1time=? WHERE user_id = ?;", 1, time,
									userId);

							System.out.println("passok");
							String replyMessageText = "出席出来ました。";
							replyMessage(replyToken, replyMessageText);
							userStateService.removeUserState(userId);
						} else if (attend.equals("4")) {
							jdbcTemplate.update("UPDATE user SET class1 = ?,class1time=? WHERE user_id = ?;", 2, time,
									userId);

							System.out.println("passok");
							String replyMessageText = "遅刻です。急ぎましょう。";
							replyMessage(replyToken, replyMessageText);
							userStateService.removeUserState(userId);

						} else {
							String replyMessageText = "既に出席情報が入力されています。";
							replyMessage(replyToken, replyMessageText);
							userStateService.removeUserState(userId);
						}

					} else {

						System.out.println("passNg");
						String replyMessageText = "パスワードが違います。最初からやり直してください。";
						replyMessage(replyToken, replyMessageText);
						userStateService.removeUserState(userId);
					}

					break;
				//////////////////////２限//////////////////////////////////////////////////////////////////////
				case 11:
					System.out.println("case2");
					resultList = jdbcTemplate.queryForList("select second FROM password");
					Map<String, Object> secondRow = resultList.get(0);
					dbpassObject = secondRow.get("second");

					dbpass = dbpassObject.toString();
					System.out.println(dbpass);

					if (dbpass.equals(password)) {

						resultList = jdbcTemplate.queryForList("select class2 FROM user where user_id = ?", userId);
						Map<String, Object> firstRow2 = resultList.get(0);
						attendObject = firstRow2.get("class2");
						attend = attendObject.toString();

						currentTime = LocalTime.now();//時間を取得するためのやつ
						hour = currentTime.getHour();//時を取得
						int min = currentTime.getMinute();//分を取得

						String time_hour = Integer.toString(hour);
						String time_min = Integer.toString(min);
						String time = time_hour + time_min;

						if (attend.equals("0")) {
							jdbcTemplate.update("UPDATE user SET class2 = ?,class2time=? WHERE user_id = ?;", 1, time,
									userId);

							System.out.println("passok");
							String replyMessageText = "出席出来ました。";
							replyMessage(replyToken, replyMessageText);
							userStateService.removeUserState(userId);
						} else if (attend.equals("4")) {
							jdbcTemplate.update("UPDATE user SET class2 = ?,class2time=? WHERE user_id = ?;", 2, time,
									userId);

							System.out.println("passok");
							String replyMessageText = "遅刻です。急ぎましょう。";
							replyMessage(replyToken, replyMessageText);
							userStateService.removeUserState(userId);

						} else {
							String replyMessageText = "既に出席情報が入力されています。";
							replyMessage(replyToken, replyMessageText);
							userStateService.removeUserState(userId);
						}
					} else {

						System.out.println("passNg");
						String replyMessageText = "パスワードが違います。最初からやり直してください。";
						replyMessage(replyToken, replyMessageText);
						userStateService.removeUserState(userId);
					}

					break;

				//////////////////////３限//////////////////////////////////////////////////////////////////////
				case 13, 14:
					System.out.println("case3");//確認

					//３限のパスワード取得
					resultList = jdbcTemplate.queryForList("select third FROM password");
					Map<String, Object> thirdRow = resultList.get(0);
					dbpassObject = thirdRow.get("third");
					dbpass = dbpassObject.toString();
					//

					System.out.println(dbpass);//確認
					System.out.println(password);//確認

					//３限の出席状況確認

					//

					System.out.println(attend);//確認

					if (dbpass.equals(password)) {//パスワード正解
						resultList = jdbcTemplate.queryForList("select class3 FROM user where user_id = ?", userId);
						Map<String, Object> firstRow2 = resultList.get(0);
						attendObject = firstRow2.get("class3");
						attend = attendObject.toString();

						currentTime = LocalTime.now();//時間を取得するためのやつ
						hour = currentTime.getHour();//時を取得
						int min = currentTime.getMinute();//分を取得

						String time_hour = Integer.toString(hour);
						String time_min = Integer.toString(min);
						String time = time_hour + time_min;

						if (attend.equals("0")) {//出席状況が未入力の場合

							//出席をDBに送信
							System.out.println(userId);
							jdbcTemplate.update("UPDATE user SET class3 = ?,class3time WHERE user_id = ?;", 1, time,
									userId);

							System.out.println("passok");
							String replyMessageText = "出席出来ました。";
							replyMessage(replyToken, replyMessageText);
							userStateService.removeUserState(userId);//ユーザーの状況リセット
						} else if (attend.equals("4")) {
							jdbcTemplate.update("UPDATE user SET class3 = ?,class3time WHERE user_id = ?;", 2, time,
									userId);

							System.out.println("passok");
							String replyMessageText = "遅刻です。急ぎましょう。";
							replyMessage(replyToken, replyMessageText);
							userStateService.removeUserState(userId);

						} else {//出席状況が入力済みの場合（遅刻等）
							String replyMessageText = "既に出席情報が入力されています。";
							replyMessage(replyToken, replyMessageText);
							userStateService.removeUserState(userId);
						}
					} else {//パスワード間違い

						System.out.println("passNg");
						String replyMessageText = "パスワードが違います。最初からやり直してください。";
						replyMessage(replyToken, replyMessageText);
						userStateService.removeUserState(userId);
					}

					break;
				default:
				}

				/////////////////////////欠席////////////////////////////////////////////////////
			} else if ("欠席".equals(replyText)) {
				String replyMessageText = "教員に電話をしてください"
						+ "TEL:043-307-1819";
				replyMessage(replyToken, replyMessageText);
				/////////////////////////早退///////////////////////////////////////////////////
			} else if ("早退".equals(replyText)) {
				String replyMessageText = "早退時間を転送します。入力してください\n例:10時15分→1015";
				replyMessage(replyToken, replyMessageText);
				userStateService.setUserState(userId, "wait_time");
			} else if ("wait_time".equals(userStateService.getUserState(userId))) {
				String replyMessageText = "早退理由を転送します。入力してください";
				replyMessage(replyToken, replyMessageText);
				userStateService.setUserState(userId, "wait_reason");
				userStateService.setUserSoutaiTime(userId, replyText);
			} else if ("wait_reason".equals(userStateService.getUserState(userId))) {

				List<Map<String, Object>> resultList;
				resultList = jdbcTemplate
						.queryForList("SELECT user_grade,user_classroom,user_name FROM user WHERE user_id=?", userId);
				Map<String, Object> userMap = resultList.get(0);
				String classroom = (String) userMap.get("user_classroom");
				String grade = (String) userMap.get("user_grade");
				String name = (String) userMap.get("user_name");

				//確認
				System.out.println(classroom + "," + grade);

				resultList = jdbcTemplate.queryForList(
						"SELECT user_id FROM teacher WHERE user_classroom=? and user_grade=?", classroom, grade);
				Map<String, Object> teacherIdMap = resultList.get(0);
				String teacherId = (String) teacherIdMap.get("user_id");
				System.out.println(teacherId);

				String time = userStateService.getUserSoutaiTime(userId);
				System.out.println(time);
				jdbcTemplate.update(
						"INSERT INTO soutai ( teacher_id ,student_name ,reason,judge,student_id,time) VALUES (?,?,?,0,?,?);",
						teacherId, name, replyText, userId, time);

				MyLine2 myline2 = new MyLine2();
				myline2.soutai(teacherId, name);

				String replyMessageText = "早退申請が送信されました";
				replyMessage(replyToken, replyMessageText);
				userStateService.removeUserState(userId);

				////////////////////////////PC貸出/////////////////////////////////
			} else if ("貸出".equals(replyText)) {

				List<Map<String, Object>> resultList = jdbcTemplate
						.queryForList("SELECT user_pc FROM user WHERE user_id = ?", userId);
				System.out.println(resultList.size());
				if (resultList.isEmpty() || resultList.get(0).get("user_pc") == null) {
					jdbcTemplate.update("UPDATE user SET user_pc = ? WHERE user_id = ?;", 1, userId);
					String replyMessageText = "PC番号を入力してください。半角英数字で入力してください。";//毎朝自動的に返却したことにする
					replyMessage(replyToken, replyMessageText);
					userStateService.setUserState(userId, "pcnumber");

				} else {
					String replyMessageText = "既に貸し出しています。";//毎朝自動的に返却したことにする
					replyMessage(replyToken, replyMessageText);
				}

			} else if ("pcnumber".equals(userStateService.getUserState(userId))) {
				String pcnumber = replyText;
				jdbcTemplate.update("UPDATE user SET user_pc = ? WHERE user_id = ?;", pcnumber, userId);
				String replyMessageText = "PC貸し出しを記録しました。";
				replyMessage(replyToken, replyMessageText);
				userStateService.removeUserState(userId);
				//////////////////////////////////////////////////////////////////
			} else {
				replyMessage(replyToken, "メニューから選択してください");
			}
		}

	}

	public void soutai_judge(String message, String id) {

		pushMessage(id, message);
	}

	public static boolean isUppercaseLetter(String input) {
		// 正規表現を使用して半角大文字の英字かどうかを判断
		return input.matches("^[A-Z]$");
	}

	/*******************************************************************:
	 * ここから↓は今は気にしないでOK!
	 *******************************************************************/

	//文字を送りたい場合はこのメソッドを呼び出す。
	//呼び出す際、第二引数に送りたい文字列を渡す。
	private void replyMessage(String replyToken, String replyText) {
		// LINE APIのエンドポイント
		String url = "https://api.line.me/v2/bot/message/reply";

		// HTTPヘッダーにChannel Access Tokenを設定
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + channelAccessToken);

		// 送信するメッセージを設定
		Map<String, Object> message = new HashMap<>();
		message.put("type", "text");
		message.put("text", replyText);

		// リクエストボディを設定
		Map<String, Object> body = new HashMap<>();
		body.put("replyToken", replyToken);
		body.put("messages", Collections.singletonList(message));

		System.out.println("test");

		// HTTPリクエストを送信
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForObject(url, new HttpEntity<>(body, headers), String.class);
	}

	// メッセージ送信メソッド
	private void pushMessage(String to, String pushText) {
		// LINE APIのエンドポイント
		String url = "https://api.line.me/v2/bot/message/push";

		// HTTPヘッダーにChannel Access Tokenを設定
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + channelAccessToken);

		// 送信するメッセージを設定
		Map<String, Object> message = new HashMap<>();
		message.put("type", "text");
		message.put("text", pushText);

		// リクエストボディを設定
		Map<String, Object> body = new HashMap<>();
		body.put("to", to);
		body.put("messages", Collections.singletonList(message));

		// HTTPリクエストを送信
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForObject(url, new HttpEntity<>(body, headers), String.class);
	}
}
