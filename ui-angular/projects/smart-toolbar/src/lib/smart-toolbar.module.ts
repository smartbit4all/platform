import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { SmartToolbarComponent } from './smart-toolbar.component';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
  declarations: [
    SmartToolbarComponent
  ],
  imports: [
    CommonModule,
    BrowserModule,
    MatButtonModule,
    MatIconModule
  ],
  exports: [
    SmartToolbarComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class SmartToolbarModule { }
