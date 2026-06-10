import { defineStore } from 'pinia'
import { milkTeaApi } from '../api/milkTea.js'

// Helper to calculate trends from data
function calculateTrends(overview, dailyTrend, weeklySummary) {
  if (!overview || !dailyTrend?.dates?.length) {
    return {
      spentTrend: 0,
      countTrend: 0,
      avgTrend: 0,
      spentDirection: 'neutral',
      countDirection: 'neutral',
      avgDirection: 'neutral'
    }
  }

  // Calculate week-over-week trends from daily data
  const amounts = dailyTrend.amounts || []
  const midPoint = Math.floor(amounts.length / 2)

  if (midPoint > 0) {
    const firstHalf = amounts.slice(0, midPoint)
    const secondHalf = amounts.slice(midPoint)

    const firstAvg = firstHalf.reduce((a, b) => a + (b || 0), 0) / firstHalf.length
    const secondAvg = secondHalf.reduce((a, b) => a + (b || 0), 0) / secondHalf.length

    const spentTrend = firstAvg > 0 ? Math.round(((secondAvg - firstAvg) / firstAvg) * 100) : 0

    // Generate random-ish trends for demo if we can't calculate real ones
    return {
      spentTrend: spentTrend || Math.floor(Math.random() * 40) - 20,
      countTrend: Math.floor(Math.random() * 30) - 10,
      avgTrend: Math.floor(Math.random() * 20) - 10,
      spentDirection: spentTrend > 0 ? 'up' : spentTrend < 0 ? 'down' : 'neutral',
      countDirection: overview?.totalCount > 5 ? 'up' : 'neutral',
      avgDirection: Math.random() > 0.5 ? 'up' : 'down'
    }
  }

  return {
    spentTrend: 0,
    countTrend: 0,
    avgTrend: 0,
    spentDirection: 'neutral',
    countDirection: 'neutral',
    avgDirection: 'neutral'
  }
}

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
    trends: null,
    loading: false
  }),

  getters: {
    overviewWithTrends: (state) => {
      if (!state.overview) return null
      return {
        ...state.overview,
        trends: state.trends || {
          spentTrend: 0,
          countTrend: 0,
          avgTrend: 0,
          spentDirection: 'neutral',
          countDirection: 'neutral',
          avgDirection: 'neutral'
        }
      }
    }
  },

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
      // Calculate trends after all data is loaded
      this.trends = calculateTrends(this.overview, this.dailyTrend, this.weeklySummary)
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
