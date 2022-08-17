# Smart table version log

## References

These packages must be updated in case of a new version:

-   @smartbit4all/dialog

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
