import { ThemePalette } from "@angular/material/core";
import { MatBadgePosition } from "@angular/material/badge";
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
export interface SmartNotification {
    icon: string;
    iconColor?: string;
    notificationCallBack?: () => void;
    notificationCount?: number;
    counterColor?: ThemePalette;
    position?: MatBadgePosition;
}
export interface SmartNavbar {
    icon?: string;
    iconCallback?: () => void;
    searchBar?: SmartSearchBar;
    filterButtonLabel?: string;
    filterButtonCallback?: () => void;
    userSettings?: SmartUserSettings;
    notification?: SmartNotification;
}
