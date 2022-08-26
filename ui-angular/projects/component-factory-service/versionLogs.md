# Smart Component Factory Service

[_@smartbit4all readme_](../../README.md)

## References

These packages must be updated in case of a new version:

-   [_@smartbit4all/expandable-section_](../smart-expandable-section/versionLogs.md)

---

## How to use

### Installation

Go to your project, open the terminal and use the following command:

    npm i ../../platform/ui-angular/npms/component-factory-service/smartbit4all-component-factory-service-0.0.1.tgz

### Usage

In this example there are two components, which are called `Example1Component` and `Example2Component`, these will be presented with the `ComponentFactoryService` in the `ParentComponent`.

`parent.component.html:`

    <button (click)="show(1)">Show 1</button>
    <button (click)="show(2)">Show 2</button>

    <ng-template #exampleRef></ng-template>

`parent.component.ts:`

    export class ParentComponent implements AfterViewInit {

        @ViewChild('exampleRef', { read: ViewContainerRef }) vcRefExample?: ViewContainerRef;

        componentRef?: ComponentRef<any>;

        constructor(private cfService: ComponentFactoryService) { ... }

        ngAfterViewInit(): void {
            this.createExampleOne();
        }

        show(componentNumber: number): void {
            if (componentNumber === 1)
                this.createExampleOne();
            else if (componentNumber === 2)
                this.createExampleTwo();
        }

        createExampleOne(): void {
            this.vcRefExample?.clear();

            this.cfService.createComponent(
                this.vcRefExample!,
                Example1Component
            );
        }

        createExampleTwo(): void {
            if (this.componentRef) {
                this.cfService.destroyComponent(this.componentRef);
            }

            let exampleInput: any = { ... }

            this.componentRef = this.cfService.createComponent(
                this.vcRefExample!,
                Example2Component,
                new Map<string, any>([['exampleInput', exampleInput]])
            );
        }
    }

`example2.component.ts:`

    export class Example2Component {
        @Input() exampleInput: any;

        ...
    }

---

## Version logs

## @smartbit4all/component-factory-service v0.0.1

**Type: Feature**

The npm package has been created.

The `ComponentFactoryService` helps to use **ng-template** in your code.
