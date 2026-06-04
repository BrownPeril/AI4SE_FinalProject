<template>
  <n-spin :show="loading">
    <OverviewCards :overview="store.overview" style="margin-bottom: 24px;" />
    <n-grid :cols="2" :x-gap="16" style="margin-bottom: 16px;">
      <n-gi>
        <ChartPanel title="每日消费趋势" type="line" :data="store.dailyTrend" />
      </n-gi>
      <n-gi>
        <ChartPanel title="品牌消费分布" type="pie" :data="store.brandDistribution" />
      </n-gi>
    </n-grid>
    <ChartPanel title="每周消费汇总" type="bar" :data="store.weeklySummary" />
    <n-result v-if="errorMsg" status="error" :title="'加载失败'" :description="errorMsg" style="margin-top: 24px;" />
  </n-spin>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useMilkTeaStore } from '../stores/milkTea.js'
import OverviewCards from '../components/OverviewCards.vue'
import ChartPanel from '../components/ChartPanel.vue'

const store = useMilkTeaStore()
const loading = ref(true)
const errorMsg = ref(null)

onMounted(async () => {
  try {
    await store.fetchAllStats()
  } catch (e) {
    errorMsg.value = e.message || '无法加载统计数据，请检查后端服务是否启动'
  } finally {
    loading.value = false
  }
})
</script>