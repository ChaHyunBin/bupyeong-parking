<template>
  <div>
    <!-- 주차장 기본 정보 카드 -->
    <div class="parking-card">
      <div class="parking-img">
        <img
          src="@/assets/Bu_parking.png"
          alt="부평역사쇼핑몰"
        />
      </div>

      <div class="parking-info">
        <div class="parking-header">
          <h3>부평역사쇼핑몰</h3>
          <button class="detail-btn" type="button" @click="openModal">
            상세보기
          </button>
        </div>

        <p>인천광역시 부평구 광장로 16</p>

        <div>
          <span class="time">운영시간</span>
          <span>24시간 연중무휴</span>
        </div>
      </div>
    </div>

    <!-- 이미지 확대 모달 (Teleport를 사용해 body 최상단으로 레이어 이동) -->
    <teleport to="body">
      <div class="modal-overlay" v-if="showModal" @click.self="closeModal">
        <div class="modal-box">
          <button type="button" class="close-btn" @click="closeModal">✕</button>
          <img
            src="@/assets/Bu_parking.png"
            alt="부평역사쇼핑몰 상세보기"
            class="modal-img"
          />
        </div>
      </div>
    </teleport>

    <!-- 정기권 신청 안내 정보 -->
    <div class="apply-info">
      <h2>정기권 신청 안내</h2>

      <!-- 요금 및 기간 테이블 -->
      <table class="info-box">
        <tbody>
          <tr>
            <th>주차장</th>
            <td>부평역사쇼핑몰</td>
          </tr>
          <tr>
            <th>정기요금</th>
            <td>일반고객: 100,000원 / 월&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;점주: 50,000원 / 월</td>
          </tr>
          <tr>
            <th>신청기간</th>
            <td>2026.08.01 ~ 2026.08.31</td>
          </tr>
        </tbody>
      </table>

      <!-- 유의사항 목록 -->
      <div class="notice-box">
        <h3>중요 알림사항</h3>

        <ul class="notice-list">
          <li><strong>정기권 신청</strong></li>
          <li>- 매월 31일 오전 10시 당첨 결과를 마이페이지에서 확인하시기 바랍니다.</li>
          <li>- 당첨 여부는 문자로 발송되지 않습니다.</li>
          <li>- 차량번호가 맞는지 반드시 확인하여 주십시오.</li>
          <li>- 감면 대상은 증빙서류를 추가 제출해야 합니다.</li>
          <li>- 결제기간 내 미결제 시 자동 취소됩니다.</li>
          <li>- 예비 당첨자는 순차적으로 추가 선정됩니다.</li>
        </ul>
      </div>

      <!-- 비로그인 상태 안내 박스 -->
      <div class="notice-login" v-if="!isLoggedIn">
        <h3>안내</h3>
        <p>정기권 신청을 하기 위해서는 로그인이 필요합니다.</p>
      </div>

      <!-- 신청 페이지 이동 버튼 -->
      <div class="btn-area">
        <button @click="goapply" class="btn-apply" type="button">
          정기권 신청
        </button>
      </div>
      <div class="pass-steps">
        <div><b>01</b><span>로그인</span></div><i>›</i>
        <div><b>02</b><span>차량 확인</span></div><i>›</i>
        <div><b>03</b><span>정기권 신청</span></div><i>›</i>
        <div><b>04</b><span>승인 후 이용</span></div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      isLoggedIn: false, // 로그인 여부 플래그
      showModal: false   // 이미지 모달 표시 여부
    };
  },
  mounted() {
    // 컴포넌트 마운트 시 세션 스토리지에서 로그인 정보 확인
    const login = JSON.parse(sessionStorage.getItem("login"));
    if (login && login.id) {
      this.isLoggedIn = true;
    }
  },
  methods: {
    // 모달 제어 메서드
    openModal() {
      this.showModal = true;
    },
    closeModal() {
      this.showModal = false;
    },
    // 정기권 신청 버튼 클릭 핸들러 (로그인 여부 검증)
    goapply() {
      const login = JSON.parse(sessionStorage.getItem("login"));

      if (!login || !login.id) {
        alert("로그인이 필요한 서비스입니다.\n로그인 페이지로 이동합니다.");
        this.$router.push("/login");
        return;
      }

      this.$router.push("/passapply");
    }
  }
};
</script>

<style scoped src="@/components/Css/Pass.styles.css"></style>
