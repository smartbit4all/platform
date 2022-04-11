/**
 * Navigation API
 * Navigation API
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { Message } from './message';
import { NavigationTarget } from './navigationTarget';
import { ViewModelData } from './viewModelData';


export interface UIState { 
    uuid: string;
    uiToOpen?: NavigationTarget;
    messageToOpen?: Message;
    views: Array<ViewModelData>;
}
