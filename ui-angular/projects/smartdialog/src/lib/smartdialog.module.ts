import { CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA } from "@angular/core";
import { SmartDialog } from "./smartdialog.component";
import { MatDialogModule } from "@angular/material/dialog";
import { MatIconModule } from "@angular/material/icon";
import { SmartformModule } from "@smartbit4all/form";
import { SmarttableModule } from "@smartbit4all/table";
import { MatCommonModule } from "@angular/material/core";
import { SmartdialogService } from "./smartdialog.service";

@NgModule({
    declarations: [SmartDialog],
    imports: [MatDialogModule, MatCommonModule, MatIconModule, SmartformModule, SmarttableModule],
    exports: [SmartDialog],
    schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
    providers: [SmartdialogService],
})
export class SmartdialogModule {}
