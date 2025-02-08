<template>
  <ShadcnCard>
    <template #title>
      <div class="ml-2 font-normal text-sm">{{ $t('function.common.list') }}</div>
    </template>

    <template #extra>
      <ShadcnSpace>
        <ShadcnButton size="small" circle @click="handlerInfo(true, null)">
          <template #icon>
            <ShadcnIcon icon="Plus"/>
          </template>
        </ShadcnButton>
        <ShadcnTooltip :content="$t('function.common.import')">
          <ShadcnButton size="small" circle @click="handlerImport(true)">
            <template #icon>
              <ShadcnIcon icon="Import" size="16"/>
            </template>
          </ShadcnButton>
        </ShadcnTooltip>
      </ShadcnSpace>
    </template>

    <div class="relative">
      <ShadcnSpin v-if="loading" fixed/>

      <ShadcnTable size="small" :columns="headers" :data="data">
        <template #type="{ row }">
          <ShadcnTag :text="$t('function.common.' + row.type.toLowerCase())"/>
        </template>

        <template #plugin="{ row }">
          <ShadcnAvatarGroup :items="extractItem(row?.plugin)" size="small" max="3"/>
        </template>

        <template #action="{row}">
          <ShadcnTooltip :content="$t('common.editData')">
            <ShadcnButton size="small" circle @click="handlerInfo(true, row)">
              <ShadcnIcon icon="Pencil" size="15"/>
            </ShadcnButton>
          </ShadcnTooltip>
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

  <FunctionInfo v-if="dataInfoVisible"
                :is-visible="dataInfoVisible"
                :info="dataInfo"
                @close="handlerInfo(false, null)"/>

  <FunctionImport v-if="dataImportVisible" :is-visible="dataImportVisible" @close="handlerImport(false)"/>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { FilterModel } from '@/model/filter'
import { useHeaders } from '@/views/pages/system/function/FunctionUtils'
import FunctionService from '@/services/function'
import FunctionInfo from '@/views/pages/system/function/FunctionInfo.vue'
import FunctionImport from '@/views/pages/system/function/FunctionImport.vue'
import { FunctionModel } from '@/model/function'

export default defineComponent({
  name: 'FunctionHome',
  components: { FunctionImport, FunctionInfo },
  setup()
  {
    const filter: FilterModel = new FilterModel()
    const { headers } = useHeaders()

    return {
      filter,
      headers
    }
  },
  data()
  {
    return {
      loading: false,
      dataInfoVisible: false,
      dataImportVisible: false,
      data: [],
      pageIndex: 1,
      pageSize: 10,
      dataCount: 0,
      dataInfo: null as FunctionModel | null
    }
  },
  created()
  {
    this.handlerInitialize()
  },
  methods: {
    handlerInitialize()
    {
      this.loading = true
      FunctionService.getAll(this.filter)
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
      this.handlerInitialize()
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
    extractItem(plugins: string[])
    {
      return plugins.map((item: string) => {
        return {
          name: item,
          src: `/static/images/plugin/${ item.toLowerCase() }.svg`
        }
      })
    },
    handlerInfo(opened: boolean, value: FunctionModel | null)
    {
      this.dataInfoVisible = opened
      this.dataInfo = value
      if (!opened) {
        this.handlerInitialize()
      }
    },
    handlerImport(value: boolean)
    {
      this.dataImportVisible = value
      this.handlerInitialize()
    }
  }
})
</script>
