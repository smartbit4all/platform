import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { SmartToolbarComponent } from './smart-toolbar.component';
import { CommonModule } from '@angular/common';


@NgModule({
  declarations: [
    SmartToolbarComponent
  ],
  imports: [
    CommonModule,
  ],
  exports: [
    SmartToolbarComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class SmartToolbarModule { }
