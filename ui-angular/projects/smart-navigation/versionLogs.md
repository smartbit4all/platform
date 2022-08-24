# Smart navigation version log

[_@smartbit4all readme_](../../README.md)

## References

These packages must be updated in case of a new version:

-   @smartbit4all/tab-group

## @smartbit4all/navigation v0.1.1

**Type: Feature**

This package was designed to manage the Angular routing as simple as it should be.

**The `SmartNavigation` interface:**

    export interface SmartNavigation {
        // properties
        urlSubscription?: Subscription;
        querySubscription?: Subscription;

        // functions
        navigateTo(
            urls: string[],
            queryParams?: Params,
            queryParamsHandling?: QueryParamsHandling,
            relativeToThis?: boolean
        ): void;
        subscribeToUrlChange(callback: (url: string) => void): void;
        subscribeToQueryChange(callback: (params: Params) => void): void;
        unsubscribeFromQueryChanges(): void;
        getCurrentPath(): string;
    }

---

### How to use the **SmartNavigationService**

**Initialize**

First of all the `SmartNavigationModule` has to be imported in the **app.module**.

Secondly, there are two ways to use the `SmartNavigationService`. The first one is to use it as a global service and provide it in the **app.module**. The second one is to use it as a local service and provide it in the header of the component.

Provide the `SmartNavigationService` as a **global service** in the app.module:

    @NgModule({
        declarations: [...],
        imports: [
            SmartNavigationModule,
            ...
        ],
        providers: [
            SmartNavigationService,
            ...
        ],
        ...
    })

Provide the `SmartNavigationService` as a **local service** in the header of the component:

    @Component({
        selector: 'app-example',
        templateUrl: './example.component.html',
        styleUrls: ['./example.component.css'],
        providers: [SmartNavigationService]
    })
    export class ExampleComponent implements OnInit { ... }

In order to **use this service**, the `ActivatedRoute` must be provided in each component.

    export class ExampleComponent {

        constructor(
            private navigationService: SmartNavigationService,
            public route: ActivatedRoute
        ) {
            this.navigationService.route = this.route;
        }
    }

---

**Usage**

From now on the **navigation between routes** is unified, handled by this service. The usage:

    someFunction(urlToNavigate: string, uuid: string): void {
    	this.navigationService.navigateTo([urlToNavigate], { uuid }); // equivalent to { uuid: uuid }
    }

**Getting the current path**:

    let path = this.navigationService.getCurrentPath();

**Subscribing to query changes** is more simple than ever, to catch every change just use this code snippet belove:

    constructor(
        private navigationService: SmartNavigationService,
        public route: ActivatedRoute
    ) {
        this.navigationService.subscribeToQueryChange(this.handleQueryChange.bind(this));
    }

    handleQueryChange(params: Params): void {
    	if (params) {
    		let uuid: string = params['uuid'];
    	}
    }

    ngOnDestroy(): void {
    	this.navigationService.unsubscribeFromQueryChanges();
    }

It is not necessary to unsubscribe manually, however, I highly recommend it.

**Subscribing to url changes** can be done easily as well:

    constructor(private navigationService: SmartNavigationService, private route: ActivatedRoute) {
        this.navigationService.route = this.route;
        this.navigationService.subscribeToUrlChange(this.handleUrlChange.bind(this));
    }

    handleUrlChange(url: string): void { ... }
