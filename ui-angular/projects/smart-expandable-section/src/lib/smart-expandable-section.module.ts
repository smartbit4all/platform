import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { MatCommonModule } from "@angular/material/core";
import { BrowserModule } from "@angular/platform-browser";
import { ExpandableSectionComponent } from "./expandable-section.component";
import { MatExpansionModule } from "@angular/material/expansion";
import { ComponentFactoryServiceModule } from "@smartbit4all/component-factory-service";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";

@NgModule({
    declarations: [ExpandableSectionComponent],
    imports: [BrowserModule, MatCommonModule, MatExpansionModule, ComponentFactoryServiceModule, MatIconModule, MatButtonModule],
    exports: [ExpandableSectionComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class SmartExpandableSectionModule { }
