# Smart form version log

## References

These packages must be updated in case of a new version:

-   @smartbit4all/dialog

## @smartbit4all/form v0.1.1

**Type: Feature**

`CONTAINER` has been added to `SmartFormWidgetType`.

This **CONTAINER** type can be used for creating inline forms with _n_ widgets in it.

**CONTAINER usage example**

    {
        key: 'container-key',
        label: '',
        value: '',
        type: SmartFormWidgetType.CONTAINER,
        direction: SmartFormWidgetDirection.ROW,
        valueList: [
            {
                key: 'container-key-1',
                label: '1st label',
                type: SmartFormWidgetType.TEXT_FIELD,
                value: this.containerValue,
                ...
            },
            {
                key: 'container-key-2',
                label: '2nd label',
                type: SmartFormWidgetType.TEXT_FIELD,
                value: this.containerValue,
                ...
            },
            ...
        ],
        ...
    }

**Type: Feature**

A new property has been added to `SmartFormWidget` which disables the SmartFormWidget.

**The new property**

    export interface SmartFormWidget<T> {
        ...
        isDisabled?: boolean;
    }

**The change in the SmartFormService**

    createFormControls(widgets: SmartFormWidget<any>[]) {
        ...
        if (widget.isDisabled) {
            formControl.disable();
        }
        ...
    }

**Type: Bugfix**

The **CHECKBOX** widget had a bug, which caused that the label was shown after the options.

**The code snippet which caused the bug**

    .checkbox {
        flex-direction: column-reverse;
    }

**The code snippet which solved the bug**

    .checkbox {
        flex-direction: column;
    }

## @smartbit4all/form v0.0.2

**Type: Feature**

The basic smart form with its features.
