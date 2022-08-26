import {
    Component,
    Input,
    OnInit
} from "@angular/core";
import { SmartToolbar, ToolbarButtonStyle, ToolbarDirection } from "./smart-toolbar.model";

@Component({
    selector: "smart-toolbar",
    templateUrl: "./smart-toolbar.component.html",
    styleUrls: ["./smart-toolbar.component.css"],
})
export class SmartToolbarComponent implements OnInit {
    @Input() toolbar!: SmartToolbar;
    toolbarDirection = ToolbarDirection;
    ToolbarButtonStyle = ToolbarButtonStyle;

    constructor() { }

    ngOnInit(): void { }
}
