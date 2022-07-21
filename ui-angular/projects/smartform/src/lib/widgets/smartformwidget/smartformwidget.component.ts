import { COMMA, ENTER } from "@angular/cdk/keycodes";
import { Component, Input, ViewEncapsulation } from "@angular/core";
import { FormGroup } from "@angular/forms";
import { MatChipInputEvent } from "@angular/material/chips";
import {
    SmartFormWidget,
    SmartFormWidgetDirection,
    SmartFormWidgetType,
} from "../../smartform.model";

@Component({
    selector: "smartformwidget",
    templateUrl: "./smartformwidget.component.html",
    styleUrls: ["./smartformwidget.component.css"],
    encapsulation: ViewEncapsulation.None,
})
export class SmartformwidgetComponent {
    @Input() form!: FormGroup;
    @Input() widgetInstance!: SmartFormWidget<any>;

    smartFormWidgetType = SmartFormWidgetType;

    constructor() {}

    addOnBlur = true;
    readonly separatorKeysCodes = [ENTER, COMMA] as const;

    add(event: MatChipInputEvent): void {
        const value = (event.value || "").trim();

        if (value) {
            if (
                (this.widgetInstance.maxValues &&
                    this.widgetInstance.valueList!.length < this.widgetInstance.maxValues) ||
                !this.widgetInstance.maxValues
            ) {
                this.widgetInstance.valueList!.push({
                    key: value,
                    label: value,
                    type: SmartFormWidgetType.ITEM,
                    value: value,
                    callback: this.widgetInstance.callback,
                });
            }
        }

        // Clear the input value
        event.chipInput!.clear();
    }

    remove(value: SmartFormWidget<any>): void {
        const index = this.widgetInstance.valueList!.indexOf(value);

        if (index >= 0) {
            this.widgetInstance.valueList!.splice(index, 1);
        }
    }

    getDirection(): string {
        if (this.widgetInstance.direction === undefined) {
            return "";
        } else if (this.widgetInstance.direction === SmartFormWidgetDirection.COL) {
            return "direction-col";
        } else {
            return "direction-row";
        }
    }
}
