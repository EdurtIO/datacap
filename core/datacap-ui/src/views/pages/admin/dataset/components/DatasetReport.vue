<template>
  <ShadcnModal v-model="localVisible" :title="$t('common.configure')" @on-close="onCancel">
    <ShadcnForm v-model="formState" @on-error="console.log($event)" @on-submit="onSubmit">
      <ShadcnSpace wrap>
        <ShadcnFormItem name="name"
                        class="w-full"
                        :label="$t('common.name')"
                        :rules="[{ required: true, message: $t('common.name') }]">
          <ShadcnInput v-model="formState.name" name="name"/>
        </ShadcnFormItem>

        <ShadcnFormItem class="w-full" name="description" :label="$t('common.description')">
          <ShadcnInput v-model="formState.description" type="textarea" name="description"/>
        </ShadcnFormItem>

        <ShadcnFormItem class="w-full" name="build" :label="$t('dataset.common.continuousBuild')">
          <ShadcnSwitch v-model="formState.build" name="build"/>
        </ShadcnFormItem>
      </ShadcnSpace>

      <ShadcnSpace>
        <ShadcnButton type="default" @click="onCancel">
          {{ $t('common.cancel') }}
        </ShadcnButton>

        <ShadcnButton submit :disabled="published" :loading="published">
          {{ $t('common.publish') }}
        </ShadcnButton>
      </ShadcnSpace>
    </ShadcnForm>
  </ShadcnModal>
</template>

<script setup lang="ts">
import { getCurrentInstance, ref } from 'vue'
import router from '@/router'
import ReportService from '@/services/report.ts'

const emit = defineEmits(['close'])
const { proxy } = getCurrentInstance()!

const props = defineProps<{
  visible: boolean,
  info?: { name: string, description: string, build: boolean }
  code?: string
  dimension?: any
  commitOptions?: any
  configure?: any
}>()

const localVisible = ref(props.visible)
const published = ref(false)
const formState = ref(props.info || { name: undefined, description: undefined, build: false })

const onSubmit = () => {
  published.value = true
  const obj = props.dimension
  const configure = {
    name: formState.value.name,
    code: props.code,
    realtime: true,
    type: 'DATASET',
    configure: JSON.stringify(props.commitOptions),
    dataset: {
      code: obj.dataset.code
    },
    query: JSON.stringify(props.configure),
    description: formState.value.description
  }
  ReportService.saveOrUpdate(configure)
               .then(response => {
                 if (response.status) {
                   // @ts-ignore
                   proxy?.$Message.success({
                     content: proxy.$t('report.tip.publishSuccess').replace('$VALUE', formState.value.name),
                     showIcon: true
                   })

                   if (formState.value.build) {
                     router.push('/admin/report')
                   }

                   onCancel()
                 }
               })
               .finally(() => published.value = false)
}

const onCancel = () => {
  emit('close', !props.visible)
}
</script>
