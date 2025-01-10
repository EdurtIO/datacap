<template>
  <div class="relative h-screen">
    <ShadcnSpin v-model="loading" fixed/>

    <ErDiagram v-if="!loading && options" :options="options"/>
  </div>
</template>
<script lang="ts">
import { defineComponent, watch } from 'vue'
import ErDiagram from '@/views/components/diagram/ErDiagram.vue'
import { ErDiagramOptions } from '@/views/components/diagram/ErDiagramOptions.ts'
import MetadataService from '@/services/metadata.ts'

export default defineComponent({
  name: 'SourceTableErDiagram',
  components: { ErDiagram },
  data()
  {
    return {
      loading: false,
      options: null as unknown as ErDiagramOptions
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
        MetadataService.getColumnsByTable(code, database, table)
                       .then(response => {
                         if (response.status && response.data && response.data.isSuccessful) {
                           const table = response.data[0]
                           this.options = new ErDiagramOptions()
                           this.options.table = { id: table, name: table }
                           this.options.columns = response.data.columns
                                                          .filter(col => col.type_name === 'column')
                                                          .map(col => ({ id: col.object_name, name: col.object_name, type: col.object_data_type }))
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
