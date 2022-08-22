import {
    Component,
    Input,
    OnInit
} from "@angular/core";
import { SmartToolbar, SmartToolbarButton, ToolbarButtonStyle, ToolbarDirection } from "./smart-toolbar.model";
import { SmartToolbarService } from "./smart-toolbar.service";

@Component({
    selector: "smart-toolbar",
    templateUrl: "./smart-toolbar.component.html",
    styleUrls: ["./smart-toolbar.component.css"],
})
export class SmartToolbarComponent implements OnInit {
    @Input() toolbar!: SmartToolbar;
    toolbarDirection = ToolbarDirection;
    ToolbarButtonStyle = ToolbarButtonStyle;

    constructor(private service: SmartToolbarService) { }

    ngOnInit(): void { }

    executeCommand(button: SmartToolbarButton) {
        this.service.executeCommand(button);
    }
}
