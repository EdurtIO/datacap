<template>
  <ShadcnCard>
    <template #title>
      <div class="ml-2 font-normal text-sm">{{ $t('user.common.list') }}</div>
    </template>

    <template #extra>
      <ShadcnButton size="small" circle @click="handlerChangeInfo(true, null)">
        <template #icon>
          <ShadcnIcon icon="Plus"/>
        </template>
      </ShadcnButton>
    </template>

    <div class="relative">
      <ShadcnSpin v-if="loading" fixed/>

      <ShadcnTable size="small" :columns="headers" :data="data">
        <template #role="{row}">
          <ShadcnTag v-for="role in row.roles" :text="role.name"/>
        </template>

        <template #action="{row}">
          <ShadcnSpace>
            <ShadcnTooltip :content="$t('user.common.assignRole')">
              <ShadcnButton size="small" circle @click="handlerChangeRole(true, row)">
                <ShadcnIcon icon="SquareArrowUp" size="15"/>
              </ShadcnButton>
            </ShadcnTooltip>

            <ShadcnTooltip :content="$t('common.editData')">
              <ShadcnButton size="small" circle @click="handlerChangeInfo(true, row)">
                <ShadcnIcon icon="Pencil" size="15"/>
              </ShadcnButton>
            </ShadcnTooltip>
          </ShadcnSpace>
        </template>
      </ShadcnTable>

      <ShadcnPagination v-model="pageIndex"
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

  <UserRole v-if="dataRoleVisible"
            :is-visible="dataRoleVisible"
            :info="dataInfo"
            @close="handlerChangeRole(false, null)"/>

  <UserInfo v-if="dataInfoVisible"
            :is-visible="dataInfoVisible"
            :info="dataInfo"
            @close="handlerChangeInfo(false, null)"/>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { FilterModel } from '@/model/filter'
import UserService from '@/services/user'
import { useHeaders } from './UserUtils'
import { UserModel } from '@/model/user'
import UserInfo from '@/views/pages/system/user/UserInfo.vue'
import UserRole from '@/views/pages/system/user/components/UserRole.vue'

export default defineComponent({
  name: 'UserHome',
  components: { UserRole, UserInfo },
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
      dataRoleVisible: false,
      data: [],
      pageIndex: 1,
      pageSize: 10,
      dataCount: 0,
      dataInfo: null as UserModel | null,
      dataInfoVisible: false
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
      UserService.getAll(this.filter)
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
    handlerChangeRole(isOpen: boolean, dataInfo: UserModel | null)
    {
      this.dataRoleVisible = isOpen
      this.dataInfo = dataInfo
      if (!isOpen) {
        this.handlerInitialize()
      }
    },
    handlerChangeInfo(opened: boolean, dataInfo: UserModel | null)
    {
      this.dataInfoVisible = opened
      this.dataInfo = dataInfo
      if (!opened) {
        this.handlerInitialize()
      }
    }
  }
})
</script>
