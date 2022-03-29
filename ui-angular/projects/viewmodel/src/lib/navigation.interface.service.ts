import { Observable } from "rxjs";
import { AuthenticationService } from "./authentication.interface.service";
import { CommandData, CommandResult, Message, MessageResult, NavigationTarget, ViewModelData } from "./core/api/policy";

export enum FetchingStatus {
    STANDBY,
    FETCHING,
    LONG_FETCHING
}

export interface NavigationService {

    auth?: AuthenticationService;
    fetchingStatus: FetchingStatus;

    setAuth(auth: AuthenticationService): void;

    setTokenToService(): void;

    onNavigate(location: any[], params: any): void;

    createViewModel(navigationTarget: NavigationTarget): any;

    handleCommandResult(res: CommandResult, callback?: (view: ViewModelData | Message) => any): void

    executeCommand(uuid: string, commandData: CommandData, callback?: (view: ViewModelData | Message) => any): void;

    getModel(uuid: string): Observable<ViewModelData>;

    message(uuid: string, messageResult: MessageResult, callback?: (view: ViewModelData | Message) => any): void;

    handleErrors(error: any): void;

    setFetching(): void;

    closeFetching(): void
}