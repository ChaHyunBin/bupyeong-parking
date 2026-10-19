<template>
  <div class="form-container">
    <!-- 1. 신청자 ID 영역 (로그인 상태일 때만 표시) -->
    <div class="user-info-bar" v-if="userId">
      <span class="info-label">신청자 ID</span>
      <span class="info-value">{{ userId }}</span>
    </div>

    <!-- 2. 차량번호 입력 영역 (회원정보 등록 여부에 따라 자동 입력/잠금) -->
    <div class="form-group border-box">
      <label class="box-label">차량번호</label>
      <input
        type="text"
        v-model="carNum"
        placeholder="차량번호를 입력해주세요 (예: 12가3456)"
        :disabled="hasRegisteredCar"
      >
      <div class="helper-text">
        {{ hasRegisteredCar ? '회원 정보에 등록된 차량번호로 자동 신청됩니다.' : '등록된 차량번호가 없어 직접 입력이 필요합니다.' }}
      </div>
    </div>

    <!-- 3. 소유자/매장명 입력 영역 -->
    <div class="form-group border-box">
      <label class="box-label">소유자 / 매장명</label>
      <input
        type="text"
        v-model="ownerName"
        placeholder="이름 또는 매장명을 입력해주세요"
        :disabled="hasRegisteredName"
      >
      <div class="helper-text">
        {{ hasRegisteredName ? '회원 정보의 이름으로 자동 등록됩니다.' : '등록된 이름 정보가 없어 직접 입력이 필요합니다.' }}
      </div>
    </div>

    <!-- 4. 이용 기간 선택 영역 (시작일 변경 시 만료일 자동 계산) -->
    <div class="form-group border-box date-area">
      <!-- 시작일 선택 (오늘 이후 날짜만 가능) -->
      <div class="date-box">
        <label class="box-label">시작일</label>
        <input
          type="date"
          v-model="startDate"
          :min="today"
          @change="setEndDate"
        >
      </div>

      <!-- 만료일 표시 (시작일 기준 +1달 자동 지정, 읽기 전용) -->
      <div class="date-box">
        <label class="box-label">만료일</label>
        <input
          type="date"
          v-model="endDate"
          readonly
        >
      </div>
    </div>

    <!-- 5. 이용 안내사항 -->
    <div class="notice-area">
      <h3>안내사항</h3>
      <p>• 일반 정기권: 100,000원 / 점주 정기권: 50,000원 (회원 권한에 따라 자동 적용)</p>
      <p>• 등록된 차량 1대에만 적용되며, 타인 양도 및 허위정보 입력 시 이용이 제한됩니다.</p>
      <p>• 정기권은 선택하신 시작일부터 적용되며 만료일 다음 날 자동으로 종료됩니다.</p>
      <p>• 차량 변경이나 중도 해지 문의는 주차 관리사무소로 연락해 주세요.</p>
    </div>

    <!-- 6. 약관 동의 체크박스 -->
    <div>
      <label class="agree-label">
        <input type="checkbox" v-model="agreeNotice">
        <span>위 안내사항을 모두 확인하였으며 이에 동의합니다.</span>
      </label>
    </div>

    <!-- 7. 신청 제출 버튼 -->
    <div class="btn-area">
      <button
        class="btn-apply"
        @click="applyPass"
      >
        정기권 신청
      </button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      // 폼 입력 데이터 데이터 상태 변수
      userId: '',
      carNum: '',
      ownerName: '',
      startDate: '',
      endDate: '',
      agreeNotice: false,
      
      // 날짜 및 자동입력 제어 변수
      today: new Date().toISOString().slice(0, 10), // 오늘 날짜 (YYYY-MM-DD)
      hasRegisteredCar: false,  // DB/세션에 차량번호 존재 여부
      hasRegisteredName: false  // DB/세션에 사용자 이름 존재 여부
    };
  },
  
  // 컴포넌트 생성 시 세션에서 로그인 사용자 정보 불러오기
  mounted() {
    const login = JSON.parse(sessionStorage.getItem("login"));
    if (login) {
      this.userId = login.id;
      this.carNum = login.carNum || '';
      this.ownerName = login.name || '';

      // 등록된 값이 있는 경우에만 input 수정 불가(:disabled) 처리
      this.hasRegisteredCar = !!login.carNum;
      this.hasRegisteredName = !!login.name;
    }
  },
  
  methods: {
    // [기능] 시작일 입력 시 만료일(+1달) 자동 계산
    setEndDate() {
      if (!this.startDate) return;

      const [year, month, day] = this.startDate.split('-').map(Number);
      const date = new Date(year, month - 1, day);

      // 1개월 추가
      date.setMonth(date.getMonth() + 1);

      // YYYY-MM-DD 형식으로 포맷팅
      const yyyy = date.getFullYear();
      const mm = String(date.getMonth() + 1).padStart(2, '0');
      const dd = String(date.getDate()).padStart(2, '0');

      this.endDate = `${yyyy}-${mm}-${dd}`;
    },

    // [기능] 정기권 신청 유효성 검사 및 서버(Axios) 전송
    applyPass() {
      // 1. 필수 입력값 및 약관 동의 검증 (Validation)
      if (!this.userId) {
        alert("로그인 정보가 없습니다.");
        return;
      }
      if (!this.carNum.trim()) {
        alert("차량번호를 입력해주세요.");
        return;
      }
      if (!this.ownerName.trim()) {
        alert("소유자 / 매장명을 입력해주세요.");
        return;
      }
      if (!this.startDate) {
        alert("시작일을 선택해주세요.");
        return;
      }
      if (!this.agreeNotice) {
        alert("안내사항을 확인하고 동의에 체크해주세요.");
        return;
      }

      // 2. 백엔드로 보낼 요청 데이터 구성
      const requestData = {
        userId: this.userId,
        carNum: this.carNum,
        ownerName: this.ownerName,
        startDate: this.startDate,
        endDate: this.endDate
      };

      // 3. 백엔드 API 호출
      axios.post("http://localhost:8282/prepaid/apply", requestData)
        .then(resp => {
          if (resp.data.status === 'success') {
            alert(resp.data.message);
            this.$router.push('/pass'); // 성공 시 정기권 메인/안내 페이지로 이동
          } else {
            alert(resp.data.message || "정기권 신청에 실패했습니다.");
          }
        })
        .catch(err => {
          console.error("정기권 신청 오류:", err);
          alert("서버 통신 중 오류가 발생했습니다.");
        });
    }
  }
};
</script>

<style scoped src="@/components/Css/PassApply.styles.css"></style>