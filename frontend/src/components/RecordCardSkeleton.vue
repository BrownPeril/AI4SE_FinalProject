<template>
  <div class="record-card-skeleton">
    <div class="skeleton-header">
      <div class="skeleton-title skeleton-shimmer"></div>
      <div class="skeleton-rating skeleton-shimmer"></div>
    </div>
    <div class="skeleton-body">
      <div class="skeleton-tags">
        <div class="skeleton-tag skeleton-shimmer"></div>
        <div class="skeleton-tag skeleton-shimmer"></div>
        <div class="skeleton-tag skeleton-shimmer"></div>
      </div>
      <div class="skeleton-meta">
        <div class="skeleton-price skeleton-shimmer"></div>
        <div class="skeleton-date skeleton-shimmer"></div>
      </div>
    </div>
    <div class="skeleton-footer">
      <div class="skeleton-actions">
        <div class="skeleton-action skeleton-shimmer"></div>
        <div class="skeleton-action skeleton-shimmer"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
// Empty - just a skeleton component
</script>

<style scoped>
.record-card-skeleton {
  background: var(--cafe-surface);
  border-radius: var(--cafe-radius);
  padding: 20px 24px;
  border: 1px solid var(--cafe-border-soft);
}

.skeleton-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.skeleton-title {
  height: 22px;
  width: 180px;
  border-radius: 4px;
}

.skeleton-rating {
  height: 18px;
  width: 100px;
  border-radius: 4px;
}

.skeleton-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.skeleton-tags {
  display: flex;
  gap: 8px;
}

.skeleton-tag {
  height: 24px;
  width: 50px;
  border-radius: 12px;
}

.skeleton-meta {
  display: flex;
  align-items: center;
  gap: 16px;
}

.skeleton-price {
  height: 24px;
  width: 55px;
  border-radius: 4px;
}

.skeleton-date {
  height: 16px;
  width: 90px;
  border-radius: 4px;
}

.skeleton-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 12px;
  border-top: 1px solid var(--cafe-border-soft);
}

.skeleton-actions {
  display: flex;
  gap: 8px;
}

.skeleton-action {
  height: 28px;
  width: 40px;
  border-radius: 4px;
}

.skeleton-shimmer {
  background: linear-gradient(
    90deg,
    var(--cafe-border-soft) 0%,
    #f8f2e8 50%,
    var(--cafe-border-soft) 100%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s ease-in-out infinite;
}

@keyframes shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}
</style>
