<template>
  <div class="record-form-view">
    <div class="form-header">
      <n-button text @click="router.push('/records')" class="back-button">
        <span class="back-icon">←</span>
        返回列表
      </n-button>
      <h2 class="form-page-title">{{ isEdit ? '编辑记录' : '记一杯奶茶' }}</h2>
      <div class="form-header-spacer"></div>
    </div>

    <div class="form-container">
      <n-spin :show="loading">
        <RecordForm :initial-data="recordData" @submit="handleSubmit" @cancel="handleCancel" :is-edit="isEdit" />
      </n-spin>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'
import { useMilkTeaStore } from '../stores/milkTea.js'
import { milkTeaApi } from '../api/milkTea.js'
import RecordForm from '../components/RecordForm.vue'

const router = useRouter()
const route = useRoute()
const message = useMessage()
const store = useMilkTeaStore()

const recordData = ref({})
const loading = ref(false)
const isEdit = computed(() => !!route.params.id)

onMounted(async () => {
  if (isEdit.value) {
    loading.value = true
    try {
      recordData.value = await milkTeaApi.getRecord(route.params.id)
    } catch (e) {
      message.error('加载记录失败')
      router.push('/records')
    } finally {
      loading.value = false
    }
  }
})

async function handleSubmit(data) {
  try {
    if (isEdit.value) {
      await store.updateRecord(route.params.id, data)
      message.success('更新成功 ✨')
    } else {
      await store.createRecord(data)
      message.success('记录成功 🧋')
    }
    router.push('/records')
  } catch (e) {
    message.error(e.message)
  }
}

function handleCancel() {
  router.push('/records')
}
</script>

<style scoped>
.record-form-view {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
}

.form-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--cafe-muted);
  font-size: 14px;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background var(--cafe-transition), color var(--cafe-transition);
}

.back-button:hover {
  background: var(--cafe-border-soft);
  color: var(--cafe-fg2);
}

.back-icon {
  font-size: 16px;
  font-weight: 600;
}

.form-page-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 24px;
  font-weight: 700;
  color: var(--cafe-fg);
  margin: 0;
  letter-spacing: 1px;
}

.form-header-spacer {
  width: 80px;
}

.form-container {
  background: var(--cafe-surface);
  border-radius: var(--cafe-radius);
  border: 1px solid var(--cafe-border-soft);
  overflow: hidden;
}
</style>
