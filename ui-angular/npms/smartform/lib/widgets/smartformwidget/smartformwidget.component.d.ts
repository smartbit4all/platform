import { FormGroup } from "@angular/forms";
import { MatChipInputEvent } from "@angular/material/chips";
import { SmartFormWidget, SmartFormWidgetType } from "../../smartform.model";
import * as i0 from "@angular/core";
export declare class SmartformwidgetComponent {
    form: FormGroup;
    widgetInstance: SmartFormWidget<any>;
    smartFormWidgetType: typeof SmartFormWidgetType;
    constructor();
    addOnBlur: boolean;
    readonly separatorKeysCodes: readonly [13, 188];
    add(event: MatChipInputEvent): void;
    remove(value: SmartFormWidget<any>): void;
    getDirection(): string;
    static ɵfac: i0.ɵɵFactoryDeclaration<SmartformwidgetComponent, never>;
    static ɵcmp: i0.ɵɵComponentDeclaration<SmartformwidgetComponent, "smartformwidget", never, { "form": "form"; "widgetInstance": "widgetInstance"; }, {}, never, never>;
}
