<template>
  <n-config-provider :theme-overrides="themeOverrides">
    <n-message-provider>
      <n-layout>
        <n-layout-header bordered style="padding: 12px 24px; display: flex; align-items: center; justify-content: space-between;">
          <span style="font-size: 20px; font-weight: bold;">🧋 奶茶记录</span>
          <n-menu mode="horizontal" :options="menuOptions" :value="activeKey" @update:value="handleMenuSelect" />
        </n-layout-header>
        <n-layout-content style="padding: 24px; min-height: calc(100vh - 64px);">
          <router-view />
        </n-layout-content>
      </n-layout>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const menuOptions = [
  { label: '📋 记录列表', key: 'records' },
  { label: '📊 消费统计', key: 'statistics' }
]

const activeKey = computed(() => {
  if (route.path.startsWith('/statistics')) return 'statistics'
  return 'records'
})

function handleMenuSelect(key) {
  if (key === 'records') router.push('/records')
  if (key === 'statistics') router.push('/statistics')
}

const themeOverrides = {
  common: {
    primaryColor: '#9b5b32',
    primaryColorHover: '#b06a3d',
    primaryColorPressed: '#8a502b',
    primaryColorSuppl: '#9b5b32',
    bodyColor: '#fbf6ee',
    cardColor: '#fffdf8',
    textColorBase: '#201914',
    textColor1: '#201914',
    textColor2: '#4c4037',
    textColor3: '#7a6d63',
    successColor: '#4f8a4f',
    warningColor: '#c9822f',
    errorColor: '#b33a3a',
    borderRadius: '10px',
    borderRadiusSmall: '6px'
  }
}
</script>

<style>
body {
  background-color: #fbf6ee;
  margin: 0;
  font-family: Inter, system-ui, sans-serif;
}
</style>
