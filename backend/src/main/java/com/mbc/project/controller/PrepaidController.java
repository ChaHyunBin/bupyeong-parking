package com.mbc.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mbc.project.dto.PrepaidDto;
import com.mbc.project.service.PrepaidService;
import com.mbc.project.service.PrepaidService.PrepaidApplyResult;

@RestController
@RequestMapping("/prepaid")
public class PrepaidController {

    private final PrepaidService service;
    public PrepaidController(PrepaidService service) {
        this.service = service;
    }

    //정기권 상태 확인
    @GetMapping("/status")
    public Map<String, Object> getPrepaidStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("list", service.getPrepaidList());
        result.put("totalCount", service.getPrepaidCount());
        return result;
    }

    //내 정기권 정보 확인
    @GetMapping("/my-status")
    public List<PrepaidDto> getMyPrepaidStatus(@RequestParam(value = "userId", required = false) String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return List.of();
        }
        return service.getMyPrepaidList(userId);
    }

    @PostMapping("/apply")
    public Map<String, Object> applyPrepaid(@RequestBody PrepaidDto dto) {
        Map<String, Object> result = new HashMap<>();
        if (dto.getUserId() == null || dto.getUserId().trim().isEmpty()) {
            result.put("status", "fail");
            result.put("message", "로그인 회원 정보(userId)가 누락되었습니다.");
            return result;
        }

        PrepaidApplyResult applyResult = service.applyPrepaid(dto);

        if (applyResult.isSuccess()) {
            result.put("status", "success");
            result.put("message", "정기권 신청이 성공적으로 접수되었습니다.");
            return result;
        }

        String message;
        switch (applyResult.getBlockReason()) {
            case "ALREADY_REQUESTED":
                message = "이미 정기권을 신청하셨습니다.";
                break;
            case "CURRENTLY_PARKED":
                message = "현재 주차중인 차량은 정기권을 신청할 수 없습니다. 출차 이후 다시 신청해 주세요.";
                break;
            case "MEMBER_NOT_FOUND":
                message = "회원 정보를 확인할 수 없습니다. 다시 로그인 후 시도해 주세요.";
                break;
            default:
                message = "신청에 실패했습니다. (회원 ID 및 권한을 확인하세요.)";
        }

        result.put("status", "fail");
        result.put("message", message);
        return result;
    }

    // 관리자용 승인 대기 목록 조회
    @GetMapping("/pending")
    public List<PrepaidDto> getPendingList() {
        return service.getPendingList();
    }

    // 관리자 승인/거절 처리
    @PostMapping("/updateStatus")
    public Map<String, Object> updateStatus(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        Integer regId = (Integer) body.get("regId");
        String status = (String) body.get("status");
        if (regId == null || status == null || status.trim().isEmpty()) {
            result.put("status", "fail");
            result.put("message", "regId 또는 status 값이 누락되었습니다.");
            return result;
        }
        boolean isSuccess = service.updateStatus(regId, status);
        if (isSuccess) {
            result.put("status", "success");
            result.put("message", "처리되었습니다.");
        } else {
            result.put("status", "fail");
            result.put("message", "처리에 실패했습니다.");
        }
        return result;
    }
}