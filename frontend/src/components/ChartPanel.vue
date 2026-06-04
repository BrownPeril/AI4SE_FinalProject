<template>
  <n-card :title="title">
    <v-chart :option="chartOption" :autoresize="true" style="height: 280px;" />
  </n-card>
</template>

<script setup>
import { computed } from 'vue'
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

const cafeColors = ['#9b5b32', '#c9822f', '#4f8a4f', '#7a6d63', '#b33a3a', '#4c4037']

const chartOption = computed(() => {
  if (props.type === 'line') {
    return {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: props.data.dates || [] },
      yAxis: { type: 'value' },
      series: [{
        type: 'line', data: props.data.amounts || [],
        smooth: true, areaStyle: { opacity: 0.15 },
        lineStyle: { color: '#9b5b32', width: 2 },
        itemStyle: { color: '#9b5b32' }
      }],
      grid: { left: 50, right: 20, top: 20, bottom: 30 }
    }
  }
  if (props.type === 'pie') {
    const pieData = (props.data.brands || []).map((brand, i) => ({
      name: brand,
      value: props.data.counts?.[i] || 0
    }))
    return {
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie', radius: ['40%', '70%'],
        data: pieData,
        color: cafeColors,
        label: { color: '#4c4037' }
      }]
    }
  }
  if (props.type === 'bar') {
    return {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: props.data.weeks || [] },
      yAxis: { type: 'value' },
      series: [{
        type: 'bar', data: props.data.amounts || [],
        itemStyle: { color: '#9b5b32', borderRadius: [4, 4, 0, 0] }
      }],
      grid: { left: 50, right: 20, top: 20, bottom: 30 }
    }
  }
  return {}
})
</script>
