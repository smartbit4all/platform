import { OnInit } from '@angular/core';
import { SmartFileUploaderI18n } from './smartfileuploader.model';
import * as i0 from "@angular/core";
export declare class SmartfileuploaderComponent implements OnInit {
    files: any[];
    uploadCallback: (files: any[]) => void;
    fileFormats: string[];
    i18n: SmartFileUploaderI18n;
    constructor();
    ngOnInit(): void;
    getFile(event: any): void;
    remove(index: number): void;
    uploadFile(): void;
    static ɵfac: i0.ɵɵFactoryDeclaration<SmartfileuploaderComponent, never>;
    static ɵcmp: i0.ɵɵComponentDeclaration<SmartfileuploaderComponent, "smartfileuploader", never, { "uploadCallback": "uploadCallback"; "fileFormats": "fileFormats"; "i18n": "i18n"; }, {}, never, never>;
}
