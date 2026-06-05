<template>
  <n-config-provider :theme-overrides="themeOverrides" :locale="zhCN" :date-locale="dateZhCN">
    <n-message-provider>
      <n-layout class="cafe-app">
        <n-layout-header bordered class="cafe-header">
          <div class="cafe-header-content">
            <div class="cafe-logo">
              <span class="cafe-logo-icon">🧋</span>
              <span class="cafe-logo-text">奶茶记录</span>
            </div>
            <n-menu mode="horizontal" :options="menuOptions" :value="activeKey" @update:value="handleMenuSelect" class="cafe-menu" />
          </div>
        </n-layout-header>
        <n-layout-content class="cafe-content">
          <router-view />
        </n-layout-content>
      </n-layout>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { cafeThemeOverrides, cafeCssVars } from './assets/styles/theme.js'
import { zhCN, dateZhCN } from 'naive-ui'

const router = useRouter()
const route = useRoute()

const menuOptions = [
  { label: '记录列表', key: 'records' },
  { label: '消费统计', key: 'statistics' }
]

const activeKey = computed(() => {
  if (route.path.startsWith('/statistics')) return 'statistics'
  return 'records'
})

function handleMenuSelect(key) {
  if (key === 'records') router.push('/records')
  if (key === 'statistics') router.push('/statistics')
}

const themeOverrides = cafeThemeOverrides
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;600;700&family=Noto+Sans+SC:wght@400;500;600&display=swap');

:root {
  --cafe-bg: #fbf6ee;
  --cafe-surface: #fffdf8;
  --cafe-surface-warm: #f1e3cf;
  --cafe-accent: #9b5b32;
  --cafe-accent-hover: #b06a3d;
  --cafe-accent-pressed: #8a502b;
  --cafe-fg: #201914;
  --cafe-fg2: #4c4037;
  --cafe-muted: #7a6d63;
  --cafe-border: #ded2c3;
  --cafe-border-soft: #eee4d7;
  --cafe-success: #4f8a4f;
  --cafe-warn: #c9822f;
  --cafe-danger: #b33a3a;
  --cafe-radius: 10px;
  --cafe-radius-sm: 6px;
  --cafe-shadow-sm: 0 1px 2px rgba(32, 25, 20, 0.06);
  --cafe-shadow-md: 0 4px 12px rgba(32, 25, 20, 0.08);
  --cafe-shadow-lg: 0 8px 24px rgba(32, 25, 20, 0.12);
  --cafe-transition: 0.2s ease;
}

body {
  background-color: var(--cafe-bg);
  margin: 0;
  font-family: 'Noto Sans SC', system-ui, sans-serif;
}

.cafe-app {
  min-height: 100vh;
}

.cafe-header {
  background: linear-gradient(135deg, #fffdf8 0%, #fbf6ee 100%);
  border-bottom: 1px solid var(--cafe-border-soft);
  padding: 0;
  height: 72px;
}

.cafe-header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.cafe-logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.cafe-logo-icon {
  font-size: 28px;
  line-height: 1;
}

.cafe-logo-text {
  font-family: 'Noto Serif SC', serif;
  font-size: 22px;
  font-weight: 700;
  color: var(--cafe-fg);
  letter-spacing: 1px;
}

.cafe-content {
  padding: 32px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  min-height: calc(100vh - 72px);
  box-sizing: border-box;
}

.cafe-menu {
  background: transparent;
  border: none;
  font-weight: 500;
}
</style>
