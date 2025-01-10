<template>
  <ShadcnModal v-model="localVisible"
               :title="$t('source.common.resetAutoIncrement')"
               :mask-closable="false"
               @on-close="onCancel">
    <ShadcnForm v-model="formState" @on-submit="onSubmit">
      <ShadcnFormItem name="autoIncrement" :label="$t('source.common.resetTo')">
        <ShadcnNumber v-model="formState.autoIncrement"
                      name="autoIncrement"
                      :min="1"
                      :placeholder="$t('snippet.placeholder.name')"/>
      </ShadcnFormItem>

      <div class="flex justify-end">
        <ShadcnSpace>
          <ShadcnButton type="default" @click="onCancel">
            {{ $t('common.cancel') }}
          </ShadcnButton>

          <ShadcnButton submit type="primary" :loading="loading">
            {{ $t('common.apply') }}
          </ShadcnButton>
        </ShadcnSpace>
      </div>
    </ShadcnForm>
  </ShadcnModal>
</template>

<script setup lang="ts">
import { getCurrentInstance, ref } from 'vue'
import MetadataService from '@/services/metadata.ts'

const emit = defineEmits(['close'])
const { proxy } = getCurrentInstance()!

const props = withDefaults(defineProps<{
  visible: boolean
  info?: any
}>(), {
  visible: false
})

const localVisible = ref(props.visible)
const loading = ref(false)
const formState = ref({ autoIncrement: props.info?.object_auto_increment ?? 1 })

const onCancel = () => emit('close')

const onSubmit = () => {

  const code = proxy.$route?.params.source
  const database = proxy.$route?.params.database
  const table = proxy.$route?.params.table

  if (props.info && code && database && table) {
    loading.value = true
    const configure = {
      autoIncrement: formState.value.autoIncrement
    }
    MetadataService.updateAutoIncrement(code as string, database as string, table as string, configure)
                   .then(response => {
                     if (response.status && response.data && response.data.isSuccessful) {
                       // @ts-ignore
                       proxy.$Message.success({
                         content: proxy.$t('source.tip.resetAutoIncrementSuccess').replace('$VALUE', String(formState.value.autoIncrement)),
                         showIcon: true
                       })

                       onCancel()
                     }
                     else {
                       // @ts-ignore
                       proxy.$Message.error({
                         content: response.message,
                         showIcon: true
                       })
                     }
                   })
                   .finally(() => loading.value = false)
  }
}
</script>
