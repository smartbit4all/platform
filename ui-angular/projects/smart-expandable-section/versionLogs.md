# Smart Expandable Section

[_@smartbit4all readme_](../../README.md)

## References

These packages must be updated in case of a new version:

-   There are no references yet

---

## How to use

### Installation

Go to your project, open the terminal and use the following command:

    npm i ../../platform/ui-angular/npms/smart-expandable-section/smartbit4all-expandable-section-0.0.4.tgz

Then import it in the AppModule:

`app.module.ts:`

    import { SmartExpandableSectionModule } from '@smartbit4all/expandable-section';
    ...
    @NgModule({
        declarations: [...]
        imports: [
            ...
            SmartExpandableSectionModule
        ],
        ...
    })

### Usage

`example-container.component.html:`

    <smart-expandable-section
        [data]="exampleData"
    ></smart-expandable-section>

`example-container.component.ts:`

    export class CreateDocumentFormComponent {
        exampleData: ExpandableSection<T>;
        ...
        setExpandableSection(): void {
            this.exampleData = {
    			title: 'Title',
    			customComponent: ExampleComponent,
    			data: { ... },
    			inputName: 'inputName'
    		};
        }
    }

`example.component.ts:`

    export class CreateDocumentFormComponent {
        @Input() inputName: T;
    }

---

## Version logs

## @smartbit4all/expandable-section v0.1.2

**Type: Feature**

In this version the exapandable-section has been extended with an optional button feature.

**Modifications:**

ExpandableSection got a new ExpandableSectionButton property:

        export interface ExpandableSection {
            title: string;
            customComponent?: any;
            button?: ExpandableSectionButton;
            data?: T;
            inputName?: string;
        }

        export interface ExpandableSectionButton {
            label: string;
            icon?: string;
            onClick: (args?: any[]) => void;
        }

Html has been modified in order to render the button element by the given ExpandableSectionButton.

If the ExpandableSectionButton is not provided no button appears on the view. NO modification need in applications has the previous version of ExpendableSection.

## @smartbit4all/expandable-section v0.0.4

**Type: Feature**

The basic smart expandable section with its features.
