/**
 * This enum containes the available types of the form widgets.
 *
 * @author Roland Fényes
 */
export declare enum SmartFormWidgetType {
    TEXT_FIELD = 0,
    TEXT_FIELD_CHIPS = 1,
    TEXT_BOX = 2,
    SELECT = 3,
    SELECT_MULTIPLE = 4,
    CHECK_BOX = 5,
    CHECK_BOX_TABLE = 6,
    RADIO_BUTTON = 7,
    DATE_PICKER = 8,
    FILE_UPLOAD = 9,
    ITEM = 10
}
export declare enum SmartFormWidgetDirection {
    COL = 0,
    ROW = 1
}
export declare enum SmartFormWidgetWidth {
    SMALL = 150,
    MEDIUM = 250,
    LARGE = 350,
    EXTRA_LARGE = 450
}
/**
 * This interface describes a widget in a form.
 *
 * @param key Must be unique
 * @param label The label of the widget
 * @param value Default value
 * @param type The type of the form widget
 * @param callback Action callback
 * @param valueList Use this if you want to define selectable values
 * @param isRequired
 * @param icon Please use Angular Material icons
 *
 * @author Roland Fényes
 */
export interface SmartFormWidget<T> {
    key: string;
    label: string;
    value: T;
    type: SmartFormWidgetType;
    callback?: (args: any[]) => any;
    placeholder?: string;
    minValues?: number;
    maxValues?: number;
    direction?: SmartFormWidgetDirection;
    valueList?: SmartFormWidget<T>[];
    isRequired?: boolean;
    icon?: string;
    minWidth?: SmartFormWidgetWidth;
}
/**
 * Dynamic forms can be easily defined with this interface.
 *
 * @param name The name of the form
 * @param widgets The widgets presented in the form
 *
 * @author Roland Fényes
 */
export interface SmartForm {
    name: string;
    direction: SmartFormWidgetDirection;
    widgets: SmartFormWidget<any>[];
}
