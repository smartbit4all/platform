# Smart Dialog

[_@smartbit4all readme_](../../README.md)

## References

These packages must be updated in case of a new version:

-   There are no references yet

---

## How to use

### Installation

Go to your project, open the terminal and use the following command:

    npm i ../../platform/ui-angular/npms/smartdialog/smartbit4all-dialog-0.0.6.tgz

Then import it in the AppModule:

`app.module.ts:`

    import { SmartdialogModule, SmartdialogService } from '@smartbit4all/dialog';
    ...
    @NgModule({
        declarations: [...]
        imports: [
            ...
            SmartdialogModule,
        ],
        providers: [
            ...
    	    SmartdialogService,
        ]
        ...
    })

### Usage

Create a component for the content of the dialog. In the folder of the component create an Angular Service.

-   src/app/.../example-dialog/
    -   example-dialog.component.css
    -   example-dialog.component.html
    -   example-dialog.component.ts
    -   example-dialog.service.ts

Extend your service with the `SmartdialogService`. You can see an example for a specific service belove.

`example-dialog.service.ts:`

    @Injectable({
        providedIn: 'root'
    })
    export class ExampleDialogService extends SmartdialogService {
        constructor(
            dialog: MatDialog,
            router: Router
        ) {
            super(dialog, router);
        }

        override handleAfterClosed(result: any): void {
            ...
        }

        openDialog(): void {
            const dialogData: SmartDialogData = { ... };
            this.createDialog(dialogData, SmartDialog);
        }

        onAction(params: any): void {
            this.closeDialog().then(() => {
                ...
            });
        }
    }

In case if you use **RouterOutlet** in your dialog, it should be used like this:

`example-dialog.component.html:`

    <router-outlet name="exampleNamedOutlet"></router-outlet>

`app-routing.module.ts:`

    ...
    {
    	path: 'example-url',
    	component: ExampleComponent,
    	outlet: 'exampleNamedOutlet'
    }

`example-dialog.service.ts:`

    const dialogData: SmartDialogData = {
        ...
        outlets: {
            exampleNamedOutlet: null
        }
    };

---

## Version logs

## @smartbit4all/dialog v0.0.6

**Type: Feature**

In this update a new way to handle dialogs and its data has been introduced.

With the `SmartdialogService` **creating** and **closing** a dialog has become easier and more unified. **This service should be used as a parent class of a workflow specific service.**

## @smartbit4all/dialog v0.0.2

**Type: Version updates**

The versions of the SmartForm and the SmartTable have been updated.

**New versions:**

-   @smartbit4all/form: v0.1.1
-   @smartbit4all/table: v0.1.3

## @smartbit4all/dialog v0.0.1

**Type: Feature**

The basic smart dialog with its features.
