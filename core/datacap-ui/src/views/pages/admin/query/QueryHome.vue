<template>
  <ShadcnLayout>
    <ShadcnLayoutWrapper>
      <ShadcnLayoutSider style="top: -8px;">
        <ShadcnCard>
          <SourceSelect v-model="selectSource.full as string" @on-change="onChange($event)"/>

          <MetadataTree v-if="selectSource.code" :code="selectSource.code as string"/>
        </ShadcnCard>
      </ShadcnLayoutSider>

      <ShadcnLayoutMain class="min-h-screen">
        <ShadcnLayoutHeader>
          <ShadcnCard ref="editorContainer">
            <template #title>
              <ShadcnSpace>
                <ShadcnButton :loading="loading.running" :disabled="(!selectSource.id && !loading.running) || loading.running" @click="onRun()">
                  <template #icon>
                    <ShadcnIcon icon="Play" :size="15"/>
                  </template>

                  {{ selectEditor.isSelection ? $t('query.common.executeSelection') : $t('query.common.execute') }}
                </ShadcnButton>

                <ShadcnButton type="default"
                              :loading="loading.formatting"
                              :disabled="(!selectSource.id && !loading.formatting) || loading.formatting"
                              @click="onFormat()">
                  <template #icon>
                    <ShadcnIcon icon="RemoveFormatting" :size="15"/>
                  </template>

                  {{ $t('query.common.format') }}
                </ShadcnButton>

                <ShadcnButton type="error"
                              :loading="loading.formatting"
                              :disabled="!selectSource.id || !loading.running"
                              @click="onCancel()">
                  <template #icon>
                    <ShadcnIcon icon="Ban" :size="15"/>
                  </template>

                  {{ $t('common.cancel') }}
                </ShadcnButton>

                <ShadcnButton v-if="responseConfigure.response" type="primary" @click="visibleSnippet(true)">
                  <template #icon>
                    <ShadcnIcon icon="Plus" :size="15"/>
                  </template>

                  {{ $t('common.snippet') }}
                </ShadcnButton>

                <div v-if="responseConfigure.response">
                  <ShadcnTooltip>
                    <template #content>
                      <ShadcnSpace wrap>
                        <ShadcnText color="white" type="small">
                          {{ $t('query.common.connectionTime') }}
                          {{ responseConfigure.response.data.connection.elapsed }} ms
                        </ShadcnText>

                        <ShadcnText color="white" type="small">
                          {{ $t('query.common.executionTime') }}
                          {{ responseConfigure.response.data.processor.elapsed }} ms
                        </ShadcnText>
                      </ShadcnSpace>
                    </template>

                    <ShadcnButton>
                      <ShadcnIcon icon="Clock" :size="15"/>

                      {{ responseConfigure.response.data.processor.elapsed }} ms
                    </ShadcnButton>
                  </ShadcnTooltip>
                </div>

                <ShadcnButton v-if="selectSource.id && (responseConfigure.response?.data || !responseConfigure.response?.status)"
                              type="primary" @click="visibleQueryHelp(true)">
                  <template #icon>
                    <ShadcnIcon icon="Bot" :size="15"/>
                  </template>

                  {{ $t('query.common.help') }}
                </ShadcnButton>

                <ShadcnButton type="default" @click="onPlusEditor">
                  <template #icon>
                    <ShadcnIcon icon="Pencil" :size="15"/>
                  </template>

                  {{ $t('common.createEditor') }}
                </ShadcnButton>
              </ShadcnSpace>
            </template>

            <ShadcnTab v-model="selectEditor.activeKey as string"
                       closable
                       @tab-remove="onMinusEditor"
                       @on-change="onChangeEditor">
              <ShadcnTabItem v-for="item in selectEditor.editorMaps.values()" :label="item.title" :value="item.key">
                <VAceEditor lang="mysql"
                            :value="selectEditor.editorInstance?.instance?.getValue() as string"
                            :theme="selectEditor.editorInstance?.configure?.theme"
                            :style="{ height: '300px', fontSize: selectEditor.editorInstance?.configure?.fontSize + 'px' }"
                            :key="selectEditor.editorInstance?.key"
                            :options="{ enableSnippets: true, enableLiveAutocompletion: true, readOnly: loading.froming }"
                            @init="handlerEditorDidMount($event, 'mysql', selectEditor.editorInstance?.key)"/>
              </ShadcnTabItem>
            </ShadcnTab>
          </ShadcnCard>
        </ShadcnLayoutHeader>

        <ShadcnLayoutContent class="mt-1">
          <GridTable v-if="responseConfigure.gridConfigure" :configure="responseConfigure.gridConfigure"/>
        </ShadcnLayoutContent>
      </ShadcnLayoutMain>
    </ShadcnLayoutWrapper>
  </ShadcnLayout>

  <QueryHelp v-if="visibility.queryHelp"
             :is-visible="visibility.queryHelp"
             :content="selectEditor.editorInstance?.instance?.getValue() as string"
             :help-type="queryConfigure.queryType"
             :engine="selectSource.engine as string"
             :message="responseConfigure.message as string"
             @close="visibleQueryHelp(false)"/>

  <SnippetInfo v-if="dataInfoVisible"
               :is-visible="dataInfoVisible"
               :info="dataInfo"
               @close="visibleSnippet(false)"/>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import SourceSelect from '@/views/components/source/SourceSelect.vue'
import { VAceEditor } from 'vue3-ace-editor'
import { Ace } from 'ace-builds'
import '@/ace-editor-theme'
import { UserEditor } from '@/model/user'
import Common from '@/utils/common'
import SnippetService from '@/services/snippet'
import AuditService from '@/services/audit'
import { ExecuteModel } from '@/model/execute'
import langTools from 'ace-builds/src-noconflict/ext-language_tools'
import { HttpUtils } from '@/utils/http'
import FunctionService from '@/services/function'
import ExecuteService from '@/services/execute'
import DatabaseService from '@/services/metadata.ts'
import TableService from '@/services/table'
import ColumnService from '@/services/column'
import axios from 'axios'
import GridTable from '@/views/components/grid/GridTable.vue'
import { GridConfigure } from '@/views/components/grid/GridConfigure'
import { ResponseModel } from '@/model/response'
import FormatService from '@/services/format'
import { HelpType } from '@/views/pages/admin/query/HelpType'
import QueryHelp from '@/views/pages/admin/query/QueryHelp.vue'
import MetadataTree from '@/views/components/tree/MetadataTree.vue'
import { SnippetModel, SnippetRequest } from '@/model/snippet'
import SnippetInfo from '@/views/pages/admin/snippet/SnippetInfo.vue'

import { FilterModel } from '@/model/filter.ts'
import Editor = Ace.Editor

interface EditorInstance
{
  title: string;
  key: string;
  instance?: Editor,
  configure?: UserEditor
}

export default defineComponent({
  name: 'QueryHome',
  components: {
    MetadataTree,
    SnippetInfo,
    QueryHelp,
    GridTable,
    SourceSelect,
    VAceEditor
  },
  data()
  {
    return {
      loading: {
        running: false,
        formatting: false,
        froming: false
      },
      visibility: {
        queryHelp: false
      },
      selectSource: {
        id: null as string | null | undefined,
        type: null as string | null | undefined,
        engine: null as string | null | undefined,
        code: null as string | null | undefined,
        full: null as string | null
      },
      selectEditor: {
        editorMaps: new Map<string, EditorInstance>(),
        activeKey: null as string | null,
        editorInstance: null as EditorInstance | null,
        configure: null as UserEditor | null,
        isSelection: false
      },
      queryConfigure: {
        configure: null as ExecuteModel | null,
        cancelToken: null as any | null,
        queryType: [HelpType.ANALYSIS, HelpType.OPTIMIZE]
      },
      responseConfigure: {
        response: null as ResponseModel | null,
        gridConfigure: null as GridConfigure | null,
        message: null as string | null
      },
      dataInfo: null as unknown as SnippetModel,
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
      this.createEditor()
      this.queryConfigure.configure = { name: this.selectSource.id as string, content: '', mode: 'ADHOC', format: 'JsonConvert' }
      const params = this.$route.params
      if (params) {
        const code = params.code
        const type = params.type
        if (code && type) {
          if (type === 'snippet') {
            this.loading.froming = true
            this.queryConfigure.configure.mode = 'SNIPPET'
            SnippetService.getByCode(code as string)
                          .then((response) => {
                            if (response.status && response.data?.code) {
                              const instance = this.selectEditor.editorMaps.get(this.selectEditor.activeKey as string)
                              if (instance) {
                                instance.instance?.setValue(response.data.context)
                              }
                            }
                          })
                          .finally(() => this.loading.froming = false)
          }
          else if (type === 'history') {
            this.loading.froming = true
            this.queryConfigure.configure.mode = 'HISTORY'
            AuditService.getByCode(code as string)
                        .then((response) => {
                          if (response.status && response.data?.content) {
                            const instance = this.selectEditor.editorMaps.get(this.selectEditor.activeKey as string)
                            if (instance) {
                              instance.instance?.setValue(response.data.content)
                              const full = `${ response.data.source.id }:${ response.data.source.type }:${ response.data.source.code }`
                              this.selectSource.full = full
                              this.onChange(full)
                            }
                          }
                        })
                        .finally(() => this.loading.froming = false)
          }
        }
      }
    },
    handlerEditorDidMount(editor: Editor, _language: string, key?: string | null)
    {
      if (key) {
        const instance = this.selectEditor.editorMaps.get(key as string)
        if (instance) {
          instance.instance = editor
        }
      }
      // Initializes the completer
      this.handlerInitializeCompleter(editor, _language)
    },
    handlerInitializeCompleter(editor: Editor, language: string)
    {
      try {
        // The configuration editor selects content events
        editor.selection.on('changeSelection', () => {
          const selection = editor.getSelection()
          this.selectEditor.isSelection = !selection.isEmpty()
        })

        langTools.addCompleter({
          // @ts-ignore
          getCompletions: function (editor: any, session: any, pos: any, prefix: any, callback: (arg0: null, arg1: never[]) => any) {
            return callback(null, [])
          }
        })

        // Clear default keywords and code snippets
        editor.completers = []
        const that = this
        const client = new HttpUtils().getAxios()
        const filter = new FilterModel()
        filter.size = 1000000
        client.all([FunctionService.getByPlugin(language.toLowerCase()), SnippetService.getAll(filter), DatabaseService.getAll(filter), TableService.getAll(filter),
                    ColumnService.getAll(filter)])
              .then(client.spread((keyword: any, snippet: any, database: any, table: any, column: any) => {
                if (keyword.status) {
                  const keywordCompleter = {
                    // @ts-ignore
                    getCompletions: function (editor, session, pos, prefix, callback) {
                      return callback(null, keyword.data.content.map(function (item: { example: string; name: string; type: any; description: string; }) {
                        return {
                          value: item.example,
                          caption: item.name,
                          meta: that.$t('function.common.' + item.type.toLowerCase()),
                          docHTML: '<div>' +
                              '<strong>' + item.name + '</strong><br/><hr/>'
                              + that.$t('common.description') + ':\n' + item.description + '<br/><hr/>'
                              + that.$t('function.common.example') + ':\n' + item.example + '<br/><hr/>'
                              + '</div>'
                        }
                      }))
                    }
                  }
                  editor.completers.push(keywordCompleter)
                }
                if (snippet.status) {
                  const snippetCompleter = {
                    // @ts-ignore
                    getCompletions: function (editor, session, pos, prefix, callback) {
                      return callback(null, snippet.data.content.map(function (item: { context: any; name: string; description: string; }) {
                        return {
                          value: item.context,
                          caption: item.name,
                          meta: that.$t('common.snippet'),
                          docHTML: '<div>' +
                              '<strong>' + item.name + '</strong><br/><hr/>'
                              + that.$t('common.description') + ':\n' + item.description + '<br/><hr/>'
                              + '</div>'
                        }
                      }))
                    }
                  }
                  editor.completers.push(snippetCompleter)
                }
                if (database.status) {
                  const snippetCompleter = {
                    // @ts-ignore
                    getCompletions: function (editor, session, pos, prefix, callback) {
                      return callback(null, database.data.content.map(function (item: { context: any; name: string; description: string; }) {
                        return {
                          value: item.name,
                          caption: item.name,
                          meta: that.$t('common.database'),
                          docHTML: '<div>' +
                              '<strong>' + item.name + '</strong><br/><hr/>'
                              + that.$t('common.description') + ':\n' + item.description + '<br/><hr/>'
                              + '</div>'
                        }
                      }))
                    }
                  }
                  editor.completers.push(snippetCompleter)
                }
                if (table.status) {
                  const snippetCompleter = {
                    // @ts-ignore
                    getCompletions: function (editor, session, pos, prefix, callback) {
                      return callback(null, table.data.content.map(function (item: { context: any; name: string; description: string; }) {
                        return {
                          value: item.name,
                          caption: item.name,
                          meta: that.$t('common.table'),
                          docHTML: '<div>' +
                              '<strong>' + item.name + '</strong><br/><hr/>'
                              + that.$t('common.description') + ':\n' + item.description + '<br/><hr/>'
                              + '</div>'
                        }
                      }))
                    }
                  }
                  editor.completers.push(snippetCompleter)
                }
                if (column.status) {
                  const snippetCompleter = {
                    // @ts-ignore
                    getCompletions: function (editor, session, pos, prefix, callback) {
                      return callback(null, column.data.content.map(function (item: { context: any; name: string; description: string; }) {
                        return {
                          value: item.name,
                          caption: item.name,
                          meta: that.$t('common.column'),
                          docHTML: '<div>' +
                              '<strong>' + item.name + '</strong><br/><hr/>'
                              + that.$t('common.description') + ':\n' + item.description + '<br/><hr/>'
                              + '</div>'
                        }
                      }))
                    }
                  }
                  editor.completers.push(snippetCompleter)
                }
              }))
      }
      catch (e) {
        console.error(e)
      }
    },
    onChange(value: string)
    {
      const idAndType = value.split(':')
      this.selectSource.id = idAndType[0]
      this.selectSource.type = idAndType[1]
      this.selectSource.engine = idAndType[1]
      this.selectSource.code = idAndType[2]
      const instance = this.selectEditor.editorMaps.get(this.selectEditor.activeKey as string)
      if (instance) {
        this.handlerEditorDidMount(instance.instance as any, idAndType[1])
      }
    },
    onPlusEditor()
    {
      this.responseConfigure.message = null
      this.createEditor()
    },
    onMinusEditor(targetKey: string)
    {
      const keys = Array.from(this.selectEditor.editorMaps.keys())
      const index = keys.indexOf(targetKey)
      if (index > 0 && index < keys.length) {
        const previousKey = keys[index - 1]
        const instance = this.selectEditor.editorMaps.get(previousKey)
        if (instance) {
          this.selectEditor.editorInstance = instance
          this.selectEditor.activeKey = previousKey
          this.selectEditor.isSelection = false
        }
      }
      this.selectEditor.editorMaps.delete(targetKey)
    },
    onChangeEditor(value: any)
    {
      this.responseConfigure.message = null
      this.queryConfigure.queryType = [HelpType.ANALYSIS, HelpType.OPTIMIZE]
      const instance = this.selectEditor.editorMaps.get(value)
      if (instance) {
        this.selectEditor.editorInstance = instance
        this.selectEditor.activeKey = value
        this.selectEditor.isSelection = false
      }
    },
    onRun()
    {
      this.responseConfigure.gridConfigure = null
      this.responseConfigure.response = null
      this.responseConfigure.message = null
      this.queryConfigure.queryType = [HelpType.ANALYSIS, HelpType.OPTIMIZE]
      this.queryConfigure.cancelToken = axios.CancelToken.source()
      const editorContainer: HTMLElement = this.$refs.editorContainer as HTMLElement
      const editorInstance = this.selectEditor.editorInstance
      //   limit: this.queryLimit,
      if (editorInstance && this.queryConfigure.configure) {
        const content = this.selectEditor.isSelection ? editorInstance.instance?.getSelectedText() : editorInstance.instance?.getValue()
        this.queryConfigure.configure.content = content as string
        this.queryConfigure.configure.name = this.selectSource.code as string
      }

      this.loading.running = true
      ExecuteService.execute(this.queryConfigure.configure!, this.queryConfigure.cancelToken.token)
                    .then((response) => {
                      if (response.status) {
                        if (editorInstance) {
                          const content = this.selectEditor.isSelection ? editorInstance.instance?.getSelectedText() : editorInstance.instance?.getValue()
                          this.responseConfigure.response = response
                          const tConfigure: GridConfigure = {
                            headers: response.data.headers,
                            columns: response.data.columns,
                            height: 340,
                            width: editorContainer.offsetWidth + 20,
                            showSeriesNumber: false,
                            sourceId: this.selectSource.id as unknown as number,
                            query: content,
                            code: this.selectSource.code as string
                          }
                          this.responseConfigure.gridConfigure = tConfigure
                          editorInstance.instance?.setValue(response.data.content)
                          localStorage.setItem('QueryContent', content as string)
                        }
                      }
                      else {
                        this.$Message.error({
                          content: response.message,
                          showIcon: true
                        })
                        this.responseConfigure.message = response.message
                        this.queryConfigure.queryType.push(HelpType.FIXEDBUGS)
                        this.responseConfigure.gridConfigure = null
                      }
                    })
                    .finally(() => this.loading.running = false)
    },
    onCancel()
    {
      this.queryConfigure.cancelToken.cancel('Cancel query')
    },
    onFormat()
    {
      const editorInstance = this.selectEditor.editorInstance
      if (editorInstance) {
        this.loading.formatting = true
        const configure = { sql: editorInstance.instance?.getValue() }
        FormatService.formatSql(configure)
                     .then((response) => {
                       if (response.status) {
                         editorInstance.instance?.setValue(response.data)
                         this.selectEditor.isSelection = false
                       }
                       else {
                         this.$Message.error({
                           content: response.message,
                           showIcon: true
                         })
                       }
                     })
                     .finally(() => this.loading.formatting = false)
      }
    },
    visibleQueryHelp(value: boolean)
    {
      this.visibility.queryHelp = value
    },
    visibleSnippet(opened: boolean)
    {
      const editorInstance = this.selectEditor.editorInstance
      this.dataInfoVisible = opened
      if (editorInstance) {
        const content = this.selectEditor.isSelection ? editorInstance.instance?.getSelectedText() : editorInstance.instance?.getValue()
        this.dataInfo = SnippetRequest.of()
        this.dataInfo.context = content as string
      }
    },
    createEditor()
    {
      const localEditorConfigure = localStorage.getItem(Common.userEditorConfigure)
      const defaultEditorConfigure: UserEditor = { fontSize: 12, theme: 'chrome' }
      this.selectEditor.configure = localEditorConfigure ? JSON.parse(localEditorConfigure) : defaultEditorConfigure
      const defaultEditor: EditorInstance = { title: 'New Query', key: Date.now().toString(), configure: this.selectEditor.configure as any }
      this.selectEditor.activeKey = defaultEditor.key
      this.selectEditor.editorMaps.set(defaultEditor.key, defaultEditor)
      this.selectEditor.editorInstance = defaultEditor
      this.selectEditor.isSelection = false
    }
  }
})
</script>
