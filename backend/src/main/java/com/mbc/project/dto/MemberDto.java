package com.mbc.project.dto;

import java.io.Serializable;
// 어디서든 활용 가능한 변수 설정
public class MemberDto implements Serializable {
	public MemberDto() {
		
	}
	
    private Integer seq;
    private String id;     
    private String pw;
    private String name;
    private String email;
    private String carTypes;
    private String carNum;    
    private Integer storeNum;
    private Integer auth;
	public MemberDto(Integer seq, String id, String pw, String name, String email, String carTypes, String carNum,
			Integer storeNum, Integer auth) {
		super();
		this.seq = seq;
		this.id = id;
		this.pw = pw;
		this.name = name;
		this.email = email;
		this.carTypes = carTypes;
		this.carNum = carNum;
		this.storeNum = storeNum;
		this.auth = auth;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCarTypes() {
		return carTypes;
	}
	public void setCarTypes(String carTypes) {
		this.carTypes = carTypes;
	}
	public String getCarNum() {
		return carNum;
	}
	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}
	public Integer getStoreNum() {
		return storeNum;
	}
	public void setStoreNum(Integer storeNum) {
		this.storeNum = storeNum;
	}
	public Integer getAuth() {
		return auth;
	}
	public void setAuth(Integer auth) {
		this.auth = auth;
	}


}
