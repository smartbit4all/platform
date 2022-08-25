# Smart component factory service version log

[_@smartbit4all readme_](../../README.md)

## References

These packages must be updated in case of a new version:

-   @smartbit4all/expandable-section

## @smartbit4all/component-factory-service v0.0.1

**Type: Feature**

The `ComponentFactoryService` helps to use **ng-template** in your code.

---

### How to use this package

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
