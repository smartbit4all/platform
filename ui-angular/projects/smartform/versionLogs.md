# Smart Form

[_@smartbit4all readme_](../../README.md)

## References

These packages must be updated in case of a new version:

-   [_@smartbit4all/dialog_](../smartdialog/versionLogs.md)

---

## How to use

### Installation

Go to your project, open the terminal and use the following command:

    npm i ../../platform/ui-angular/npms/smartform/smartbit4all-form-0.1.7.tgz

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

`example.component.html`

    <smartform #exampleForm [smartForm]="form!"></smartform>

`example.component.ts`

    @ViewChild('exampleForm') child?: SmartformComponent;

    smartForm: SmartForm;

    constructor() {
        this.smartForm = {
    		name: '',
    		direction: SmartFormWidgetDirection.COL,
    		widgets: [
    			{
    				key: 'exampleEmail',
    				label: 'Email',
    				type: SmartFormWidgetType.TEXT_FIELD,
    				value: '',
    				validators: [Validators.required, Validators.email],
    				errorMessage: 'It is not a valid email.'
    			}
    		]
    	};
    }

    submit(): void {
    	this.smartForm = this.child?.submitForm();
    }

---

## Version logs

## @smartbit4all/form v0.1.7

**Type: Bugfix**

A major bug has been fixed which made _getting the value of a container_ impossible.

In the previous version the **SmartFormService** only had the **toSmartForm()** function, which was not able to recreate the smartForm in depth.

**The bugfix:**

    toSmartForm(group: FormGroup, smartForm: SmartForm): SmartForm {
        smartForm = this.toSmartFormDeeply(smartForm.widgets, group, smartForm);

        return smartForm;
    }

    toSmartFormDeeply(
        widgets: SmartFormWidget<any>[],
        group: FormGroup,
        smartForm: SmartForm
    ): SmartForm {
        widgets.forEach((widget) => {
            if (widget.type === SmartFormWidgetType.CONTAINER && widget.valueList?.length) {
                smartForm = this.toSmartFormDeeply(widget.valueList, group, smartForm);
            } else {
                widget.value = group.controls[widget.key].value;
            }
        });
        return smartForm;
    }

## @smartbit4all/form v0.1.6

**Type: Feature**

This version contains one major change: **TIME** picker component have been added.
Small layout change: 1em margin have been added to **CONTAINER** typed widgets.

**Usage have not changed**.

## @smartbit4all/form v0.1.5

**Type: Feature**

This version contains two changes: **LABEL** type to the SmartFormWidgetType enum
and
**widgetDescription** to the SmartFormWidget have been added.

**Changes:**

The LABEL type widget can be used as separated label for the input field.
Description can be added to the label if you'd like to.

## @smartbit4all/form v0.1.4

**Type: Feature**

This version contains two major changes: a **submit function** and **custom validators** have been added to the `SmartForm`.

---

**Changes:**

The **submitForm()** function is implemented in the `SmartformComponent`. The new values are translated back to the given `SmartForm` object if the status of the form is valid. If it is not, the function throws an error with the current state.

    submitForm(): SmartForm {
        if (this.form.status === "VALID") {
            return this.service.toSmartForm(this.form, this.smartForm);
        } else {
            throw new Error(`The form status is ${this.form.status}.`);
        }
    }

In order to use **multiple custom validators**, the `SmartFormWidget` got a validators property. A custom error message can be set with the **errorMessage** property.

    export interface SmartFormWidget<T> {
        ...
        validators?: ValidatorFn[];
        errorMessage?: string;
    }

## @smartbit4all/form v0.1.2

**Type: Feature**

_Prefix_ and _Suffix_ have been added to `SmartFormWidget`. These two can be used with **TextFields**.

**TEXT_FIELD_NUMBER** as a new `SmartFormWidgetType` can be used for numbers.

**Example for prefix, suffix and TEXT_FIELD_NUMBER:**

    {
        key: 'money',
        label: 'Money',
        type: SmartFormWidgetType.TEXT_FIELD_NUMBER,
        value: 10,
        prefix: '€',
        suffix: '.00',
        ...
    }

**Output:** _€ 10.00_

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
