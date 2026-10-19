<template>
  <div class="signup-container">
    <div class="form-wrapper">
      <div class="logo-box">
        <img src="@/assets/logo.png" alt="로고" />
      </div>

      <div class="center">
        <table border="1" class="table table-hover align-middle">
          <tbody>
            <!-- 회원 구분 선택 -->
            <tr>
              <th>회원 구분</th>
              <td class="user">
                <label class="member-type" for="user">
                  <input type="radio" id="user" value="3" name="role" v-model="auth" />
                  <span>일반 회원</span>
                </label>

                <label class="member-type" for="seller">
                  <input type="radio" id="seller" value="2" name="role" v-model="auth" />
                  <span>상인 회원</span>
                </label>
              </td>
            </tr>

            <!-- 상인 선택 시에만 보이는 칸 -->
            <tr v-if="auth === '2'">
              <th>점포 선택</th>
              <td>
                <select v-model.number="storeNum" @change="onStoreSelect" class="form-select">
                  <option :value="null" disabled>점포를 선택하세요</option>
                  <option v-for="s in storeList" :key="s.storeNum" :value="s.storeNum">
                    {{ s.storeName }} ({{ s.floor }}층 - {{ s.storeNum }}번)
                  </option>
                </select>
              </td>
            </tr>

            <!-- 아이디 -->
            <tr>
              <th>아이디</th>
              <td>
                
                <input type="text" id="id" size="20" placeholder="아이디" v-model="id" class="form-control" /><br />
                <p id="idcheck" style="font-size: 8px"></p>
                <button type="button" id="id_chk_btn" @click="checkId" class="btn btn-primary">id 확인</button>
              </td>
            </tr>

            <!-- 패스워드 -->
            <tr>
              <th>패스워드</th>
             
              <td><input type="password" size="20" placeholder="비밀번호" v-model="pw" class="form-control" /></td>
            </tr>

            <!-- 패스워드 재확인 -->
            <tr>
              <th>패스워드 재확인</th>
              <td><input type="password" size="20" placeholder="비밀번호 확인" v-model="passwordConfirm" class="form-control" /></td>
            </tr>

            <!-- 이름 -->
            <tr>
              <th>이름</th>
              <td><input type="text" size="20" placeholder="이름" v-model="name" class="form-control" /></td>
            </tr>

            <!-- 이메일 -->
            <tr>
              <th>이메일</th>
              <td><input type="text" size="20" placeholder="asdfg123@naver.com" v-model="email" class="form-control" /></td>
            </tr>

            <!-- 차량번호 등록 여부 -->
            <tr>
              <th>차량번호 등록 여부</th>
              <td>
                 <label for="caruser" class="car-register-option">
                   <input type="checkbox" id="caruser" v-model="car" />
                   <span>차량번호 등록하기</span>
                    </label>
            </td>
            </tr>

            <!-- 차량 등록 시 나타나는 상세 입력 칸 -->
            <template v-if="car">
              <tr class="carnumber">
                <th>차량번호</th>
                <td><input type="text" v-model="carNum" placeholder="123가4567" class="form-control" /></td>
              </tr>
              <tr class="cartype">
                <th>특수차량</th>
                <td>
                  <input type="checkbox" id="pregnant" value="pregnant" v-model="carTypes" />
                  <label for="pregnant">임산부</label>

                  <input type="checkbox" id="ev" value="ev" v-model="carTypes" />
                  <label for="ev">전기차</label>

                  <input type="checkbox" id="disabled" value="disabled" v-model="carTypes" />
                  <label for="disabled">장애인</label>
                </td>
              </tr>
            </template>

            <!-- 회원가입 버튼 -->
            <tr>
              <td colspan="2" align="center">
                <input type="button" value="회원가입" @click="signup"/>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      // 회원 구분: 2는 상인, 3은 일반 회원
      auth: "3",         
      
      // 상인 회원만 입력하는 점포 정보
      storeName: "",     
      //  DB INT 타입에 맞춰 null로 초기화
      storeNum: null,    
      
      // 회원 기본 정보
      id: "",            
      pw: "",            
      passwordConfirm: "", // UI 확인용 (DB 안 들어감)
      name: "",          
      email: "",         
      
      // 4. 차량 등록 정보
      car: false,        // UI 토글용
      carNum: "",        
      carTypes: [],      // UI 다중체크 배열 (전송 시 join으로 묶음)
      isIdChecked: false, // 아이디 중복확인 완료 여부
      storeList: []       // DB 상가 목록
    };
  },
  watch: {
    id() {
      // 아이디가 변경되면 중복 확인 상태 초기화 (QA-06 / QA-54 해결)
      this.isIdChecked = false;
    }
  },
  mounted() {
    this.getStores();
  },
  methods: {
    getStores() {
      axios.get("http://localhost:8282/allstores")
        .then(resp => {
          if (Array.isArray(resp.data)) {
            this.storeList = resp.data;
          }
        })
        .catch(err => {
          console.error("상가 목록 불러오기 실패:", err);
        });
    },
    onStoreSelect() {
      const selected = this.storeList.find(s => s.storeNum === this.storeNum);
      if (selected) {
        this.storeName = selected.storeName;
      }
    },
    checkId() {
      if (!this.id.trim()) {
        alert("아이디를 입력해주세요.");
        return;
      }
      axios.post("http://localhost:8282/idcheck", null, { params: { id: this.id } })
        .then((resp) => {
          if (resp.data === true || resp.data === "true") {
            alert("이미 사용 중인 아이디입니다.");
            this.isIdChecked = false;
          } else {
            alert("사용 가능한 아이디입니다!");
            this.isIdChecked = true;
          }
        })
        .catch((err) => {
          console.error("아이디 중복확인 오류:", err);
          alert("아이디 중복확인 중 오류가 발생했습니다.");
        });
    },

    signup() {
      // 필수 유효성 검사
      if (!this.id.trim()) {
        alert("아이디를 입력해주세요.");
        return;
      }
      if (!this.isIdChecked) {
        alert("아이디 중복확인을 진행해주세요.");
        return;
      }
      if (!this.pw.trim()) {
        alert("비밀번호를 입력해주세요.");
        return;
      }
      if (this.pw !== this.passwordConfirm) {
        alert("비밀번호가 일치하지 않습니다.");
        return;
      }
      if (!this.name.trim()) {
        alert("이름을 입력해주세요.");
        return;
      }

      // 이메일 필수 + 형식 검사 (DB가 email VARCHAR(100) NOT NULL이라 필수로 처리)
      const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!this.email.trim()) {
        alert("이메일을 입력해주세요.");
        return;
      }
      if (!EMAIL_REGEX.test(this.email.trim())) {
        alert("이메일 형식이 올바르지 않습니다. 예) example@naver.com");
        return;
      }

      if (this.auth === '2' && !this.storeNum) {
        alert("점포를 선택해주세요.");
        return;
      }

      // 차량번호 등록을 선택했을 때만 형식 검사 (신형 123가4567 / 구형 12가3456)
      const CAR_NUM_REGEX = /^\d{2,3}[가-힣]\d{4}$/;
      if (this.car && !CAR_NUM_REGEX.test(this.carNum.trim())) {
        alert("차량번호 형식이 올바르지 않습니다. 예) 123가4567");
        return;
      }

      //  백엔드 전송 전 데이터 안전장치 (가비지 데이터 방지)
      if (this.auth !== '2') {
        this.storeName = "";
        this.storeNum = null;
      }
      if (!this.car) {
        this.carNum = "";
        this.carTypes = [];
      }

      // 백엔드 DTO(또는 Map)에 넘길 최종 데이터 조립
      const requestData = {
        auth: parseInt(this.auth), 
        storeName: this.storeName,
        storeNum: this.storeNum,
        id: this.id,
        pw: this.pw,
        name: this.name,
        email: this.email,
        carNum: this.carNum,
        carTypes: this.carTypes.join(",") 
      };

      console.log("서버로 전송될 최종 데이터:", requestData);

      axios.post("http://localhost:8282/addmember", requestData)
        .then(resp => {
          if (resp.data === true || resp.data === "YES") {
            alert("회원가입이 완료되었습니다!");
            this.$router.push('/login');
          } else {
            alert("회원가입에 실패했습니다. 다시 시도해주세요.");
          }
        })
        .catch(err => {
          console.error("회원가입 에러 발생:", err);
          alert("점포가 중복입니다.");
        });
    }
  }
};
</script>






<!-- 새 회원 정보를 입력하고 가입하는 화면입니다. -->