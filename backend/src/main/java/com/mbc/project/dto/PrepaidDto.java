package com.mbc.project.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PrepaidDto implements Serializable{
	private Integer regId;       // reg_id (PK, 정기권 고유 ID)
    private String userId;       // user_id (회원 ID)
    private String carNum;       // car_num (차량 번호)
    private String ownerName;    // owner_name (소유자 이름 또는 매장명)

    // Vue/React 등 프론트엔드와 JSON 통신 시 yyyy-MM-dd 포맷으로 전달되도록 설정
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date startDate;      // start_date (정기권 시작 날짜)

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date endDate;        // end_date (정기권 만료 날짜)

    private String type;         // type (예: STORE_OWNER, MONTHLY)
    private String status;       // status (예: REQUESTED, APPROVED, REJECTED, EXPIRED)
    private Integer price;
    // 날짜와 시/분/초까지 포함하는 등록일
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date createdAt;      // created_at (신청/등록 일시)

    // 기본 생성자
    public PrepaidDto() {}

    // Getter & Setter
    public Integer getRegId() {
        return regId;
    }

    public void setRegId(Integer regId) {
        this.regId = regId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Integer getPrice() { return price; }
    
    public void setPrice(Integer price) { this.price = price; }
    
    
}