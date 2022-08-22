export interface SmartToolbarButton {
    label: string;
    icon?: string;
    btnAction: Command;
    style: ToolbarButtonStyle;
    color?: ToolbarButtonColor;
    disabled: boolean;
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
    MAT_BUTTON = "MAT_BUTTON",
    MAT_STROKED_BUTTON = "MAT_STROKED_BUTTON",
    MAT_FLAT_BUTTON = "MAT_FLAT_BUTTON",
    MAT_ICON_BUTTON = "MAT_ICON_BUTTON",
    MAT_FAB = "MAT_FAB",
    MAT_MINI_FAB = "MAT_MINI_FAB"
}
export declare enum ToolbarButtonColor {
    PRIMARY = "primary",
    ACCENT = "accent",
    WARN = "warn"
}
