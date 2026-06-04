<template>
  <n-card title="🔍 筛选" size="small" style="margin-bottom: 16px;">
    <n-space vertical>
      <n-select v-model:value="brand" :options="brandOptions" placeholder="品牌" clearable />
      <n-select v-model:value="sugarLevel" :options="sugarLevelOptions" placeholder="糖度" clearable />
      <n-input-group>
        <n-input-number v-model:value="minPrice" placeholder="最低价" :min="0" clearable style="width: 50%" />
        <n-input-number v-model:value="maxPrice" placeholder="最高价" :min="0" clearable style="width: 50%" />
      </n-input-group>
      <n-input v-model:value="keyword" placeholder="搜索饮品或品牌..." clearable />
      <n-button type="primary" block @click="applyFilters">筛选</n-button>
      <n-button block @click="resetFilters">重置</n-button>
    </n-space>
  </n-card>
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
