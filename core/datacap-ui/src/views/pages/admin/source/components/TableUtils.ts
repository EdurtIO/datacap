import GridOptions from '@/views/components/grid/GridOptions'

const createHeaders = (i18n: any) => {
    return [
        { key: 'object_name', label: i18n.t('common.name') },
        { key: 'object_data_type', label: i18n.t('common.type') },
        { key: 'object_position', label: i18n.t('common.position') },
        { key: 'object_nullable', label: i18n.t('source.common.isNullable'), slot: 'object_nullable' },
        { key: 'object_default_value', label: i18n.t('source.common.defaultValue') },
        { key: 'object_comment', label: i18n.t('source.common.comment') },
        { key: 'object_definition', label: i18n.t('source.common.extra') }
    ]
}

/**
 * Create column definitions based on headers and types.
 *
 * @param {any[]} headers - The array of headers.
 * @param {any[]} types - The array of types.
 * @return {any[]} The array of column definitions.
 */
const createColumnDefs = (headers: any[], types: any[]): any[] => {
    const columnDefs = [] as any[]
    headers.forEach((header, index) => {
        const columnDef = {
            headerName: header,
            field: header,
            headerTooltip: header + ' [' + types[index] + ']',
            unSortIcon: true,
            cellRenderer: (params: { value: string }) => {
                return '<div style="white-space: pre-wrap;">' + params.value + '</div>'
            },
            cellEditorPopup: true,
            cellEditor: 'agLargeTextCellEditor',
            cellEditorParams: { maxLength: 9999999999999, rows: 10 },
            checked: true
        }
        columnDefs.push(columnDef)
    })
    return columnDefs
}

/**
 * Creates data editor options.
 *
 * @param {any} i18n - The i18n object.
 * @return {Object} The grid options.
 */
const createDataEditorOptions = (i18n: any): object => {
    const gridOptions = {
        animateRows: true,
        localeText: GridOptions.createLocale(i18n),
        // Fixed issues: https://github.com/EdurtIO/datacap/issues/219
        suppressFieldDotNotation: true,
        // Turn on multi-column sorting
        alwaysMultiSort: true,
        defaultColDef: {
            sortable: true,
            resizable: true,
            wrapText: true,
            editable: true,
            // Prevents the front-end from reordering data
            comparator: () => 0
        }
    }
    return gridOptions
}

export {
    createHeaders,
    createColumnDefs,
    createDataEditorOptions
}
