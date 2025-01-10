<template>
  <div class="relative min-h-screen">
    <ShadcnSpin v-if="loading" fixed/>

    <div v-if="dataInfo">
      <ShadcnRow class="mt-4" :gutter="20">
        <ShadcnCol span="4">
          <div class="flex items-center space-x-2">
            <ShadcnIcon icon="Type"/>
            <span>{{ $t('common.'+ dataInfo.type_name) }}</span>
          </div>
        </ShadcnCol>

        <ShadcnCol span="4">
          <div class="flex items-center space-x-2">
            <ShadcnIcon icon="Table"/>
            <span>{{ dataInfo.object_name }}</span>
          </div>
        </ShadcnCol>

        <ShadcnCol span="4">
          <ShadcnTooltip arrow :content="$t('common.createTime')">
            <div class="flex items-center space-x-2">
              <ShadcnIcon icon="Clock"/>
              <span>{{ dataInfo?.object_create_time === null ? $t('source.common.notSpecified') : dataInfo.object_create_time }}</span>
            </div>
          </ShadcnTooltip>
        </ShadcnCol>

        <ShadcnCol span="4">
          <ShadcnTooltip arrow :content="$t('common.updateTime')">
            <div class="flex items-center space-x-2">
              <ShadcnIcon icon="Clock"/>
              <span>{{ dataInfo?.object_update_time === null ? $t('source.common.notUpdated') : dataInfo.object_update_time }}</span>
            </div>
          </ShadcnTooltip>
        </ShadcnCol>

        <ShadcnCol span="4">
          <ShadcnTooltip arrow :content="$t('source.common.engine')">
            <div class="flex items-center space-x-2">
              <ShadcnIcon icon="CalendarHeart"/>
              <span>{{ dataInfo.object_engine === null ? $t('source.common.notSpecifiedEngine') : dataInfo.object_engine }}</span>
            </div>
          </ShadcnTooltip>
        </ShadcnCol>

        <ShadcnCol span="4">
          <ShadcnTooltip arrow :content="$t('source.common.collation')">
            <div class="flex items-center space-x-2">
              <ShadcnIcon icon="ArrowUpDown"/>
              <span>{{ dataInfo.object_collation === null ? $t('source.common.notSpecifiedCollation') : dataInfo.object_collation }}</span>
            </div>
          </ShadcnTooltip>
        </ShadcnCol>

        <ShadcnCol span="4">
          <ShadcnTooltip arrow :content="$t('source.common.totalRows')">
            <div class="flex items-center space-x-2">
              <ShadcnIcon icon="TableCellsMerge"/>
              <span>{{ dataInfo.object_rows }}</span>
            </div>
          </ShadcnTooltip>
        </ShadcnCol>

        <ShadcnCol span="4">
          <ShadcnTooltip arrow :content="$t('source.common.format')">
            <div class="flex items-center space-x-2">
              <ShadcnIcon icon="RemoveFormatting"/>
              <span>{{ dataInfo.object_format === null ? $t('source.common.notSpecifiedFormat') : dataInfo.object_format }}</span>
            </div>
          </ShadcnTooltip>
        </ShadcnCol>

        <ShadcnCol span="4">
          <ShadcnTooltip arrow :content="$t('source.common.avgRowLength')">
            <div class="flex items-center space-x-2">
              <ShadcnIcon icon="ArrowUp10"/>
              <span>{{ dataInfo.object_avg_row_length === null ? 0 : dataInfo.object_avg_row_length }}</span>
            </div>
          </ShadcnTooltip>
        </ShadcnCol>

        <ShadcnCol span="4">
          <ShadcnTooltip arrow :content="$t('source.common.dataSize')">
            <div class="flex items-center space-x-2">
              <ShadcnIcon icon="ArrowUpDown"/>
              <span>{{ dataInfo.object_data_size === null ? 0 : dataInfo.object_data_size }} MB</span>
            </div>
          </ShadcnTooltip>
        </ShadcnCol>

        <ShadcnCol span="4">
          <ShadcnTooltip arrow :content="$t('source.common.indexSize')">
            <div class="flex items-center space-x-2">
              <ShadcnIcon icon="Search"/>
              <span>{{ dataInfo.object_index_size === null ? $t('source.common.notSpecifiedIndex') : dataInfo.object_index_size }}</span>
            </div>
          </ShadcnTooltip>
        </ShadcnCol>

        <ShadcnCol span="4">
          <ShadcnTooltip arrow :content="$t('common.columnCount')">
            <div class="flex items-center space-x-2">
              <ShadcnIcon icon="Columns"/>
              <span>{{ dataInfo.object_column_count === null ? $t('source.common.notSpecifiedIndex') : dataInfo.object_column_count }}</span>
            </div>
          </ShadcnTooltip>
        </ShadcnCol>

        <ShadcnCol span="4">
          <ShadcnTooltip arrow :content="$t('common.indexCount')">
            <div class="flex items-center space-x-2">
              <ShadcnIcon icon="Search"/>
              <span>{{ dataInfo.object_index_count === null ? $t('source.common.notSpecifiedIndex') : dataInfo.object_index_count }}</span>
            </div>
          </ShadcnTooltip>
        </ShadcnCol>

        <ShadcnCol span="4">
          <div class="flex items-center space-x-4 justify-between">
            <ShadcnTooltip arrow :content="$t('source.common.autoIncrement')">
              <div class="flex items-center space-x-2">
                <ShadcnIcon icon="ArrowUpDown"/>
                <span>{{ dataInfo.object_auto_increment === null ? $t('source.common.notSpecifiedPrimaryKey') : dataInfo.object_auto_increment }}</span>
              </div>
            </ShadcnTooltip>
            <div>
              <ShadcnTooltip arrow :content="$t('source.common.resetAutoIncrement')">
                <ShadcnButton circle size="small" :disabled="dataInfo.object_auto_increment === null" @click="autoIncrement = true">
                  <template #icon>
                    <ShadcnIcon icon="Cog" :size="16"/>
                  </template>
                </ShadcnButton>
              </ShadcnTooltip>
            </div>
          </div>
        </ShadcnCol>

        <ShadcnCol span="12">
          <ShadcnDivider class="mt-4 mb-4"/>

          <ShadcnText class="mb-2">{{ $t('source.common.comment') }}</ShadcnText>

          <ShadcnInput v-model="dataInfo.object_comment" type="textarea"/>
        </ShadcnCol>
      </ShadcnRow>
    </div>
  </div>

  <TableAutoIncrement v-if="autoIncrement"
                      :visible="autoIncrement"
                      :info="dataInfo"
                      @close="autoIncrement = false"/>
</template>
<script lang="ts">
import { defineComponent, watch } from 'vue'
import MetadataService from '@/services/metadata.ts'
import TableAutoIncrement from '@/views/pages/admin/source/components/TableAutoIncrement.vue'

export default defineComponent({
  name: 'SourceTableInfo',
  components: { TableAutoIncrement },
  created()
  {
    this.handleInitialize()
    this.watchChange()
  },
  data()
  {
    return {
      loading: false,
      submitting: false,
      autoIncrement: false,
      dataInfo: null as any | null
    }
  },
  methods: {
    handleInitialize()
    {
      const code = this.$route?.params.source
      const database = this.$route?.params.database
      const table = this.$route?.params.table
      if (code && database && table) {
        this.loading = true
        MetadataService.getTable(code, database, table)
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
          () => this.$route?.params.table,
          () => this.handleInitialize()
      )
    }
  }
})
</script>
