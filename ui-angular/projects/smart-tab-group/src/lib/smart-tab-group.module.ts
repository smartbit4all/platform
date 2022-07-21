import { NgModule } from "@angular/core";
import { TabGroupComponent } from "./smart-tab-group.component";
import { MatTabsModule } from "@angular/material/tabs";
import { BrowserModule } from "@angular/platform-browser";

@NgModule({
    declarations: [TabGroupComponent],
    imports: [BrowserModule, MatTabsModule],
    exports: [TabGroupComponent],
})
export class SmartTabGroupModule {}
