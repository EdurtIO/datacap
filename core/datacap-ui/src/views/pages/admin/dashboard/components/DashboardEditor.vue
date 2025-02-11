<template>
  <ShadcnCard :title="title">
    <template #extra>
      <ShadcnSpace>
        <ShadcnButton type="default" @click="visibleAddReport(true)">
          {{ $t('dashboard.common.addReport') }}
        </ShadcnButton>

        <ShadcnButton @click="visibleSave(true)">
          {{ $t('common.save') }}
        </ShadcnButton>
      </ShadcnSpace>
    </template>

    <div class="min-h-screen">
      <GridLayout ref="refLayout"
                  :layout="layouts"
                  :responsive="true"
                  :col-num="12"
                  :row-height="60"
                  :is-draggable="true"
                  :is-resizable="true"
                  :vertical-compact="true"
                  :use-css-transforms="true">
        <GridItem v-for="item in layouts"
                  :ref="el => set$Children(el)"
                  :key="item.i"
                  :x="item.x"
                  :y="item.y"
                  :w="item.w"
                  :h="item.h"
                  :i="item.i"
                  :min-h="3"
                  :min-w="3"
                  @resized="onResize">
          <ShadcnCard class="h-full w-full">
            <template #title>{{ item.title ? item.title : $t('dataset.common.notSpecifiedTitle') }}</template>

            <template #extra>
              <ShadcnButton circle size="small" type="error" @click="onRemove(item.i)">
                <ShadcnIcon icon="Trash" size="15"/>
              </ShadcnButton>
            </template>

            <VisualView v-if="item.original"
                        :width="calculateWidth(item)"
                        :height="calculateHeight(item)"
                        :code="item.node.code"
                        :configuration="JSON.parse(item.node.configure)"
                        :type="item.original?.type"
                        :query="item.original.type === 'DATASET' ? JSON.parse(item.original.query as string) : item.original.query"
                        :original="item?.original?.source?.code"/>

            <VisualView v-else
                        :width="calculateWidth(item)"
                        :height="calculateHeight(item)"
                        :code="item.node.code"
                        :configuration="JSON.parse(item.node.configure)"
                        :query="JSON.parse(item.node.query)"/>
          </ShadcnCard>
        </GridItem>
      </GridLayout>
    </div>
  </ShadcnCard>

  <ShadcnModal v-model="configureVisible" :title="$t('common.configure')">
    <ShadcnForm v-if="configureVisible && formState"
                v-model="formState"
                @on-submit="onSubmit">
      <ShadcnFormItem name="name"
                      :label="$t('common.name')"
                      :rules="[{ required: true, message: $t('common.name') }]">
        <ShadcnInput v-model="formState.name" name="name"/>
      </ShadcnFormItem>

      <ShadcnFormItem name="description" :label="$t('common.description')">
        <ShadcnInput v-model="formState.description" type="textarea" name="description"/>
      </ShadcnFormItem>

      <ShadcnFormItem name="avatar" :label="$t('common.avatar')">
        <CropperHome :pic="formState?.avatar?.path" @update:value="onCropper"/>
      </ShadcnFormItem>

      <div class="flex justify-end">
        <ShadcnSpace>
          <ShadcnButton type="default" @click="configureVisible = false">
            {{ $t('common.cancel') }}
          </ShadcnButton>

          <ShadcnButton submit :loading="loading" :disabled="loading">
            {{ $t('common.save') }}
          </ShadcnButton>
        </ShadcnSpace>
      </div>
    </ShadcnForm>
  </ShadcnModal>

  <ChartContainer v-if="dataReportVisible"
                  :is-visible="dataReportVisible"
                  @change="onChange"
                  @close="visibleAddReport(false)"/>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import { GridItem, GridLayout } from 'vue3-grid-layout-next'
import DashboardService from '@/services/dashboard'
import { ReportModel } from '@/model/report.ts'
import VisualView from '@/views/components/visual/VisualView.vue'
import { DashboardModel, DashboardRequest } from '@/model/dashboard.ts'
import CropperHome from '@/views/components/cropper/CropperHome.vue'
import UploadService from '@/services/upload'
import ChartContainer from '@/views/pages/admin/dashboard/components/ChartContainer.vue'

export default defineComponent({
  name: 'DashboardEditor',
  components: {
    ChartContainer, CropperHome, VisualView, GridItem, GridLayout
  },
  props: {
    info: {
      type: Object as PropType<DashboardModel | null>,
      default: null
    }
  },
  data()
  {
    return {
      title: null as string | null,
      reportItems: [] as ReportModel[],
      mapCache: new Map(),
      columnNumber: 12,
      layouts: [] as any[],
      loading: false,
      configureVisible: false,
      formState: null as DashboardModel | null,
      dataReportVisible: false,
      rowHeight: 60
    }
  },
  created()
  {
    this.handleInitialize()
  },
  methods: {
    handleInitialize()
    {
      setTimeout(() => {
        if (!this.info) {
          this.title = this.$t('dashboard.common.create')
          this.formState = DashboardRequest.of()
        }
        else {
          this.title = this.$t('dashboard.common.modifyInfo').replace('$VALUE', String(this.info.name))
          this.formState = {
            code: this.info.code,
            name: this.info.name,
            configure: this.info.configure,
            version: this.info.version,
            description: this.info.description,
            avatar: this.info.avatar,
            reports: this.info.reports?.map(report => ({ code: report.code })) || []
          }
          this.layouts = JSON.parse(String(this.info.configure))
        }
      }, 300)
    },
    // Modified resize handler
    onResize(i: string | number, w: number, h: number)
    {
      const node = this.layouts.find((obj: { i: string; }) => obj.i === i)
      if (node) {
        node.w = w
        node.h = h
      }
    },
    onRemove(i: string | number)
    {
      this.layouts = this.layouts.filter((obj: { i: string; }) => obj.i !== i)
    },
    // The method of calculating the width
    calculateWidth(item: any): string
    {
      const widthPercentage = (item.w * (100 / this.columnNumber))
      return `calc(${ widthPercentage }% - rem)` // Subtract margins
    },
    // How to calculate the height
    calculateHeight(item: any): string
    {
      const totalHeight = item.h * this.rowHeight
      return `${ totalHeight - 48 }px` // Subtract the height of the card head
    },
    onCropper(value: any)
    {
      const configure = {
        code: this.formState?.code,
        mode: 'DASHBOARD',
        file: value
      }
      UploadService.upload(configure)
                   .then(response => {
                     if (response.status) {
                       if (this.formState) {
                         this.formState.avatar = response.data
                       }
                       this.$Message.success({
                         content: this.$t('common.successfully'),
                         showIcon: true
                       })
                     }
                     else {
                       this.$Message.error({
                         content: response.message,
                         showIcon: true
                       })
                     }
                   })
    },
    onChange(nodes: ReportModel[])
    {
      nodes.forEach((node: ReportModel) => {
        const newItem = {
          x: 0,
          y: 0,
          w: 3,
          h: 4,
          i: 'new-' + Date.now(),
          title: node.name,
          node: {
            id: node.code,
            configure: node.configure,
            code: node.dataset?.code,
            query: node?.query
          },
          original: node
        }
        this.layouts.push(newItem)
      })
    },
    onSubmit()
    {
      if (this.formState) {
        this.formState.configure = JSON.stringify(this.layouts)
        this.layouts.forEach((item: { original: { code: any; }; }) => {
          this.formState?.reports?.push({ code: item.original.code })
        })
        this.formState.version = '1.0'
        this.loading = true
        DashboardService.saveOrUpdate(this.formState)
                        .then(response => {
                          if (response.status) {
                            this.$Message.success({
                              content: this.$t('dashboard.tip.publishSuccess').replace('$VALUE', String(this.formState?.name)),
                              showIcon: true
                            })
                            if (response.data) {
                              this.$router.push(`/admin/dashboard/preview/${ response.data?.code }`)
                            }
                            else {
                              this.$router.push('/console/dashboard')
                            }
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
    visibleSave(opened: boolean)
    {
      this.configureVisible = opened
    },
    visibleAddReport(opened: boolean)
    {
      this.dataReportVisible = opened
    },
    set$Children(vm: any)
    {
      if (vm && vm.i) {
        this.mapCache.set(vm.i, vm)
      }
    }
  }
})
</script>
