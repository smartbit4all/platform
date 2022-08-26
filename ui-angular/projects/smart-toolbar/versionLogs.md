# Smart Toolbar

[_@smartbit4all readme_](../../README.md)

## References

These packages must be updated in case of a new version:

-   [_@smartbit4all/table_](../smarttable/versionLogs.md)

---

## How to use

### Installation

Go to your project, open the terminal and use the following command:

    npm i ../../platform/ui-angular/npms/smart-toolbar/smartbit4all-smart-toolbar-0.0.3.tgz

Then import it in the AppModule:

`app.module.ts:`

    import { SmartToolbarModule } from '@smartbit4all/smart-toolbar';
    ...
    @NgModule({
        declarations: [...]
        imports: [
            ...
            SmartToolbarModule,
        ],
        ...
    })

### Usage

`example.component.html:`

    <smart-toolbar [toolbar]="toolbar"></smart-toolbar>

`example.component.ts:`

    export class ExampleComponent {
        toolbar!: SmartToolbar;

        constructor() {
            this.toolbar = {
                direction: ToolbarDirection.ROW,
                buttons: [
                    {
                        label: 'Button 1',
                        icon: 'add',
                        btnAction: this.buttonOneClicked.bind(this),
                        btnStyle: 'tree-toolbar-btn'
                    },
                    ...
                ]
            };
        }
    }

---

## Version logs

## @smartbit4all/smart-toolbar v0.1.4

**Type: Feature**

In this version the operation of the toolbar has been changed.

Mayor changes:

1.  Command inteface modification:

            export interface CommandParameter {
                objectUri?: string;
                url: string;
            }

            export interface Command {
                parameter?: CommandParameter;
                execute: (args?: any[]) => void;
            }

    From this version executable calls have to be placed in the execute property. Paramteres for api calls is placed inside the CommandParamter.

2.  SmartToolbarService has been removed, because it lost its functionality.

## @smartbit4all/smart-toolbar v0.1.3

**Type: Feature**

This version contain one styling update: Material buttons border setting.

Rounded property set the Material buttons border-radious to 25px.

        export interface SmartToolbarButton {
            label: string;
            icon?: string;
            btnAction: Command;
            style: ToolbarButtonStyle;
            color?: ToolbarButtonColor;
            disabled: boolean;
            rounded?: boolean;
        }

## @smartbit4all/smart-toolbar v0.1.1

**Type: Feature**

Add new feature:

1.  Material Color Theme customizable in SmartToolbarButton color property:
    primary, warn accent

        export enum ToolbarButtonColor {
            PRIMARY = "primary",
            ACCENT = "accent",
            WARN = "warn",
        }

2.  Material Button variants customizable in style property:

        export enum ToolbarButtonStyle {
            MAT_RAISED_BUTTON = "MAT_RAISED_BUTTON",
            MAT_BUTTON = "MAT_BUTTON",
            MAT_STROKED_BUTTON = "MAT_STROKED_BUTTON",
            MAT_FLAT_BUTTON = "MAT_FLAT_BUTTON",
            MAT_ICON_BUTTON = "MAT_ICON_BUTTON",
            MAT_FAB = "MAT_FAB",
            MAT_MINI_FAB = "MAT_MINI_FAB"
        }

3.  Add disabled property to SmartToolbarButton.

New SmartToolbarButton interface:

    export interface SmartToolbarButton {
        label: string;
        icon?: string;
        btnAction: Command;
        style: ToolbarButtonStyle;
        color: ToolbarButtonColor;
        disabled: boolean;
    }

## @smartbit4all/smart-toolbar v0.1.0

**Type: Feature**

Now SmartToolbar can handle navigtion and has default Material UI styling.

Modifications:

1.  Add Command interface for the button click action.

        export interface Command {
            objectUri?: string;
            url: string;
            commandType: CommandType
        }

        export enum CommandType {
            NAVIGATION = "NAVIGATION"
        }

        export enum ToolbarButtonStyle {
            MAT_RAISED_BUTTON = "MAT_RAISED_BUTTON",
            MAT_BUTTON = "MAT_BUTTON"
        }

2.  Change SmartToolbarButton btnAction property type from Function to Command
3.  Implement command execution in ToolbarService. Available commmand: Navigation

        executeCommand(button: SmartToolbarButton) {
            if (button.btnAction.commandType === CommandType.NAVIGATION) {
            let params = button.btnAction!.objectUri ? { queryParams: { uri: button.btnAction.objectUri } } : {};
            this.router.navigate([button.btnAction.url], params);
            }
        }

4.  Remove button CSS classes from smart-toolbar.component.css file, add ToolbarButtonStyle

Usage example:

            {
                direction: ToolbarDirection.ROW,
                buttons: [
                    {
                        label: 'Új dokumentum adatlap',
                        icon: 'add',
                        btnAction: {
                            url: 'create',
                            objectUri: 'asdf-232-sdfs34',
                            commandType: CommandType.NAVIGATION
                        },
                        btnStyle: ToolbarButtonStyle.MAT_RAISED_BUTTON
                    },
                    {
                        label: 'Importálás',
                        icon: 'import_export',
                        btnAction: {
                            url: 'fileUpload',
                            commandType: CommandType.NAVIGATION
                        },
                        btnStyle: ToolbarButtonStyle.MAT_RAISED_BUTTON
                    }
                ]
            }

## @smartbit4all/smart-toolbar v0.0.3

**Type: Feature**

The basic smart toolbar with its features.
