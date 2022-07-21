import { Component, Input, OnInit } from '@angular/core';
import { SmartFileUploaderI18n } from './smartfileuploader.model';

@Component({
	selector: 'smartfileuploader',
	templateUrl: './smartfileuploader.component.html',
	styleUrls: ['./smartfileuploader.component.css']
})
export class SmartfileuploaderComponent implements OnInit {
	files: any[] = [];
	@Input() uploadCallback!: (files: any[]) => void;
	@Input() fileFormats: string[] = [];
	@Input() i18n!: SmartFileUploaderI18n;

	constructor() {
		this.i18n = {
			addFile: 'dokumentum hozzáadása',
			browseOrDrag: 'tallózás vagy behúzás',
			maxSize: 'max. 25 MB',
			formats: 'PDF, docx, xls formátum'
		};
	}

	ngOnInit(): void {
		console.log(this.fileFormats);
	}

	getFile(event: any): void {
		if (event.target.files && event.target.files.length) {
			this.files = [...this.files, ...event.target.files];
		}
	}

	remove(index: number): void {
		this.files.splice(index, 1);
	}

	uploadFile(): void {
		this.uploadCallback(this.files);
	}
}
