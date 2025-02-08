<template>
  <ShadcnLayout>
    <ShadcnLayoutWrapper class="flex">
      <ShadcnLayoutSider style="top: -8px;">
        <ShadcnCard :border="false">
          <SourceSelect v-model="selectSource.full as string" @on-change="onChange($event)"/>

          <MetadataTree v-if="selectSource.code" :code="selectSource.code as string"/>
        </ShadcnCard>
      </ShadcnLayoutSider>

      <ShadcnLayoutMain class="min-h-screen flex-1 overflow-hidden">
        <ShadcnLayoutHeader>
          <ShadcnCard ref="editorContainer" :border="false">
            <template #title>
              <ShadcnSpace v-if="selectSource.code">
                <ShadcnButton :loading="loading.running" :disabled="(!selectSource.id && !loading.running) || loading.running" @click="onRun()">
                  <template #icon>
                    <ShadcnIcon icon="Play" :size="15"/>
                  </template>
                  {{ $t('query.common.execute') }}
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

                <ShadcnButton type="default" :disabled="!selectSource.code" @click="onPlusEditor">
                  <template #icon>
                    <ShadcnIcon icon="Pencil" :size="15"/>
                  </template>

                  {{ $t('common.createEditor') }}
                </ShadcnButton>
              </ShadcnSpace>
            </template>

            <div v-if="!selectSource.code" class="flex justify-center" style="height: 300px">
              <div class="flex items-center justify-center">
                <ShadcnAlert>{{ this.$t('source.tip.selected') }}</ShadcnAlert>
              </div>
            </div>

            <ShadcnTab v-else v-model="activeEditor" :closable="Object.keys(editors).length > 1">
              <ShadcnTabItem v-for="item in Object.values(editors)"
                             :label="item.title"
                             :key="item.key"
                             :value="item.key">
                <ShadcnCodeEditor v-model="item.content"
                                  :config="{language: 'sql', ...editorConfig}"
                                  :auto-complete-config="{
                                      endpoint: `http://localhost:9096/api/v1/metadata/${selectSource.code}/databases`,
                                      method: 'GET',
                                      trigger: ['.', '@'],
                                      headers: { 'Authorization': auth?.type + ' ' + auth?.token },
                                      transform: (response: any) => {
                                        return response[0].data.columns.map((item: any) => ({
                                          label: item.object_name,
                                          insertText: item.object_name,
                                          detail: item.object_name,
                                          icon: 'Database'
                                        }))
                                      }
                                  }">
                </ShadcnCodeEditor>
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
             :content="editors[activeEditor]?.content"
             :help-type="queryConfigure.queryType"
             :engine="selectSource.engine as string"
             :message="responseConfigure.message as string"
             @close="visibleQueryHelp(false)">
  </QueryHelp>

  <SnippetInfo v-if="dataInfoVisible"
               :is-visible="dataInfoVisible"
               :info="dataInfo"
               @close="visibleSnippet(false)">
  </SnippetInfo>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import SourceSelect from '@/views/components/source/SourceSelect.vue'
import SnippetService from '@/services/snippet'
import AuditService from '@/services/audit'
import { ExecuteModel } from '@/model/execute'
import ExecuteService from '@/services/execute'
import GridTable from '@/views/components/grid/GridTable.vue'
import { GridConfigure } from '@/views/components/grid/GridConfigure'
import { ResponseModel } from '@/model/response'
import FormatService from '@/services/format'
import { HelpType } from '@/views/pages/admin/query/HelpType'
import QueryHelp from '@/views/pages/admin/query/QueryHelp.vue'
import MetadataTree from '@/views/components/tree/MetadataTree.vue'
import { SnippetModel, SnippetRequest } from '@/model/snippet'
import SnippetInfo from '@/views/pages/admin/snippet/SnippetInfo.vue'
import { TokenUtils } from '@/utils/token.ts'
import axios from 'axios'

interface EditorInstance
{
  title: string;
  key: string;
  content: string;
}

export default defineComponent({
  name: 'QueryHome',
  components: {
    MetadataTree,
    SnippetInfo,
    QueryHelp,
    GridTable,
    SourceSelect
  },
  setup()
  {
    const auth = TokenUtils.getAuthUser()

    return {
      auth
    }
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
      editors: {} as Record<string, EditorInstance>,
      activeEditor: null as string | null,
      editorConfig: {
        fontSize: 12,
        theme: 'chrome'
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
                              const activeEditor = this.editors[this.activeEditor]
                              activeEditor.content = response.data.context
                            }
                          })
                          .finally(() => this.loading.froming = false)
          }
          else if (type === 'history') {
            this.loading.froming = true
            this.queryConfigure.configure.mode = 'HISTORY'
            AuditService.getByCode(code as string)
                        .then((response) => {
                          if (response.status && response.data) {
                            const activeEditor = this.editors[this.activeEditor]
                            activeEditor.content = response.data.content
                            const full = `${ response.data.source.id }:${ response.data.source.type }:${ response.data.source.code }`
                            this.selectSource.full = full
                            this.onChange(full)
                          }
                        })
                        .finally(() => this.loading.froming = false)
          }
        }
      }
    },
    onChange(value: string)
    {
      const idAndType = value.split(':')
      this.selectSource.id = idAndType[0]
      this.selectSource.type = idAndType[1]
      this.selectSource.engine = idAndType[1]
      this.selectSource.code = idAndType[2]
    },
    onPlusEditor()
    {
      this.responseConfigure.message = null
      this.createEditor()
    },
    onRun()
    {
      this.responseConfigure.gridConfigure = null
      this.responseConfigure.response = null
      this.responseConfigure.message = null
      this.queryConfigure.queryType = [HelpType.ANALYSIS, HelpType.OPTIMIZE]
      this.queryConfigure.cancelToken = axios.CancelToken.source()
      this.queryConfigure.configure.name = this.selectSource.code as string
      const editor = this.editors[this.activeEditor]
      this.queryConfigure.configure.content = editor.content
      const editorContainer: HTMLElement = this.$refs.editorContainer as HTMLElement

      this.loading.running = true
      ExecuteService.execute(this.queryConfigure.configure!, this.queryConfigure.cancelToken.token)
                    .then((response) => {
                      if (response.status) {

                        this.responseConfigure.response = response
                        this.responseConfigure.gridConfigure = {
                          headers: response.data.headers,
                          columns: response.data.columns,
                          height: 340,
                          width: editorContainer.offsetWidth + 20,
                          showSeriesNumber: false,
                          sourceId: this.selectSource.id as unknown as number,
                          query: editor.content,
                          code: this.selectSource.code as string
                        }
                        editor.content = response.data.content
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
      this.loading.formatting = true

      const activeEditor = this.editors[this.activeEditor]
      const configure = { sql: activeEditor.content }
      FormatService.formatSql(configure)
                   .then((response) => {
                     if (response.status) {
                       activeEditor.content = response.data
                     }
                     else {
                       this.$Message.error({
                         content: response.message,
                         showIcon: true
                       })
                     }
                   })
                   .finally(() => this.loading.formatting = false)
    },
    visibleQueryHelp(value: boolean)
    {
      this.visibility.queryHelp = value
    },
    visibleSnippet(opened: boolean)
    {
      const activeEditor = this.editors[this.activeEditor]
      this.dataInfoVisible = opened
      if (activeEditor) {
        const content = activeEditor.content
        this.dataInfo = SnippetRequest.of()
        this.dataInfo.context = content as string
      }
    },
    createEditor()
    {
      const newEditor: EditorInstance = {
        title: `Query`,
        key: `editor-${ Date.now() }`,
        content: ''
      }

      this.activeEditor = newEditor.key
      this.editors = {
        ...this.editors,
        [newEditor.key]: newEditor
      }
    }
  }
})
</script>
