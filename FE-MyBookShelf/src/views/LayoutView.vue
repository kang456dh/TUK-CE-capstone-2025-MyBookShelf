<template>
    <div>
      <!-- 조건부로 상단 고정바 표시 -->
      <header v-if="showHeader">
        <img src="@/assets/book.jpg" alt="Logo" class="logo" />
        <input type="text" placeholder="검색..." class="search-input" />
        <button @click="goToLogin" class="login-button">로그인 및 회원가입</button>
      </header>
  
      <!-- 메뉴바 -->
      <nav class="navbar" v-if="showHeader">
        <ul>
          <li><router-link to="/category">카테고리</router-link></li>
          <li><router-link to="/community">커뮤니티</router-link></li>
          <li><router-link to="/guidelines">이용안내</router-link></li>
          <li><router-link to="/mybooks">나만의책장</router-link></li>
        </ul>
      </nav>
  
      <!-- 본문 내용 (여기서 내용이 동적으로 바뀐다) -->
      <main>
        <router-view></router-view> <!-- 현재 경로에 맞는 컴포넌트를 이곳에 렌더링 -->
      </main>
    </div>
  </template>
  
  <script>
  export default {
    name: 'LayoutView',
    computed: {
      showHeader() {
        // 로그인 화면일 때는 상단 바를 숨기도록 조건을 설정
        return this.$route.name !== 'Login' && this.$route.name !== 'Signup';
      },
    },
    methods: {
      goToLogin() {
        this.$router.push('/login'); // 로그인 화면으로 이동
      },
    },
  };
  </script>
  
  <style scoped>
  /* 상단 고정바 스타일 */
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
  
  /* 로고 */
  .logo {
    width: 10%;
    height: 50px;
  }
  
  /* 검색창 */
  .search-input {
    padding: 10px 100px;
    font-size: 16px;
    border: 5px solid #FFA500; /* 연한 주황색 테두리 */
    border-radius: 100px;
  }
  
  /* 로그인 및 회원가입 버튼 */
  .login-button {
    padding: 10px 20px;
    font-size: 16px;
    cursor: pointer;
    background-color: #FFA500; /* 연한 주황색 배경 */
    color: white;
    border: none;
    border-radius: 4px;
    margin-right: 40px; /* 오른쪽 벽에서 띄우기 */
    border: 2px solid #FFA500; /* 연한 주황색 테두리 */
  }
  
  /* 메뉴바 스타일 */
  .navbar {
    position: fixed;
    top: 70px; /* 상단 고정바 아래 위치 */
    left: 0;
    width: 100%;
    background-color: #FFA500; /* 메뉴바 배경색을 연한 주황색으로 변경 */
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    z-index: 90;
  }
  
  .navbar ul {
    display: flex;
    justify-content: center;
    margin: 0;
    padding: 25px 0; /* 메뉴 간격을 4배로 띄움 */
  }
  
  .navbar ul li {
    list-style: none;
    margin: 0 80px; /* 메뉴 항목 간의 간격을 4배로 띄움 */
    position: relative;
  }
  
  /* 메뉴 링크 */
  .navbar ul li a {
    text-decoration: none;
    color: #fff; /* 메뉴 텍스트 색을 흰색으로 변경 */
    font-size: 16px;
    padding: 5px 0; /* 텍스트와 메뉴 바깥 경계의 여백을 좀 더 추가 */
  }
  
  /* 본문 내용 */
  main {
    margin-top: 150px; /* 상단 고정바 + 메뉴바 높이만큼 여백 */
    padding: 20px;
  }
  </style>
  