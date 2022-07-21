import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { SmartformComponent } from "./smartform.component";
import { MatChipsModule } from "@angular/material/chips";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatSelectModule } from "@angular/material/select";
import { MatButtonModule } from "@angular/material/button";
import { MatInputModule } from "@angular/material/input";
import { MatIconModule } from "@angular/material/icon";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";
import { MatRadioModule } from "@angular/material/radio";

import { SmartformwidgetComponent } from "./widgets/smartformwidget/smartformwidget.component";
import { SmartfileuploaderComponent } from "./smartfileuploader/smartfileuploader.component";
import { MatCommonModule } from "@angular/material/core";
import { BrowserModule } from "@angular/platform-browser";
import { SmartFormService } from "./services/smartform.service";

@NgModule({
    declarations: [SmartformComponent, SmartformwidgetComponent, SmartfileuploaderComponent],
    imports: [
        BrowserModule,
        MatCommonModule,
        MatChipsModule,
        FormsModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatCheckboxModule,
        MatSelectModule,
        MatButtonModule,
        MatInputModule,
        MatIconModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatRadioModule,
    ],
    exports: [SmartformComponent, SmartformwidgetComponent, SmartfileuploaderComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    providers: [SmartFormService],
})
export class SmartformModule {}
