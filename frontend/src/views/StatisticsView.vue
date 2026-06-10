<template>
  <div class="statistics-view">
    <div class="stats-header">
      <div class="stats-title-wrapper">
        <span class="stats-icon">📊</span>
        <h2 class="stats-title">消费统计</h2>
      </div>
      <div class="stats-subtitle">你的奶茶消费洞察</div>
    </div>

    <n-spin :show="loading">
      <div v-if="errorMsg" class="error-state">
        <n-result status="error" title="加载失败" :description="errorMsg">
          <template #footer>
            <n-button type="primary" @click="retry">重试</n-button>
          </template>
        </n-result>
      </div>

      <template v-else>
        <!-- Overview Cards -->
        <div class="stats-section">
          <OverviewCards :overview="store.overviewWithTrends" />
        </div>

        <!-- Charts Grid -->
        <div class="stats-section">
          <div class="charts-grid">
            <div class="chart-item chart-large">
              <ChartPanel title="每日消费趋势" type="line" :data="store.dailyTrend" />
            </div>
            <div class="chart-item">
              <ChartPanel title="品牌消费分布" type="pie" :data="store.brandDistribution" />
            </div>
            <div class="chart-item chart-full">
              <ChartPanel title="每周消费汇总" type="bar" :data="store.weeklySummary" />
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-if="!hasData && !loading" class="stats-empty">
          <div class="empty-state-large">
            <div class="empty-large-icon">🧋</div>
            <h3 class="empty-large-title">还没有数据</h3>
            <p class="empty-large-desc">开始记录你的奶茶消费，即可查看精美的统计图表和消费洞察</p>
            <n-button type="primary" size="large" @click="goToAdd" class="empty-action-btn">
              记录第一杯
            </n-button>
          </div>
        </div>
      </template>
    </n-spin>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMilkTeaStore } from '../stores/milkTea.js'
import OverviewCards from '../components/OverviewCards.vue'
import ChartPanel from '../components/ChartPanel.vue'

const router = useRouter()
const store = useMilkTeaStore()
const loading = ref(true)
const errorMsg = ref(null)

const hasData = computed(() => {
  return store.overview?.totalCount > 0
})

async function loadStats() {
  loading.value = true
  errorMsg.value = null
  try {
    await store.fetchAllStats()
  } catch (e) {
    errorMsg.value = e.message || '无法加载统计数据，请检查后端服务是否启动'
  } finally {
    loading.value = false
  }
}

function retry() {
  loadStats()
}

function goToAdd() {
  router.push('/records/new')
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.statistics-view {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 28px;
}

.stats-title-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
}

.stats-icon {
  font-size: 28px;
}

.stats-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 28px;
  font-weight: 700;
  color: var(--cafe-fg);
  margin: 0;
  letter-spacing: 1px;
}

.stats-subtitle {
  font-size: 15px;
  color: var(--cafe-muted);
}

.stats-section {
  margin-bottom: 24px;
}

.stats-section:last-of-type {
  margin-bottom: 0;
}

.charts-grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 16px;
}

.chart-item {
  min-height: 320px;
}

.chart-large {
  grid-row: span 1;
}

.chart-full {
  grid-column: span 2;
}

.error-state {
  padding: 40px 20px;
}

.stats-empty {
  padding: 60px 20px;
}

.empty-state-large {
  text-align: center;
  max-width: 450px;
  margin: 0 auto;
}

.empty-large-icon {
  font-size: 72px;
  margin-bottom: 20px;
  opacity: 0.8;
}

.empty-large-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 24px;
  font-weight: 700;
  color: var(--cafe-fg);
  margin: 0 0 12px;
}

.empty-large-desc {
  font-size: 15px;
  color: var(--cafe-muted);
  margin: 0 0 28px;
  line-height: 1.6;
}

.empty-action-btn {
  border-radius: 10px;
  height: 44px;
  padding-left: 28px;
  padding-right: 28px;
  font-weight: 600;
}

@media (max-width: 900px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }

  .chart-full {
    grid-column: span 1;
  }
}
</style>
