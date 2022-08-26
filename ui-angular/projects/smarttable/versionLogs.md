# Smart Table

[_@smartbit4all readme_](../../README.md)

## References

These packages must be updated in case of a new version:

-   [_@smartbit4all/dialog_](../smartdialog/versionLogs.md)

---

## How to use

### Installation

Go to your project, open the terminal and use the following command:

    npm i ../../platform/ui-angular/npms/smarttable/smartbit4all-table-0.1.7.tgz

Then import it in the AppModule:

`app.module.ts:`

    import { SmartformModule } from '@smartbit4all/form';
    ...
    @NgModule({
        declarations: [...]
        imports: [
            ...
            SmartformModule,
        ]
        ...
    })

### Usage

`example.component.html:`

    <smarttable [smartTable]="exampletable"></smarttable>

`example.component.ts:`

    export class SelectFolderContentComponent {
        exampleTable: SmartTable<T>;

        constructor() {
            this.setUpTable();
        }

        setUpTable(): void {
            let tableData: T[] =
            [
                {
                    tableData1: 'Table Data 1',
                    tableData2: 'Table Data 2',
                    tableData3: 'Table Data 3'
                },
                ...
            ];

            // Simple table without tableData3
            this.table = new SmartTable<T>(
                tableData
                SmartTableType.INHERITED,
                [
                    {
                        label: 'Table Header 1',
                        propertyName: 'tableData1'
                    },
                    {
                        label: 'Table Header 2',
                        propertyName: 'tableData2'
                    },
                    {
                        label: '',
                        propertyName: 'tableData3',
                        isHidden: true
                    },
                ],
            );

            // Checkbox table without tableData3
            this.table = new SmartTable<T>(
                tableData
                SmartTableType.CHECK_BOX,
                [
                    {
                        label: 'Table Header 1',
                        propertyName: 'tableData1'
                    },
                    {
                        label: 'Table Header 2',
                        propertyName: 'tableData2'
                    },
                    {
                        label: '',
                        propertyName: 'tableData3',
                        isHidden: true
                    },
                ],
            );

            // Custom order
            this.table = new SmartTable<T>(
                tableData
                SmartTableType.CHECK_BOX,
                [
                    {
                        label: 'Table Header 3',
                        propertyName: 'tableData3',
                    },
                    {
                        label: 'Table Header 1',
                        propertyName: 'tableData1'
                    }
                    {
                        label: 'Table Header 2',
                        propertyName: 'tableData2'
                    },
                ],
            );

            // Table with options
            this.table = new SmartTable<T>(
                tableData
                SmartTableType.INHERITED,
                [
                    {
                        label: 'Table Header 1',
                        propertyName: 'tableData1'
                    },
                    {
                        label: 'Table Header 2',
                        propertyName: 'tableData2'
                    }
                ],
    			undefined,
    			undefined,
    			[
    				{
    					label: 'Example option',
    					callback: () => {},
    					icon: 'remove'
    				}
    			]
            );
        }
    }

**Example output for Simple table without tableData3:**
| Table Header 1 | Table Header 2 |
| -------------- | -------------- |
| Table Data 1 | Table Data 2 |
|

**Example output for Checkbox table without tableData3:**

| [ ] | Table Header 1 | Table Header 2 |
| --- | -------------- | -------------- |
| [x] | Table Data 1   | Table Data 2   |

|

**Example output for Custom order:**
| Table Header 3 | Table Header 1 | Table Header 2 |
| -------------- | -------------- | -------------- |
| Table Data 3 | Table Data 1 | Table Data 1 |
|

**Example output for table with Options:**
| Table Header 1 | Table Header 2 | Options |
| -------------- | -------------- | --------|
| Table Data 1 | Table Data 2 | ... -> Example option
|

In order to **get the selected values** of a Checkbox Smart Table, you can simply use the SmartTable<T> property:

`example.component.ts:`

    getSelectedRows(): void {
        let selectedRows: SelectionModel<T> = this.table.selection;
    }

---

## Version logs

## @smartbit4all/table v0.1.7

**Type: Feature**

In this version the SmartTableComponent is able to use SmartToolbarComponent inside the table cells.

**Modifications:**

1.  SmartTableComponent html

        <div *ngIf="header === 'button'">
            <smart-toolbar [toolbar]="element[header]"></smart-toolbar>
        </div>

**Usage update:**

To use this toolbar feature, the objects given in the tableRow array must have a SmartToolbar object inside a property named button.

Checkout the usage of the SmartToolbar: [@smartbit4all/smart-toolbar](../smart-toolbar/versionLogs.md)

## @smartbit4all/table v0.1.4 - v0.1.6

**Type: Feature**

The update contains a new feature to SmartTable.
In this version the package is able to generate buttons into the table cell.

Modifications:

1.  SmartTableComponent html
2.  New type represent a button:

        export interface SmartTableButton {
            lable: string;
            icon?: string;
            onClick?: (args?: any[]) => void;
        }

**Usage:**
The elements of the tableRow array have to contain a button property which is a SmartTableButton.

## @smartbit4all/table v0.1.2 - v0.1.3

**Type: Bugfix**

The `SmartTableHeader` had a bug which may have caused serious issues in case of a model property change.

A function described by the `SmartTableInterface<T>` **has been removed** due to its lack of usage in the new version.

    equalsIgnoreOrder: (a: string[], b: string[]) => boolean;

Instead of comparing all the properties to each other, property by property comparison has been introduced.

**Old code snippet:**

    if (
            this.customSmartTableHeaders &&
            this.equalsIgnoreOrder(
                this.tableHeaders,
                this.customSmartTableHeaders.map((tableHeader) => tableHeader.propertyName)
            )
        ) {
            this.customSmartTableHeaders.forEach((tableHeader) => { ... }
        }

**New code snippet:**

    if (this.customSmartTableHeaders && this.customSmartTableHeaders.length) {
        ...
        this.customSmartTableHeaders.forEach((tableHeader) => {
            if (originalHeaders.includes(tableHeader.propertyName) && !tableHeader.isHidden) { ... }
        }
    }

## @smartbit4all/table v0.1.1

**Type: Feature**

With this update you can easily change the order of your smartTable with custom headers.

A new **SmartTableHeader** type has been introduced, with which the order and visibility can be set.

**The SmartTableHeader**

    export interface SmartTableHeader {
        propertyName: string;
        label: string;
        isHidden?: boolean;
    }

Example for creating a new SmartTable:

    new SmartTable(
        rows,
        SmartTableType.INHERITED,
        [
            {
                propertyName: 'uuid',
                label: '',
                isHidden: true
            },
            {
                propertyName: 'name',
                label: 'Full name',
            }
        ]
    );

## @smartbit4all/table v0.0.3

**Type: Feature**

Now images can be added to the SmartTable.

In order to use this new ability, **the model**, _which is used in the SmartTable_, **must contain** an `img: string` property which is the **path of the image**.

**Do not forget to add** the 'img' to the `customTableHeaders` at the **proper** position.

## @smartbit4all/table v0.0.2

**Type: Feature**

Default version. The smarttable works.
