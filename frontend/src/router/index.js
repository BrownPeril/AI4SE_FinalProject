import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/records'
  },
  {
    path: '/records',
    name: 'RecordList',
    component: () => import('../views/RecordListView.vue')
  },
  {
    path: '/records/new',
    name: 'NewRecord',
    component: () => import('../views/RecordFormView.vue')
  },
  {
    path: '/records/:id/edit',
    name: 'EditRecord',
    component: () => import('../views/RecordFormView.vue')
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: () => import('../views/StatisticsView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
