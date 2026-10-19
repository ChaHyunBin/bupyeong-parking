package com.mbc.project.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mbc.project.dto.MemberDto;
import com.mbc.project.dto.PrepaidDto;

@Mapper
@Repository
public interface PrepaidDao {

    // 1. 정기권 발매 현황 목록 조회
    List<PrepaidDto> getPrepaidList();

    // 2. 활성화된 정기권 총 개수
    int getPrepaidCount();

    // 3. 로그인 사용자 본인의 정기권 신청/이용 현황 조회
    List<PrepaidDto> getMyPrepaidList(@Param("userId") String userId);

    // 4. 회원 정보 조회 (auth 확인용)
    MemberDto getMember(@Param("id") String id);

    // 5. 정기권 신청 등록
    int insertPrepaid(PrepaidDto dto);

    // 관리자용: 6. 승인 대기 중인 정기권 목록 조회
    List<PrepaidDto> getPendingList();

    // 관리자용: 7. 정기권 상태 변경 (승인/거절)
    int updateStatus(@Param("regId") Integer regId, @Param("status") String status);

    // 8. 특정 회원의 미승인(REQUESTED) 정기권 신청 이력 존재 여부
    boolean hasPendingRequest(@Param("userId") String userId);

    // 9. 특정 차량이 현재 주차중인지 확인
    boolean isCarCurrentlyParked(@Param("carNum") String carNum);
}