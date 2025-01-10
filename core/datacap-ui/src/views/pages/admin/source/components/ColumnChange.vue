<template>
  <ShadcnModal v-model="visible"
               height="60%"
               width="40%"
               :title="$t('source.common.newColumn')"
               @on-close="onCancel">
    <ShadcnForm v-model="formState" v-if="formState" @on-submit="onSubmit">
      <ShadcnRow gutter="16">
        <ShadcnCol v-for="(item, index) in formState.columns" span="12">
          <ShadcnDivider orientation="left">{{ item.name }}</ShadcnDivider>

          <ShadcnRow gutter="16">
            <ShadcnCol span="6">
              <ShadcnFormItem :name="`columns[${index}].name`"
                              :label="`${$t('source.common.columnName')} ${index + 1}`"
                              :rules="[
                                  { required: true, message: $t('source.validator.columnName.required') },
                                  { pattern: /^[A-Za-z][A-Za-z0-9_-]*$/, message: $t('source.validator.columnName.pattern') }
                              ]">
                <ShadcnInput v-model="formState.columns[index].name" :placeholder="$t('source.placeholder.columnName')" :name="`columns[${index}].name`"/>
              </ShadcnFormItem>
            </ShadcnCol>

            <ShadcnCol span="6">
              <ShadcnFormItem :name="`columns[${index}].type`"
                              :label="$t('source.common.columnType')"
                              :rules="[{ required: true, message: $t('source.validator.columnType.required') }]">
                <ShadcnInput v-model="formState.columns[index].type" :placeholder="$t('source.placeholder.columnType')" :name="`columns[${index}].type`"/>
              </ShadcnFormItem>
            </ShadcnCol>

            <ShadcnCol span="6">
              <ShadcnFormItem :name="`columns[${index}].length`"
                              :label="$t('source.common.columnLength')"
                              :rules="[
                                  { required: true, message: $t('source.validator.columnLength.required') },
                                  { min: 1, message: $t('source.validator.columnLength.min') }
                              ]">
                <ShadcnNumber v-model="formState.columns[index].length"
                              :placeholder="$t('source.placeholder.columnLength')"
                              :name="`columns[${index}].length`"
                              :min="1"/>
              </ShadcnFormItem>
            </ShadcnCol>

            <ShadcnCol span="6">
              <ShadcnFormItem :name="`columns[${index}].defaultValue`" :label="$t('source.common.columnDefaultValue')">
                <ShadcnInput v-model="formState.columns[index].defaultValue" :placeholder="$t('source.placeholder.columnDefaultValue')" :name="`columns[${index}].defaultValue`"/>
              </ShadcnFormItem>
            </ShadcnCol>

            <ShadcnCol span="4">
              <ShadcnFormItem :name="`columns[${index}].primaryKey`" :label="$t('source.common.columnPrimaryKey')">
                <ShadcnSwitch v-model="formState.columns[index].primaryKey" :placeholder="$t('source.placeholder.columnPrimaryKey')" :name="`columns[${index}].primaryKey`"/>
              </ShadcnFormItem>
            </ShadcnCol>

            <ShadcnCol span="4">
              <ShadcnFormItem :name="`columns[${index}].autoIncrement`" :label="$t('source.common.columnAutoIncrement')">
                <ShadcnSwitch v-model="formState.columns[index].autoIncrement" :placeholder="$t('source.placeholder.columnAutoIncrement')"
                              :name="`columns[${index}].autoIncrement`"/>
              </ShadcnFormItem>
            </ShadcnCol>

            <ShadcnCol span="4">
              <ShadcnFormItem :name="`columns[${index}].isNullable`" :label="$t('source.common.columnIsNullable')">
                <ShadcnSwitch v-model="formState.columns[index].isNullable" :placeholder="$t('source.placeholder.columnIsNullable')" :name="`columns[${index}].isNullable`"/>
              </ShadcnFormItem>
            </ShadcnCol>

            <ShadcnCol span="12">
              <ShadcnFormItem :name="`columns[${index}].comment`" :label="$t('source.common.columnComment')">
                <ShadcnInput v-model="formState.columns[index].comment"
                             type="textarea"
                             :placeholder="$t('source.placeholder.columnComment')"
                             :name="`columns[${index}].comment`"/>
              </ShadcnFormItem>
            </ShadcnCol>
          </ShadcnRow>
        </ShadcnCol>
      </ShadcnRow>

      <ShadcnSpace>
        <ShadcnButton type="default" @click="onCancel">{{ $t('common.cancel') }}</ShadcnButton>

        <ShadcnButton submit :loading="submitting" :disabled="submitting">
          {{ $t('common.save') }}
        </ShadcnButton>
      </ShadcnSpace>
    </ShadcnForm>
  </ShadcnModal>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { StructureModel } from '@/model/structure'

export default defineComponent({
  name: 'ColumnChange',
  computed: {
    visible: {
      get(): boolean
      {
        return this.isVisible
      },
      set(value: boolean)
      {
        this.$emit('close', value)
      }
    }
  },
  props: {
    isVisible: {
      type: Boolean
    },
    info: {
      type: Object as () => StructureModel | null
    }
  },
  data()
  {
    return {
      loading: false,
      submitting: false,
      formState: null as unknown as TableModel
    }
  },
  created()
  {
    this.formState = TableRequest.of()
    this.formState.type = SqlType.MODIFY
    this.handleInitialize()
  },
  methods: {
    handleInitialize()
    {
      if (this.info) {
        this.loading = true
        ColumnService.getByCode(String(this.info.value))
                     .then(response => {
                       if (response.status) {
                         const data = response.data
                         const column: ColumnModel = {
                           name: data.name,
                           type: data.dataType,
                           length: data.maximumLength,
                           comment: data.comment,
                           defaultValue: data.defaultValue,
                           primaryKey: data.isKey === 'PRI',
                           autoIncrement: data.extra === 'auto_increment',
                           isNullable: data.isNullable,
                           opened: true
                         }
                         if (this.formState.columns) {
                           this.formState.columns.push(column)
                         }
                       }
                     })
                     .finally(() => this.loading = false)
      }
    },
    onSubmit()
    {
      if (this.info) {
        this.submitting = true
        TableService.manageColumn(String(this.info.tableId), this.formState)
                    .then(response => {
                      if (response.status) {
                        if (response.data.isSuccessful) {
                          const columns = this.formState
                                              ?.columns
                                              ?.map(item => item.name)
                                              .join(', ') as string
                          this.$Message.success({
                            content: this.$t('source.tip.changeColumnSuccess').replace('$VALUE', columns),
                            showIcon: true
                          })

                          this.onCancel()
                        }
                        else {
                          this.$Message.error({
                            content: response.data.message,
                            showIcon: true
                          })
                        }
                      }
                      else {
                        this.$Message.error({
                          content: response.message,
                          showIcon: true
                        })
                      }
                    })
                    .finally(() => this.submitting = false)
      }
    },
    onCancel()
    {
      this.visible = false
    }
  }
})
</script>
