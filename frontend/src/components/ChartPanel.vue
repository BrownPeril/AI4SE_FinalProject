<template>
  <div class="chart-panel">
    <div class="chart-header">
      <div class="chart-title-wrapper">
        <span class="chart-icon">{{ chartIcon }}</span>
        <h3 class="chart-title">{{ title }}</h3>
      </div>
      <div class="chart-meta">
        <span class="chart-range">{{ chartRange }}</span>
      </div>
    </div>

    <div class="chart-content" ref="chartContainer">
      <div v-if="hasData" class="chart-wrapper">
        <v-chart :option="chartOption" :autoresize="true" style="width: 100%; height: 100%;" />
      </div>
      <div v-else class="chart-empty">
        <div class="empty-chart-icon">📊</div>
        <div class="empty-chart-text">暂无数据</div>
        <div class="empty-chart-desc">记录几杯奶茶后即可查看统计</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart, BarChart } from 'echarts/charts'
import {
  TitleComponent, TooltipComponent, LegendComponent,
  GridComponent
} from 'echarts/components'

use([CanvasRenderer, LineChart, PieChart, BarChart,
  TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const props = defineProps({
  title: { type: String, default: '' },
  type: { type: String, required: true },
  data: { type: Object, default: () => ({}) }
})

const chartContainer = ref(null)

const chartIcon = computed(() => {
  if (props.type === 'line') return '📈'
  if (props.type === 'pie') return '🥧'
  if (props.type === 'bar') return '📊'
  return '📈'
})

const chartRange = computed(() => {
  if (props.type === 'line') return '最近30天'
  if (props.type === 'pie') return '累计'
  if (props.type === 'bar') return '最近4周'
  return ''
})

const hasData = computed(() => {
  const data = props.data || {}
  if (props.type === 'line') return (data.amounts || []).some(v => v > 0)
  if (props.type === 'pie') return (data.counts || []).some(v => v > 0)
  if (props.type === 'bar') return (data.amounts || []).some(v => v > 0)
  return false
})

const cafeColors = ['#9b5b32', '#c9822f', '#4f8a4f', '#7a6d63', '#b33a3a', '#4c4037', '#8b7355', '#a67c52']

const chartOption = computed(() => {
  const data = props.data || {}

  if (props.type === 'line') {
    return {
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 253, 248, 0.95)',
        borderColor: '#ded2c3',
        borderWidth: 1,
        textStyle: { color: '#201914' },
        formatter: (params) => {
          const date = params[0].axisValue
          const value = params[0].value
          return `${date}<br/><strong style="color: #9b5b32;">¥${value}</strong>`
        }
      },
      grid: {
        left: 50,
        right: 20,
        top: 20,
        bottom: 40
      },
      xAxis: {
        type: 'category',
        data: data.dates || [],
        axisLine: { lineStyle: { color: '#ded2c3' } },
        axisLabel: { color: '#7a6d63', fontSize: 11, formatter: (value) => value?.slice(5) || '' },
        axisTick: { show: false }
      },
      yAxis: {
        type: 'value',
        axisLine: { show: false },
        axisTick: { show: false },
        splitLine: { lineStyle: { color: '#eee4d7', type: 'dashed' } },
        axisLabel: { color: '#7a6d63', fontSize: 11, formatter: '¥{value}' }
      },
      series: [{
        type: 'line',
        data: data.amounts || [],
        smooth: true,
        lineStyle: { color: '#9b5b32', width: 3 },
        itemStyle: { color: '#9b5b32' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(155, 91, 50, 0.2)' },
              { offset: 1, color: 'rgba(155, 91, 50, 0.02)' }
            ]
          }
        },
        symbol: 'circle',
        symbolSize: 6,
        emphasis: {
          itemStyle: { color: '#9b5b32', borderColor: '#fff', borderWidth: 2 }
        }
      }]
    }
  }

  if (props.type === 'pie') {
    const pieData = (data.brands || []).map((brand, i) => ({
      name: brand,
      value: data.counts?.[i] || 0
    })).filter(item => item.value > 0)

    return {
      tooltip: {
        trigger: 'item',
        backgroundColor: 'rgba(255, 253, 248, 0.95)',
        borderColor: '#ded2c3',
        borderWidth: 1,
        textStyle: { color: '#201914' },
        formatter: '{b}: {c}杯 ({d}%)'
      },
      legend: {
        bottom: 0,
        itemGap: 16,
                textStyle: { color: '#4c4037', fontSize: 12 },
        icon: 'circle'
      },
      series: [{
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['50%', '45%'],
        data: pieData,
        color: cafeColors,
        label: {
          show: true,
          color: '#4c4037',
          fontSize: 12,
          formatter: '{b}\n{c}杯'
        },
        labelLine: {
          lineStyle: { color: '#ded2c3' },
          smooth: true
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.1)'
          }
        }
      }]
    }
  }

  if (props.type === 'bar') {
    return {
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 253, 248, 0.95)',
        borderColor: '#ded2c3',
        borderWidth: 1,
        textStyle: { color: '#201914' },
        formatter: (params) => {
          const week = params[0].axisValue
          const value = params[0].value
          return `${week}<br/><strong style="color: #9b5b32;">¥${value}</strong>`
        }
      },
      grid: {
        left: 50,
        right: 20,
        top: 20,
        bottom: 40
      },
      xAxis: {
        type: 'category',
        data: data.weeks || [],
        axisLine: { lineStyle: { color: '#ded2c3' } },
        axisLabel: { color: '#7a6d63', fontSize: 12 },
        axisTick: { show: false }
      },
      yAxis: {
        type: 'value',
        axisLine: { show: false },
        axisTick: { show: false },
        splitLine: { lineStyle: { color: '#eee4d7', type: 'dashed' } },
        axisLabel: { color: '#7a6d63', fontSize: 11, formatter: '¥{value}' }
      },
      series: [{
        type: 'bar',
        data: data.amounts || [],
        itemStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: '#b06a3d' },
              { offset: 1, color: '#9b5b32' }
            ]
          },
          borderRadius: [6, 6, 0, 0]
        },
        barWidth: '40%',
        emphasis: {
          itemStyle: {
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: '#c47a45' },
                { offset: 1, color: '#9b5b32' }
              ]
            }
          }
        }
      }]
    }
  }

  return {}
})
</script>

<style scoped>
.chart-panel {
  background: var(--cafe-surface);
  border-radius: 14px;
  border: 1px solid var(--cafe-border-soft);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--cafe-border-soft);
  background: linear-gradient(180deg, rgba(251, 246, 238, 0.5) 0%, transparent 100%);
}

.chart-title-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-icon {
  font-size: 18px;
}

.chart-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 16px;
  font-weight: 700;
  color: var(--cafe-fg);
  margin: 0;
}

.chart-meta {
  font-size: 12px;
  color: var(--cafe-muted);
}

.chart-range {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  background: var(--cafe-bg);
  border-radius: 12px;
  font-weight: 500;
}

.chart-content {
  height: 280px;
  padding: 20px;
  box-sizing: border-box;
  position: relative;
}

.chart-wrapper {
  width: 100%;
  height: 100%;
}

.chart-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: 8px;
}

.empty-chart-icon {
  font-size: 40px;
  opacity: 0.6;
}

.empty-chart-text {
  font-family: 'Noto Serif SC', serif;
  font-size: 16px;
  font-weight: 600;
  color: var(--cafe-fg2);
}

.empty-chart-desc {
  font-size: 13px;
  color: var(--cafe-muted);
}
</style>
