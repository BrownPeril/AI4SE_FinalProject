import { defineStore } from 'pinia'
import { milkTeaApi } from '../api/milkTea.js'

export const useMilkTeaStore = defineStore('milkTea', {
  state: () => ({
    records: [],
    totalElements: 0,
    totalPages: 0,
    currentPage: 0,
    pageSize: 10,
    filters: {
      brand: null,
      sugarLevel: null,
      minPrice: null,
      maxPrice: null,
      keyword: null
    },
    overview: null,
    dailyTrend: { dates: [], amounts: [] },
    brandDistribution: { brands: [], counts: [], amounts: [] },
    weeklySummary: { weeks: [], amounts: [], counts: [] },
    loading: false
  }),

  actions: {
    async fetchRecords() {
      this.loading = true
      try {
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          ...this.filters
        }
        const data = await milkTeaApi.listRecords(params)
        this.records = data.content
        this.totalElements = data.totalElements
        this.totalPages = data.totalPages
      } finally {
        this.loading = false
      }
    },

    async createRecord(recordData) {
      await milkTeaApi.createRecord(recordData)
      await this.fetchRecords()
    },

    async updateRecord(id, recordData) {
      await milkTeaApi.updateRecord(id, recordData)
      await this.fetchRecords()
    },

    async deleteRecord(id) {
      await milkTeaApi.deleteRecord(id)
      await this.fetchRecords()
    },

    async fetchOverview() {
      this.overview = await milkTeaApi.getOverview()
    },

    async fetchDailyTrend(days = 30) {
      this.dailyTrend = await milkTeaApi.getDailyTrend(days)
    },

    async fetchBrandDistribution() {
      this.brandDistribution = await milkTeaApi.getBrandDistribution()
    },

    async fetchWeeklySummary(weeks = 4) {
      this.weeklySummary = await milkTeaApi.getWeeklySummary(weeks)
    },

    async fetchAllStats() {
      await Promise.allSettled([
        this.fetchOverview(),
        this.fetchDailyTrend(),
        this.fetchBrandDistribution(),
        this.fetchWeeklySummary()
      ])
    },

    setFilters(filters) {
      this.filters = { ...this.filters, ...filters }
      this.currentPage = 0
    },

    setPage(page) {
      this.currentPage = page
      this.fetchRecords()
    }
  }
})
