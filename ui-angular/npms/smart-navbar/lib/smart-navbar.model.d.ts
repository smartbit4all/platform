export interface SmartUserSetting {
    label: string;
    callback: () => void;
}
export interface SmartUserSettings {
    icon?: string;
    label: string;
    settings?: SmartUserSetting[];
    callback?: () => void;
}
export interface SmartSearchRequest {
    value: string;
    quickFilter: string;
}
export interface SmartSearchBar {
    placeholder: string;
    icon?: string;
    quickFilterLabel: string;
    quickFilters: string[];
    searchCallback: (request: SmartSearchRequest) => void;
}
export interface SmartNavbar {
    icon?: string;
    iconCallback?: () => void;
    searchBar?: SmartSearchBar;
    filterButtonLabel?: string;
    filterButtonCallback?: () => void;
    userSettings?: SmartUserSettings;
}
