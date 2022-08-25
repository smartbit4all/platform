# Smart table version log

[_@smartbit4all readme_](../../README.md)

## References

These packages must be updated in case of a new version:

- @smartbit4all/dialog

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
