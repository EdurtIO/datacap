<template>
  <div class="relative min-h-screen">
    <ShadcnSpin v-if="loading" fixed/>

    <ShadcnTable v-else-if="!loading && data.length > 0"
                 size="small"
                 :columns="headers"
                 :data="data">
      <template #object_nullable="{ row }">
        <ShadcnSwitch v-model="row.object_nullable" size="small" disabled/>
      </template>
    </ShadcnTable>
  </div>
</template>

<script lang="ts" setup>
import { getCurrentInstance, onMounted, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRoute } from 'vue-router'
import { createHeaders } from '@/views/pages/admin/source/components/TableUtils.ts'
import MetadataService from '@/services/metadata'

const { t } = useI18n()
const route = useRoute()
const { proxy } = getCurrentInstance()!

const headers = createHeaders({ t })

const loading = ref(false)
const data = ref<Array<any>>([])

const handleInitialize = () => {

  const code = route.params.source
  const database = route.params.database
  const table = route.params.table

  if (code && database && table) {
    loading.value = true
    MetadataService.getColumnsByTable(code as string, database as string, table as string)
                   .then(response => {
                     if (response.status && response.data && response.data.isSuccessful) {
                       data.value = response.data.columns
                                            .filter(col => col.type_name === 'column')
                     }
                     else {
                       // @ts-ignore
                       proxy.$Message.error({
                         content: response.message,
                         showIcon: true
                       })
                     }
                   })
                   .finally(() => loading.value = false)
  }
}

watch(
    () => route.params.table,
    () => handleInitialize()
)

onMounted(() => handleInitialize())
</script>
