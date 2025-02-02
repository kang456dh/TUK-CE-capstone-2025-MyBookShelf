import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import axios from './axios';

// Axios 기본 설정
axios.defaults.baseURL = 'http://localhost:8081/'; // Spring Boot 서버 주소

// Vue 애플리케이션 생성
const app = createApp(App);

// Axios를 전역 프로퍼티로 추가
app.config.globalProperties.$axios = axios;

// Vue Router 사용
app.use(router);

// 애플리케이션 마운트
app.mount('#app');