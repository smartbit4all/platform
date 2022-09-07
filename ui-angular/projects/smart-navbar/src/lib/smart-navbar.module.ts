import { NgModule } from "@angular/core";
import { SmartNavbarComponent } from "./smart-navbar.component";
import { BrowserModule } from "@angular/platform-browser";

import { MatToolbarModule } from "@angular/material/toolbar";
import { MatButtonModule } from "@angular/material/button";
import { MatInputModule } from "@angular/material/input";
import { MatIconModule } from "@angular/material/icon";
import { MatBadgeModule } from "@angular/material/badge";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatCommonModule } from "@angular/material/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";


@NgModule({
    declarations: [SmartNavbarComponent],
    imports: [
        BrowserModule,
        MatCommonModule,
        MatButtonModule,
        MatToolbarModule,
        MatInputModule,
        MatIconModule,
        FormsModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatBadgeModule
    ],
    exports: [SmartNavbarComponent],
})
export class SmartNavbarModule {}
