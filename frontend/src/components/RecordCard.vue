<template>
  <n-card hoverable style="margin-bottom: 12px;">
    <template #header>
      <span style="font-weight: bold;">{{ record.brand }} · {{ record.drinkName }}</span>
    </template>
    <template #header-extra>
      <n-rate :value="record.rating || 0" readonly size="small" />
    </template>
    <n-space justify="space-between" align="center">
      <div>
        <n-tag v-if="record.sugarLevel" size="small" type="info" style="margin-right: 4px;">{{ record.sugarLevel }}</n-tag>
        <n-tag v-if="record.iceLevel" size="small" type="info" style="margin-right: 4px;">{{ record.iceLevel }}</n-tag>
        <n-tag v-if="record.cupSize" size="small" type="info" style="margin-right: 4px;">{{ record.cupSize }}</n-tag>
        <n-tag v-if="record.teaBase" size="small">{{ record.teaBase }}底</n-tag>
      </div>
      <div style="display: flex; align-items: center; gap: 12px;">
        <span style="color: #9b5b32; font-weight: bold; font-size: 18px;">¥{{ record.price }}</span>
        <span style="color: #7a6d63; font-size: 12px;">{{ formatDate(record.consumeDate) }}</span>
      </div>
    </n-space>
    <div v-if="record.comment" style="margin-top: 8px; color: #4c4037; font-size: 13px;">{{ record.comment }}</div>
    <template #action>
      <n-space justify="end">
        <n-button text type="primary" @click="$emit('edit', record)">编辑</n-button>
        <n-popconfirm @positive-click="$emit('delete', record.id)">
          <template #trigger>
            <n-button text type="error">删除</n-button>
          </template>
          确定删除这条记录吗？
        </n-popconfirm>
      </n-space>
    </template>
  </n-card>
</template>

<script setup>
defineProps({
  record: { type: Object, required: true }
})
defineEmits(['edit', 'delete'])

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}
</script>
