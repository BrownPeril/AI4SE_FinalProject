<template>
  <n-form ref="formRef" :model="formData" :rules="rules" label-placement="top">
    <n-grid :cols="3" :x-gap="24">
      <!-- Basic Info -->
      <n-gi>
        <n-h4 style="color: #7a6d63; margin-top: 0;">基本信息</n-h4>
        <n-form-item label="品牌" path="brand">
          <n-input v-model:value="formData.brand" placeholder="如：喜茶" />
        </n-form-item>
        <n-form-item label="饮品名" path="drinkName">
          <n-input v-model:value="formData.drinkName" placeholder="如：多肉葡萄" />
        </n-form-item>
        <n-form-item label="价格" path="price">
          <n-input-number v-model:value="formData.price" :min="0" :precision="2" style="width: 100%">
            <template #prefix>¥</template>
          </n-input-number>
        </n-form-item>
        <n-form-item label="消费时间" path="consumeDate">
          <n-date-picker v-model:formatted-value="formData.consumeDate" type="datetime" format="yyyy-MM-dd HH:mm:ss" style="width: 100%" />
        </n-form-item>
      </n-gi>

      <!-- Drink Details -->
      <n-gi>
        <n-h4 style="color: #7a6d63; margin-top: 0;">饮品详情</n-h4>
        <n-form-item label="糖度" path="sugarLevel">
          <n-select v-model:value="formData.sugarLevel" :options="sugarLevelOptions" placeholder="选择糖度" clearable />
        </n-form-item>
        <n-form-item label="冰度" path="iceLevel">
          <n-select v-model:value="formData.iceLevel" :options="iceLevelOptions" placeholder="选择冰度" clearable />
        </n-form-item>
        <n-form-item label="杯型" path="cupSize">
          <n-select v-model:value="formData.cupSize" :options="cupSizeOptions" placeholder="选择杯型" clearable />
        </n-form-item>
        <n-form-item label="茶底" path="teaBase">
          <n-input v-model:value="formData.teaBase" placeholder="如：绿茶、红茶、乌龙茶" />
        </n-form-item>
      </n-gi>

      <!-- Subjective Rating -->
      <n-gi>
        <n-h4 style="color: #7a6d63; margin-top: 0;">主观评价</n-h4>
        <n-form-item label="评分" path="rating">
          <n-rate v-model:value="formData.rating" />
        </n-form-item>
        <n-form-item label="点评" path="comment">
          <n-input v-model:value="formData.comment" type="textarea" :rows="3" placeholder="这杯奶茶怎么样？" />
        </n-form-item>
        <n-form-item label="推荐" path="wouldRecommend">
          <n-switch v-model:value="formData.wouldRecommend">
            <template #checked>推荐</template>
            <template #unchecked>不推荐</template>
          </n-switch>
        </n-form-item>
      </n-gi>
    </n-grid>

    <n-divider />
    <n-space justify="end">
      <n-button @click="handleCancel">取消</n-button>
      <n-button type="primary" @click="handleSubmit" :loading="submitting">保存</n-button>
    </n-space>
  </n-form>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  initialData: { type: Object, default: () => ({}) }
})
const emit = defineEmits(['submit', 'cancel'])

const formRef = ref(null)
const submitting = ref(false)

const formData = ref({
  brand: props.initialData.brand || null,
  drinkName: props.initialData.drinkName || null,
  price: props.initialData.price || null,
  consumeDate: props.initialData.consumeDate || null,
  sugarLevel: props.initialData.sugarLevel || null,
  iceLevel: props.initialData.iceLevel || null,
  cupSize: props.initialData.cupSize || null,
  teaBase: props.initialData.teaBase || null,
  rating: props.initialData.rating || null,
  comment: props.initialData.comment || null,
  wouldRecommend: props.initialData.wouldRecommend || false
})

const rules = {
  brand: { required: true, message: '请输入品牌', trigger: 'blur' },
  drinkName: { required: true, message: '请输入饮品名', trigger: 'blur' },
  price: { required: true, type: 'number', message: '请输入价格', trigger: 'blur' },
  consumeDate: { required: true, message: '请选择消费时间', trigger: 'change' }
}

const sugarLevelOptions = [
  { label: '无糖', value: '无糖' },
  { label: '微糖', value: '微糖' },
  { label: '半糖', value: '半糖' },
  { label: '少糖', value: '少糖' },
  { label: '全糖', value: '全糖' }
]

const iceLevelOptions = [
  { label: '去冰', value: '去冰' },
  { label: '少冰', value: '少冰' },
  { label: '正常冰', value: '正常冰' },
  { label: '多冰', value: '多冰' },
  { label: '温', value: '温' },
  { label: '热', value: '热' }
]

const cupSizeOptions = [
  { label: '小杯', value: '小杯' },
  { label: '中杯', value: '中杯' },
  { label: '大杯', value: '大杯' },
  { label: '超大杯', value: '超大杯' }
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
