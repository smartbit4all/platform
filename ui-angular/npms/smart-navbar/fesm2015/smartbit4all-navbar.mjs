import * as i0 from '@angular/core';
import { Injectable, Component, ViewEncapsulation, Input, NgModule } from '@angular/core';
import * as i1 from '@angular/material/toolbar';
import { MatToolbarModule } from '@angular/material/toolbar';
import * as i2 from '@angular/material/button';
import { MatButtonModule } from '@angular/material/button';
import * as i3 from '@angular/material/icon';
import { MatIconModule } from '@angular/material/icon';
import * as i4 from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';
import { MatInputModule } from '@angular/material/input';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';

class SmartNavbarService {
    constructor() { }
}
SmartNavbarService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartNavbarService, deps: [], target: i0.ɵɵFactoryTarget.Injectable });
SmartNavbarService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartNavbarService, providedIn: 'root' });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartNavbarService, decorators: [{
            type: Injectable,
            args: [{
                    providedIn: 'root'
                }]
        }], ctorParameters: function () { return []; } });

class SmartNavbarComponent {
    constructor() { }
    ngOnInit() {
        console.log(this.smartNavbar);
    }
    onIconClick() {
        if (this.smartNavbar.iconCallback) {
            this.smartNavbar.iconCallback();
        }
    }
    openFilters() {
        if (this.smartNavbar.filterButtonCallback) {
            this.smartNavbar.filterButtonCallback();
        }
    }
}
SmartNavbarComponent.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartNavbarComponent, deps: [], target: i0.ɵɵFactoryTarget.Component });
SmartNavbarComponent.ɵcmp = i0.ɵɵngDeclareComponent({ minVersion: "12.0.0", version: "13.2.7", type: SmartNavbarComponent, selector: "smart-navbar", inputs: { smartNavbar: "smartNavbar" }, ngImport: i0, template: "<mat-toolbar class=\"navbarHeight\">\n    <img *ngIf=\"smartNavbar.icon\" src=\"{{ smartNavbar.icon }}\" alt=\"\" (click)=\"onIconClick()\" />\n    <span class=\"spacer\"></span>\n    <div *ngIf=\"smartNavbar.searchBar\" class=\"input\">\n        <input type=\"text\" placeholder=\"{{ smartNavbar.searchBar.placeholder }}\" />\n        <div\n            *ngIf=\"\n                smartNavbar.searchBar.quickFilterLabel && smartNavbar.searchBar.quickFilters.length\n            \"\n            class=\"drop_down_btn\"\n        >\n            <button mat-button>\n                {{ smartNavbar.searchBar.quickFilterLabel }}\n                <mat-icon>arrow_drop_down</mat-icon>\n            </button>\n            <mat-icon *ngIf=\"smartNavbar.searchBar.icon\" class=\"search_icon\">\n                {{ smartNavbar.searchBar.icon }}\n            </mat-icon>\n            <mat-icon *ngIf=\"!smartNavbar.searchBar.icon\" class=\"search_icon\">search</mat-icon>\n        </div>\n        <div class=\"filter-btn\">\n            <button mat-raised-button color=\"primary\" (click)=\"openFilters()\">\n                <mat-icon>filter_list</mat-icon> {{ smartNavbar.filterButtonLabel }}\n            </button>\n        </div>\n    </div>\n    <span class=\"spacer\"></span>\n    <div *ngIf=\"smartNavbar.userSettings\" class=\"account-btn\">\n        <button mat-button color=\"primary\">\n            <mat-icon *ngIf=\"smartNavbar.userSettings.icon\">\n                {{ smartNavbar.userSettings.icon }}\n            </mat-icon>\n            <mat-icon *ngIf=\"!smartNavbar.userSettings.icon\"> person_outline </mat-icon>\n            {{ smartNavbar.userSettings.label }}\n            <mat-icon>arrow_drop_down</mat-icon>\n        </button>\n    </div>\n</mat-toolbar>\n", styles: [".app-name{padding-left:15px;padding-top:3px}.filter-btn ::ng-deep .mat-raised-button{border:none;margin-left:20px}input[type=text]{flex:1;background:#fff;height:40px;border:none;outline:none;padding:0 25px;border-radius:25px 0 0 25px}.drop_down_btn{position:relative;left:-5;border-radius:0 25px 25px 0;height:40px;border:none;outline:none;cursor:pointer;background:white;color:#00a8da;display:flex;align-items:center;padding-right:20px}.input{flex:1;position:relative;font-size:20px;display:flex;flex-direction:row;align-items:center;padding-left:30px}.mat-icon{position:relative;left:-5}.navbarHeight{height:var(--navbar-height)}.spacer{width:75px}::ng-deep .navbar-form .mat-form-field-appearance-outline .mat-form-field-outline,::ng-deep .navbar-form .mat-form-field-appearance-outline:not(.mat-form-field-disabled) .mat-form-field-flex:hover .mat-form-field-outline,::ng-deep .navbar-form .mat-form-field-appearance-outline.mat-focused .mat-form-field-outline{opacity:.75!important;color:#fff}\n"], components: [{ type: i1.MatToolbar, selector: "mat-toolbar", inputs: ["color"], exportAs: ["matToolbar"] }, { type: i2.MatButton, selector: "button[mat-button], button[mat-raised-button], button[mat-icon-button],             button[mat-fab], button[mat-mini-fab], button[mat-stroked-button],             button[mat-flat-button]", inputs: ["disabled", "disableRipple", "color"], exportAs: ["matButton"] }, { type: i3.MatIcon, selector: "mat-icon", inputs: ["color", "inline", "svgIcon", "fontSet", "fontIcon"], exportAs: ["matIcon"] }], directives: [{ type: i4.NgIf, selector: "[ngIf]", inputs: ["ngIf", "ngIfThen", "ngIfElse"] }] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartNavbarComponent, decorators: [{
            type: Component,
            args: [{ selector: 'smart-navbar', encapsulation: ViewEncapsulation.Emulated, template: "<mat-toolbar class=\"navbarHeight\">\n    <img *ngIf=\"smartNavbar.icon\" src=\"{{ smartNavbar.icon }}\" alt=\"\" (click)=\"onIconClick()\" />\n    <span class=\"spacer\"></span>\n    <div *ngIf=\"smartNavbar.searchBar\" class=\"input\">\n        <input type=\"text\" placeholder=\"{{ smartNavbar.searchBar.placeholder }}\" />\n        <div\n            *ngIf=\"\n                smartNavbar.searchBar.quickFilterLabel && smartNavbar.searchBar.quickFilters.length\n            \"\n            class=\"drop_down_btn\"\n        >\n            <button mat-button>\n                {{ smartNavbar.searchBar.quickFilterLabel }}\n                <mat-icon>arrow_drop_down</mat-icon>\n            </button>\n            <mat-icon *ngIf=\"smartNavbar.searchBar.icon\" class=\"search_icon\">\n                {{ smartNavbar.searchBar.icon }}\n            </mat-icon>\n            <mat-icon *ngIf=\"!smartNavbar.searchBar.icon\" class=\"search_icon\">search</mat-icon>\n        </div>\n        <div class=\"filter-btn\">\n            <button mat-raised-button color=\"primary\" (click)=\"openFilters()\">\n                <mat-icon>filter_list</mat-icon> {{ smartNavbar.filterButtonLabel }}\n            </button>\n        </div>\n    </div>\n    <span class=\"spacer\"></span>\n    <div *ngIf=\"smartNavbar.userSettings\" class=\"account-btn\">\n        <button mat-button color=\"primary\">\n            <mat-icon *ngIf=\"smartNavbar.userSettings.icon\">\n                {{ smartNavbar.userSettings.icon }}\n            </mat-icon>\n            <mat-icon *ngIf=\"!smartNavbar.userSettings.icon\"> person_outline </mat-icon>\n            {{ smartNavbar.userSettings.label }}\n            <mat-icon>arrow_drop_down</mat-icon>\n        </button>\n    </div>\n</mat-toolbar>\n", styles: [".app-name{padding-left:15px;padding-top:3px}.filter-btn ::ng-deep .mat-raised-button{border:none;margin-left:20px}input[type=text]{flex:1;background:#fff;height:40px;border:none;outline:none;padding:0 25px;border-radius:25px 0 0 25px}.drop_down_btn{position:relative;left:-5;border-radius:0 25px 25px 0;height:40px;border:none;outline:none;cursor:pointer;background:white;color:#00a8da;display:flex;align-items:center;padding-right:20px}.input{flex:1;position:relative;font-size:20px;display:flex;flex-direction:row;align-items:center;padding-left:30px}.mat-icon{position:relative;left:-5}.navbarHeight{height:var(--navbar-height)}.spacer{width:75px}::ng-deep .navbar-form .mat-form-field-appearance-outline .mat-form-field-outline,::ng-deep .navbar-form .mat-form-field-appearance-outline:not(.mat-form-field-disabled) .mat-form-field-flex:hover .mat-form-field-outline,::ng-deep .navbar-form .mat-form-field-appearance-outline.mat-focused .mat-form-field-outline{opacity:.75!important;color:#fff}\n"] }]
        }], ctorParameters: function () { return []; }, propDecorators: { smartNavbar: [{
                type: Input
            }] } });

class SmartNavbarModule {
}
SmartNavbarModule.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartNavbarModule, deps: [], target: i0.ɵɵFactoryTarget.NgModule });
SmartNavbarModule.ɵmod = i0.ɵɵngDeclareNgModule({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartNavbarModule, declarations: [SmartNavbarComponent], imports: [BrowserModule,
        MatButtonModule,
        MatToolbarModule,
        MatInputModule,
        MatIconModule,
        FormsModule,
        ReactiveFormsModule,
        MatFormFieldModule], exports: [SmartNavbarComponent] });
SmartNavbarModule.ɵinj = i0.ɵɵngDeclareInjector({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartNavbarModule, imports: [[
            BrowserModule,
            MatButtonModule,
            MatToolbarModule,
            MatInputModule,
            MatIconModule,
            FormsModule,
            ReactiveFormsModule,
            MatFormFieldModule,
        ]] });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartNavbarModule, decorators: [{
            type: NgModule,
            args: [{
                    declarations: [SmartNavbarComponent],
                    imports: [
                        BrowserModule,
                        MatButtonModule,
                        MatToolbarModule,
                        MatInputModule,
                        MatIconModule,
                        FormsModule,
                        ReactiveFormsModule,
                        MatFormFieldModule,
                    ],
                    exports: [SmartNavbarComponent],
                }]
        }] });

/*
 * Public API Surface of smart-navbar
 */

/**
 * Generated bundle index. Do not edit.
 */

export { SmartNavbarComponent, SmartNavbarModule, SmartNavbarService };
//# sourceMappingURL=smartbit4all-navbar.mjs.map
