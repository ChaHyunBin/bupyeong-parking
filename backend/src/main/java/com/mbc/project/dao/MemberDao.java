package com.mbc.project.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mbc.project.dto.MemberDto;

@Mapper
@Repository
public interface MemberDao {
	
	// 전체 조회 (목록용)
    List<MemberDto> allmember();
    // 회원 가입
    int addmember(MemberDto dto);
    // 로그인
    MemberDto login(MemberDto dto); 
    // ID 중복확인
    int idcheck(String id);
    // 아이디 찾기 (이름, 이메일 이용)
    MemberDto findId(@Param("name") String name, @Param("email") String email);
    
    // 상가의 소유주(id)를 admin에서 실제 상인 아이디로 교체
    int updateStoreOwner(@Param("storeNum") Integer storeNum, @Param("id") String id);

}
    

