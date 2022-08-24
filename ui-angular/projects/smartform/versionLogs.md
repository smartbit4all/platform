# Smart form version log

[_@smartbit4all readme_](../../README.md)

## References

These packages must be updated in case of a new version:

-   @smartbit4all/dialog

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

---

**How to use this version properly:**

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
