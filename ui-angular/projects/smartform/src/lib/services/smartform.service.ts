import { Injectable } from "@angular/core";
import { FormControl, FormGroup, ValidatorFn, Validators } from "@angular/forms";
import { SmartForm, SmartFormWidget, SmartFormWidgetType } from "../smartform.model";

@Injectable()
export class SmartFormService {
    group: any = {};

    toFormGroup(smartForm: SmartForm): FormGroup {
        this.createFormControls(smartForm.widgets);

        return new FormGroup(this.group);
    }

    createFormControls(widgets: SmartFormWidget<any>[]) {
        widgets.forEach((widget) => {
            if (
                widget.validators &&
                widget.isRequired &&
                !this.isValidatorInList(Validators.required, widget.validators)
            ) {
                widget.validators.push(Validators.required);
            } else if (!widget.validators && widget.isRequired) {
                widget.validators = [Validators.required];
            }

            let formControl = new FormControl(widget.value || "", widget.validators);

            if (widget.isDisabled) {
                formControl.disable();
            }

            this.group[widget.key] = formControl;
            if (widget.type === SmartFormWidgetType.CONTAINER && widget.valueList?.length) {
                this.createFormControls(widget.valueList);
            }
        });
    }

    isValidatorInList(validator: Validators, list?: ValidatorFn[]): boolean {
        return list !== undefined && list.length > 0 && list.some((v) => v === validator);
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

    toSmartForm(group: FormGroup, smartForm: SmartForm): SmartForm {
        smartForm = this.toSmartFormDeeply(smartForm.widgets, group, smartForm);

        return smartForm;
    }
}
