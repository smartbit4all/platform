import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { SmartNavbar } from './smart-navbar.model';

@Component({
	selector: 'smart-navbar',
	templateUrl: './smart-navbar.component.html',
	styleUrls: ['./smart-navbar.component.css'],
	encapsulation: ViewEncapsulation.Emulated
})
export class SmartNavbarComponent implements OnInit {
	@Input() smartNavbar!: SmartNavbar;

	constructor() {}

	ngOnInit(): void {
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

	onNotificationClick() {
		if (this.smartNavbar?.notification?.notificationCallBack) {
			this.smartNavbar?.notification?.notificationCallBack();
		}
	}
}
