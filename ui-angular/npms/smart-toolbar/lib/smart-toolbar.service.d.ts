import { ActivatedRoute, Router } from '@angular/router';
import { SmartToolbarButton } from './smart-toolbar.model';
import * as i0 from "@angular/core";
export declare class SmartToolbarService {
    private router;
    private route;
    constructor(router: Router, route: ActivatedRoute);
    executeCommand(button: SmartToolbarButton): void;
    static ɵfac: i0.ɵɵFactoryDeclaration<SmartToolbarService, never>;
    static ɵprov: i0.ɵɵInjectableDeclaration<SmartToolbarService>;
}
