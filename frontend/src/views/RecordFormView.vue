<template>
  <n-card :title="isEdit ? '编辑记录' : '记一杯奶茶'">
    <RecordForm :initial-data="recordData" @submit="handleSubmit" @cancel="handleCancel" />
  </n-card>
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
const isEdit = computed(() => !!route.params.id)

onMounted(async () => {
  if (isEdit.value) {
    try {
      recordData.value = await milkTeaApi.getRecord(route.params.id)
    } catch (e) {
      message.error('加载记录失败')
      router.push('/records')
    }
  }
})

async function handleSubmit(data) {
  try {
    if (isEdit.value) {
      await store.updateRecord(route.params.id, data)
      message.success('更新成功')
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
