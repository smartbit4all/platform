import * as i0 from '@angular/core';
import { Injectable, Component, Input, NgModule, CUSTOM_ELEMENTS_SCHEMA, InjectionToken, Optional, Inject, SkipSelf } from '@angular/core';
import { NestedTreeControl } from '@angular/cdk/tree';
import * as i2 from '@angular/material/tree';
import { MatTreeNestedDataSource, MatTreeModule } from '@angular/material/tree';
import * as i1 from '@angular/router';
import * as i3 from '@angular/material/icon';
import { MatIconModule } from '@angular/material/icon';
import * as i4 from '@angular/material/button';
import { MatButtonModule } from '@angular/material/button';
import * as i5 from '@angular/common';
import { MatCommonModule } from '@angular/material/core';
import { BrowserModule } from '@angular/platform-browser';
import * as i1$1 from '@angular/common/http';
import { HttpHeaders, HttpContext } from '@angular/common/http';

class SmarttreeService {
    constructor() { }
}
SmarttreeService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttreeService, deps: [], target: i0.ɵɵFactoryTarget.Injectable });
SmarttreeService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttreeService, providedIn: 'root' });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttreeService, decorators: [{
            type: Injectable,
            args: [{
                    providedIn: 'root'
                }]
        }], ctorParameters: function () { return []; } });

class SmartTreeComponent {
    constructor(router) {
        this.router = router;
        this.treeControl = new NestedTreeControl((node) => node.childrenNodes);
        this.dataSource = new MatTreeNestedDataSource();
        this.hasChild = (_, node) => node.hasChildren;
    }
    ngOnInit() {
        this.dataSource.data = this.treeData.rootNodes;
    }
    onNodeClick(node) {
        if (this.tempActiveNode === node) {
            this.tempActiveNode.selected = false;
            this.tempActiveNode = undefined;
            node.selected = false;
            return;
        }
        if (this.tempActiveNode)
            this.tempActiveNode.selected = false;
        node.selected = true;
        this.tempActiveNode = node;
        let navigationUrlByNodeType = this.treeData.navigationUrlsByNodeType.find((nav) => {
            return nav.nodeType === node.nodeType;
        });
        if (navigationUrlByNodeType) {
            this.router.navigate(navigationUrlByNodeType.navigationUrl, {
                queryParams: { uri: node.objectUri },
            });
        }
    }
    getNodeStyle(node) {
        var _a, _b;
        if (this.treeStyle) {
            var style = node.selected
                ? {
                    background: (_a = this.treeStyle.activeStyle) === null || _a === void 0 ? void 0 : _a.backgroundColor,
                    color: (_b = this.treeStyle.activeStyle) === null || _b === void 0 ? void 0 : _b.color,
                    "padding-left": 15 * node.level + "px",
                }
                : {
                    background: this.treeStyle.levelBackgroundColor[node.level - 1],
                    color: this.treeStyle.color,
                    "padding-left": 15 * node.level + "px",
                };
            return style;
        }
        return {};
    }
}
SmartTreeComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartTreeComponent, deps: [{ token: i1.Router }], target: i0.ɵɵFactoryTarget.Component });
SmartTreeComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartTreeComponent, selector: "smart-tree", inputs: { treeData: "treeData", treeStyle: "treeStyle" }, ngImport: i0, template: "<mat-tree [dataSource]=\"dataSource\" [treeControl]=\"treeControl\" class=\"sm-tree\">\n\t<mat-nested-tree-node *matTreeNodeDef=\"let node; when: hasChild\">\n\t\t<div\n\t\t\tclass=\"mat-tree-node sm-tree-node\"\n\t\t\t(click)=\"onNodeClick(node)\"\n\t\t\t[ngStyle]=\"getNodeStyle(node)\"\n\t\t>\n\t\t\t<mat-icon class=\"mat-icon-rtl-mirror\">\n\t\t\t\t<div *ngIf=\"hasChild(node.level, node)\">\n\t\t\t\t\t{{ treeControl.isExpanded(node) ? 'expand_more' : 'chevron_right' }}\n\t\t\t\t</div>\n\t\t\t</mat-icon>\n\t\t\t<button mat-icon-button matTreeNodeToggle [attr.aria-label]=\"'Toggle ' + node.name\">\n\t\t\t\t<mat-icon>\n\t\t\t\t\t{{ node.icon }}\n\t\t\t\t</mat-icon>\n\t\t\t</button>\n\t\t\t<div class=\"sm-tree-node-name\">\n\t\t\t\t{{ node.caption }}\n\t\t\t\t<p class=\"sm-tree-node-id\">Azonos\u00EDt\u00F3:{{ node.shortDescription }}</p>\n\t\t\t</div>\n\t\t</div>\n\t\t<div [class.sm-tree-invisible]=\"!treeControl.isExpanded(node)\" role=\"group\">\n\t\t\t<ng-container matTreeNodeOutlet></ng-container>\n\t\t</div>\n\t</mat-nested-tree-node>\n</mat-tree>\n", styles: [".sm-tree-invisible{display:none}.sm-tree ul,.sm-tree li{margin-top:0;margin-bottom:0;list-style-type:none}.sm-tree div[role=group]>.mat-tree-node{padding-left:40px}.sm-tee-node{padding-left:40px}.sm-tree-node-name{padding-left:15px;padding-top:15px;display:flex;flex-direction:column}.sm-tee-node-id{font-weight:lighter}.mat-tree-node:hover{cursor:pointer}::ng-deep .mat-icon-rtl-mirror{display:flex;flex-direction:row}\n"], components: [{ type: i2.MatTree, selector: "mat-tree", exportAs: ["matTree"] }, { type: i3.MatIcon, selector: "mat-icon", inputs: ["color", "inline", "svgIcon", "fontSet", "fontIcon"], exportAs: ["matIcon"] }, { type: i4.MatButton, selector: "button[mat-button], button[mat-raised-button], button[mat-icon-button],             button[mat-fab], button[mat-mini-fab], button[mat-stroked-button],             button[mat-flat-button]", inputs: ["disabled", "disableRipple", "color"], exportAs: ["matButton"] }], directives: [{ type: i2.MatTreeNodeDef, selector: "[matTreeNodeDef]", inputs: ["matTreeNodeDefWhen", "matTreeNode"] }, { type: i2.MatNestedTreeNode, selector: "mat-nested-tree-node", inputs: ["role", "disabled", "tabIndex", "matNestedTreeNode"], exportAs: ["matNestedTreeNode"] }, { type: i5.NgStyle, selector: "[ngStyle]", inputs: ["ngStyle"] }, { type: i5.NgIf, selector: "[ngIf]", inputs: ["ngIf", "ngIfThen", "ngIfElse"] }, { type: i2.MatTreeNodeToggle, selector: "[matTreeNodeToggle]", inputs: ["matTreeNodeToggleRecursive"] }, { type: i2.MatTreeNodeOutlet, selector: "[matTreeNodeOutlet]" }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartTreeComponent, decorators: [{
            type: Component,
            args: [{ selector: "smart-tree", template: "<mat-tree [dataSource]=\"dataSource\" [treeControl]=\"treeControl\" class=\"sm-tree\">\n\t<mat-nested-tree-node *matTreeNodeDef=\"let node; when: hasChild\">\n\t\t<div\n\t\t\tclass=\"mat-tree-node sm-tree-node\"\n\t\t\t(click)=\"onNodeClick(node)\"\n\t\t\t[ngStyle]=\"getNodeStyle(node)\"\n\t\t>\n\t\t\t<mat-icon class=\"mat-icon-rtl-mirror\">\n\t\t\t\t<div *ngIf=\"hasChild(node.level, node)\">\n\t\t\t\t\t{{ treeControl.isExpanded(node) ? 'expand_more' : 'chevron_right' }}\n\t\t\t\t</div>\n\t\t\t</mat-icon>\n\t\t\t<button mat-icon-button matTreeNodeToggle [attr.aria-label]=\"'Toggle ' + node.name\">\n\t\t\t\t<mat-icon>\n\t\t\t\t\t{{ node.icon }}\n\t\t\t\t</mat-icon>\n\t\t\t</button>\n\t\t\t<div class=\"sm-tree-node-name\">\n\t\t\t\t{{ node.caption }}\n\t\t\t\t<p class=\"sm-tree-node-id\">Azonos\u00EDt\u00F3:{{ node.shortDescription }}</p>\n\t\t\t</div>\n\t\t</div>\n\t\t<div [class.sm-tree-invisible]=\"!treeControl.isExpanded(node)\" role=\"group\">\n\t\t\t<ng-container matTreeNodeOutlet></ng-container>\n\t\t</div>\n\t</mat-nested-tree-node>\n</mat-tree>\n", styles: [".sm-tree-invisible{display:none}.sm-tree ul,.sm-tree li{margin-top:0;margin-bottom:0;list-style-type:none}.sm-tree div[role=group]>.mat-tree-node{padding-left:40px}.sm-tee-node{padding-left:40px}.sm-tree-node-name{padding-left:15px;padding-top:15px;display:flex;flex-direction:column}.sm-tee-node-id{font-weight:lighter}.mat-tree-node:hover{cursor:pointer}::ng-deep .mat-icon-rtl-mirror{display:flex;flex-direction:row}\n"] }]
        }], ctorParameters: function () { return [{ type: i1.Router }]; }, propDecorators: { treeData: [{
                type: Input
            }], treeStyle: [{
                type: Input
            }] } });

class SmarttreeModule {
}
SmarttreeModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttreeModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmarttreeModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttreeModule, declarations: [SmartTreeComponent], imports: [BrowserModule, MatCommonModule, MatButtonModule, MatIconModule, MatTreeModule], exports: [SmartTreeComponent] });
SmarttreeModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttreeModule, imports: [[BrowserModule, MatCommonModule, MatButtonModule, MatIconModule, MatTreeModule]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmarttreeModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [SmartTreeComponent],
                    imports: [BrowserModule, MatCommonModule, MatButtonModule, MatIconModule, MatTreeModule],
                    exports: [SmartTreeComponent],
                    schemas: [CUSTOM_ELEMENTS_SCHEMA],
                }]
        }] });

/**
 * Custom HttpParameterCodec
 * Workaround for https://github.com/angular/angular/issues/18261
 */
class CustomHttpParameterCodec {
    encodeKey(k) {
        return encodeURIComponent(k);
    }
    encodeValue(v) {
        return encodeURIComponent(v);
    }
    decodeKey(k) {
        return decodeURIComponent(k);
    }
    decodeValue(v) {
        return decodeURIComponent(v);
    }
}

const BASE_PATH = new InjectionToken('basePath');
const COLLECTION_FORMATS = {
    'csv': ',',
    'tsv': '   ',
    'ssv': ' ',
    'pipes': '|'
};

class Configuration {
    constructor(configurationParameters = {}) {
        this.apiKeys = configurationParameters.apiKeys;
        this.username = configurationParameters.username;
        this.password = configurationParameters.password;
        this.accessToken = configurationParameters.accessToken;
        this.basePath = configurationParameters.basePath;
        this.withCredentials = configurationParameters.withCredentials;
        this.encoder = configurationParameters.encoder;
        if (configurationParameters.credentials) {
            this.credentials = configurationParameters.credentials;
        }
        else {
            this.credentials = {};
        }
    }
    /**
     * Select the correct content-type to use for a request.
     * Uses {@link Configuration#isJsonMime} to determine the correct content-type.
     * If no content type is found return the first found type if the contentTypes is not empty
     * @param contentTypes - the array of content types that are available for selection
     * @returns the selected content-type or <code>undefined</code> if no selection could be made.
     */
    selectHeaderContentType(contentTypes) {
        if (contentTypes.length === 0) {
            return undefined;
        }
        const type = contentTypes.find((x) => this.isJsonMime(x));
        if (type === undefined) {
            return contentTypes[0];
        }
        return type;
    }
    /**
     * Select the correct accept content-type to use for a request.
     * Uses {@link Configuration#isJsonMime} to determine the correct accept content-type.
     * If no content type is found return the first found type if the contentTypes is not empty
     * @param accepts - the array of content types that are available for selection.
     * @returns the selected content-type or <code>undefined</code> if no selection could be made.
     */
    selectHeaderAccept(accepts) {
        if (accepts.length === 0) {
            return undefined;
        }
        const type = accepts.find((x) => this.isJsonMime(x));
        if (type === undefined) {
            return accepts[0];
        }
        return type;
    }
    /**
     * Check if the given MIME is a JSON MIME.
     * JSON MIME examples:
     *   application/json
     *   application/json; charset=UTF8
     *   APPLICATION/JSON
     *   application/vnd.company+json
     * @param mime - MIME (Multipurpose Internet Mail Extensions)
     * @return True if the given MIME is JSON, false otherwise.
     */
    isJsonMime(mime) {
        const jsonMime = new RegExp('^(application\/json|[^;/ \t]+\/[^;/ \t]+[+]json)[ \t]*(;.*)?$', 'i');
        return mime !== null && (jsonMime.test(mime) || mime.toLowerCase() === 'application/json-patch+json');
    }
    lookupCredential(key) {
        const value = this.credentials[key];
        return typeof value === 'function'
            ? value()
            : value;
    }
}

/**
 * Tree domain objects
 * Tree domain objects
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
class DefaultService {
    constructor(httpClient, basePath, configuration) {
        this.httpClient = httpClient;
        this.basePath = 'http://localhost';
        this.defaultHeaders = new HttpHeaders();
        this.configuration = new Configuration();
        if (configuration) {
            this.configuration = configuration;
        }
        if (typeof this.configuration.basePath !== 'string') {
            if (typeof basePath !== 'string') {
                basePath = this.basePath;
            }
            this.configuration.basePath = basePath;
        }
        this.encoder = this.configuration.encoder || new CustomHttpParameterCodec();
    }
    addToHttpParams(httpParams, value, key) {
        if (typeof value === "object" && value instanceof Date === false) {
            httpParams = this.addToHttpParamsRecursive(httpParams, value);
        }
        else {
            httpParams = this.addToHttpParamsRecursive(httpParams, value, key);
        }
        return httpParams;
    }
    addToHttpParamsRecursive(httpParams, value, key) {
        if (value == null) {
            return httpParams;
        }
        if (typeof value === "object") {
            if (Array.isArray(value)) {
                value.forEach(elem => httpParams = this.addToHttpParamsRecursive(httpParams, elem, key));
            }
            else if (value instanceof Date) {
                if (key != null) {
                    httpParams = httpParams.append(key, value.toISOString().substr(0, 10));
                }
                else {
                    throw Error("key may not be null if value is Date");
                }
            }
            else {
                Object.keys(value).forEach(k => httpParams = this.addToHttpParamsRecursive(httpParams, value[k], key != null ? `${key}.${k}` : k));
            }
        }
        else if (key != null) {
            httpParams = httpParams.append(key, value);
        }
        else {
            throw Error("key may not be null if value is not object or array");
        }
        return httpParams;
    }
    nopePost(observe = 'body', reportProgress = false, options) {
        let localVarHeaders = this.defaultHeaders;
        let localVarHttpHeaderAcceptSelected = options && options.httpHeaderAccept;
        if (localVarHttpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts = [];
            localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (localVarHttpHeaderAcceptSelected !== undefined) {
            localVarHeaders = localVarHeaders.set('Accept', localVarHttpHeaderAcceptSelected);
        }
        let localVarHttpContext = options && options.context;
        if (localVarHttpContext === undefined) {
            localVarHttpContext = new HttpContext();
        }
        let responseType_ = 'json';
        if (localVarHttpHeaderAcceptSelected) {
            if (localVarHttpHeaderAcceptSelected.startsWith('text')) {
                responseType_ = 'text';
            }
            else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
                responseType_ = 'json';
            }
            else {
                responseType_ = 'blob';
            }
        }
        return this.httpClient.post(`${this.configuration.basePath}/nope`, null, {
            context: localVarHttpContext,
            responseType: responseType_,
            withCredentials: this.configuration.withCredentials,
            headers: localVarHeaders,
            observe: observe,
            reportProgress: reportProgress
        });
    }
}
DefaultService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: DefaultService, deps: [{ token: i1$1.HttpClient }, { token: BASE_PATH, optional: true }, { token: Configuration, optional: true }], target: i0.ɵɵFactoryTarget.Injectable });
DefaultService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: DefaultService, providedIn: 'root' });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: DefaultService, decorators: [{
            type: Injectable,
            args: [{
                    providedIn: 'root'
                }]
        }], ctorParameters: function () {
        return [{ type: i1$1.HttpClient }, { type: undefined, decorators: [{
                        type: Optional
                    }, {
                        type: Inject,
                        args: [BASE_PATH]
                    }] }, { type: Configuration, decorators: [{
                        type: Optional
                    }] }];
    } });

const APIS = [DefaultService];

/**
 * Tree domain objects
 * Tree domain objects
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
const TreeNodeKind = {
    Entry: 'entry',
    Association: 'association',
    Reference: 'reference'
};

class ApiModule {
    constructor(parentModule, http) {
        if (parentModule) {
            throw new Error('ApiModule is already loaded. Import in your base AppModule only.');
        }
        if (!http) {
            throw new Error('You need to import the HttpClientModule in your AppModule! \n' +
                'See also https://github.com/angular/angular/issues/20575');
        }
    }
    static forRoot(configurationFactory) {
        return {
            ngModule: ApiModule,
            providers: [{ provide: Configuration, useFactory: configurationFactory }]
        };
    }
}
ApiModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: ApiModule, deps: [{ token: ApiModule, optional: true, skipSelf: true }, { token: i1$1.HttpClient, optional: true }], target: i0.ɵɵFactoryTarget.NgModule });
ApiModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: ApiModule });
ApiModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: ApiModule, providers: [], imports: [[]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: ApiModule, decorators: [{
            type: NgModule,
            args: [{
                    imports: [],
                    declarations: [],
                    exports: [],
                    providers: []
                }]
        }], ctorParameters: function () {
        return [{ type: ApiModule, decorators: [{
                        type: Optional
                    }, {
                        type: SkipSelf
                    }] }, { type: i1$1.HttpClient, decorators: [{
                        type: Optional
                    }] }];
    } });

/*
 * Public API Surface of smarttree
 */

/**
 * Generated bundle index. Do not edit.
 */

export { APIS, ApiModule, BASE_PATH, COLLECTION_FORMATS, Configuration, DefaultService, SmartTreeComponent, SmarttreeModule, SmarttreeService, TreeNodeKind };
//# sourceMappingURL=smartbit4all-tree.mjs.map
