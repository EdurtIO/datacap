<template>
  <ShadcnModal v-model="visible"
               height="300"
               :title="title"
               @on-close="onCancel">
    <ShadcnForm v-model="formState" @on-submit="onCommit">
      <div v-if="String(columnType) === 'FILTER'">
        <ShadcnFormItem name="expression" :label="$t('common.expression')">
          <ShadcnSelect v-model="formState.expression" :placeholder="$t('dataset.tip.selectExpression')">
            <template #options>
              <ShadcnSelectOption :value="Expression.IS_NULL" :label="$t('dataset.common.columnExpressionIsNull')"/>
              <ShadcnSelectOption :value="Expression.IS_NOT_NULL" :label="$t('dataset.common.columnExpressionIsNotNull')"/>
              <ShadcnSelectOption :value="Expression.IS_LIKE" :label="$t('dataset.common.columnExpressionIsLike')"/>
              <ShadcnSelectOption :value="Expression.IS_NOT_LIKE" :label="$t('dataset.common.columnExpressionIsNotLike')"/>
              <ShadcnSelectOption :value="Expression.EQ" :label="$t('dataset.common.columnExpressionEquals')"/>
              <ShadcnSelectOption :value="Expression.NE" :label="$t('dataset.common.columnExpressionNotEquals')"/>
              <ShadcnSelectOption :value="Expression.GT" :label="$t('dataset.common.columnExpressionGreaterThan')"/>
              <ShadcnSelectOption :value="Expression.GTE" :label="$t('dataset.common.columnExpressionGreaterThanOrEquals')"/>
              <ShadcnSelectOption :value="Expression.LT" :label="$t('dataset.common.columnExpressionLessThan')"/>
              <ShadcnSelectOption :value="Expression.LTE" :label="$t('dataset.common.columnExpressionLessThanOrEquals')"/>
            </template>
          </ShadcnSelect>
        </ShadcnFormItem>

        <ShadcnFormItem v-if="formState.expression && formState.expression !== Expression.IS_NULL && formState.expression !== Expression.IS_NOT_NULL"
                        name="value"
                        :label="$t('common.value')">
          <ShadcnInput v-model="formState.value"/>
        </ShadcnFormItem>
      </div>

      <div v-else>
        <ShadcnSpace wrap>
          <ShadcnFormItem v-if="String(columnType) === 'METRIC'"
                          class="w-full"
                          name="expression"
                          :label="$t('common.expression')">
            <ShadcnSelect v-model="formState.expression" :placeholder="$t('dataset.tip.selectExpression')" @on-change="onUpdateAlias">
              <template #options>
                <ShadcnSelectOption v-if="formState.type === ColumnType.NUMBER" :value="Expression.SUM" :label="$t('dataset.common.columnExpressionSum')"/>
                <ShadcnSelectOption :value="Expression.COUNT" :label="$t('dataset.common.columnExpressionCount')"/>
                <ShadcnSelectOption :value="Expression.MAX" :label="$t('dataset.common.columnExpressionMax')"/>
                <ShadcnSelectOption :value="Expression.MIN" :label="$t('dataset.common.columnExpressionMin')"/>
                <ShadcnSelectOption v-if="formState.type === ColumnType.NUMBER" :value="Expression.AVG" :label="$t('dataset.common.columnExpressionAvg') "/>
              </template>
            </ShadcnSelect>
          </ShadcnFormItem>

          <ShadcnFormItem class="w-full" name="alias" :label="$t('common.alias')">
            <ShadcnInput v-model="formState.alias"/>
          </ShadcnFormItem>

          <ShadcnFormItem class="w-full" name="order" :label="$t('common.sort')">
            <ShadcnToggleGroup v-model="formState.order" @onChange="onChangeToggle">
              <ShadcnToggle class="w-10 h-10" value="">{{ $t('dataset.common.columnSortNone') }}</ShadcnToggle>
              <ShadcnToggle class="w-10 h-10" value="ASC">{{ $t('dataset.common.columnOrderAsc') }}</ShadcnToggle>
              <ShadcnToggle class="w-10 h-10" value="DESC">{{ $t('dataset.common.columnOrderDesc') }}</ShadcnToggle>
            </ShadcnToggleGroup>
          </ShadcnFormItem>

          <ShadcnFormItem v-if="String(columnType) === 'DIMENSION'"
                          class="w-full"
                          name="function"
                          :label="$t('dataset.common.customFunction')">
            <ShadcnInput v-model="formState.function"/>
          </ShadcnFormItem>
        </ShadcnSpace>
      </div>


      <div class="flex justify-end">
        <ShadcnSpace>
          <ShadcnButton type="default" @click="onCancel">
            {{ $t('common.cancel') }}
          </ShadcnButton>

          <ShadcnButton submit>
            {{ $t('common.save') }}
          </ShadcnButton>
        </ShadcnSpace>
      </div>
    </ShadcnForm>
  </ShadcnModal>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import { cloneDeep } from 'lodash'
import { ColumnType, Type } from '@/views/pages/admin/dataset/Type'
import { Expression } from '@/views/pages/admin/dataset/Expression'
import { Model } from '../../model/model'

export default defineComponent({
  name: 'DatasetColumnConfigure',
  props: {
    isVisible: {
      type: Boolean,
      default: () => false
    },
    columnType: {
      type: String as unknown as PropType<Type | null>,
      default: () => Type.DIMENSION
    },
    content: {
      type: Object,
      default: () => null
    },
    configure: {
      type: Object as () => any
    }
  },
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
    },
    Expression()
    {
      return Expression
    },
    ColumnType()
    {
      return ColumnType
    }
  },
  data()
  {
    return {
      title: '',
      formState: {} as Model
    }
  },
  created()
  {
    this.handleInitialize()
  },
  methods: {
    handleInitialize()
    {
      this.formState = {} as Model
      if (this.formState) {
        let prefix = `${ this.$t('dataset.common.columnModeMetric') }`
        if (this.columnType === Type.DIMENSION) {
          prefix = `${ this.$t('dataset.common.columnModeDimension') }`
        }
        else if (this.columnType === Type.FILTER) {
          prefix = `${ this.$t('dataset.common.columnModeFilter') }`
        }
        this.title = `${ prefix } [ ${ this.content.aliasName ? this.content.aliasName : this.content.name } ] ${ this.$t('common.configure') }`
        if (this.configure) {
          const cloneValue = cloneDeep(this.configure) as Model
          this.formState = cloneValue
        }
        else {
          this.formState.id = this.content.id
        }
      }
    },
    onUpdateAlias()
    {
      let prefix = `${ this.$t('dataset.common.columnModeMetric') }`
      if (this.columnType === Type.DIMENSION) {
        prefix = `${ this.$t('dataset.common.columnModeDimension') }`
      }
      else if (this.columnType === Type.FILTER) {
        prefix = `${ this.$t('dataset.common.columnModeFilter') }`
      }
      const startWith = `${ prefix } [ ${ this.content.aliasName ? this.content.aliasName : this.content.name } ]`
      if (this.formState) {
        if (this.formState.expression === Expression.SUM) {
          this.formState.alias = `${ startWith } ${ this.$t('dataset.common.columnExpressionSum') }`
        }
        else if (this.formState.expression === Expression.COUNT) {
          this.formState.alias = `${ startWith } ${ this.$t('dataset.common.columnExpressionCount') }`
        }
        else if (this.formState.expression === Expression.MAX) {
          this.formState.alias = `${ startWith } ${ this.$t('dataset.common.columnExpressionMax') }`
        }
        else if (this.formState.expression === Expression.MIN) {
          this.formState.alias = `${ startWith } ${ this.$t('dataset.common.columnExpressionMin') }`
        }
        else if (this.formState.expression === Expression.AVG) {
          this.formState.alias = `${ startWith } ${ this.$t('dataset.common.columnExpressionAvg') }`
        }
        else {
          this.formState.alias = startWith
        }
      }
    },
    onCommit()
    {
      if (this.formState) {
        this.formState.mode = String(this.columnType)
        this.$emit('commit', this.formState)

        this.onCancel()
      }
    },
    onChangeToggle(value: any)
    {
      this.formState.order = value
    },
    onCancel()
    {
      this.visible = false
    }
  }
})
</script>
