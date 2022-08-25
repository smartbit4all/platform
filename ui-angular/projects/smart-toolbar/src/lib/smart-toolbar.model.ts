export interface SmartToolbarButton {
    label: string;
    icon?: string;
    btnAction: Command;
    style: ToolbarButtonStyle;
    color?: ToolbarButtonColor;
    disabled: boolean;
    rounded?: boolean;
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
    MAT_BUTTON = "MAT_BUTTON",
    MAT_STROKED_BUTTON = "MAT_STROKED_BUTTON",
    MAT_FLAT_BUTTON = "MAT_FLAT_BUTTON",
    MAT_ICON_BUTTON = "MAT_ICON_BUTTON",
    MAT_FAB = "MAT_FAB",
    MAT_MINI_FAB = "MAT_MINI_FAB"
}

export enum ToolbarButtonColor {
    PRIMARY = "primary",
    ACCENT = "accent",
    WARN = "warn",
}