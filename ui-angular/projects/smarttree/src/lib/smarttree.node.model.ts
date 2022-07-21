export interface TreeStyle {
    arrowPosition?: TreeArrowPosition;
    levelBackgroundColor?: string[];
    color?: string;
    activeStyle?: { color: string, backgroundColor: string };
}

export enum TreeArrowPosition {
    START, END
}