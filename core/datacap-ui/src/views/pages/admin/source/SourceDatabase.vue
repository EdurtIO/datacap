<template>
  <div class="relative min-h-screen">
    <ShadcnSpin v-if="loading" fixed/>

    <div v-if="dataInfo">
      <ShadcnRow class="mt-4" :gutter="20">
        <ShadcnCol span="4">
          <div class="flex items-center space-x-2">
            <ShadcnIcon icon="Database"/>
            <span>{{ dataInfo.object_name }}</span>
          </div>
        </ShadcnCol>

        <ShadcnCol span="4">
          <ShadcnTooltip arrow :content="$t('common.createTime')">
            <div class="flex items-center space-x-2">
              <ShadcnIcon icon="Clock"/>
              <span>{{ dataInfo.object_create_time === null ? $t('source.common.notSpecified') : dataInfo.object_create_time }}</span>
            </div>
          </ShadcnTooltip>
        </ShadcnCol>

        <ShadcnCol span="4">
          <ShadcnTooltip arrow :content="$t('common.updateTime')">
            <div class="flex items-center space-x-2">
              <ShadcnIcon icon="Clock"/>
              <span>{{ dataInfo.object_update_time === null ? $t('source.common.notUpdated') : dataInfo.object_update_time }}</span>
            </div>
          </ShadcnTooltip>
        </ShadcnCol>

        <ShadcnCol span="4">
          <div class="flex items-center space-x-2">
            <span>{{ $t('common.charset') }}</span>
            <span class="text-gray-500">{{ dataInfo.object_charset }}</span>
          </div>
        </ShadcnCol>

        <ShadcnCol span="4">
          <div class="flex items-center space-x-2">
            <span>{{ $t('common.collation') }}</span>
            <span class="text-gray-500">{{ dataInfo.object_collation }}</span>
          </div>
        </ShadcnCol>

        <ShadcnCol span="4">
          <div class="flex items-center space-x-2">
            <span>{{ $t('common.dataSize') }}</span>
            <span class="text-gray-500">{{ dataInfo.object_data_size }} MB</span>
          </div>
        </ShadcnCol>

        <ShadcnCol span="4">
          <div class="flex items-center space-x-2">
            <span>{{ $t('common.indexSize') }}</span>
            <span class="text-gray-500">{{ dataInfo.object_index_size }} MB</span>
          </div>
        </ShadcnCol>

        <ShadcnCol span="4">
          <div class="flex items-center space-x-2">
            <span>{{ $t('common.totalSize') }}</span>
            <span class="text-gray-500">{{ dataInfo.object_total_size }}</span>
          </div>
        </ShadcnCol>

        <ShadcnCol span="4">
          <div class="flex items-center space-x-2">
            <span>{{ $t('common.tableCount') }}</span>
            <span class="text-gray-500">{{ dataInfo.object_table_count }}</span>
          </div>
        </ShadcnCol>

        <ShadcnCol span="4">
          <div class="flex items-center space-x-2">
            <span>{{ $t('common.columnCount') }}</span>
            <span class="text-gray-500">{{ dataInfo.object_column_count }}</span>
          </div>
        </ShadcnCol>

        <ShadcnCol span="4">
          <div class="flex items-center space-x-2">
            <span>{{ $t('common.indexCount') }}</span>
            <span class="text-gray-500">{{ dataInfo.object_index_count }}</span>
          </div>
        </ShadcnCol>

        <ShadcnCol span="4">
          <div class="flex items-center space-x-2">
            <span>{{ $t('common.viewCount') }}</span>
            <span class="text-gray-500">{{ dataInfo.object_view_count }}</span>
          </div>
        </ShadcnCol>

        <ShadcnCol span="4">
          <div class="flex items-center space-x-2">
            <span>{{ $t('common.procedureCount') }}</span>
            <span class="text-gray-500">{{ dataInfo.object_procedure_count }}</span>
          </div>
        </ShadcnCol>

        <ShadcnCol span="4">
          <div class="flex items-center space-x-2">
            <span>{{ $t('common.triggerCount') }}</span>
            <span class="text-gray-500">{{ dataInfo.object_trigger_count }}</span>
          </div>
        </ShadcnCol>

        <ShadcnCol span="4">
          <div class="flex items-center space-x-2">
            <span>{{ $t('common.foreignKeyCount') }}</span>
            <span class="text-gray-500">{{ dataInfo.object_foreign_key_count }}</span>
          </div>
        </ShadcnCol>

        <ShadcnCol span="4">
          <div class="flex items-center space-x-2">
            <span>{{ $t('common.totalCount') }}</span>
            <span class="text-gray-500">{{ dataInfo.object_total_rows }}</span>
          </div>
        </ShadcnCol>
      </ShadcnRow>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, watch } from 'vue'
import MetadataService from '@/services/metadata.ts'

export default defineComponent({
  name: 'SourceDatabase',
  created()
  {
    this.handleInitialize()
    this.watchChange()
  },
  data()
  {
    return {
      loading: false,
      dataInfo: null as any | null
    }
  },
  methods: {
    handleInitialize()
    {
      const code = this.$route?.params.source
      const database = this.$route?.params.database
      if (code && database) {
        this.loading = true
        MetadataService.getDatabase(code, database)
                       .then(response => {
                         if (response.status && response.data && response.data.isSuccessful) {
                           this.dataInfo = response.data.columns[0] || null
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
          () => this.$route?.params.database,
          () => this.handleInitialize()
      )
    }
  }
})
</script>
