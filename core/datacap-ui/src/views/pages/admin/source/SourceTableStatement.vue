<template>
  <div class="relative min-h-screen">
    <ShadcnSpin v-model="loading" fixed/>

    <AceEditor v-if="!loading"
               :value="statement as string"
               :read-only="true"
               :height="'100vh'"/>
  </div>
</template>

<script lang="ts">
import { defineComponent, watch } from 'vue'
import AceEditor from '@/views/components/editor/AceEditor.vue'
import MetadataService from '@/services/metadata'

export default defineComponent({
  name: 'SourceTableStatement',
  components: { AceEditor },
  data()
  {
    return {
      loading: false,
      statement: null as string | null,
      formState: null as any
    }
  },
  created()
  {
    this.handleInitialize()
    this.watchChange()
  },
  methods: {
    handleInitialize()
    {
      const code = this.$route?.params.source
      const database = this.$route?.params.database
      const table = this.$route?.params.table

      if (code && database && table) {
        this.loading = true
        this.statement = null
        MetadataService.getTableStatement(code, database, table)
                       .then(response => {
                         if (response.status && response.data && response.data.isSuccessful) {
                           const content = response.data.columns[0]
                           this.statement = content.create_table_sql
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
    watchChange()
    {
      watch(
          () => this.$route?.params.table,
          () => this.handleInitialize()
      )
    }
  }
})
</script>
