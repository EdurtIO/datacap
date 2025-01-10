<template>
  <ShadcnModal v-model="visible"
               height="33%"
               width="40%"
               :title="title"
               @on-close="onCancel">
    <ShadcnForm v-model="formState" v-if="formState" @on-submit="onSubmit">
      <ShadcnFormItem name="format" :label="$t('source.common.exportDataFormat')">
        <ShadcnRadioGroup v-model="formState.format" name="format">
          <ShadcnRadio value="CSV">CSV</ShadcnRadio>
        </ShadcnRadioGroup>
      </ShadcnFormItem>

      <ShadcnFormItem name="count" :label="$t('source.common.exportDataCount')">
        <ShadcnNumber v-model="formState.count" name="count"/>
      </ShadcnFormItem>

      <ShadcnFormItem name="path" :label="$t('source.common.downloadPath')">
        <div class="flex items-center space-x-1">
          <ShadcnInput v-model="formState.path" disabled="" name="path"/>
          <ShadcnButton :disabled="!formState.path" @click="onDownload()">
            {{ $t('source.common.downloadFile') }}
          </ShadcnButton>
        </div>
      </ShadcnFormItem>

      <ShadcnSpace>
        <ShadcnButton type="default" @click="onCancel">{{ $t('common.cancel') }}</ShadcnButton>

        <ShadcnButton submit :loading="loading" :disabled="loading">
          {{ $t('common.save') }}
        </ShadcnButton>
      </ShadcnSpace>
    </ShadcnForm>
  </ShadcnModal>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { StructureModel } from '@/model/structure.ts'

export default defineComponent({
  name: 'TableExport',
  computed: {
    visible: {
      get(): boolean
      {
        return this.isVisible
      },
      set(value: boolean)
      {
        this.$emit('close', value)
      }
    }
  },
  props: {
    isVisible: {
      type: Boolean
    },
    info: {
      type: Object as () => StructureModel | null
    }
  },
  data()
  {
    return {
      loading: false,
      title: null as string | null,
      formState: null as unknown as TableExportModel
    }
  },
  created()
  {
    this.formState = TableExportRequest.of()
    if (this.info) {
      this.title = this.$t('source.common.exportDataTable').replace('$VALUE', String(this.info.title))
    }
  },
  methods: {
    onSubmit()
    {
      if (this.info) {
        this.loading = true
        TableService.exportData(Number(this.info.value), this.formState)
                    .then(response => {
                      if (response.status) {
                        this.formState.path = response.data
                      }
                      else {
                        this.$Message.error({
                          content: response.message,
                          showIcon: true
                        })
                      }
                    })
                    .finally(() => this.loading = false)
      }
    },
    onDownload()
    {
      if (this.formState) {
        window.open(this.formState.path, '_target')
      }
    },
    onCancel()
    {
      this.visible = false
    }
  }
})
</script>
