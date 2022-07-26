import { Component, ComponentFactoryResolver, Input, OnInit, ViewChild, ViewContainerRef } from '@angular/core';
import { SmartToolbarButtonComponent } from './smart-toolbar-button/smart-toolbar-button.component';
import { SmartToolbarButton } from './smart-toolbar-button/smart-toolbar-button.model';

@Component({
  selector: 'smart-toolbar',
  templateUrl: './smart-toolbar.component.html',
  styleUrls: ['./smart-toolbar.component.css']
})
export class SmartToolbarComponent implements OnInit {
  @Input() buttons!: SmartToolbarButton[];
  @Input() direction?: ToolbarDirection = ToolbarDirection.ROW;
  toolbarDirection = ToolbarDirection;

  @ViewChild('renderToolbar', { read: ViewContainerRef })
  vcRef?: ViewContainerRef;

  constructor(private resolver: ComponentFactoryResolver) { }

  ngOnInit(): void {
  }

  ngAfterViewInit() {
    for (let btn of this.buttons) {
      const factory = this.resolver.resolveComponentFactory(SmartToolbarButtonComponent);
      const ref = this.vcRef!.createComponent(factory);
      console.log(btn);
      ref.instance.button = btn;
      ref.changeDetectorRef.detectChanges();
    }
  }

}

export enum ToolbarDirection {
  COL, ROW
}
