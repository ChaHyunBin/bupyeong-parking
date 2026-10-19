package com.mbc.project.service;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mbc.project.dao.MemberDao;
import com.mbc.project.dto.MemberDto;

@Service
@Transactional
public class MemberService {

    // 차량번호 형식: 숫자2~3자리 + 한글1자리 + 숫자4자리 (신형 123가4567 / 구형 12가3456)
    private static final Pattern CAR_NUM_PATTERN = Pattern.compile("^\\d{2,3}[가-힣]\\d{4}$");

    private final MemberDao dao;

    public MemberService(MemberDao dao) {
        this.dao = dao;
    }

    public List<MemberDto> allMemberDao(){
        return dao.allmember();
    }

 // 회원가입
    public boolean addmember(MemberDto dto) {
        // 차량번호는 선택사항 → 값이 있을 때만 형식 검사
        if (dto.getCarNum() != null && !dto.getCarNum().isBlank()
                && !CAR_NUM_PATTERN.matcher(dto.getCarNum()).matches()) {
            return false;
        }
        int count = dao.addmember(dto);

        // 상인 회원가입(auth=2)이고 점포를 선택했다면,
        // 그 점포의 소유주가 아직 admin(미배정)일 때만 새 회원 아이디로 교체
        if (count > 0 && dto.getAuth() != null && dto.getAuth() == 2 && dto.getStoreNum() != null) {
            dao.updateStoreOwner(dto.getStoreNum(), dto.getId());
        }

        return count > 0;
    }

    // 로그인
	public MemberDto login(MemberDto dto) {
        return dao.login(dto);
    }

    // 아이디 중복확인
	public boolean idcheck(String id) {
	    int count = dao.idcheck(id);
	    return count > 0;
	}

    // 아이디 찾기
    public MemberDto findId(String name, String email) {
        return dao.findId(name, email);
    }
}