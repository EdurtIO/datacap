<template>
  <ShadcnCard :border="false">
    <ShadcnSelect v-model="selectDatabase" @on-change="onChangeDatabase">
      <template #options>
        <ShadcnSelectOption v-for="item in databaseArray" :label="item.title" :value="item.code"/>
      </template>
    </ShadcnSelect>

    <div class="relative h-screen overflow-x-auto overflow-y-auto">
      <ShadcnSpin v-if="loading" fixed/>

      <ShadcnTree v-model="databaseModel"
                  :data="dataTreeArray"
                  :loadData="onLoadData"
                  @on-node-click="onNodeClick">
        <template #label="{ node }">
          <div class="flex items-center space-x-1" @contextmenu.prevent="visibleContextMenu($event, node)">
            <ShadcnIcon v-if="node.level === StructureEnum.TYPE && node.type === 'table'"
                        class="text-xs font-semibold text-gray-500"
                        size="16"
                        icon="Table"/>
            <ShadcnIcon v-else-if="node.level === StructureEnum.TYPE && node.type === 'view'"
                        class="text-xs font-semibold text-gray-500"
                        size="16"
                        icon="View"/>
            <ShadcnIcon v-else-if="node.level === StructureEnum.TYPE && node.type === 'function'"
                        class="text-xs font-semibold text-gray-500"
                        size="16"
                        icon="SquareFunction"/>
            <ShadcnIcon v-else-if="node.level === StructureEnum.TYPE && node.type === 'procedure'"
                        class="text-xs font-semibold text-gray-500"
                        size="16"
                        icon="Cpu"/>
            <ShadcnIcon v-else-if="node.level === StructureEnum.TYPE && node.type === 'column'"
                        class="text-xs font-semibold text-gray-500"
                        size="16"
                        icon="Columns"/>
            <ShadcnIcon v-else-if="node.level === StructureEnum.TYPE && node.type === 'index'"
                        class="text-xs font-semibold text-gray-500"
                        size="16"
                        icon="Blinds"/>
            <ShadcnIcon v-else-if="node.level === StructureEnum.TYPE && node.type === 'trigger'"
                        class="text-xs font-semibold text-gray-500"
                        size="16"
                        icon="Tangent"/>
            <ShadcnIcon v-else-if="node.level === StructureEnum.TYPE && node.type === 'primary'"
                        class="text-xs font-semibold text-gray-500"
                        size="16"
                        icon="Key"/>

            <span class="text-xs font-normal text-gray-900">
              {{ node.title }}
            </span>

            <span v-if="node.level === StructureEnum.COLUMN" class="text-xs font-normal text-gray-500 ml-1">
              {{ node.typeName === 'column' ? node.type : node.definition }}
            </span>
          </div>
        </template>
      </ShadcnTree>

      <ShadcnContextMenu v-if="contextmenu.visible && dataInfo" v-model="contextmenu.visible" :position="contextmenu.position">
        <ShadcnContextMenuSub v-if="dataInfo.level === StructureEnum.TABLE || dataInfo.level === StructureEnum.COLUMN || dataInfo.type === 'table'"
                              :label="$t('source.common.menuNew')">
          <ShadcnContextMenuItem v-if="dataInfo.level === StructureEnum.TABLE || dataInfo.type === 'table'"
                                 @click="visibleCreateTable(true)">
            <div class="flex items-center space-x-1">
              <ShadcnIcon icon="Table" size="15"/>
              <span>{{ $t('source.common.menuNewTable') }}</span>
            </div>
          </ShadcnContextMenuItem>

          <ShadcnContextMenuItem v-else-if="dataInfo.level === StructureEnum.COLUMN"
                                 @click="visibleCreateColumn(true)">
            <div class="flex items-center space-x-1">
              <ShadcnIcon icon="Columns" size="15"/>
              <span>{{ $t('source.common.newColumn') }}</span>
            </div>
          </ShadcnContextMenuItem>
        </ShadcnContextMenuSub>

        <ShadcnContextMenuSub v-if="dataInfo.level === StructureEnum.TABLE" :label="$t('source.common.menuExport')">
          <ShadcnContextMenuItem @click="visibleExportData(true)">
            <div class="flex items-center space-x-1">
              <ShadcnIcon icon="ArrowUpFromLine" size="15"/>
              <span>{{ $t('source.common.exportData') }}</span>
            </div>
          </ShadcnContextMenuItem>
        </ShadcnContextMenuSub>

        <ShadcnContextMenuItem v-if="dataInfo?.level === StructureEnum.TABLE" @click="visibleTruncateTable(true)">
          <div class="flex items-center space-x-1">
            <ShadcnIcon icon="Trash" size="15"/>
            <span>{{ $t('source.common.truncateTable') }}</span>
          </div>
        </ShadcnContextMenuItem>

        <ShadcnContextMenuItem v-if="dataInfo?.level === StructureEnum.TABLE" @click="visibleDropTable(true)">
          <div class="flex items-center space-x-1">
            <ShadcnIcon icon="Delete" size="15"/>
            <span>{{ $t('source.common.dropTable') }}</span>
          </div>
        </ShadcnContextMenuItem>

        <ShadcnContextMenuItem v-if="dataInfo?.level === StructureEnum.COLUMN" @click="visibleChangeColumn(true)">
          <div class="flex items-center space-x-1">
            <ShadcnIcon icon="Pencil" size="15"/>
            <span>{{ $t('source.common.changeColumn') }}</span>
          </div>
        </ShadcnContextMenuItem>

        <ShadcnContextMenuItem v-if="dataInfo?.level === StructureEnum.COLUMN" @click="visibleDropColumn(true)">
          <div class="flex items-center space-x-1">
            <ShadcnIcon icon="Delete" size="15"/>
            <span>{{ $t('source.common.dropColumn') }}</span>
          </div>
        </ShadcnContextMenuItem>
      </ShadcnContextMenu>
    </div>
  </ShadcnCard>

  <TableCreate v-if="tableCreateVisible" :is-visible="tableCreateVisible" @close="visibleCreateTable(false)"/>

  <ColumnCreate v-if="columnCreateVisible"
                :is-visible="columnCreateVisible"
                :info="dataInfo as any"
                @close="visibleCreateColumn(false)"/>

  <TableExport v-if="tableExportVisible"
               :isVisible="tableExportVisible"
               :info="dataInfo as any"
               @close="visibleExportData(false)"/>

  <TableTruncate v-if="tableTruncateVisible" :is-visible="tableTruncateVisible" @close="visibleTruncateTable(false)"/>

  <TableDrop v-if="tableDropVisible" :is-visible="tableDropVisible" @close="visibleDropTable(false)"/>

  <ColumnChange v-if="columnChangeVisible"
                :is-visible="columnChangeVisible"
                :info="dataInfo as any"
                @close="visibleChangeColumn(false)"/>

  <ColumnDrop v-if="columnDropVisible"
              :is-visible="columnDropVisible"
              :info="dataInfo as any"
              @close="visibleDropColumn(false)"/>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import MetadataService from '@/services/metadata.ts'
import { StructureEnum, StructureModel } from '@/model/structure.ts'
import ColumnCreate from '@/views/pages/admin/source/components/ColumnCreate.vue'
import ColumnDrop from '@/views/pages/admin/source/components/ColumnDrop.vue'
import TableExport from '@/views/pages/admin/source/components/TableExport.vue'
import ColumnChange from '@/views/pages/admin/source/components/ColumnChange.vue'
import TableTruncate from '@/views/pages/admin/source/components/TableTruncate.vue'
import TableDrop from '@/views/pages/admin/source/components/TableDrop.vue'
import TableCreate from '@/views/pages/admin/source/components/TableCreate.vue'

interface MenuItem
{
  type: string;        // 节点类型（表、视图等）
  title: string;        // 节点名称
  comment?: string;    // 注释
  isLeaf?: boolean;
  level?: StructureEnum;
  code?: string;
  children?: MenuItem[]; // 子节点
  value?: string
  dataType?: string;
  nullable?: string;
  defaultValue?: string;
  position?: number;
  definition?: string;
  typeName?: string;
  disabled?: boolean
}

interface SourceData
{
  type_name: string;
  object_name: string;
  object_comment: string;
  object_data_type: string;
  object_nullable: string;
  object_default_value: string;
  object_position: number;
  object_definition: string;
}

export default defineComponent({
  name: 'MetadataSidebar',
  computed: {
    StructureEnum()
    {
      return StructureEnum
    }
  },
  components: { TableCreate, TableDrop, TableTruncate, ColumnChange, TableExport, ColumnDrop, ColumnCreate },
  data()
  {
    return {
      loading: false,
      selectDatabase: undefined,
      databaseModel: '',
      originalSource: null as string | null,
      originalDatabase: null as string | null,
      originalTable: null as string | null,
      selectNode: null as StructureModel | null,
      databaseArray: Array<StructureModel>(),
      dataTreeArray: [] as any[],
      dataInfo: null as StructureModel | null,
      tableCreateVisible: false,
      tableExportVisible: false,
      tableTruncateVisible: false,
      tableDropVisible: false,
      columnCreateVisible: false,
      columnChangeVisible: false,
      columnDropVisible: false,
      contextmenu: {
        visible: false,
        position: { x: 0, y: 0 }
      }
    }
  },
  created()
  {
    this.originalSource = this.$route.params?.source as string
    this.handleInitialize()
  },
  methods: {
    handleInitialize()
    {
      const source = this.$route.params?.source as string
      const database = this.$route.params?.database as string
      if (source) {
        this.originalSource = source
        this.loading = true
        MetadataService.getDatabaseBySource(source)
                       .then(response => {
                         if (response.status) {
                           response.data.columns.forEach(item => {
                             const structure: StructureModel = {
                               title: item.object_name,
                               catalog: item.object_name,
                               code: item.object_name
                             }
                             this.databaseArray.push(structure)
                           })
                           if (database) {
                             this.originalDatabase = database
                             this.selectDatabase = database as any
                             this.onChangeDatabase()
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
    onChangeDatabase()
    {
      this.loading = true
      this.dataTreeArray = []
      MetadataService.getTablesByDatabase(this.originalSource, this.selectDatabase as string)
                     .then(response => {
                       if (response.status) {
                         this.dataTreeArray = [...this.convertToTreeData(response.data.columns, StructureEnum.TABLE)]
                       }
                       else {
                         this.$Message.error({
                           content: response.message,
                           showIcon: true
                         })
                       }
                     })
                     .finally(() => {
                       this.loading = false
                       const table = this.$route.params?.table
                       if (table) {
                         const node = this.dataTreeArray.find(item => item.code === table)
                         if (node) {
                           node.selected = true
                           this.handlerSelectNode([node])
                         }
                       }
                       else {
                         this.$router.push(`/admin/source/${ this.originalSource }/d/${ this.selectDatabase }`)
                       }
                     })
    },
    onNodeClick(node: any)
    {
      if (node.level === StructureEnum.TYPE) {
        return
      }

      const type = this.$route.meta.type
      this.$router.push(`/admin/source/${ this.originalSource }/d/${ this.selectDatabase }/t/${ type ? type : 'info' }/${ node.code }`)
    },
    onLoadData(item: StructureModel, callback: any)
    {
      let dataChildArray = [] as StructureModel[]
      if (item.level === StructureEnum.COLUMN) {
        callback(dataChildArray)
        return
      }

      MetadataService.getColumnsByTable(this.originalSource, this.selectDatabase as string, item.code as string)
                     .then(response => {
                       if (response.status) {
                         dataChildArray = [...this.convertToTreeData(response.data.columns, StructureEnum.COLUMN)]
                       }
                       else {
                         this.$Message.error({
                           content: response.message,
                           showIcon: true
                         })
                       }
                     })
                     .finally(() => callback(dataChildArray))
    },
    visibleCreateTable(opened: boolean)
    {
      this.tableCreateVisible = opened

      if (!opened) {
        this.onChangeDatabase()
      }
    },
    visibleCreateColumn(opened: boolean)
    {
      this.columnCreateVisible = opened
    },
    visibleExportData(opened: boolean)
    {
      this.tableExportVisible = opened
    },
    visibleTruncateTable(opened: boolean)
    {
      this.tableTruncateVisible = opened
    },
    visibleDropTable(opened: boolean)
    {
      this.tableDropVisible = opened

      if (!opened) {
        this.onChangeDatabase()
      }
    },
    visibleChangeColumn(opened: boolean)
    {
      this.columnChangeVisible = opened
    },
    visibleDropColumn(opened: boolean)
    {
      this.columnDropVisible = opened
    },
    visibleContextMenu(event: any, node: any)
    {
      this.contextmenu.position = {
        x: event.clientX,
        y: event.clientY
      }
      this.dataInfo = node
      this.contextmenu.visible = true
    },
    convertToTreeData(flatData: SourceData[], level: StructureEnum = StructureEnum.DATABASE): MenuItem[]
    {
      // 按type_name分组
      const groupedData = flatData.reduce((acc, curr) => {
        if (!acc[curr.type_name]) {
          acc[curr.type_name] = []
        }
        acc[curr.type_name].push(curr)
        return acc
      }, {} as Record<string, SourceData[]>)

      // 转换为树形结构
      return Object.entries(groupedData).map(([type, items]) => ({
        type,
        title: `${ this.$t('common.' + type) } (${ items.length })`,
        level: StructureEnum.TYPE,
        value: type,
        disabled: true,
        children: items.map(item => ({
          type: item.object_data_type || '',
          title: item.object_name,
          level: level,
          isLeaf: false,
          code: item.object_name,
          value: `${ item.object_name }_${ item.type_name }`,
          comment: item.object_comment,
          dataType: item.object_data_type,
          nullable: item.object_nullable,
          defaultValue: item.object_default_value,
          position: item.object_position,
          definition: item.object_definition,
          typeName: item.type_name
        }))
      }))
    }
  }
})
</script>
