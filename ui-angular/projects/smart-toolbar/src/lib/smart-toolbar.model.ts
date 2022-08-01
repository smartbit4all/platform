export interface SmartToolbarButton {
    label: string;
    icon?: string;
    btnAction: Function;
    btnStyle?: any;
}

export interface SmartToolbar {
    direction?: ToolbarDirection;
    buttons: SmartToolbarButton[];
}

export enum ToolbarDirection {
    COL,
    ROW,
}