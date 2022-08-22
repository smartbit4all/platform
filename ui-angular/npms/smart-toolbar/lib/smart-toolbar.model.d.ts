export interface SmartToolbarButton {
    label: string;
    icon?: string;
    btnAction: Command;
    btnStyle?: ToolbarButtonStyle;
}
export interface SmartToolbar {
    direction?: ToolbarDirection;
    buttons: SmartToolbarButton[];
}
export declare enum ToolbarDirection {
    COL = 0,
    ROW = 1
}
export interface Command {
    objectUri?: string;
    url: string;
    commandType: CommandType;
}
export declare enum CommandType {
    NAVIGATION = "NAVIGATION"
}
export declare enum ToolbarButtonStyle {
    MAT_RAISED_BUTTON = "MAT_RAISED_BUTTON",
    MAT_BUTTON = "MAT_BUTTON"
}
