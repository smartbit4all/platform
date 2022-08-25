export interface ExpandableSection<T> {
    title: string;
    customComponent?: any;
    button?: ExpandableSectionButton;
    data?: T;
    inputName?: string;
}

export interface ExpandableSectionButton {
    label: string;
    icon?: string;
    onClick: (args?: any[]) => void;
}