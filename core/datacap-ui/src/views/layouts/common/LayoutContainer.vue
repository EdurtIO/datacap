<template>
  <div class="relative">
    <div class="relative" v-if="loadingState" style="height: 100vh;">
      <ShadcnSpin v-model="loadingState" fixed>
        {{ loadingText }}
      </ShadcnSpin>
    </div>

    <div v-show="!loadingState">
      <ShadcnLayout>
        <LayoutHeader/>
      </ShadcnLayout>

      <div class="container my-2 min-h-screen">
        <LayoutBreadcrumb class="mb-2"/>
        <RouterView/>
      </div>

      <LayoutFooter/>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onBeforeMount } from 'vue'
// @ts-ignore
import { setLocale } from 'view-shadcn-ui'
import LayoutHeader from '@/views/layouts/common/components/LayoutHeader.vue'
import LayoutFooter from '@/views/layouts/common/components/LayoutFooter.vue'
import LayoutBreadcrumb from '@/views/layouts/common/components/LayoutBreadcrumb.vue'
import { provideI18nHandler } from '@/i18n/I18n'

const { loadLocale, loadingState, loadingText } = provideI18nHandler()

onBeforeMount(async () => {
  try {
    const locale = localStorage.getItem('locale') || 'zh-CN'
    await loadLocale(locale)
    await setLocale(locale)
  }
  catch (error) {
    console.error('Failed to load locale:', error)
  }
})
</script>
