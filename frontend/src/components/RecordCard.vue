<template>
  <div class="record-card">
    <div class="record-card-main">
      <div class="record-card-header">
        <div class="record-card-title">
          <span class="record-brand">{{ record.brand }}</span>
          <span class="record-separator">·</span>
          <span class="record-drink">{{ record.drinkName }}</span>
        </div>
        <div class="record-rating">
          <n-rate :value="record.rating || 0" readonly :size="18" color="#c9822f" />
        </div>
      </div>

      <div class="record-card-content">
        <div class="record-tags">
          <n-tag v-if="record.sugarLevel" size="small" class="cafe-tag">
            <span class="tag-icon">🍬</span>
            {{ record.sugarLevel }}
          </n-tag>
          <n-tag v-if="record.iceLevel" size="small" class="cafe-tag">
            <span class="tag-icon">🧊</span>
            {{ record.iceLevel }}
          </n-tag>
          <n-tag v-if="record.cupSize" size="small" class="cafe-tag">
            <span class="tag-icon">🥤</span>
            {{ record.cupSize }}
          </n-tag>
          <n-tag v-if="record.teaBase" size="small" class="cafe-tag">
            <span class="tag-icon">🍃</span>
            {{ record.teaBase }}底
          </n-tag>
        </div>

        <div class="record-meta">
          <div class="record-price">
            <span class="price-currency">¥</span>
            <span class="price-value">{{ record.price }}</span>
          </div>
          <div class="record-date">
            <span class="date-icon">📅</span>
            <span>{{ formatDate(record.consumeDate) }}</span>
          </div>
          <div v-if="record.wouldRecommend" class="record-recommend">
            <span class="recommend-badge">👍 推荐</span>
          </div>
        </div>
      </div>

      <div v-if="record.comment" class="record-comment">
        <span class="comment-quote">"</span>
        {{ record.comment }}
        <span class="comment-quote">"</span>
      </div>
    </div>

    <div class="record-card-actions">
      <n-button text type="primary" size="small" @click="$emit('edit', record)" class="action-btn">
        编辑
      </n-button>
      <n-popconfirm @positive-click="$emit('delete', record.id)">
        <template #trigger>
          <n-button text type="error" size="small" class="action-btn">
            删除
          </n-button>
        </template>
        确定删除这条记录吗？
      </n-popconfirm>
    </div>
  </div>
</template>

<script setup>
defineProps({
  record: { type: Object, required: true }
})
defineEmits(['edit', 'delete'])

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}
</script>

<style scoped>
.record-card {
  background: var(--cafe-surface);
  border-radius: var(--cafe-radius);
  border: 1px solid var(--cafe-border-soft);
  overflow: hidden;
  transition: transform var(--cafe-transition), box-shadow var(--cafe-transition), border-color var(--cafe-transition);
}

.record-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--cafe-shadow-md);
  border-color: var(--cafe-border);
}

.record-card-main {
  padding: 20px 24px;
}

.record-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  gap: 16px;
}

.record-card-title {
  display: flex;
  align-items: baseline;
  gap: 8px;
  flex-wrap: wrap;
}

.record-brand {
  font-family: 'Noto Serif SC', serif;
  font-size: 18px;
  font-weight: 700;
  color: var(--cafe-accent);
  letter-spacing: 0.5px;
}

.record-separator {
  color: var(--cafe-muted);
  font-size: 16px;
}

.record-drink {
  font-size: 18px;
  font-weight: 600;
  color: var(--cafe-fg);
}

.record-rating {
  flex-shrink: 0;
}

.record-card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.record-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.cafe-tag {
  border-radius: 20px;
  padding: 2px 10px;
  border: 1px solid var(--cafe-border-soft);
  background: var(--cafe-bg);
  color: var(--cafe-fg2);
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.tag-icon {
  font-size: 12px;
}

.record-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.record-price {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.price-currency {
  font-size: 14px;
  font-weight: 600;
  color: var(--cafe-accent);
}

.price-value {
  font-family: 'Noto Serif SC', serif;
  font-size: 22px;
  font-weight: 700;
  color: var(--cafe-accent);
}

.record-date {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--cafe-muted);
  font-size: 13px;
}

.date-icon {
  font-size: 12px;
}

.record-recommend {
  flex-shrink: 0;
}

.recommend-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: rgba(79, 138, 79, 0.1);
  color: var(--cafe-success);
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.record-comment {
  margin-top: 16px;
  padding: 12px 16px;
  background: var(--cafe-bg);
  border-radius: var(--cafe-radius-sm);
  color: var(--cafe-fg2);
  font-size: 14px;
  line-height: 1.6;
  position: relative;
}

.comment-quote {
  font-family: 'Noto Serif SC', serif;
  color: var(--cafe-accent);
  opacity: 0.4;
  font-size: 18px;
  line-height: 1;
  display: inline-block;
  margin: 0 2px;
}

.record-card-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 10px 24px;
  border-top: 1px solid var(--cafe-border-soft);
  background: linear-gradient(180deg, transparent 0%, rgba(251, 246, 238, 0.5) 100%);
  gap: 4px;
}

.action-btn {
  border-radius: 6px;
  padding: 6px 12px;
  font-weight: 500;
  transition: background var(--cafe-transition);
}

.action-btn:hover {
  background: var(--cafe-bg);
}
</style>
