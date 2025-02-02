import axios from 'axios';

axios.defaults.baseURL = 'http://localhost:8081/'; // API 기본 URL 설정
axios.defaults.withCredentials = true; // 쿠키 전송 설정

export default axios;
