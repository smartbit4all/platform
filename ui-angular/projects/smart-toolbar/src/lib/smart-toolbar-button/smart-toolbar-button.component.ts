import { Component, Input, OnInit } from '@angular/core';
import { SmartToolbarButton } from './smart-toolbar-button.model';

@Component({
  selector: 'smart-toolbar-button',
  templateUrl: './smart-toolbar-button.component.html',
  styleUrls: ['./smart-toolbar-button.component.css']
})
export class SmartToolbarButtonComponent implements OnInit {
  @Input() button!: SmartToolbarButton;

  // label!: string;
  // icon?: string;
  // btnAction!: Function;
  // btnStyle?: any;

  constructor() {
  }

  ngOnInit(): void {
    console.log(this.button);
  }

}
