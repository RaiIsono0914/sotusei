package com.example.demo.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class UserStateService {
	private final ConcurrentHashMap<String, String> userStates = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> userName = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> userGrade = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> userNumber = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> userClassroom = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> userSoutaiid = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> userSoutaiTime = new ConcurrentHashMap<>();


	public void setUserState(String userId, String state) {
		userStates.put(userId, state);
	}

	public String getUserState(String userId) {
		return userStates.get(userId);
	}

	public void removeUserState(String userId) {
		userStates.remove(userId);
	}

	public void setUserName(String userId, String name) {
	    userName.put(userId, name);
	}

	public String getUserName(String userId) {
	    return userName.get(userId);
	}

	public void setUserNumber(String userId, String number) {
	    userNumber.put(userId, number);
	}

	public String getUserNumber(String userId) {
	    return userNumber.get(userId);
	}

	public void setUserGrade(String userId, String grade) {
	    userGrade.put(userId, grade);
	}

	public String getUserGrade(String userId) {
	    return userGrade.get(userId);
	}

	public void setUserClassroom(String userId, String grade) {
	    userClassroom.put(userId, grade);
	}

	public String getUserClassroom(String userId) {
	    return userClassroom.get(userId);
	}

	public void setUserSoutaiid(String userId, String soutaiid) {
	    userSoutaiid.put(userId, soutaiid);
	}

	public String getUserSoutaiid(String userId) {
	    return userSoutaiid.get(userId);
	}

	public void setUserSoutaiTime(String userId, String soutaitime) {
	    userSoutaiTime.put(userId, soutaitime);
	}

	public String getUserSoutaiTime(String userId) {
	    return userSoutaiTime.get(userId);
	}

}
