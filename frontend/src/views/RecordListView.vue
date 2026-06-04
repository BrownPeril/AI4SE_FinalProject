<template>
  <div style="display: flex; gap: 24px;">
    <div style="width: 240px; flex-shrink: 0;">
      <FilterBar @filter="handleFilter" />
    </div>
    <div style="flex: 1;">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <span style="font-size: 16px; font-weight: bold;">共 {{ store.totalElements }} 条记录</span>
        <n-button type="primary" @click="router.push('/records/new')">+ 记一杯奶茶</n-button>
      </div>
      <n-spin :show="store.loading">
        <div v-if="store.records.length === 0 && !store.loading" style="text-align: center; padding: 48px; color: #7a6d63;">
          还没有记录，快去记录第一杯奶茶吧 🧋
        </div>
        <RecordCard
          v-for="record in store.records"
          :key="record.id"
          :record="record"
          @edit="handleEdit"
          @delete="handleDelete"
        />
      </n-spin>
      <n-pagination
        v-if="store.totalPages > 1"
        :page="store.currentPage + 1"
        :page-count="store.totalPages"
        @update:page="handlePageChange"
        style="margin-top: 16px; justify-content: center;"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { useMilkTeaStore } from '../stores/milkTea.js'
import FilterBar from '../components/FilterBar.vue'
import RecordCard from '../components/RecordCard.vue'

const router = useRouter()
const message = useMessage()
const store = useMilkTeaStore()

onMounted(() => {
  store.fetchRecords()
})

function handleFilter(filters) {
  store.setFilters(filters)
  store.fetchRecords()
}

function handleEdit(record) {
  router.push(`/records/${record.id}/edit`)
}

async function handleDelete(id) {
  try {
    await store.deleteRecord(id)
    message.success('删除成功')
  } catch (e) {
    message.error(e.message)
  }
}

function handlePageChange(page) {
  store.setPage(page - 1)
}
</script>
