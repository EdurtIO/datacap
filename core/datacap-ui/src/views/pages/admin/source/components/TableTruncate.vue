<template>
  <ShadcnModal v-model="visible"
               :title="title"
               width="60%"
               @on-close="onCancel">
    <ShadcnSpace wrap>
      <ShadcnAlert type="error" :title="$t('source.tip.truncateTable1')"/>
      <ShadcnAlert type="error" :title="$t('source.tip.truncateTable2')"/>
      <ShadcnAlert type="error" :title="$t('source.tip.truncateTable3')"/>
      <ShadcnAlert type="error" :title="$t('source.tip.truncateTable4')"/>
      <ShadcnAlert type="error" :title="$t('source.tip.truncateTable5')"/>
    </ShadcnSpace>

    <div class="relative">
      <ShadcnSpin v-model="loading"/>

      <AceEditor v-if="!loading && formState.statement"
                 class="mt-2"
                 :value="formState.statement"
                 :read-only="true"/>
    </div>

    <template #footer>
      <ShadcnSpace>
        <ShadcnButton type="default" @click="onCancel">
          {{ $t('common.cancel') }}
        </ShadcnButton>

        <ShadcnButton type="error" :loading="submitting" :disabled="submitting" @click="onSubmit(false)">
          {{ $t('source.common.truncateTable') }}
        </ShadcnButton>
      </ShadcnSpace>
    </template>
  </ShadcnModal>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import AceEditor from '@/views/components/editor/AceEditor.vue'
import MetadataService from '@/services/metadata.ts'

export default defineComponent({
  name: 'TableTruncate',
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
      statement: null
    }

    const table = this.$route.params?.table
    if (table) {
      this.title = this.$t('source.common.truncateTable').replace('$VALUE', table)

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

        MetadataService.truncateTable(code, database, table, { preview })
                       .then(response => {
                         if (response.status) {
                           if (preview) {
                             this.formState.statement = response.data.content
                           }
                           else {
                             this.$Message.success({
                               content: this.$t('source.tip.truncateTableSuccess').replace('$VALUE', table),
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
