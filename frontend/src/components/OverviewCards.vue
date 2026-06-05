<template>
  <div class="overview-cards">
    <!-- Total Spent Card -->
    <div class="overview-card card-primary">
      <div class="card-icon-wrapper icon-primary">
        <span class="card-icon">💰</span>
      </div>
      <div class="card-content">
        <div class="card-label">总消费</div>
        <div class="card-value">
          <span class="currency">¥</span>
          <span class="number">{{ overview?.totalSpent || '0.00' }}</span>
        </div>
        <div class="card-trend" :class="trendClass(overview?.trends?.spentDirection)">
          <span class="trend-icon">{{ trendIcon(overview?.trends?.spentDirection) }}</span>
          <span class="trend-value">{{ Math.abs(overview?.trends?.spentTrend || 0) }}%</span>
          <span class="trend-label">vs 上周</span>
        </div>
      </div>
      <div class="card-sparkline">
        <svg viewBox="0 0 100 40" preserveAspectRatio="none">
          <path :d="sparklinePath('spent')" fill="none" stroke="rgba(155, 91, 50, 0.6)" stroke-width="2" />
        </svg>
      </div>
    </div>

    <!-- Total Count Card -->
    <div class="overview-card card-success">
      <div class="card-icon-wrapper icon-success">
        <span class="card-icon">🧋</span>
      </div>
      <div class="card-content">
        <div class="card-label">总杯数</div>
        <div class="card-value">
          <span class="number">{{ overview?.totalCount || 0 }}</span>
          <span class="unit">杯</span>
        </div>
        <div class="card-trend" :class="trendClass(overview?.trends?.countDirection)">
          <span class="trend-icon">{{ trendIcon(overview?.trends?.countDirection) }}</span>
          <span class="trend-value">{{ Math.abs(overview?.trends?.countTrend || 0) }}%</span>
          <span class="trend-label">vs 上周</span>
        </div>
      </div>
      <div class="card-sparkline">
        <svg viewBox="0 0 100 40" preserveAspectRatio="none">
          <path :d="sparklinePath('count')" fill="none" stroke="rgba(79, 138, 79, 0.6)" stroke-width="2" />
        </svg>
      </div>
    </div>

    <!-- Average Price Card -->
    <div class="overview-card card-warm">
      <div class="card-icon-wrapper icon-warm">
        <span class="card-icon">📊</span>
      </div>
      <div class="card-content">
        <div class="card-label">平均单价</div>
        <div class="card-value">
          <span class="currency">¥</span>
          <span class="number">{{ overview?.avgPrice || '0.00' }}</span>
        </div>
        <div class="card-trend" :class="trendClass(overview?.trends?.avgDirection)">
          <span class="trend-icon">{{ trendIcon(overview?.trends?.avgDirection) }}</span>
          <span class="trend-value">{{ Math.abs(overview?.trends?.avgTrend || 0) }}%</span>
          <span class="trend-label">vs 上周</span>
        </div>
      </div>
      <div class="card-sparkline">
        <svg viewBox="0 0 100 40" preserveAspectRatio="none">
          <path :d="sparklinePath('avg')" fill="none" stroke="rgba(201, 130, 47, 0.6)" stroke-width="2" />
        </svg>
      </div>
    </div>

    <!-- Top Brand Card -->
    <div class="overview-card card-neutral">
      <div class="card-icon-wrapper icon-neutral">
        <span class="card-icon">🏆</span>
      </div>
      <div class="card-content">
        <div class="card-label">最常喝</div>
        <div class="card-value card-value-brand">
          <span class="brand-name">{{ overview?.topBrand || '-' }}</span>
        </div>
        <div class="card-meta" v-if="overview?.topBrandCount">
          <span class="meta-value">{{ overview.topBrandCount }} 杯</span>
        </div>
      </div>
      <div class="card-decoration">
        <div class="decoration-circle"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  overview: { type: Object, default: null }
})

function trendClass(direction) {
  return {
    'trend-up': direction === 'up',
    'trend-down': direction === 'down',
    'trend-neutral': direction === 'neutral' || !direction
  }
}

function trendIcon(direction) {
  if (direction === 'up') return '↑'
  if (direction === 'down') return '↓'
  return '—'
}

// Generate a simple sparkline path for visual appeal
function sparklinePath(type) {
  const points = []
  const baseY = 35
  let prevY = 20

  for (let i = 0; i <= 10; i++) {
    const x = i * 10
    // Create different patterns based on type
    let variation
    if (type === 'spent') {
      variation = Math.sin(i * 0.5) * 10 + Math.cos(i * 0.8) * 5
    } else if (type === 'count') {
      variation = Math.sin(i * 0.7) * 8 + Math.random() * 5
    } else {
      variation = Math.cos(i * 0.6) * 12
    }
    const y = Math.max(5, Math.min(35, prevY + variation))
    prevY = y
    points.push(`${i === 0 ? 'M' : 'L'} ${x} ${y}`)
  }

  return points.join(' ')
}
</script>

<style scoped>
.overview-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.overview-card {
  background: var(--cafe-surface);
  border-radius: 14px;
  padding: 20px;
  border: 1px solid var(--cafe-border-soft);
  position: relative;
  overflow: hidden;
  display: flex;
  gap: 16px;
  transition: transform var(--cafe-transition), box-shadow var(--cafe-transition), border-color var(--cafe-transition);
}

.overview-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--cafe-shadow-md);
}

.card-primary:hover {
  border-color: var(--cafe-accent);
}

.card-success:hover {
  border-color: var(--cafe-success);
}

.card-warm:hover {
  border-color: var(--cafe-warn);
}

.card-neutral:hover {
  border-color: var(--cafe-muted);
}

.card-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon-primary {
  background: linear-gradient(135deg, rgba(155, 91, 50, 0.15) 0%, rgba(155, 91, 50, 0.05) 100%);
}

.icon-success {
  background: linear-gradient(135deg, rgba(79, 138, 79, 0.15) 0%, rgba(79, 138, 79, 0.05) 100%);
}

.icon-warm {
  background: linear-gradient(135deg, rgba(201, 130, 47, 0.15) 0%, rgba(201, 130, 47, 0.05) 100%);
}

.icon-neutral {
  background: linear-gradient(135deg, rgba(122, 109, 99, 0.12) 0%, rgba(122, 109, 99, 0.04) 100%);
}

.card-icon {
  font-size: 24px;
}

.card-content {
  flex: 1;
  min-width: 0;
}

.card-label {
  font-size: 13px;
  color: var(--cafe-muted);
  font-weight: 500;
  margin-bottom: 6px;
}

.card-value {
  display: flex;
  align-items: baseline;
  gap: 2px;
  margin-bottom: 8px;
}

.currency {
  font-size: 16px;
  font-weight: 700;
  color: var(--cafe-fg2);
}

.number {
  font-family: 'Noto Serif SC', serif;
  font-size: 26px;
  font-weight: 700;
  color: var(--cafe-fg);
  line-height: 1.2;
}

.unit {
  font-size: 14px;
  color: var(--cafe-muted);
  font-weight: 500;
}

.card-value-brand {
  margin-bottom: 4px;
}

.brand-name {
  font-family: 'Noto Serif SC', serif;
  font-size: 22px;
  font-weight: 700;
  color: var(--cafe-fg);
  line-height: 1.3;
}

.card-meta {
  font-size: 13px;
  color: var(--cafe-muted);
}

.meta-value {
  font-weight: 600;
}

.card-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
}

.trend-up {
  color: var(--cafe-success);
}

.trend-down {
  color: var(--cafe-danger);
}

.trend-neutral {
  color: var(--cafe-muted);
}

.trend-icon {
  font-weight: 700;
}

.trend-value {
  font-weight: 600;
}

.trend-label {
  opacity: 0.7;
}

.card-sparkline {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 100px;
  height: 50px;
  opacity: 0.6;
  pointer-events: none;
}

.card-decoration {
  position: absolute;
  right: -20px;
  bottom: -20px;
  width: 80px;
  height: 80px;
  pointer-events: none;
}

.decoration-circle {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(201, 130, 47, 0.1) 0%, transparent 50%);
}

@media (max-width: 1000px) {
  .overview-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .overview-cards {
    grid-template-columns: 1fr;
  }
}
</style>
