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
import { ViewModelDataSimple } from './viewModelDataSimple';
import { NavigationTarget } from './navigationTarget';


/**
 * Represent a viewmodel\'s data.
 */
export interface ViewModelData { 
    uuid: string;
    path?: string;
    navigationTarget?: NavigationTarget;
    model?: object;
    children: { [key: string]: ViewModelDataSimple; };
}

