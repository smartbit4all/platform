import { Component, Input, OnInit } from "@angular/core";
import { FormGroup } from "@angular/forms";
import { SmartFormService } from "./services/smartform.service";
import { SmartForm, SmartFormWidgetDirection } from "./smartform.model";

@Component({
    selector: "smartform",
    templateUrl: "./smartform.component.html",
    styleUrls: ["./smartform.component.css"],
    providers: [SmartFormService],
})
export class SmartformComponent implements OnInit {
    @Input() smartForm!: SmartForm;

    form!: FormGroup;

    direction = SmartFormWidgetDirection;

    constructor(private service: SmartFormService) {}

    ngOnInit(): void {
        this.form = this.service.toFormGroup(this.smartForm);
    }

    getForm(): FormGroup {
        return this.form;
    }

    submitForm(): SmartForm {
        if (this.form.status === "VALID") {
            return this.service.toSmartForm(this.form, this.smartForm);
        } else {
            throw new Error(`The form status is ${this.form.status}.`);
        }
    }
}
