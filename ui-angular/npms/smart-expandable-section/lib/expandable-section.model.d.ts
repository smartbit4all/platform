export interface ExpandableSection<T> {
    title: string;
    customComponent?: any;
    data?: T;
    inputName?: string;
}
