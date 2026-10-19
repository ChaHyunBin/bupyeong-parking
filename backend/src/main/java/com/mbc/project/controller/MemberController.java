package com.mbc.project.controller;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mbc.project.dto.MemberDto;
import com.mbc.project.service.MemberService;

@RestController
public class MemberController {
	
	final MemberService service;
	
	MemberController(MemberService service) {
		this.service = service;
	}
	// 전체 회원조회
	@PostMapping("/allmember")
	public List<MemberDto> allmember(){
		System.out.println("MemberController allmember()" + new Date());

		 return service.allMemberDao();
	}
	// 회원가입
	@PostMapping("/addmember")
	public boolean addmember(@RequestBody MemberDto dto) {
		// VUE에서 요청이 오면 JSON데이터를 DTO객체로 변환
		System.out.println("MemberController addmember()" + new Date());
		
		return service.addmember(dto);
	}
	// 로그인
	@PostMapping("/login")
	public MemberDto login(MemberDto dto) {
		// 아이디와 비밀번호를 확인하여 회원 정보를 반환
		System.out.println("MemberController login() " + new Date());
		
		return service.login(dto);
	}
	// 아이디 중복확인
	@PostMapping("/idcheck")
	public boolean idcheck(String id) {
		// 아이디가 중복인지 확인(true/false 반환)
		System.out.println("MemberController idcheck()" + new Date());
		
		return service.idcheck(id);	
	}
	// 아이디 찾기
	@PostMapping("/findid")
	public MemberDto findId(@RequestBody MemberDto dto) {
		System.out.println("MemberController findId() " + new Date());
		return service.findId(dto.getName(), dto.getEmail());
	}
}