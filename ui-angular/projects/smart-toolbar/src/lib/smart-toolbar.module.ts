import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { SmartToolbarButtonComponent } from './smart-toolbar-button/smart-toolbar-button.component';
import { SmartToolbarComponent } from './smart-toolbar.component';
import { CommonModule } from '@angular/common';


@NgModule({
  declarations: [
    SmartToolbarComponent,
    SmartToolbarButtonComponent,
  ],
  imports: [
    CommonModule
  ],
  exports: [
    SmartToolbarComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class SmartToolbarModule { }
