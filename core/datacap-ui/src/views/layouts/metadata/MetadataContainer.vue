<template>
  <BaseLayout>
    <ShadcnLayout>
      <div class="container my-2 min-h-screen">
        <ShadcnLayoutWrapper>
          <ShadcnLayoutSider class="mr-4" width="250">
            <MetadataSidebar/>
          </ShadcnLayoutSider>
          <ShadcnLayoutMain class="ml-2">
            <ShadcnLayoutContent>
              <MetadataContent/>
            </ShadcnLayoutContent>
          </ShadcnLayoutMain>
        </ShadcnLayoutWrapper>
      </div>
    </ShadcnLayout>
  </BaseLayout>
</template>

<script setup lang="ts">
import MetadataSidebar from '@/views/layouts/metadata/components/MetadataSidebar.vue'
import MetadataContent from '@/views/layouts/metadata/components/MetadataContent.vue'
import BaseLayout from '@/views/layouts/base/BaseLayout.vue'
</script>
