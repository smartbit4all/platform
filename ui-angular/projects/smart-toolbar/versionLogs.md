# Smart toolbar version log

## References

These packages must be updated in case of a new version:

- There are no references yet

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
