import { NgModule, CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { SmarttableComponent } from "./smarttable.component";
import { MatTableModule } from "@angular/material/table";
import { MatCommonModule } from "@angular/material/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatMenuModule } from "@angular/material/menu";
import { BrowserModule } from "@angular/platform-browser";
import { SmartToolbarModule } from "@smartbit4all/smart-toolbar";

@NgModule({
    declarations: [SmarttableComponent],
    imports: [
        BrowserModule,
        MatCommonModule,
        FormsModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatCheckboxModule,
        MatButtonModule,
        MatTableModule,
        MatIconModule,
        MatMenuModule,
        SmartToolbarModule
    ],
    exports: [SmarttableComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class SmarttableModule { }
