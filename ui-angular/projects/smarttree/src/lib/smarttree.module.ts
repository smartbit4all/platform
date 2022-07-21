import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { MatCommonModule } from "@angular/material/core";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatTreeModule } from "@angular/material/tree";
import { SmartTreeComponent } from "./smarttree.component";
import { BrowserModule } from "@angular/platform-browser";

@NgModule({
    declarations: [SmartTreeComponent],
    imports: [BrowserModule, MatCommonModule, MatButtonModule, MatIconModule, MatTreeModule],
    exports: [SmartTreeComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class SmarttreeModule {}
