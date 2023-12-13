package com.example.demo.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.bean.Event;
import com.example.demo.bean.LineData;
import com.example.demo.service.UserStateService;

@RestController
public class MyLine2 {

	@Autowired
	JdbcTemplate jdbcTemplate;

	// ここにチャンネルアクセストークンを貼る！
	String channelAccessToken = "lZxjfkwT/mI+IyjB2s/UZ9reKMXtUev0215AmuMEG+rZy4MlD7882bRjx+S1S3We4+80FRJP6pCDlK/P+bhWbqvcXDgobQjy7ZADbrCej0pTjALbrXPUZyPsTW36nJ6Iv3x85k6iu2ETm2Vk5uBqoQdB04t89/1O/w1cDnyilFU=";

	@Autowired
	private UserStateService userStateService;

	@PostMapping("/sotusei2")
	@CrossOrigin(origins = "*")
	public void postApidata(@RequestBody LineData webhookData) {

		for (Event event : webhookData.getEvents()) {

			String replyToken = event.getReplyToken();
			String replyText = event.getMessage().getText();

			// ユーザIDを取得する。
			String userId = event.getSource().getUserId();

			///////////////////登録/////////////////
			if ("キャンセル".equals(replyText)) {
				userStateService.removeUserState(userId);
				String replyMessageText = "操作をキャンセルしました\nメニューから選択してください";
				replyMessage(replyToken, replyMessageText);
			} else if ("登録".equals(replyText)) {
				String replyMessageText = "名前を入力して下さい";
				replyMessage(replyToken, replyMessageText);
				userStateService.setUserState(userId, "wait_name");
				userStateService.setUserName(userId, replyText);
			} else if ("wait_name".equals(userStateService.getUserState(userId))) {
				String replyMessageText = "学年を入力してください";
				replyMessage(replyToken, replyMessageText);
				userStateService.setUserState(userId, "gakunen");//状態保存
				userStateService.setUserName(userId, replyText);
			} else if ("gakunen".equals(userStateService.getUserState(userId))) {
				String replyMessageText = "クラスを入力して下さい";
				replyMessage(replyToken, replyMessageText);
				userStateService.setUserState(userId, "wait_classroom");
				userStateService.setUserGrade(userId, replyText);

			} else if ("wait_classroom".equals(userStateService.getUserState(userId))) {
				String replyMessageText = "登録完了しました";
				replyMessage(replyToken, replyMessageText);
				userStateService.removeUserState(userId);
				userStateService.setUserClassroom(userId, replyText);

				//DB保存

				String name = userStateService.getUserName(userId);
				String grade = userStateService.getUserGrade(userId);
				String classroom = userStateService.getUserClassroom(userId);

				System.out.println(name + grade + classroom);//確認

				//登録情報入力DB
				jdbcTemplate.update(
						"INSERT INTO teacher ( user_id ,user_name ,user_grade,user_classroom ) VALUES (?,?,?,?);",
						userId, name, grade, classroom);

			} else if ("遅刻確認".equals(replyText)) {

				List<Map<String, Object>> resultList;
				String userName = "";
				// クエリを使用してデータベースから結果を取得
				resultList = jdbcTemplate
						.queryForList("SELECT user_name FROM user WHERE class1=4 or class2=4 or class3=4");

				// resultListの要素を取り出して処理
				for (Map<String, Object> resultMap : resultList) {

					userName = userName + ((String) resultMap.get("user_name") + "さん、");

				}

				if (userName != "") {
					pushMessage(userId, userName + "が遅刻しています。");
				} else {
					pushMessage(userId, "遅刻者はいません。");
				}

				////////////////////////////////////////////////////////////////////////////////////
			} else if ("早退確認".equals(replyText)) {

				String message = "早退申請リスト\n\n";
				//DBからもらう
				List<Map<String, Object>> resultList;
				resultList = jdbcTemplate
						.queryForList("SELECT student_name,reason,id,time FROM soutai WHERE teacher_id=? AND judge=0",
								userId);

				for (Map<String, Object> resultMap : resultList) {
					String name = (String) resultMap.get("student_name");
					String reason = (String) resultMap.get("reason");
					Object id = resultMap.get("id");
					String time =(String) resultMap.get("time");
					message = message + (name + "さん\n申請ID:" + id +"\n早退時間:" + time + "\n-------早退理由-------\n" + reason
							+ "\n-------早退理由-------\n" + "\n");

				}

				message += "申請IDを送信して許可、不許可を決定してください";

				String replyMessageText = message;
				replyMessage(replyToken, replyMessageText);

				userStateService.setUserState(userId, "soutai_kakunin");

			} else if ("soutai_kakunin".equals(userStateService.getUserState(userId))) {
				String id = replyText;
				String message = "早退申請審査\n\n";
				userStateService.setUserSoutaiid(userId, replyText);
				//DBからもらう
				List<Map<String, Object>> resultList;
				resultList = jdbcTemplate
						.queryForList("SELECT student_name,reason,id,time FROM soutai WHERE id=?", id);

				if (resultList.isEmpty()) {
					String replyMessageText = "適切なIDではありません。";
					replyMessage(replyToken, replyMessageText);
					userStateService.removeUserState(userId);
				} else {
					for (Map<String, Object> resultMap : resultList) {
						String name = (String) resultMap.get("student_name");
						String reason = (String) resultMap.get("reason");
						String time =(String) resultMap.get("time");

						message += name + "さん\n申請ID:" + id+"\n早退時間:" + time + "\n-------早退理由-------\n" + reason
								+ "\n-------早退理由-------\n上記の申請を許可しますか？\n許可する場合は「許可」\n許可しない場合は「不許可」\nを送信してください";
					}

					String replyMessageText = message;
					replyMessage(replyToken, replyMessageText);

					userStateService.setUserState(userId, "soutai_sinsa");
				}
			} else if ("soutai_sinsa".equals(userStateService.getUserState(userId)) && "許可".equals(replyText)) {
				System.out.println("申請を許可しました");
				String replyMessageText = "許可しました。\n学生に通知します";
				replyMessage(replyToken, replyMessageText);

				String judgeid = userStateService.getUserSoutaiid(userId);
				//通知

				List<Map<String, Object>> resultList;
				resultList = jdbcTemplate
						.queryForList("SELECT student_name,reason,id,student_id,time FROM soutai WHERE id=?", judgeid);
				String message = "以下の申請が許可されました\n\n";
				String name = "";
				String id = "";
				String time="";
				for (Map<String, Object> resultMap : resultList) {
					name = (String) resultMap.get("student_name");
					String reason = (String) resultMap.get("reason");
					id = (String) resultMap.get("student_id");
					time = (String) resultMap.get("time");
					message += name + "さん\n申請ID:" + judgeid+"\n早退時間:" + time + "\n-------早退理由-------\n" + reason
							+ "\n-------早退理由-------";
				}



				MyLine myline = new MyLine();
				myline.soutai_judge(message, id);

				jdbcTemplate.update(
						"UPDATE soutai SET judge = 1 where id=?;", judgeid);

				int timeint = Integer.parseInt(time);

				if(timeint<=1015) {//一限と二限と三限
					jdbcTemplate.update(
							"UPDATE user SET class1 = 5, class2 = 5, class3 = 5 WHERE user_id = ?", id);
				}else if(timeint<=1200) {//三限と二限
					jdbcTemplate.update(
							"UPDATE user SET class2 = 5, class3 = 5 WHERE user_id = ?", id);
				}else if(timeint>=1201){//三限
					jdbcTemplate.update(
							"UPDATE user SET class3 = 5 WHERE user_id = ?", id);
				}


			} else if ("soutai_sinsa".equals(userStateService.getUserState(userId)) && "不許可".equals(replyText)) {
				System.out.println("申請を不許可しました");
				String replyMessageText = "不許可しました。\n学生に通知します";
				replyMessage(replyToken, replyMessageText);

				String judgeid = userStateService.getUserSoutaiid(userId);
				//通知

				List<Map<String, Object>> resultList;
				resultList = jdbcTemplate
						.queryForList("SELECT student_name,reason,id,student_id FROM soutai WHERE id=?", judgeid);
				String message = "以下の申請が不許可されました\n\n";
				String name = "";
				String id = "";
				for (Map<String, Object> resultMap : resultList) {
					name = (String) resultMap.get("student_name");
					String reason = (String) resultMap.get("reason");
					id = (String) resultMap.get("student_id");

					message += name + "さん\n申請ID:" + judgeid + "\n-------早退理由-------\n" + reason
							+ "\n-------早退理由-------";
				}

				MyLine myline = new MyLine();
				myline.soutai_judge(message, id);

				jdbcTemplate.update(
						"UPDATE soutai SET judge = 2 where id=?;", judgeid);

				//				////////////////////////////////////////////////////////////////////////////////////
			} else {
				replyMessage(replyToken, "メニューから選択してください");
			}
		}

	}

	public void soutai(String teacherId, String name) {
		pushMessage(teacherId, name + "さんが早退申請を送信しました。\nメニューから「早退確認」を選択してください。");
	}

	///////////////以下遅刻通知////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Scheduled(cron = "0 36 9 * * MON-FRI")
	public void class1tikoku() {

		List<Map<String, Object>> resultList;
		List<Map<String, Object>> teacherList;

		resultList = jdbcTemplate.queryForList("SELECT user_name FROM user WHERE class1=4");
		teacherList = jdbcTemplate.queryForList("SELECT user_id FROM teacher");

		String teacher = "";
		String userName = "";
		for (Map<String, Object> teacherMap : teacherList) {
			// nameListの要素を取り出して処理
			teacher = (String) teacherMap.get("user_id");
			System.out.println(teacher);
			for (Map<String, Object> resultMap : resultList) {
				userName = userName + (String) resultMap.get("user_name") + "さん、";
				System.out.println(userName);
			}

			if (userName != "") {
				pushMessage(teacher, userName + "が遅刻しています。");
			} else {
				pushMessage(teacher, "遅刻者はいません。");
			}
		}
		System.out.println("一限の遅刻者を教員に通知しました");
	}

	@Scheduled(cron = "30 39 11 * * MON-FRI")
	public void class2tikoku() {

		List<Map<String, Object>> resultList;
		List<Map<String, Object>> teacherList;

		resultList = jdbcTemplate.queryForList("SELECT user_name FROM user WHERE class2=4");
		teacherList = jdbcTemplate.queryForList("SELECT user_id FROM teacher");

		String teacher = "";
		String userName = "";
		for (Map<String, Object> teacherMap : teacherList) {
			// nameListの要素を取り出して処理
			teacher = (String) teacherMap.get("user_id");
			System.out.println(teacher);
			for (Map<String, Object> resultMap : resultList) {
				userName = userName + (String) resultMap.get("user_name") + "さん、";
				System.out.println(userName);
			}
			if (userName != "") {
				pushMessage(teacher, userName + "が遅刻しています。");
			} else {
				pushMessage(teacher, "遅刻者はいません。");
			}
		}
		System.out.println("二限の遅刻者を教員に通知しました");
	}

	@Scheduled(cron = "0 51 13 * * MON-FRI")
	public void class3tikoku() {

		List<Map<String, Object>> resultList;
		List<Map<String, Object>> teacherList;

		resultList = jdbcTemplate.queryForList("SELECT user_name FROM user WHERE class3=4");
		teacherList = jdbcTemplate.queryForList("SELECT user_id FROM teacher");

		String teacher = "";
		String userName = "";
		for (Map<String, Object> teacherMap : teacherList) {
			// nameListの要素を取り出して処理
			teacher = (String) teacherMap.get("user_id");
			System.out.println(teacher);
			for (Map<String, Object> resultMap : resultList) {
				userName = userName + (String) resultMap.get("user_name") + "さん、";
				System.out.println(userName);
			}
			if (userName != "") {
				pushMessage(teacher, userName + "が遅刻しています。");
			} else {
				pushMessage(teacher, "遅刻者はいません。");
			}
		}
		System.out.println("三限の遅刻者を教員に通知しました");
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
