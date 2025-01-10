<template>
  <ShadcnModal v-model="visible"
               width="40%"
               :title="title"
               @on-close="onCancel">
    <ShadcnSpace wrap>
      <ShadcnAlert type="error" :title="$t('source.tip.dropColumn1')"/>
      <ShadcnAlert type="error" :title="$t('source.tip.dropColumn2')"/>
      <ShadcnAlert type="error" :title="$t('source.tip.dropColumn3')"/>
      <ShadcnAlert type="error" :title="$t('source.tip.dropColumn4')"/>
      <ShadcnAlert type="error" :title="$t('source.tip.dropColumn5')"/>
    </ShadcnSpace>

    <div class="relative mt-2">
      <ShadcnSpin v-model="loading"/>

      <AceEditor v-if="formState.statement" :value="formState.statement" :read-only="true"/>
    </div>

    <template #footer>
      <ShadcnSpace>
        <ShadcnButton type="default" @click="onCancel">
          {{ $t('common.cancel') }}
        </ShadcnButton>

        <ShadcnButton type="error"
                      :loading="submitting"
                      :disabled="submitting"
                      @click="onSubmit(false)">
          {{ $t('source.common.dropColumn') }}
        </ShadcnButton>
      </ShadcnSpace>
    </template>
  </ShadcnModal>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { StructureModel } from '@/model/structure.ts'

import AceEditor from '@/views/components/editor/AceEditor.vue'

export default defineComponent({
  name: 'ColumnDrop',
  components: { AceEditor },
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
      submitting: false,
      title: null as string | null,
      formState: null as unknown as TableFilter
    }
  },
  created()
  {
    this.formState = TableFilterRequest.of()
    this.formState.type = SqlType.DROP
    if (this.info) {
      this.formState.columnId = Number(this.info.applyId)
      this.title = this.$t('source.common.dropColumnInfo').replace('$VALUE', this.info.title as string)

      this.onSubmit(true)
    }
  },
  methods: {
    onSubmit(preview: boolean)
    {
      if (this.info && this.formState) {
        if (preview) {
          this.loading = true
        }
        else {
          this.submitting = true
        }
        this.formState.preview = preview
        TableService.getData(String(this.info.tableId), this.formState)
                    .then(response => {
                      if (response.status) {
                        if (preview) {
                          this.formState.statement = response.data.content
                        }
                        else {
                          this.$Message.success({
                            content: this.$t('source.tip.dropColumnSuccess').replace('$VALUE', String(this.info?.title)),
                            showIcon: true
                          })

                          this.onCancel()
                        }
                      }
                      else {
                        this.$Message.error({
                          content: response.message,
                          showIcon: true
                        })
                      }
                    })
                    .finally(() => {
                      if (preview) {
                        this.loading = false
                      }
                      else {
                        this.submitting = false
                      }
                    })
      }
    },
    onCancel()
    {
      this.visible = false
    }
  }
})
</script>