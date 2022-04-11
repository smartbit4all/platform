import { Observable } from "rxjs";
import { ViewModelData } from "./core/api/policy";

export interface ViewModel<T> {

    uuid?: string | undefined;
    parent?: ViewModel<any> | undefined;
    path?: string | undefined;
    model: T;

    executeCommand(command: string, path?: string, params?: Object[]): Promise<ViewModelData>;
    executeCommandWithoutModel(command: string, path?: string, params?: Object[]): Promise<ViewModelData>;

    onCloseWindow(): void;
    getDownloadData(dataIdentifier: string): Observable<Blob>;
    getChild(path: string): ViewModel<any>;
    upload(uploadCommand: string, params: Array<object>, content: Blob, commandPath?: string): Promise<ViewModelData>;
    getFileLink(vmUuid: string, dataIdentifier: string, commandPath?: string): Promise<string | undefined>;
}