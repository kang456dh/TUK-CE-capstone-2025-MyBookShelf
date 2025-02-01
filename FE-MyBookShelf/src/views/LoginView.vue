<template>
    <div class="form-container">
    <!-- 뒤로가기 버튼 추가 -->
    <button @click="goBack" class="back-button">◁</button>

      <h2>로그인</h2>
      <form>
        <div class="form-group">
          <label for="email">이메일</label>
          <input type="email" id="email" placeholder="이메일 입력" />
        </div>
        <div class="form-group">
          <label for="password">비밀번호</label>
          <input type="password" id="password" placeholder="비밀번호 입력" />
        </div>
        <button type="submit" class="login-button">로그인</button>
        
        <!-- 구분선 아래에 레이블 및 회원가입 버튼 -->
        <div class="separator">
          <hr />
        </div>
        <p class="signup-label">계정이 없으신가요?</p>
        <button @click="goToSignup" class="signup-button">회원가입</button>
      </form>
    </div>
  </template>

<script>
export default {
  name: 'LoginView',
  data() {
    return {
      email: '',
      password: '',
    };
  },
  methods: {
    handleLogin() {
      console.log('로그인 시도:', { email: this.email, password: this.password });
    },
    goToSignup() {
      this.$router.push('/signup'); // 회원가입 화면으로 이동
    },
    goBack() {
        this.$router.push('/'); // HomeView로 이동
    }
  },
};
</script>
  
<style scoped>
  .form-container {
    max-width: 400px;
    margin: 0 auto;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }
  
  h2 {
    text-align: center;
    margin-bottom: 20px;
  }
  
  .form-group {
    margin-bottom: 20px;
  }
  
  label {
    display: block;
    margin-bottom: 8px;
    font-weight: bold;
  }
  
  input {
    width: 100%;
    padding: 10px;
    font-size: 14px;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-sizing: border-box;
  }
  
  .login-button {
    width: 100%;
    padding: 12px;
    font-size: 16px;
    color: #fff;
    background: #28a745;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s;
    margin-bottom: 20px;
  }
  
  .login-button:hover {
    background: #218838;
  }
  
  .separator {
    margin: 20px 0;
  }
  
  .separator hr {
    border: none;
    border-top: 1px solid #ccc;
  }
  
  .signup-label {
    text-align: center;
    margin: 10px 0;
    font-size: 14px;
    color: #666;
  }
  
  .signup-button {
    width: 100%;
    padding: 12px;
    font-size: 16px;
    color: #28a745;
    background: #fff;
    border: 1px solid #28a745;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s, color 0.3s;
  }
  
  .signup-button:hover {
    background: #28a745;
    color: #fff;
  }
  </style>
  