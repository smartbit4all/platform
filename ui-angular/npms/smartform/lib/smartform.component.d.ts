import { OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { SmartFormService } from './services/smartform.service';
import { SmartForm, SmartFormWidgetDirection } from './smartform.model';
import * as i0 from "@angular/core";
export declare class SmartformComponent implements OnInit {
    private service;
    smartForm: SmartForm;
    form: FormGroup;
    direction: typeof SmartFormWidgetDirection;
    constructor(service: SmartFormService);
    ngOnInit(): void;
    getForm(): FormGroup;
    static ɵfac: i0.ɵɵFactoryDeclaration<SmartformComponent, never>;
    static ɵcmp: i0.ɵɵComponentDeclaration<SmartformComponent, "smartform", never, { "smartForm": "smartForm"; }, {}, never, never>;
}
