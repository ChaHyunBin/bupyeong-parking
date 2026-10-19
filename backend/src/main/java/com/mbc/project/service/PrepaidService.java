package com.mbc.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mbc.project.dao.PrepaidDao;
import com.mbc.project.dto.MemberDto;
import com.mbc.project.dto.PrepaidDto;

@Service
public class PrepaidService {

    private final PrepaidDao dao;

    public PrepaidService(PrepaidDao dao) {
        this.dao = dao;
    }

    // 정기권 발매 현황 목록 조회
    public List<PrepaidDto> getPrepaidList() {
        return dao.getPrepaidList();
    }

    // 활성화된 정기권 총 개수
    public int getPrepaidCount() {
        return dao.getPrepaidCount();
    }

    // 로그인 사용자 본인의 정기권 신청/이용 현황 조회
    public List<PrepaidDto> getMyPrepaidList(String userId) {
        return dao.getMyPrepaidList(userId);
    }

    // 정기권 신청 등록
    // - 미승인(REQUESTED) 상태의 기존 신청 이력이 있으면 중복 신청 차단
    // - 신청 차량이 현재 주차중이면 신청 차단
    // - 회원 권한(auth)에 따라 정기권 종류/가격 자동 결정 (일반 100,000원 / 점주 50,000원)
    public PrepaidApplyResult applyPrepaid(PrepaidDto dto) {

        // 1) 중복 신청 체크 (미승인 이력 존재 여부)
        if (dao.hasPendingRequest(dto.getUserId())) {
            return PrepaidApplyResult.blocked("ALREADY_REQUESTED");
        }

        // 2) 현재 주차중 여부 체크
        if (dao.isCarCurrentlyParked(dto.getCarNum())) {
            return PrepaidApplyResult.blocked("CURRENTLY_PARKED");
        }

        // 3) 회원 권한에 따라 정기권 종류/가격 자동 설정
        MemberDto member = dao.getMember(dto.getUserId());
        if (member == null) {
            return PrepaidApplyResult.blocked("MEMBER_NOT_FOUND");
        }

        boolean isOwner = member.getAuth() != null && member.getAuth() == 2; // 2: 상인(점주)
        dto.setType("monthly");
        dto.setPrice(isOwner ? 50000 : 100000);

        int inserted = dao.insertPrepaid(dto);
        return inserted > 0 ? PrepaidApplyResult.success() : PrepaidApplyResult.blocked("INSERT_FAILED");
    }

    // 관리자용: 승인 대기 중인 정기권 목록 조회
    public List<PrepaidDto> getPendingList() {
        return dao.getPendingList();
    }

    // 관리자용: 정기권 상태 변경 (승인/거절)
    public boolean updateStatus(Integer regId, String status) {
        return dao.updateStatus(regId, status) > 0;
    }

    // applyPrepaid 결과를 담는 결과 객체 (성공 여부 + 차단 사유)
    public static class PrepaidApplyResult {
        private final boolean success;
        private final String blockReason; // null이면 성공

        private PrepaidApplyResult(boolean success, String blockReason) {
            this.success = success;
            this.blockReason = blockReason;
        }

        public static PrepaidApplyResult success() {
            return new PrepaidApplyResult(true, null);
        }

        public static PrepaidApplyResult blocked(String reason) {
            return new PrepaidApplyResult(false, reason);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getBlockReason() {
            return blockReason;
        }
    }
}