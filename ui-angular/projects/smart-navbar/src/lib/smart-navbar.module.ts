import { NgModule } from "@angular/core";
import { SmartNavbarComponent } from "./smart-navbar.component";
import { BrowserModule } from "@angular/platform-browser";

import { MatToolbarModule } from "@angular/material/toolbar";
import { MatButtonModule } from "@angular/material/button";
import { MatInputModule } from "@angular/material/input";
import { MatIconModule } from "@angular/material/icon";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";

@NgModule({
    declarations: [SmartNavbarComponent],
    imports: [
        BrowserModule,
        MatButtonModule,
        MatToolbarModule,
        MatInputModule,
        MatIconModule,
        FormsModule,
        ReactiveFormsModule,
        MatFormFieldModule,
    ],
    exports: [SmartNavbarComponent],
})
export class SmartNavbarModule {}
