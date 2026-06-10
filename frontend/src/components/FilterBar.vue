<template>
  <div class="filter-bar">
    <div class="filter-header">
      <span class="filter-icon">🔍</span>
      <span class="filter-title">筛选</span>
    </div>

    <div class="filter-content">
      <n-space vertical :size="16">
        <div class="filter-item">
          <label class="filter-label">品牌</label>
          <n-select v-model:value="brand" :options="brandOptions" placeholder="选择品牌" clearable />
        </div>

        <div class="filter-item">
          <label class="filter-label">糖度</label>
          <n-select v-model:value="sugarLevel" :options="sugarLevelOptions" placeholder="选择糖度" clearable />
        </div>

        <div class="filter-item">
          <label class="filter-label">价格范围</label>
          <div class="price-range">
            <n-input-number
              v-model:value="minPrice"
              placeholder="最低"
              :min="0"
              clearable
              class="price-input"
            />
            <span class="price-separator">—</span>
            <n-input-number
              v-model:value="maxPrice"
              placeholder="最高"
              :min="0"
              clearable
              class="price-input"
            />
          </div>
        </div>

        <div class="filter-item">
          <label class="filter-label">搜索</label>
          <n-input v-model:value="keyword" placeholder="饮品或品牌..." clearable />
        </div>

        <n-divider style="margin: 8px 0;" />

        <div class="filter-actions">
          <n-button type="primary" block @click="applyFilters" class="filter-apply-btn">
            应用筛选
          </n-button>
          <n-button text block @click="resetFilters" class="filter-reset-btn">
            重置
          </n-button>
        </div>
      </n-space>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['filter'])

const brand = ref(null)
const sugarLevel = ref(null)
const minPrice = ref(null)
const maxPrice = ref(null)
const keyword = ref(null)

const brandOptions = [
  { label: '喜茶', value: '喜茶' },
  { label: '茶百道', value: '茶百道' },
  { label: '蜜雪冰城', value: '蜜雪冰城' },
  { label: '奈雪的茶', value: '奈雪的茶' },
  { label: 'CoCo', value: 'CoCo' },
  { label: '一点点', value: '一点点' }
]

const sugarLevelOptions = [
  { label: '无糖', value: '无糖' },
  { label: '微糖', value: '微糖' },
  { label: '半糖', value: '半糖' },
  { label: '少糖', value: '少糖' },
  { label: '全糖', value: '全糖' }
]

function applyFilters() {
  emit('filter', {
    brand: brand.value,
    sugarLevel: sugarLevel.value,
    minPrice: minPrice.value,
    maxPrice: maxPrice.value,
    keyword: keyword.value
  })
}

function resetFilters() {
  brand.value = null
  sugarLevel.value = null
  minPrice.value = null
  maxPrice.value = null
  keyword.value = null
  emit('filter', {})
}
</script>

<style scoped>
.filter-bar {
  background: var(--cafe-surface);
  border-radius: var(--cafe-radius);
  border: 1px solid var(--cafe-border-soft);
  overflow: hidden;
}

.filter-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 20px;
  background: linear-gradient(135deg, var(--cafe-surface-warm) 0%, var(--cafe-bg) 100%);
  border-bottom: 1px solid var(--cafe-border-soft);
}

.filter-icon {
  font-size: 18px;
}

.filter-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 16px;
  font-weight: 700;
  color: var(--cafe-fg);
  letter-spacing: 1px;
}

.filter-content {
  padding: 20px;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--cafe-fg2);
}

.price-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.price-input {
  flex: 1;
}

.price-separator {
  color: var(--cafe-muted);
  font-size: 14px;
}

.filter-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-apply-btn {
  border-radius: 8px;
  font-weight: 600;
  height: 40px;
  transition: transform var(--cafe-transition), box-shadow var(--cafe-transition);
}

.filter-apply-btn:hover {
  transform: translateY(-1px);
  box-shadow: var(--cafe-shadow-sm);
}

.filter-reset-btn {
  color: var(--cafe-muted);
  border-radius: 8px;
  height: 36px;
}

.filter-reset-btn:hover {
  color: var(--cafe-fg2);
}
</style>
