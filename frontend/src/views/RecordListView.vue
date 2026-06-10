<template>
  <div class="record-list-view">
    <div class="record-list-layout">
      <aside class="filter-sidebar">
        <FilterBar @filter="handleFilter" />
      </aside>
      <main class="record-list-main">
        <div class="record-list-header">
          <div class="record-list-title">
            <h2 class="page-title">奶茶记录</h2>
            <span class="record-count" v-if="!store.loading">共 {{ store.totalElements }} 杯</span>
          </div>
          <n-button type="primary" size="large" @click="router.push('/records/new')" class="add-button">
            <span class="add-icon">+</span>
            记一杯
          </n-button>
        </div>

        <!-- Loading Skeleton State -->
        <div v-if="store.loading" class="record-skeletons">
          <RecordCardSkeleton v-for="i in 3" :key="i" />
        </div>

        <!-- Empty State -->
        <div v-else-if="store.records.length === 0" class="empty-state">
          <div class="empty-state-inner">
            <div class="empty-icon">🧋</div>
            <h3 class="empty-title">还没有记录</h3>
            <p class="empty-desc">开始记录你的第一杯奶茶吧！</p>
            <n-button type="primary" size="large" @click="router.push('/records/new')">
              记第一杯
            </n-button>
          </div>
        </div>

        <!-- Record List -->
        <div v-else class="record-list">
          <RecordCard
            v-for="record in store.records"
            :key="record.id"
            :record="record"
            @edit="handleEdit"
            @delete="handleDelete"
          />
        </div>

        <!-- Pagination -->
        <div class="pagination-wrapper" v-if="store.totalPages > 1 && !store.loading">
          <n-pagination
            :page="store.currentPage + 1"
            :page-count="store.totalPages"
            @update:page="handlePageChange"
          />
        </div>
      </main>
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
import RecordCardSkeleton from '../components/RecordCardSkeleton.vue'

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
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>

<style scoped>
.record-list-view {
  width: 100%;
}

.record-list-layout {
  display: flex;
  gap: 28px;
  align-items: flex-start;
}

.filter-sidebar {
  width: 260px;
  flex-shrink: 0;
  position: sticky;
  top: 24px;
}

.record-list-main {
  flex: 1;
  min-width: 0;
}

.record-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.record-list-title {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.page-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 28px;
  font-weight: 700;
  color: var(--cafe-fg);
  margin: 0;
  letter-spacing: 1px;
}

.record-count {
  font-size: 15px;
  color: var(--cafe-muted);
  font-weight: 500;
}

.add-button {
  border-radius: 12px;
  font-weight: 600;
  padding-left: 20px;
  padding-right: 20px;
  height: 44px;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: transform var(--cafe-transition), box-shadow var(--cafe-transition);
}

.add-button:hover {
  transform: translateY(-1px);
  box-shadow: var(--cafe-shadow-md);
}

.add-icon {
  font-size: 20px;
  line-height: 1;
  font-weight: 600;
}

.record-skeletons {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.empty-state {
  padding: 80px 24px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.empty-state-inner {
  text-align: center;
  max-width: 320px;
}

.empty-icon {
  font-size: 72px;
  line-height: 1;
  margin-bottom: 20px;
  opacity: 0.9;
}

.empty-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 22px;
  font-weight: 600;
  color: var(--cafe-fg);
  margin: 0 0 8px;
}

.empty-desc {
  font-size: 15px;
  color: var(--cafe-muted);
  margin: 0 0 28px;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.pagination-wrapper {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}
</style>
