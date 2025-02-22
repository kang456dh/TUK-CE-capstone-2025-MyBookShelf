<template>
  <div class="app-layout">
    <!-- 상단 고정바 -->
    <header>
      <img src="@/assets/logo.png" alt="Logo" class="logo" @click="goToHome" />
      <div class="search-container">
        <div class="search-box">
          <input
            type="text"
            v-model="searchQuery"
            @keyup.enter="performSearch"
            placeholder="검색..."
            class="search-input"
          />
          <button @click="performSearch" class="search-button">
            검색
          </button>
        </div>
      </div>

      <!-- 로그인 상태에 따라 버튼 변경 -->
      <div v-if="isLoggedIn" class="user-info">
        <span class="nickname">{{ nickname }} 님</span>
        <button @click="handleLogout" class="logout-button">로그아웃</button>
      </div>
      <button v-else @click="goToLogin" class="login-button">로그인 및 회원가입</button>
    </header>

    <!-- 메뉴바 -->
    <nav class="navbar">
      <ul>
        <li><router-link to="/category">카테고리</router-link></li>
        <li><router-link to="/community">커뮤니티</router-link></li>
        <li><router-link to="/guidelines">이용안내</router-link></li>
        <li><router-link to="/mybooks">나만의책장</router-link></li>
      </ul>
    </nav>

    <!-- 화면별 콘텐츠 -->
    <main>
      <router-view />
    </main>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      isLoggedIn: false, // 로그인 상태
      nickname: '', // 닉네임 저장
      searchQuery: '' // 검색 쿼리
    };
  },
  async mounted() {
    await this.checkLoginStatus(); // 페이지 로드 시 로그인 상태 확인
    window.addEventListener('storage', this.syncLoginStatus); // 다른 탭에서 로그인 상태 변경 감지
  },
  beforeUnmount() {
    window.removeEventListener('storage', this.syncLoginStatus);
  },
  watch: {
    '$route'() {
      this.checkLoginStatus(); // 페이지 이동 시 로그인 상태 확인
    }
  },
  methods: {
    goToHome() {
      this.$router.push('/');
    },
    goToLogin() {
      this.$router.push('/login');
    },
    async handleLogout() {
      try {
        await axios.post('/api/user/logout'); // 로그아웃 요청
        alert('로그아웃 되었습니다.');

        // localStorage에서 로그인 정보 삭제
        localStorage.removeItem('user');

        // 로그인 상태 초기화
        this.isLoggedIn = false;
        this.nickname = '';

        // 모든 탭에 로그아웃 반영
        window.dispatchEvent(new Event('storage'));

        // 로그인 페이지로 이동
        this.$router.push('/login');
      } catch (error) {
        console.error('로그아웃 실패', error);
      }
    },
    async checkLoginStatus() {
  try {
    const response = await axios.get('/api/user/info');
    console.log('서버 응답:', response.data); // 응답 로그 추가

    // 서버 응답에서 isSuccess가 true일 때만 로그인 상태를 업데이트
    if (response.data.isSuccess) {
      this.isLoggedIn = true;
      this.nickname = response.data.result.nickname; // `result`에서 닉네임 가져오기
      console.log('로그인된 사용자:', response.data.result);
    } else {
      console.log('로그인되지 않음');
      this.isLoggedIn = false;
      this.nickname = '';
    }
  } catch (error) {
    console.error('로그인 상태 확인 실패', error);
    this.isLoggedIn = false;
    this.nickname = '';
  }
},
    syncLoginStatus() {
      this.checkLoginStatus(); // localStorage 변경 감지 시 로그인 상태 확인
    },
    performSearch() {
      // 검색 수행 로직 추가
      console.log('검색:', this.searchQuery);
    }
  },
};
</script>

<style scoped>
header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background-color: white;
  padding: 10px 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.logo {
  width: 15%;
  height: 50px;
  cursor: pointer;
  border: 7px solid rgb(188, 110, 0);
  border-radius: 4px;
}

.search-input {
  padding: 10px 100px;
  font-size: 16px;
  border: 5px solid #FFA500;
  border-radius: 100px;
}

.login-button {
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  background-color: #FFA500;
  color: white;
  border: none;
  border-radius: 4px;
  margin-right: 40px;
  border: 2px solid #FFA500;
}

.navbar {
  position: fixed;
  top: 70px;
  left: 0;
  width: 100%;
  background-color: #FFA500;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 90;
}

.navbar ul {
  display: flex;
  justify-content: center;
  margin: 0;
  padding: 25px 0;
}

.navbar ul li {
  list-style: none;
  margin: 0 80px;
  position: relative;
}

.navbar ul li a {
  text-decoration: none;
  color: #fff;
  font-size: 16px;
  padding: 5px 0;
  transition: color 0.3s ease;
}

.navbar ul li a:hover {
  color: #333;
}

main {
  margin-top: 80px;
  padding: 20px;
}

.logout-button {
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  background-color: red;
  color: white;
  border: none;
  border-radius: 4px;
  margin-right: 40px;
  border: 2px solid red;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-right: 40px;
}

.nickname {
  font-size: 16px;
  color: #333;
  font-weight: bold;
}
</style>
