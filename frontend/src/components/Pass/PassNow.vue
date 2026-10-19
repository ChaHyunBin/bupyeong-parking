<template>
  <div class="status-container">
    <!-- 헤더 -->
    <div class="header-section">
      <h1>정기권 발매 현황</h1>
      <p>부평역사쇼핑몰 정기권 발매 현황입니다.</p>
    </div>

    <!-- 1. 현황 요약 카드 (3개) -->
    <div class="summary-cards">
      <div class="card">
        <div class="card-label">총 발매 가능 수량</div>
        <div class="card-value">{{ maxCapacity }}대</div>
      </div>
      <div class="card">
        <div class="card-label">현재 이용 중</div>
        <div class="card-value">{{ totalCount }}대</div>
      </div>
      <div class="card">
        <div class="card-label">잔여 수량</div>
        <div class="card-value">{{ remainingCapacity }}대</div>
      </div>
    </div>

    <!-- 안내 문구 -->
    <div class="info-banner">
      현재 이용 중인 정기권은 승인 완료되고, 만료일이 오늘 이후인 정기권만 포함됩니다.
    </div>

    <!-- 관리자 전용 승인 대기 목록 -->
    <div v-if="isAdmin" class="section-block admin-block">
      <h2>승인 대기 목록 (관리자)</h2>
      <table class="data-table">
        <thead>
          <tr>
            <th>신청번호</th>
            <th>아이디</th>
            <th>차량번호</th>
            <th>소유자</th>
            <th>구분</th>
            <th>신청기간</th>
            <th>처리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="pendingList.length === 0">
            <td colspan="7">승인 대기 중인 신청 내역이 없습니다.</td>
          </tr>
          <tr v-for="item in pendingList" :key="item.regId">
            <td>{{ item.regId }}</td>
            <td>{{ item.userId }}</td>
            <td><strong>{{ item.carNum }}</strong></td>
            <td>{{ item.ownerName }}</td>
            <td>{{ item.type }}</td>
            <td>{{ formatDate(item.startDate) }} ~ {{ formatDate(item.endDate) }}</td>
            <td class="admin-actions">
              <button class="btn-approve" @click="handleUpdateStatus(item.regId, 'approved')">승인</button>
              <button class="btn-reject" @click="handleUpdateStatus(item.regId, 'rejected')">거절</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 발매 정보 테이블 -->
    <div class="section-block">
      <h2>발매 정보</h2>
      <div class="info-table">
        <div class="info-row">
          <div class="info-label">발매 기간</div>
          <div class="info-content">2026.08.01 (토) 10:00 ~ 2026.08.31 (월) 17:00</div>
        </div>
        <div class="info-row">
          <div class="info-label">이용 기간</div>
          <div class="info-content">2026.09.01 (화) ~ 2026.09.30 (수)</div>
        </div>
        <div class="info-row">
          <div class="info-label">이용 요금</div>
          <div class="info-content">일반고객: 100,000원/월 | 점주: 50,000원/월</div>
        </div>
        <div class="info-row">
          <div class="info-label">정기권 종류</div>
          <div class="info-content">일반 정기권 (MONTHLY) | 점주 정기권 (STORE_OWNER)</div>
        </div>
      </div>
    </div>

    <!-- 전체 이용 중인 정기권 목록 (관리자 전용) -->
    <div v-if="isAdmin" class="section-block">
      <h2>현재 이용 중인 정기권 목록 (관리자 전용)</h2>
      <table class="data-table">
        <thead>
          <tr>
            <th>번호</th>
            <th>차량번호</th>
            <th>소유자</th>
            <th>구분</th>
            <th>시작일</th>
            <th>종료일</th>
            <th>상태</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="visibleList.length === 0">
            <td colspan="7">현재 등록되어 이용 중인 정기권이 없습니다.</td>
          </tr>
          <tr v-for="(item, index) in visibleList" :key="item.regId || index">
            <td>{{ index + 1 }}</td>
            <td><strong>{{ item.carNum }}</strong></td>
            <td>{{ item.ownerName }}</td>
            <td>{{ item.type }}</td>
            <td>{{ formatDate(item.startDate) }}</td>
            <td>{{ formatDate(item.endDate) }}</td>
            <td>
              <span :class="['status-tag', item.status?.toUpperCase()]">
                {{ formatStatus(item.status) }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="hasMore" class="more-btn-area">
        <button @click="loadMore">더보기 ∨</button>
      </div>
    </div>

    <!-- 하단 안내사항 -->
    <div class="notice-box">
      <ul>
        <li>승인된 정기권만 집계 및 표시됩니다.</li>
        <li>만료된 정기권은 제외됩니다.</li>
        <li>신청 현황 및 결과는 내 현황 상단에서 확인하실 수 있습니다.</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8282';

// 총 발매 가능 수량: 층당 12면 × 3층 = 36대
const maxCapacity = ref(36);
const prepaidList = ref([]);
const totalCount = ref(0);
const displayLimit = ref(5);

const isLoggedIn = ref(false);

const loginUser = ref(null);
const isAdmin = computed(() => loginUser.value?.auth === 1 || loginUser.value?.auth === '1');

const pendingList = ref([]);

// 잔여 수량 계산 (총 발매 가능 수량 36대 - 현재 이용 중인 대수)
const remainingCapacity = computed(() => {
  const rem = maxCapacity.value - totalCount.value;
  return rem > 0 ? rem : 0;
});

const visibleList = computed(() => {
  return prepaidList.value.slice(0, displayLimit.value);
});

const hasMore = computed(() => {
  return displayLimit.value < prepaidList.value.length;
});

const loadMore = () => {
  displayLimit.value += 5;
};

const formatDate = (dateStr) => {
  if (!dateStr) return '-';
  return String(dateStr).substring(0, 10);
};

const formatStatus = (status) => {
  if (!status) return '-';
  const upper = status.toUpperCase();
  if (upper === 'APPROVED') return '승인완료';
  if (upper === 'REQUESTED') return '승인대기';
  if (upper === 'REJECTED') return '승인거절';
  return status;
};

const checkLoginStatus = async () => {
  const saved = sessionStorage.getItem('login') || localStorage.getItem('loginUser') || localStorage.getItem('login');
  if (!saved) return;

  try {
    const loginObj = JSON.parse(saved);
    loginUser.value = loginObj;
    isLoggedIn.value = true;

    if (loginObj.auth === 1 || loginObj.auth === '1') {
      await fetchPendingList();
    }
  } catch (e) {
    console.error('로그인 정보 파싱 실패:', e);
  }
};

const fetchStatusData = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/prepaid/status`);

    if (response.data) {
      if (Array.isArray(response.data)) {
        prepaidList.value = response.data;
        totalCount.value = response.data.length;
      } else {
        prepaidList.value = response.data.list || [];
        totalCount.value = response.data.totalCount ?? response.data.count ?? prepaidList.value.length;
      }
    }
  } catch (error) {
    console.error('전체 정기권 조회 실패:', error);
  }
};

const fetchPendingList = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/prepaid/pending`);
    pendingList.value = Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error('승인 대기 목록 조회 실패:', error);
  }
};

const handleUpdateStatus = async (regId, status) => {
  const label = status === 'approved' ? '승인' : '거절';
  if (!confirm(`이 신청을 ${label} 처리하시겠습니까?`)) return;

  try {
    const response = await axios.post(`${API_BASE_URL}/prepaid/updateStatus`, {
      regId,
      status
    });

    if (response.data.status === 'success') {
      alert(`${label} 처리되었습니다.`);
      await fetchPendingList();
      await fetchStatusData();
    } else {
      alert(response.data.message || '처리에 실패했습니다.');
    }
  } catch (error) {
    console.error('상태 변경 실패:', error);
    alert('서버 통신 중 오류가 발생했습니다.');
  }
};

onMounted(() => {
  checkLoginStatus();
  fetchStatusData();
});
</script>

<style scoped src="@/components/Css/PassNow.styles.css"></style>