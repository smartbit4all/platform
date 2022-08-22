import { FormGroup } from "@angular/forms";
import { SmartForm, SmartFormWidget } from "../smartform.model";
import * as i0 from "@angular/core";
export declare class SmartFormService {
    group: any;
    toFormGroup(smartForm: SmartForm): FormGroup;
    createFormControls(widgets: SmartFormWidget<any>[]): void;
    static ɵfac: i0.ɵɵFactoryDeclaration<SmartFormService, never>;
    static ɵprov: i0.ɵɵInjectableDeclaration<SmartFormService>;
}
