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

export enum ToolbarDirection {
    COL,
    ROW,
}

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