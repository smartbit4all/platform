import { SmartForm } from "@smartbit4all/form/lib/smartform.model";
import { SmartTable } from "@smartbit4all/table/lib/smarttable.model";

/**
 * This interface defines the size of a dialog.
 *
 * @author Roland Fényes
 */
export interface SmartDialogSize {
    width?: number;
    height?: number;
    fullScreen?: boolean;
}

/**
 * This enum helps to define the action of a dialog.
 *
 * @author Roland Fényes
 */
export enum SmartActionType {
    ADD,
    CREATE,
    UPDATE,
    REMOVE,
    DELETE,
    SAVE,
    OK,
}

/**
 * This interface describes the content of a dialog.
 *
 * @author Roland Fényes
 */
export interface SmartContent {
    title: string;
    description?: string;
}

/**
 * With this interface any kind of dialogs can be easily created.
 *
 * You must close the dialog manually!
 *
 * @param size Required. You can define the desired size of the dialog
 * @param actionType Required. The main action type of the dialog
 * @param content Required. Defines the title and the description of the dialog
 * @param form Not required. You can present a custom and dynamic form.
 * @param table Not required. You can present a custom table.
 * @param okCallback Not required. A custom callback function.
 * @param closeCallback Not required. A custom callback function.
 * @param actionCallback Required. A custom action callback function.
 * @param actionLabel Required. The name of the action button.
 *
 * @author Roland Fényes
 */
export interface SmartDialogData {
    size: SmartDialogSize;
    content: SmartContent;
    customComponent?: any;
    customComponentInputs?: any;
    actionType?: SmartActionType;
    form?: SmartForm;
    table?: SmartTable<any>;
    okCallback?: () => void;
    cancelCallback?: (args: any[]) => void;
    closeCallback?: (args: any[]) => void;
    actionCallback?: (args: any[]) => void;
    actionLabel?: string;
    outlets?: any;
}
