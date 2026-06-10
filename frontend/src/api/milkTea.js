import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.response.use(
  response => {
    const data = response.data
    if (data.code !== 200) {
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return data.data
  },
  error => {
    const message = error.response?.data?.message || error.message || '网络错误'
    return Promise.reject(new Error(message))
  }
)

export const milkTeaApi = {
  listRecords(params) {
    return api.get('/milk-tea-records', { params })
  },
  getRecord(id) {
    return api.get(`/milk-tea-records/${id}`)
  },
  createRecord(data) {
    return api.post('/milk-tea-records', data)
  },
  updateRecord(id, data) {
    return api.put(`/milk-tea-records/${id}`, data)
  },
  deleteRecord(id) {
    return api.delete(`/milk-tea-records/${id}`)
  },
  getOverview() {
    return api.get('/stats/overview')
  },
  getDailyTrend(days = 30) {
    return api.get('/stats/daily-trend', { params: { days } })
  },
  getBrandDistribution() {
    return api.get('/stats/brand-distribution')
  },
  getWeeklySummary(weeks = 4) {
    return api.get('/stats/weekly-summary', { params: { weeks } })
  }
}
