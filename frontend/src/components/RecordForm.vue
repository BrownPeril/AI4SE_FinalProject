<template>
  <div class="record-form">
    <n-form ref="formRef" :model="formData" :rules="rules" label-placement="top" label-align="left">
      <!-- Basic Info Section -->
      <div class="form-section">
        <div class="section-header">
          <span class="section-icon">📝</span>
          <h3 class="section-title">基本信息</h3>
        </div>
        <div class="section-content">
          <n-grid :cols="2" :x-gap="20" :y-gap="16">
            <n-gi>
              <n-form-item label="品牌" path="brand">
                <n-input v-model:value="formData.brand" placeholder="例如：喜茶" size="large" />
              </n-form-item>
            </n-gi>
            <n-gi>
              <n-form-item label="饮品名称" path="drinkName">
                <n-input v-model:value="formData.drinkName" placeholder="例如：多肉葡萄" size="large" />
              </n-form-item>
            </n-gi>
            <n-gi>
              <n-form-item label="价格" path="price">
                <n-input-number v-model:value="formData.price" :min="0" :precision="2" placeholder="请输入价格" size="large" style="width: 100%;">
                  <template #prefix>¥</template>
                </n-input-number>
              </n-form-item>
            </n-gi>
            <n-gi>
              <n-form-item label="消费时间" path="consumeDate">
                <n-date-picker v-model:formatted-value="formData.consumeDate" type="datetime" format="yyyy-MM-dd HH:mm:ss" placeholder="请输入时间" size="large" style="width: 100%;" />
              </n-form-item>
            </n-gi>
          </n-grid>
        </div>
      </div>

      <!-- Drink Details Section -->
      <div class="form-section">
        <div class="section-header">
          <span class="section-icon">🧋</span>
          <h3 class="section-title">饮品详情</h3>
        </div>
        <div class="section-content">
          <n-grid :cols="2" :x-gap="20" :y-gap="16">
            <n-gi>
              <n-form-item label="糖度">
                <n-select v-model:value="formData.sugarLevel" :options="sugarLevelOptions" placeholder="选择糖度" clearable size="large" />
              </n-form-item>
            </n-gi>
            <n-gi>
              <n-form-item label="冰度">
                <n-select v-model:value="formData.iceLevel" :options="iceLevelOptions" placeholder="选择冰度" clearable size="large" />
              </n-form-item>
            </n-gi>
            <n-gi>
              <n-form-item label="杯型">
                <n-select v-model:value="formData.cupSize" :options="cupSizeOptions" placeholder="选择杯型" clearable size="large" />
              </n-form-item>
            </n-gi>
            <n-gi>
              <n-form-item label="茶底">
                <n-input v-model:value="formData.teaBase" placeholder="例如：绿茶、红茶、乌龙茶" size="large" />
              </n-form-item>
            </n-gi>
          </n-grid>
        </div>
      </div>

      <!-- Rating Section -->
      <div class="form-section">
        <div class="section-header">
          <span class="section-icon">⭐</span>
          <h3 class="section-title">体验评价</h3>
        </div>
        <div class="section-content">
          <n-grid :cols="2" :x-gap="20" :y-gap="16">
            <n-gi :span="1">
              <n-form-item label="评分">
                <n-rate v-model:value="formData.rating" :size="28" color="#c9822f" />
              </n-form-item>
              <n-form-item label="推荐">
                <n-switch v-model:value="formData.wouldRecommend" size="large">
                  <template #checked>👍 推荐</template>
                  <template #unchecked>不推荐</template>
                </n-switch>
              </n-form-item>
            </n-gi>
            <n-gi :span="1">
              <n-form-item label="评价备注">
                <n-input v-model:value="formData.comment" type="textarea" :rows="4" placeholder="这杯奶茶怎么样？写下你的感受..." size="large" />
              </n-form-item>
            </n-gi>
          </n-grid>
        </div>
      </div>

      <!-- Form Actions -->
      <div class="form-actions">
        <div class="form-actions-inner">
          <n-button size="large" @click="handleCancel" class="cancel-btn">
            取消
          </n-button>
          <n-button type="primary" size="large" @click="handleSubmit" :loading="submitting" class="submit-btn">
            {{ isEdit ? '保存修改' : '保存记录' }}
          </n-button>
        </div>
      </div>
    </n-form>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  initialData: { type: Object, default: () => ({}) },
  isEdit: { type: Boolean, default: false }
})
const emit = defineEmits(['submit', 'cancel'])

const formRef = ref(null)
const submitting = ref(false)

const formData = ref({
  brand: null,
  drinkName: null,
  price: null,
  consumeDate: null,
  sugarLevel: null,
  iceLevel: null,
  cupSize: null,
  teaBase: null,
  rating: null,
  comment: null,
  wouldRecommend: false
})

// Update form when initialData changes
watch(() => props.initialData, (data) => {
  if (data) {
    formData.value = {
      brand: data.brand || null,
      drinkName: data.drinkName || null,
      price: data.price || null,
      consumeDate: data.consumeDate || null,
      sugarLevel: data.sugarLevel || null,
      iceLevel: data.iceLevel || null,
      cupSize: data.cupSize || null,
      teaBase: data.teaBase || null,
      rating: data.rating || null,
      comment: data.comment || null,
      wouldRecommend: data.wouldRecommend || false
    }
  }
}, { immediate: true, deep: true })

const rules = {
  brand: { required: true, message: '请输入品牌', trigger: 'blur' },
  drinkName: { required: true, message: '请输入饮品名称', trigger: 'blur' },
  price: { required: true, type: 'number', message: '请输入价格', trigger: 'blur' },
  consumeDate: { required: true, message: '请选择消费时间', trigger: 'change' }
}

const sugarLevelOptions = [
  { label: '🍬 无糖', value: '无糖' },
  { label: '🍬 微糖', value: '微糖' },
  { label: '🍬 半糖', value: '半糖' },
  { label: '🍬 少糖', value: '少糖' },
  { label: '🍬 全糖', value: '全糖' }
]

const iceLevelOptions = [
  { label: '🧊 去冰', value: '去冰' },
  { label: '🧊 少冰', value: '少冰' },
  { label: '🧊 正常冰', value: '正常冰' },
  { label: '🧊 多冰', value: '多冰' },
  { label: '☕ 温', value: '温' },
  { label: '☕ 热', value: '热' }
]

const cupSizeOptions = [
  { label: '🥤 小杯', value: '小杯' },
  { label: '🥤 中杯', value: '中杯' },
  { label: '🥤 大杯', value: '大杯' },
  { label: '🥤 超大杯', value: '超大杯' }
]

async function handleSubmit() {
  try {
    await formRef.value?.validate()
    submitting.value = true
    emit('submit', { ...formData.value })
  } catch {
    // validation failed
  } finally {
    submitting.value = false
  }
}

function handleCancel() {
  emit('cancel')
}
</script>

<style scoped>
.record-form {
  padding: 0;
}

.form-section {
  padding: 28px 32px;
  border-bottom: 1px solid var(--cafe-border-soft);
}

.form-section:last-of-type {
  border-bottom: none;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.section-icon {
  font-size: 20px;
}

.section-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 18px;
  font-weight: 700;
  color: var(--cafe-fg);
  margin: 0;
  letter-spacing: 0.5px;
}

.section-content {
  padding-left: 30px;
}

.form-actions {
  padding: 24px 32px;
  background: linear-gradient(180deg, transparent 0%, rgba(251, 246, 238, 0.6) 100%);
}

.form-actions-inner {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-left: 30px;
}

.cancel-btn {
  border-radius: 10px;
  height: 44px;
  padding-left: 24px;
  padding-right: 24px;
  font-weight: 500;
}

.submit-btn {
  border-radius: 10px;
  height: 44px;
  padding-left: 28px;
  padding-right: 28px;
  font-weight: 600;
  transition: transform var(--cafe-transition), box-shadow var(--cafe-transition);
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: var(--cafe-shadow-md);
}

:deep(.n-form-item-label) {
  font-weight: 600;
  color: var(--cafe-fg2);
  font-size: 13px;
  padding-bottom: 6px;
}
</style>
