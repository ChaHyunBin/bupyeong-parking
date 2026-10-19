<template>
  <div class="parking-guide-wrapper">
    <!-- 상단 헤더 -->
    <header class="page-header">
      <h1 class="page-title">정기권 신청 및 이용안내</h1>
      <p class="page-subtitle">부평역사쇼핑몰 정기권 안내</p>
    </header>

    <!-- 중요 안내 -->
    <div class="notice-box">
      <h2 class="notice-title">⚠ 중요 안내</h2>
      <ul class="notice-list">
        <li>정기권은 타인에게 <strong>양도 및 대여할 수 없으며</strong>, 부정 사용 적발 시 이용 정지 및 주차료를 징수합니다.</li>
        <li><strong>만차 시 우선주차는 보장되지 않습니다.</strong> 주차장 상황에 따라 입차가 지연될 수 있는 점 양해 부탁드립니다.</li>
        <li>중도 해지 시 환불 규정은 관련 주차장 운영 조례 및 규정에 따릅니다.</li>
      </ul>
    </div>

    <!-- 이용안내 / 이용제한 2단 -->
    <div class="two-col-grid">
      <div class="guide-card">
        <h2 class="card-title">이용안내</h2>
        <ul class="guide-list">
          <li>당월 신청(결제)한 정기권은 익월 1일부터 말일까지이며, 해당 주차장에 한하여 이용이 가능합니다.</li>
          <li>정기권 이용 관련 문의는 주차관리사무소로 연락해 주세요.</li>
        </ul>
      </div>

      <div class="guide-card">
        <h2 class="card-title">이용제한</h2>
        <ul class="guide-list">
          <li>정기권 신청 시 관련 서류를 미제출한 경우</li>
          <li>정기권 감면 대상자 중 부정사용 적발 시 주차요금 감액 금액 환수 및 신청 제한</li>
          <li>1인이 1대의 차량을 신청하지 아니한 경우 (중복 신청 및 타인 명의 신청 등)</li>
          <li>주차구획선을 넘어간 차량</li>
        </ul>
      </div>
    </div>

    <!-- 신청 및 이용요금 -->
    <div class="fee-card">
      <h2 class="card-title">신청 및 이용요금</h2>
      <ul class="guide-list">
        <li><strong>신청 기간:</strong> 매월 1일 10시 ~ 28일 17시까지 접수</li>
        <li><strong>이용 요금:</strong> 일반 정기권 100,000원 / 점주 정기권 50,000원</li>
        <li><strong>결과 확인:</strong> 마이페이지에서 결과 및 선정 여부를 확인해 주시기 바랍니다.</li>
      </ul>
    </div>

    <!-- 신청 절차 -->
    <div class="process-card">
      <h2 class="card-title">✔ 정기권 신청 절차</h2>
      <div class="step-flow">
        <div class="step-item">
          <span class="step-num">①</span>
          <span class="step-text">회원가입</span>
        </div>
        <span class="step-arrow">→</span>

        <div class="step-item">
          <span class="step-num">②</span>
          <span class="step-text">로그인</span>
        </div>
        <span class="step-arrow">→</span>

        <div class="step-item">
          <span class="step-num">③</span>
          <span class="step-text">신청</span>
        </div>
        <span class="step-arrow">→</span>

        <div class="step-item">
          <span class="step-num">④</span>
          <span class="step-text">승인</span>
        </div>
        <span class="step-arrow">→</span>

        <div class="step-item">
          <span class="step-num">⑤</span>
          <span class="step-text">결제</span>
        </div>
        <span class="step-arrow">→</span>

        <div class="step-item">
          <span class="step-num">⑥</span>
          <span class="step-text">이용</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped src="@/components/Css/PassInfo.styles.css"></style>