import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { CommandType, SmartToolbarButton } from './smart-toolbar.model';

@Injectable({
  providedIn: 'root'
})
export class SmartToolbarService {

  constructor(private router: Router) { }

  executeCommand(button: SmartToolbarButton) {
    if (button.btnAction.commandType === CommandType.NAVIGATION) {
      let params = button.btnAction!.objectUri ? { queryParams: { uri: button.btnAction.objectUri } } : {};
      this.router.navigate([button.btnAction.url], params);
    }
  }
}
