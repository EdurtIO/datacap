<template>
  <ShadcnCard>
    <template #title>
      <div class="ml-2 font-normal text-sm">{{ $t('dataset.common.list') }}</div>
    </template>

    <div class="relative">
      <ShadcnSpin v-if="loading" fixed/>

      <ShadcnTable size="small" :columns="headers" :data="data">
        <template #source="{row}">
          <ShadcnTooltip :content="row?.source.type">
            <ShadcnAvatar size="small" :src="'/static/images/plugin/' + row?.source.type.toLowerCase() + '.svg'" :alt="row?.source.type"/>
          </ShadcnTooltip>
        </template>

        <template #syncMode="{ row }">
          <ShadcnTag v-if="row?.syncMode === 'MANUAL'" :text=" $t('dataset.common.syncModeManual')"/>
          <ShadcnTag v-else-if="row?.syncMode === 'TIMING'" :text="$t('dataset.common.syncModeTiming')"/>
          <ShadcnTag v-else-if="row?.syncMode === 'OUT_SYNC'" :text="$t('dataset.common.syncModeOutSync')"/>
        </template>

        <template #state="{ row }">
          <ShadcnHoverCard>
            <template #content>
              <DatasetState :states="row?.state"/>
            </template>
            {{ getState(row?.state) }}
          </ShadcnHoverCard>
        </template>

        <template #action="{ row }">
          <ShadcnSpace>
            <ShadcnTooltip :content="$t('dataset.common.adhoc')">
              <ShadcnLink :link="`/admin/dataset/adhoc/${row?.code}`" target="_blank">
                <ShadcnButton circle size="small" :disabled="!isSuccess(row?.state)">
                  <ShadcnIcon icon="BarChart2" size="15"/>
                </ShadcnButton>
              </ShadcnLink>
            </ShadcnTooltip>

            <ShadcnDropdown trigger="click" position="right">
              <template #trigger>
                <ShadcnButton circle size="small">
                  <ShadcnIcon icon="Cog" size="15"/>
                </ShadcnButton>
              </template>

              <ShadcnDropdownItem>
                <RouterLink :to="`/admin/dataset/info/${row?.code}`" target="_blank" class="flex items-center">
                  <ShadcnIcon icon="Info" size="15"/>
                  <span class="ml-2">{{ $t('dataset.common.info') }}</span>
                </RouterLink>
              </ShadcnDropdownItem>

              <ShadcnDropdownItem :disabled="!isSuccess(row?.state)" @on-click="visibleSyncData(row, true)">
                <div class="flex items-center space-x-2">
                  <ShadcnIcon icon="RefreshCcw" size="15"/>
                  <span>{{ $t('dataset.common.syncData') }}</span>
                </div>
              </ShadcnDropdownItem>

              <ShadcnDropdownItem @on-click="visibleHistory(row, true)">
                <div class="flex items-center space-x-2">
                  <ShadcnIcon icon="History" size="15"/>
                  <span>{{ $t('dataset.common.history') }}</span>
                </div>
              </ShadcnDropdownItem>

              <ShadcnDropdownItem :disabled="isSuccess(row?.state)" @on-click="visibleError(row, true)">
                <div class="flex items-center space-x-2">
                  <ShadcnIcon icon="TriangleAlert" size="15"/>
                  <span>{{ $t('dataset.common.error') }}</span>
                </div>
              </ShadcnDropdownItem>

              <ShadcnDropdownItem :disabled="isSuccess(row?.state)" @on-click="visibleRebuild(row, true)">
                <div class="flex items-center space-x-2">
                  <ShadcnIcon :icon="row?.state === 'SUCCESS' ? 'CirclePlay' : 'CircleStop'" size="15"/>
                  <span>{{ $t('dataset.common.rebuild') }}</span>
                </div>
              </ShadcnDropdownItem>

              <ShadcnDropdownItem :disabled="!(row?.totalRows > 0)" @on-click="visibleClearData(row, true)">
                <div class="flex items-center space-x-2">
                  <ShadcnIcon icon="SquareX" size="15"/>
                  <span>{{ $t('dataset.common.clearData') }}</span>
                </div>
              </ShadcnDropdownItem>
            </ShadcnDropdown>
          </ShadcnSpace>
        </template>
      </ShadcnTable>

      <ShadcnPagination v-if="data?.length > 0"
                        v-model="pageIndex"
                        class="py-2"
                        show-total
                        show-sizer
                        :page-size="pageSize"
                        :total="dataCount"
                        :sizerOptions="[10, 20, 50]"
                        @on-change="onPageChange"
                        @on-prev="onPrevChange"
                        @on-next="onNextChange"
                        @on-change-size="onSizeChange"/>
    </div>
  </ShadcnCard>

  <DatasetRebuild v-if="rebuildVisible"
                  :is-visible="rebuildVisible"
                  :data="contextData"
                  @close="visibleRebuild(null, false)"/>

  <DatasetHistory v-if="historyVisible"
                  :is-visible="historyVisible"
                  :info="contextData"
                  @close="visibleHistory(null, false)"/>

  <DatasetSync v-if="syncDataVisible"
               :is-visible="syncDataVisible"
               :info="contextData"
               @close="visibleSyncData(null, false)"/>

  <DatasetClear v-if="clearDataVisible"
                :is-visible="clearDataVisible"
                :info="contextData"
                @close="visibleClearData(null, false)"/>

  <MarkdownPreview v-if="errorVisible && contextData"
                   :is-visible="errorVisible"
                   :content="'```java\n' + contextData.message + '\n```'"
                   @close="visibleError(null, false)"/>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { FilterModel } from '@/model/filter'
import { useDatasetHeaders } from './DatasetUtils'
import DatasetService from '@/services/dataset'
import { DatasetModel } from '@/model/dataset'
import DatasetState from '@/views/pages/admin/dataset/components/DatasetState.vue'
import DatasetSync from '@/views/pages/admin/dataset/DatasetSync.vue'
import DatasetHistory from '@/views/pages/admin/dataset/DatasetHistory.vue'
import MarkdownPreview from '@/views/components/markdown/MarkdownView.vue'
import DatasetRebuild from '@/views/pages/admin/dataset/DatasetRebuild.vue'
import DatasetClear from '@/views/pages/admin/dataset/DatasetClear.vue'

export default defineComponent({
  name: 'DatasetHome',
  components: { DatasetClear, DatasetRebuild, MarkdownPreview, DatasetHistory, DatasetSync, DatasetState },
  setup()
  {
    const filter: FilterModel = new FilterModel()
    const { headers } = useDatasetHeaders()

    return {
      filter,
      headers
    }
  },
  data()
  {
    return {
      loading: false,
      data: [],
      pageIndex: 1,
      pageSize: 10,
      dataCount: 0,
      contextData: null as DatasetModel | null,
      rebuildVisible: false,
      historyVisible: false,
      syncDataVisible: false,
      clearDataVisible: false,
      errorVisible: false
    }
  },
  created()
  {
    this.handleInitialize()
  },
  methods: {
    handleInitialize()
    {
      this.loading = true
      DatasetService.getAll(this.filter)
                    .then((response) => {
                      if (response.status) {
                        this.data = response.data.content
                        this.dataCount = response.data.total
                        this.pageSize = response.data.size
                        this.pageIndex = response.data.page
                      }
                    })
                    .finally(() => this.loading = false)
    },
    fetchData(value: number)
    {
      this.filter.page = value
      this.filter.size = this.pageSize
      this.handleInitialize()
    },
    onPageChange(value: number)
    {
      this.fetchData(value)
    },
    onPrevChange(value: number)
    {
      this.fetchData(value)
    },
    onNextChange(value: number)
    {
      this.fetchData(value)
    },
    onSizeChange(value: number)
    {
      this.pageSize = value
      this.fetchData(this.pageIndex)
    },
    visibleRebuild(record: DatasetModel | null, opened: boolean)
    {
      if (record && this.isSuccess(record.state)) {
        return
      }
      this.rebuildVisible = opened
      this.contextData = record
    },
    visibleHistory(record: DatasetModel | null, opened: boolean)
    {
      this.contextData = record
      this.historyVisible = opened
    },
    visibleSyncData(record: DatasetModel | null, opened: boolean)
    {
      if (record && !this.isSuccess(record.state)) {
        return
      }
      this.contextData = record
      this.syncDataVisible = opened
    },
    visibleClearData(record: DatasetModel | null, opened: boolean)
    {
      if (record && !(record.totalRows > 0)) {
        return
      }
      this.contextData = record
      this.clearDataVisible = opened
      if (!opened) {
        this.handleInitialize()
      }
    },
    visibleError(record: DatasetModel | null, opened: boolean)
    {
      this.errorVisible = opened
      this.contextData = record
    },
    getState(state: Array<any> | null): string | null
    {
      if (state && state.length > 0) {
        return state[state.length - 1]
      }
      return null
    },
    isSuccess(state: Array<any> | null)
    {
      if (state && state.length > 0) {
        return state[state.length - 1].endsWith('SUCCESS')
      }
      return false
    }
  }
})

</script>
