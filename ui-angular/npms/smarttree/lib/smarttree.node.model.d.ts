export interface TreeStyle {
    arrowPosition?: TreeArrowPosition;
    levelBackgroundColor?: string[];
    color?: string;
    activeStyle?: {
        color: string;
        backgroundColor: string;
    };
}
export declare enum TreeArrowPosition {
    START = 0,
    END = 1
}
