import { ValidatorFn } from "@angular/forms";

/**
 * This enum containes the available types of the form widgets.
 *
 * @author Roland Fényes
 */
export enum SmartFormWidgetType {
    TEXT_FIELD,
    TEXT_FIELD_NUMBER,
    TEXT_FIELD_CHIPS,
    TEXT_BOX,
    SELECT,
    SELECT_MULTIPLE,
    CHECK_BOX,
    CHECK_BOX_TABLE,
    RADIO_BUTTON,
    DATE_PICKER,
    FILE_UPLOAD,
    ITEM,
    CONTAINER,
}

export enum SmartFormWidgetDirection {
    COL,
    ROW,
}

export enum SmartFormWidgetWidth {
    SMALL = 150,
    MEDIUM = 250,
    LARGE = 350,
    EXTRA_LARGE = 450,
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
    showLabel?: boolean;
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
    isDisabled?: boolean;
    prefix?: string;
    suffix?: string;
    validators?: ValidatorFn[];
    errorMessage?: string;
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
