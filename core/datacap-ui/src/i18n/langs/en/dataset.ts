export default {
    common: {
        list: 'Dataset List',
        adhoc: 'Adhoc Query',
        showPageSize: 'Show Page Size',
        totalRows: 'Total Rows',
        totalSize: 'Total Size',
        dataPreview: 'Data Preview',
        dataColumn: 'Data Columns',
        dataConfigure: 'Data Configure',
        dataLifeCycle: 'Data Life Cycle',
        onlyPreviewCreate: 'Only preview data can be used to create datasets',
        returnQuery: 'Return Query',
        columnName: 'Column Name',
        columnAlias: 'Column Alias',
        columnType: 'Column Type',
        columnTypeString: 'String',
        columnTypeNumber: 'Number',
        columnTypeNumberSigned: 'Number (Signed)',
        columnTypeBoolean: 'Boolean',
        columnTypeDateTime: 'DateTime',
        columnDescription: 'Column Description',
        columnComment: 'Column Comment',
        columnDefaultValue: 'Default Value',
        columnIsNullable: 'Is Nullable',
        columnLength: 'Length',
        columnIsOrderByKey: 'Sort key',
        columnIsPartitionKey: 'Partition key',
        columnIsPrimaryKey: 'Primary key',
        columnIsSampling: 'Is Sampling',
        columnExpression: 'Expression',
        columnMode: 'Column Mode',
        columnModeMetric: 'Metric',
        columnModeDimension: 'Dimension',
        columnModeGroup: 'Group',
        columnModeFilter: 'Filter',
        columnSortNone: 'None',
        columnOrderAsc: 'Ascending',
        columnOrderDesc: 'Descending',
        create: 'Create Dataset',
        modify: 'Modify Dateset',
        actuator: 'Actuator',
        syncMode: 'Sync Mode',
        syncModeManual: 'Manual',
        syncModeTiming: 'Timing synchronization',
        syncModeOutSync: 'Out Sync',
        rebuild: 'Rebuild',
        complete: 'Complete',
        failed: 'Failed',
        stateOfStart: 'Start',
        stateOfMetadata: 'Metadata State',
        stateOfMetadataStarted: 'Metadata Started',
        stateOfCreateTable: 'Create Table State',
        syncData: 'Sync Data',
        visualType: 'Visual Type',
        visualTypeTable: 'Table',
        visualTypeLine: 'Line',
        visualTypeBar: 'Bar',
        visualTypeArea: 'Area',
        visualTypePie: 'Pie',
        visualTypeHistogram: 'Histogram',
        visualTypeWordCloud: 'Word Cloud',
        visualTypeScatter: 'Scatter',
        visualTypeRadar: 'Radar',
        visualTypeFunnel: 'Funnel',
        visualConfigure: 'Visual Configure',
        visualConfigureNotSpecified: 'No configuration items are available',
        visualConfigureXAxis: 'X Axis',
        visualConfigureYAxis: 'Y Axis',
        visualConfigureCategoryField: 'Category',
        visualConfigureCategoryLeftField: 'Left Category',
        visualConfigureCategoryRightField: 'Right Category',
        visualConfigureValueField: 'Value',
        visualConfigureSeriesField: 'Series',
        visualConfigureOuterRadius: 'Outer Radius',
        visualConfigureShowLegend: 'Show Legend',
        visualConfigureDataBreakpoint: 'Data Breakpoint',
        visualConfigureDataBreakpointBreak: 'Break',
        visualConfigureDataBreakpointContinuous: 'Continuous',
        visualConfigureDataBreakpointZero: 'Zero',
        visualConfigureDataBreakpointIgnore: 'Ignore',
        columnExpressionMax: 'Maximum',
        columnExpressionMin: 'Minimum',
        columnExpressionSum: 'Sum',
        columnExpressionAvg: 'Average',
        columnExpressionCount: 'Count',
        columnExpressionEquals: 'Equals',
        columnExpressionNotEquals: 'Not Equals',
        columnExpressionIsNull: 'Is Null',
        columnExpressionIsNotNull: 'Is Not Null',
        columnExpressionIsIn: 'Is In',
        columnExpressionIsNotIn: 'Is Not In',
        columnExpressionIsLike: 'Is Like',
        columnExpressionIsNotLike: 'Is Not Like',
        columnExpressionIsContains: 'Is Contains',
        columnExpressionGreaterThan: 'Greater Than',
        columnExpressionGreaterThanOrEquals: 'Greater Than Or Equals',
        columnExpressionLessThan: 'Less Than',
        columnExpressionLessThanOrEquals: 'Less Than Or Equals',
        columnExpressionIsNotContains: 'Is Not Contains',
        customFunction: 'Custom Function',
        lifeCycleMonth: 'Month',
        lifeCycleWeek: 'Week',
        lifeCycleDay: 'Day',
        lifeCycleHour: 'Hour',
        notSpecifiedTitle: 'Not Specified',
        history: 'Sync History',
        clearData: 'Clear Data',
        error: 'View Error',
        info: 'View Info',
        lifeCycleColumn: 'Lifecycle columns',
        lifeCycleNumber: 'Lifecycle number',
        continuousBuild: 'Continuous Build',
    },
    validator: {
        duplicateColumn: 'Column name [ $VALUE ] already exists',
        specifiedColumn: 'Sort key or primary key must be specified',
        specifiedName: 'Name must be specified',
    },
    tip: {
        selectExpression: 'Please select the expression',
        syncData: 'The data synchronization schedule will run in the background, see the logs for the specific synchronization results',
        clearData: 'Clear data will not be able to rollback, clear operation will run in the background, please be patient',
        lifeCycle: 'Data set life cycle will be calculated according to the specified list expression',
        validatorSampling: 'The order by key must contain a sampling key',
        adhocDnd: 'Drag the indicator dimension on the left to the corresponding position to query and render the data',
        rebuildProgress: 'Rebuilding will only progress unfinished',
        lifeCycleMustDateColumn: 'The lifecycle must contain a date column',
        modifyNotSupportDataPreview: 'Data preview is not supported to modify',
        publishSuccess: 'Dataset [ $VALUE ] published successfully',
    }
}