<template>
  <n-grid :cols="4" :x-gap="16" :y-gap="16">
    <n-gi>
      <n-card style="text-align: center;">
        <div style="color: #7a6d63; font-size: 13px; margin-bottom: 4px;">总消费</div>
        <div style="color: #9b5b32; font-size: 28px; font-weight: bold;">¥{{ overview?.totalSpent || '0.00' }}</div>
      </n-card>
    </n-gi>
    <n-gi>
      <n-card style="text-align: center;">
        <div style="color: #7a6d63; font-size: 13px; margin-bottom: 4px;">总杯数</div>
        <div style="color: #4f8a4f; font-size: 28px; font-weight: bold;">{{ overview?.totalCount || 0 }}</div>
      </n-card>
    </n-gi>
    <n-gi>
      <n-card style="text-align: center;">
        <div style="color: #7a6d63; font-size: 13px; margin-bottom: 4px;">均价</div>
        <div style="color: #4c4037; font-size: 28px; font-weight: bold;">¥{{ overview?.avgPrice || '0.00' }}</div>
      </n-card>
    </n-gi>
    <n-gi>
      <n-card style="text-align: center;">
        <div style="color: #7a6d63; font-size: 13px; margin-bottom: 4px;">最常喝</div>
        <div style="color: #9b5b32; font-size: 28px; font-weight: bold;">{{ overview?.topBrand || '-' }}</div>
      </n-card>
    </n-gi>
  </n-grid>
</template>

<script setup>
defineProps({
  overview: { type: Object, default: null }
})
</script>
