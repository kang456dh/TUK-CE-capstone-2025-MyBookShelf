<template>
    <div class="app-layout">
      <!-- 상단 고정바 -->
      <header>
        <img src="@/assets/logo.png" alt="Logo" class="logo" @click="goToHome" />
        <input type="text" placeholder="검색..." class="search-input" />
        <button @click="goToLogin" class="login-button">로그인 및 회원가입</button>
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
  export default {
    methods: {
      goToHome() {
        this.$router.push('/'); // 루트 경로로 이동
      },
      goToLogin() {
        this.$router.push('/login'); // 로그인 화면으로 이동
      }
    }
  };
  </script>
  
  <style scoped>
  /* 기존 홈 뷰 스타일 상단 고정바와 동일 */
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
    border: 7px solid rgb(188, 110, 0); /* 흰색 테두리 추가 */
    border-radius: 4px; /* 테두리의 모서리를 둥글게 */
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
    transition: color 0.3s ease; /* 색상 변화에 부드러운 전환 효과 추가 */
  }
  
  .navbar ul li a:hover {
    color: #333; /* 마우스를 올렸을 때 글자 색상을 변경 (어두운 색) */
  }

  main {
    margin-top: 80px;
    padding: 20px;
  }
  </style>
  