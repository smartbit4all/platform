import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { MatCommonModule } from "@angular/material/core";
import { BrowserModule } from "@angular/platform-browser";
import { ExpandableSectionComponent } from "./expandable-section.component";
import { MatExpansionModule } from "@angular/material/expansion";
import { SmarttableModule } from "@smartbit4all/table";
import { SmartformModule } from "@smartbit4all/form";

@NgModule({
    declarations: [ExpandableSectionComponent],
    imports: [
        BrowserModule,
        MatCommonModule,
        MatExpansionModule,
        SmarttableModule,
        SmartformModule,
    ],
    exports: [ExpandableSectionComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class SmartExpandableSectionModule {}
