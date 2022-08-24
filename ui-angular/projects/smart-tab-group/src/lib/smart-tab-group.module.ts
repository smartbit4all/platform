import { NgModule } from "@angular/core";
import { TabGroupComponent } from "./smart-tab-group.component";
import { MatTabsModule } from "@angular/material/tabs";
import { BrowserModule } from "@angular/platform-browser";
import { SmartNavigationModule, SmartNavigationService } from "@smartbit4all/navigation";

@NgModule({
    declarations: [TabGroupComponent],
    imports: [BrowserModule, MatTabsModule, SmartNavigationModule],
    exports: [TabGroupComponent],
    providers: [SmartNavigationService],
})
export class SmartTabGroupModule {}
