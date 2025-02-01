import { createRouter, createWebHistory } from 'vue-router';
import LayoutView from '../layouts/LayoutView.vue'; // 레이아웃 컴포넌트
import HomeView from '../views/HomeView.vue';
import LoginView from '../views/LoginView.vue';
import SignupView from '../views/SignupView.vue';

const routes = [
  {
    path: '/',
    component: LayoutView, // 공통 레이아웃 사용
    children: [
      { path: '', name: 'Home', component: HomeView },
      { path: 'category', name: 'Category', component: () => import('../views/CategoryView.vue') },
      { path: 'community', name: 'Community', component: () => import('../views/CommunityView.vue') },
      { path: 'guidelines', name: 'Guidelines', component: () => import('../views/GuidelinesView.vue') },
      { path: 'mybooks', name: 'MyBooks', component: () => import('../views/MyBooksView.vue') },
    ],
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
    meta: { layout: false }, // 레이아웃 제외
  },
  {
    path: '/signup',
    name: 'Signup',
    component: SignupView,
    meta: { layout: false }, // 레이아웃 제외
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/', // 404 처리: 홈으로 리다이렉트
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
