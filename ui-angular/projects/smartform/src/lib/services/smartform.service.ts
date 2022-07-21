import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { SmartForm } from '../smartform.model';

@Injectable()
export class SmartFormService {
	toFormGroup(smartForm: SmartForm): FormGroup {
		const group: any = {};

		smartForm.widgets.forEach((widget) => {
			group[widget.key] = new FormControl(
				widget.value || '',
				widget.isRequired ? Validators.required : undefined
			);
		});

		return new FormGroup(group);
	}
}
