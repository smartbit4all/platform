import { Injectable } from "@angular/core";
import { FormControl, FormGroup, Validators } from "@angular/forms";
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
            let formControl = new FormControl(
                widget.value || "",
                widget.isRequired ? Validators.required : undefined
            );

            if (widget.isDisabled) {
                formControl.disable();
            }

            this.group[widget.key] = formControl;
            if (widget.type === SmartFormWidgetType.CONTAINER && widget.valueList?.length) {
                this.createFormControls(widget.valueList);
            }
        });
    }
}
