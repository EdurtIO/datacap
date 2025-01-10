<template>
  <ShadcnModal v-model="visible"
               width="40%"
               :title="title"
               @on-close="onCancel">
    <ShadcnSpace wrap>
      <ShadcnAlert type="error" :title="$t('source.tip.dropTable1')"/>
      <ShadcnAlert type="error" :title="$t('source.tip.dropTable2')"/>
      <ShadcnAlert type="error" :title="$t('source.tip.dropTable3')"/>
      <ShadcnAlert type="error" :title="$t('source.tip.dropTable4')"/>
      <ShadcnAlert type="error" :title="$t('source.tip.dropTable5')"/>
    </ShadcnSpace>

    <div class="relative mt-2">
      <ShadcnSpin v-model="loading"/>

      <AceEditor v-if="!loading && formState.statement" :value="formState.statement" :read-only="true"/>
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
          {{ $t('source.common.dropTable') }}
        </ShadcnButton>
      </ShadcnSpace>
    </template>
  </ShadcnModal>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import MetadataService from '@/services/metadata'
import AceEditor from '@/views/components/editor/AceEditor.vue'

export default defineComponent({
  name: 'TableDrop',
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
    }
  },
  data()
  {
    return {
      loading: false,
      submitting: false,
      title: null as string | null,
      formState: null as any
    }
  },
  created()
  {
    this.formState = {
      preview: false,
      statement: null
    }

    const table = this.$route.params?.table
    if (table) {
      this.title = this.$t('source.common.dropTableInfo').replace('$VALUE', table)
      this.onSubmit(true)
    }
  },
  methods: {
    onSubmit(preview: boolean)
    {
      const code = this.$route.params?.source
      const database = this.$route.params?.database
      const table = this.$route.params?.table

      if (code && database && table) {
        if (preview) {
          this.loading = true
        }
        else {
          this.submitting = true
        }
        MetadataService.dropTable(code, database, table, { preview })
                       .then(response => {
                         if (response.status) {
                           if (preview) {
                             this.formState.statement = response.data.content
                           }
                           else {
                             this.$Message.success({
                               content: this.$t('source.tip.dropTableSuccess').replace('$VALUE', table),
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
