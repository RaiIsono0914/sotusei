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
	String channelAccessToken = "xSx1CZGmHE/bn4aPagnd1iJq3+M+D0uRdF7wVKOR1BKz0FdB6M3Ob8ulxy8xz069p8qKHdYbEuKnLrC/Lf4ec5I9fD+bFS/ioRKbzmsVhs/rjYDMCpeXHTi4Cr00XVH46g4rXcnIkXJAQHFKKhjS8gdB04t89/1O/w1cDnyilFU=";
	private Map<String, String> userDeadlines = new HashMap<>();

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

			String numataImg = "https://www.itc.ac.jp/_cms/wp-content/themes/itc1.1.0/assets/img/teacher/img-teacher-numata-s-on.jpg";

			///////////////////登録/////////////////
			if ("登録".equals(replyText)) {
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

			} else if ("確認".equals(replyText)) {

				List<Map<String, Object>> resultList;
				List<Map<String, Object>> nameList;
				String userName = "";
				// クエリを使用してデータベースから結果を取得
				resultList = jdbcTemplate
						.queryForList("SELECT user_id FROM attend WHERE class1=4 or class2=4 or class3=4");

				// resultListの要素を取り出して処理
				for (Map<String, Object> resultMap : resultList) {
					String tikoku = (String) resultMap.get("user_id");

					// ユーザーIDに対応するユーザー名を取得
					nameList = jdbcTemplate.queryForList("SELECT user_name FROM user WHERE user_id=?", tikoku);

					// nameListの要素を取り出して処理
					for (Map<String, Object> nameMap : nameList) {
						userName = userName + ((String) nameMap.get("user_name") + "さん、");
					}
				}
				pushMessage(userId, userName + "さんが遅刻しています。");
				////////////////////////////////////////////////////////////////////////////////////
			} else if ("soutai".equals(userStateService.getUserState(userId))) {
				String replyMessageText = "承認しますか？\n承認する場合は「はい」\n承認しない場合は「いいえ」\nを送信してください";
				replyMessage(replyToken, replyMessageText);
				////////////////////////////////////////////////////////////////////////////////////
			} else {
				replyMessage(replyToken, "メニューから選択してください");
			}
		}

	}

	public void soutai(String teacherId, String name, String reason) {
		pushMessage(teacherId, name + "さんが早退申請を送信しました。\n 以下理由です。\n-------------------------------\n" + reason+"\n-------------------------------\n承認しますか？\n承認する場合は「はい」\n承認しない場合は「いいえ」\nを送信してください");
	}

	///////////////以下遅刻通知////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Scheduled(cron = "0 36 9 * * ?")
	public void class1tikoku() {

		List<Map<String, Object>> resultList;
		List<Map<String, Object>> nameList;
		List<Map<String, Object>> teacherList;

		resultList = jdbcTemplate.queryForList("SELECT user_id FROM attend WHERE class1=4");
		teacherList = jdbcTemplate.queryForList("SELECT user_id FROM teacher");

		for (Map<String, Object> resultMap : resultList) {
			String tikoku = (String) resultMap.get("user_id");

			// ユーザーIDに対応するユーザー名を取得
			nameList = jdbcTemplate.queryForList("SELECT user_name FROM user WHERE user_id=?", tikoku);

			for (Map<String, Object> teacherMap : teacherList) {
				// nameListの要素を取り出して処理
				String teacher = (String) teacherMap.get("user_id");

				for (Map<String, Object> nameMap : nameList) {
					String userName = (String) nameMap.get("user_name");

					// 遅刻している場合にメッセージを送信
					pushMessage(teacher, userName + "さんが遅刻しています。");
				}
			}
		}

		System.out.println("一限の遅刻者を教員に通知しました");
	}

	@Scheduled(cron = "0 21 11 * * ?")
	public void class2tikoku() {

		List<Map<String, Object>> resultList;
		List<Map<String, Object>> nameList;
		List<Map<String, Object>> teacherList;

		resultList = jdbcTemplate.queryForList("SELECT user_id FROM attend WHERE class2=4");
		teacherList = jdbcTemplate.queryForList("SELECT user_id FROM teacher");

		for (Map<String, Object> resultMap : resultList) {
			String tikoku = (String) resultMap.get("user_id");

			// ユーザーIDに対応するユーザー名を取得
			nameList = jdbcTemplate.queryForList("SELECT user_name FROM user WHERE user_id=?", tikoku);

			for (Map<String, Object> teacherMap : teacherList) {
				// nameListの要素を取り出して処理
				String teacher = (String) teacherMap.get("user_id");

				for (Map<String, Object> nameMap : nameList) {
					String userName = (String) nameMap.get("user_name");

					// 遅刻している場合にメッセージを送信
					pushMessage(teacher, userName + "さんが遅刻しています。");
				}
			}
		}
		System.out.println("二限の遅刻者を教員に通知しました");
	}

	@Scheduled(cron = "0 51 13 * * ?")
	public void class3tikoku() {

		List<Map<String, Object>> resultList;
		List<Map<String, Object>> nameList;
		List<Map<String, Object>> teacherList;

		resultList = jdbcTemplate.queryForList("SELECT user_id FROM attend WHERE class3=4");
		teacherList = jdbcTemplate.queryForList("SELECT user_id FROM teacher");

		for (Map<String, Object> resultMap : resultList) {
			String tikoku = (String) resultMap.get("user_id");

			// ユーザーIDに対応するユーザー名を取得
			nameList = jdbcTemplate.queryForList("SELECT user_name FROM user WHERE user_id=?", tikoku);

			for (Map<String, Object> teacherMap : teacherList) {
				// nameListの要素を取り出して処理
				String teacher = (String) teacherMap.get("user_id");

				for (Map<String, Object> nameMap : nameList) {
					String userName = (String) nameMap.get("user_name");

					// 遅刻している場合にメッセージを送信
					pushMessage(teacher, userName + "さんが遅刻しています。");
				}
			}
		}
	}

	/*******************************************************************:
	 * ここから↓は今は気にしないでOK!
	 *******************************************************************/
	private void replyImageMessage(String replyToken, String originalContentUrl, String previewImageUrl) {

		// LINE APIのエンドポイント
		String url = "https://api.line.me/v2/bot/message/reply";

		// HTTPヘッダーにChannel Access Tokenを設定
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + channelAccessToken);

		// 送信する画像を設定
		Map<String, Object> message = new HashMap<>();
		message.put("type", "image");
		message.put("originalContentUrl", originalContentUrl);
		message.put("previewImageUrl", previewImageUrl);

		// リクエストボディを設定（画像用）
		Map<String, Object> body = new HashMap<>();
		body.put("replyToken", replyToken);
		body.put("messages", Collections.singletonList(message));

		// 画像を送信
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForObject(url, new HttpEntity<>(body, headers), String.class);

	}

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

	private void replyMovieMessage(String replyToken, String movieUrl, String thumnailImg) {
		// LINE APIのエンドポイント
		String url = "https://api.line.me/v2/bot/message/reply";

		// HTTPヘッダーにChannel Access Tokenを設定
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + channelAccessToken);

		// 送信するメッセージを設定
		Map<String, Object> message = new HashMap<>();
		message.put("type", "video");
		message.put("originalContentUrl", movieUrl);
		message.put("previewImageUrl", thumnailImg);

		// リクエストボディを設定
		Map<String, Object> body = new HashMap<>();
		body.put("replyToken", replyToken);
		body.put("messages", Collections.singletonList(message));

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
