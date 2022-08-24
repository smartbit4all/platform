import { Injectable } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandType, SmartToolbarButton } from './smart-toolbar.model';

@Injectable({
  providedIn: 'root'
})
export class SmartToolbarService {

  constructor(private router: Router, private route: ActivatedRoute) { }

  executeCommand(button: SmartToolbarButton) {
    if (button.btnAction.commandType === CommandType.NAVIGATION) {
      let params = button.btnAction!.objectUri ? { uri: button.btnAction.objectUri } : {};
      this.router.navigate([button.btnAction.url], { queryParams: params, relativeTo: this.route });
    }
  }
}
