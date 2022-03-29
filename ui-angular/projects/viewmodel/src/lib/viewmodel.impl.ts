import { Observable } from "rxjs";
import { ViewModelService, ViewModelDataSimple, ViewModelData, Message, CommandData, CommandResult, BinaryContent } from "./core/api/policy";
import { NavigationService } from "./navigation.interface.service";
import { ViewModel } from "./viewmodel.interface";

export class ViewModelImpl<T extends object> implements ViewModel<T> {

    uuid?: string;
    parent?: ViewModel<any>;
    children?: Map<string, ViewModelImpl<any>>;
    path?: string;
    model!: T;

    constructor(
        protected service: ViewModelService,
        protected navigationService: NavigationService,
        uuid: string
    ) {
        this.uuid = uuid;
        this.children = new Map();
    }

    async init(
        parent?: ViewModelImpl<any>,
        viewModelDataSimple?: ViewModelDataSimple,
        model?: any
    ): Promise<void> {
        if (parent && viewModelDataSimple && model) {
            this.parent = parent;
            this.path = viewModelDataSimple.path;
            this.model = model;
            const children = new Map(Object.entries(viewModelDataSimple.children));
            this.initChildren(children);
        } else {
            await this.getModel().then(model => {
                this.model = model.model as T;
                const children = new Map(Object.entries(model.children));
                this.initChildren(children);
            });
        }
        return;
    }

    initChildren(children: Map<string, ViewModelDataSimple>) {
        const properties = new Map(Object.entries(this.model));
        children.forEach((vm, path) => {
            const childModel = properties.get(path);
            const viewModelImpl = new ViewModelImpl(this.service, this.navigationService, vm.uuid);
            viewModelImpl.init(this, vm, childModel).then(() => {
                this.children!.set(path, viewModelImpl);
            });
        });
    }

    async getModel(): Promise<ViewModelData> {
        let viewModelData: ViewModelData | undefined;
        try {
            viewModelData = await this.service.getModel(this.uuid!).toPromise();
        } catch (error) {
            this.navigationService.handleErrors(error);
        }
        return viewModelData!;
    }
    getChild(path: string): ViewModel<any> {
        return this.children?.get(path)?.model;
    }

    setModel(model: T) {
        if (!model) {
            return;
        }
        this.model = model;
        const properties = new Map(Object.entries(this.model));
        this.children!.forEach((value, key) => {
            value.setModel(properties.get(key));
        });
    }

    handleCallback(viewModelData: ViewModelData | Message) {
        if ((viewModelData as ViewModelData).model) {
            this.setModel((viewModelData as ViewModelData).model as T);
        }
    }

    private async doExecuteCommand(commandData: CommandData): Promise<ViewModelData> {
        this.navigationService.setFetching();
        let commandResult: CommandResult | undefined;
        try {
            commandResult = await this.service.executeCommand(this.uuid!, commandData).toPromise();
        } catch (error) {
            this.navigationService.handleErrors(error);
        }

        this.navigationService.closeFetching();

        if (commandResult) {
            if (commandResult.view) {
                this.setModel(commandResult.view.model as T);
            }
            this.navigationService.handleCommandResult(commandResult, this.handleCallback.bind(this));
        }

        return commandResult?.view as ViewModelData;
    }

    getCommandPath(commandPath?: string): string {
        if (!commandPath) {
            return commandPath = this.path!;
        }
        return commandPath = this.path! + '/' + commandPath;
    }

    async executeCommand(command: string, commandPath?: string, params?: Object[]): Promise<ViewModelData> {

        if (this.parent) {
            commandPath = this.getCommandPath(commandPath);
            return this.parent.executeCommand(command, commandPath, params);
        }

        const commandData: CommandData = {
            model: this.model,
            commandPath: commandPath,
            commandCode: command,
            params: params
        }
        return await this.doExecuteCommand(commandData);
    }
    async executeCommandWithoutModel(command: string, commandPath?: string, params?: Object[]): Promise<ViewModelData> {

        if (this.parent) {
            commandPath = this.getCommandPath(commandPath);
            return this.parent.executeCommandWithoutModel(command, commandPath, params);
        }

        const commandData: CommandData = {
            model: undefined,
            commandPath: commandPath,
            commandCode: command,
            params: params
        }
        return await this.doExecuteCommand(commandData);
    }
    onCloseWindow(): void {
        this.service.close(this.uuid!);
    }
    getDownloadData(dataIdentifier: string): Observable<Blob> {
        return this.service.download(this.uuid!, dataIdentifier);
    }

    async upload(uploadCommand: string, params: Array<object>, content: Blob): Promise<ViewModelData> {

        const folderVm = this.children?.get("folder");

        const path = folderVm?.getCommandPath();

        const commandData: CommandData = {
            model: undefined,
            commandPath: path,
            commandCode: uploadCommand,
            params: params
        }

        let res;
        try {
            res = await this.service.upload(this.uuid, commandData, content).toPromise();
        } catch (error) {
            this.navigationService.handleErrors(error);
        }

        return {
            uuid: res?.view?.uuid!,
            children: res?.view?.children!,
            model: (res?.view?.model)!
        };
    }

    private async downloadFile(vmUuid: string, dataIdentifier: string): Promise<Blob | undefined> {
        return await this.service.download(vmUuid, dataIdentifier).toPromise();
    }

    async getFileLink(vmUuid: string, dataIdentifier: string): Promise<string | undefined> {
        var link;
        await this.downloadFile(vmUuid, dataIdentifier).then(res => {
            if (!res) {
                return;
            }
            link = window.URL.createObjectURL(res!);
        },
            error => this.navigationService.handleErrors(error)
        );
        return link;
    }

    download(dataIdentifier: string, binaryContent: BinaryContent) {

        let vmUuid = this.children?.get("folder")?.uuid!;

        this.getFileLink(vmUuid, dataIdentifier).then(res => {
            if (!res) {
                return;
            }
            var link = document.createElement('a');
            link.href = res!;
            link.download = binaryContent.fileName;
            link.click();
            setTimeout(() => {
                // For Firefox it is necessary to delay revoking the ObjectURL
                window.URL.revokeObjectURL(res);
            });
        });
    }

}