<template>
  <div class="relative overflow-auto" style="height: 500px; max-height: 500px; max-width: 200px;">
    <ShadcnSpin v-model="loading" class="mt-2.5" fixed/>

    <ShadcnTree v-model="value"
                :data="data"
                :loadData="onLoadData"
                @on-node-click="onNodeClick">
      <template #label="{ node }">
        <div class="flex items-center space-x-1">
          <ShadcnIcon class="text-xs font-semibold text-gray-500"
                      size="16"
                      :icon="node.level === 2 ? 'Table' : 'Database'"/>
          <span class="text-xs font-normal text-gray-500">{{ node.title }}</span>
        </div>
      </template>
    </ShadcnTree>
  </div>
</template>
<script lang="ts">
import { defineComponent, watch } from 'vue'
import { StructureEnum, StructureModel } from '@/model/structure'

import DatabaseService from '@/services/metadata.ts'
import TableService from '@/services/table'
import ColumnService from '@/services/column'

import { ObjectUtils } from '@/utils/object'

export default defineComponent({
  name: 'MetadataTree',
  props: {
    code: {
      type: String
    }
  },
  data()
  {
    return {
      loading: false,
      value: [],
      data: Array<StructureModel>()
    }
  },
  created()
  {
    this.handleInitialize()
    watch(() => this.code, () => this.handleInitialize())
  },
  methods: {
    handleInitialize()
    {
      this.data = []
      this.loading = true
      DatabaseService.getAllBySource(this.code as string)
                     .then(response => {
                       if (response.status) {
                         response.data.forEach((item: any) => this.data.push({
                           title: item.name, value: item.code, catalog: item.catalog, code: item.code,
                           level: StructureEnum.DATABASE, isLeaf: false, children: []
                         }))
                       }
                     })
                     .finally(() => this.loading = false)
    },
    onLoadData(item: StructureModel, callback: any)
    {
      const children = [] as StructureModel[]
      if (item.level === StructureEnum.DATABASE) {
        TableService.getAllByDatabase(item.code as string)
                    .then(response => {
                      if (response.status) {
                        response.data.forEach((item: any) => children.push({
                          title: item.name, value: item.code, database: item.database.name, catalog: item.catalog,
                          level: StructureEnum.TABLE, type: item.type, engine: item.engine, isLeaf: false, children: []
                        }))
                      }
                    })
                    .finally(() => callback(children))
      }
      else if (item.level === StructureEnum.TABLE) {
        ColumnService.getAllByTable(item.code as string)
                     .then(response => {
                       if (response.status) {
                         response.data.forEach((item: any) => children.push({
                           title: item.name, value: item.code, database: item.table.database.name, table: item.table.name, catalog: item.catalog,
                           code: item.code, level: StructureEnum.COLUMN, type: item.type, engine: item.engine, isLeaf: false
                         }))
                       }
                     })
                     .finally(() => callback(children))
      }
      else {
        callback(children)
      }
    },
    onNodeClick(item: StructureModel)
    {
      let text: string = item.title as string
      switch (item.level) {
        case StructureEnum.TABLE:
          text = item.database + '.' + text
          break
        case StructureEnum.COLUMN:
          text = item.database + '.' + item.table + '.' + text
          break
      }
      ObjectUtils.copy(text)
    }
  }
})
</script>
