<template>
  <ShadcnCard :title="$t('dashboard.common.list')">
    <template #extra>
      <ShadcnTooltip :content="$t('dashboard.common.create')">
        <ShadcnLink link="/admin/dashboard/info">
          <ShadcnButton circle size="small">
            <ShadcnIcon icon="Plus" size="15"/>
          </ShadcnButton>
        </ShadcnLink>
      </ShadcnTooltip>
    </template>

    <div class="mb-3 min-h-screen p-2">
      <ShadcnSpin v-if="loading" fixed/>

      <ShadcnRow :gutter="16">
        <ShadcnCol v-for="item in data" span="3">
          <ShadcnCard>
            <template #title>
              <RouterLink :to="`/admin/dashboard/preview/${item.code}`" target="_blank">{{ item.name }}</RouterLink>
            </template>

            <template #extra>
              <ShadcnSpace class="items-center">
                <div v-if="item.description" class="cursor-pointer mt-1">
                  <ShadcnTooltip :content="item.description">
                    <ShadcnIcon icon="Info" size="20"/>
                  </ShadcnTooltip>
                </div>

                <ShadcnDropdown trigger="click" position="right">
                  <template #trigger>
                    <ShadcnButton circle size="small">
                      <ShadcnIcon icon="Cog" size="18"/>
                    </ShadcnButton>
                  </template>

                  <ShadcnDropdownItem>
                    <RouterLink :to="`/admin/dashboard/info/${item.code}`" target="_blank" class="flex items-center space-x-2">
                      <ShadcnIcon icon="Pencil" size="15"/>
                      <span>{{ $t('dashboard.common.modify') }}</span>
                    </RouterLink>
                  </ShadcnDropdownItem>

                  <ShadcnDropdownItem @on-click="visibleDelete(true, item)">
                    <div class="flex items-center space-x-2">
                      <ShadcnIcon icon="Trash" size="15"/>
                      <span>{{ $t('dashboard.common.delete') }}</span>
                    </div>
                  </ShadcnDropdownItem>
                </ShadcnDropdown>
              </ShadcnSpace>
            </template>

            <div class="p-3">
              <div class="aspect-video w-full relative overflow-hidden rounded-md flex items-center justify-center">
                <img class="w-32 h-32 object-cover"
                     :src="`${item.avatar?.path ? item.avatar.path : '/static/images/dashboard.png'}`"
                     :alt="item.name"/>
              </div>

              <div class="text-xs text-muted-foreground text-right mt-2">{{ item.createTime }}</div>
            </div>
          </ShadcnCard>
        </ShadcnCol>

        <ShadcnCol span="12">
          <div v-if="!loading && data.length === 0" class="text-center">
            {{ $t('common.noData') }}
          </div>
        </ShadcnCol>
      </ShadcnRow>

      <ShadcnPagination v-if="data.length > 0"
                        v-model="pageIndex"
                        class="py-2"
                        show-total
                        show-sizer
                        :page-size="pageSize"
                        :total="dataCount"
                        :sizerOptions="[12, 24, 36]"
                        @on-change="onPageChange"
                        @on-prev="onPrevChange"
                        @on-next="onNextChange"
                        @on-change-size="onSizeChange"/>
    </div>
  </ShadcnCard>

  <DashboardDelete v-if="deleteVisible"
                   :is-visible="deleteVisible"
                   :data="dataInfo"
                   @close="visibleDelete(false, null)"/>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import DashboardService from '@/services/dashboard'
import { FilterModel } from '@/model/filter'
import { DashboardModel } from '@/model/dashboard'
import DashboardDelete from '@/views/pages/admin/dashboard/DashboardDelete.vue'

export default defineComponent({
  name: 'DashboardHome',
  components: { DashboardDelete },
  setup()
  {
    const filter: FilterModel = new FilterModel()
    filter.size = 12

    return {
      filter
    }
  },
  data()
  {
    return {
      loading: false,
      deleteVisible: false,
      data: [] as DashboardModel[],
      pageIndex: 1,
      pageSize: 10,
      dataCount: 0,
      dataInfo: null as DashboardModel | null
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
      DashboardService.getAll(this.filter)
                      .then(response => {
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
    visibleDelete(opened: boolean, data: DashboardModel | null)
    {
      this.deleteVisible = opened
      this.dataInfo = data
      if (!opened) {
        this.handleInitialize()
      }
    }
  }
})
</script>
