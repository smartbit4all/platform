# Smart Tab Group

[_@smartbit4all readme_](../../README.md)

## References

These packages must be updated in case of a new version:

-   There are no references yet

---

## How to use

### Installation

Go to your project, open the terminal and use the following command:

    npm i ../../platform/ui-angular/npms/smart-tab-group/smartbit4all-tab-group-0.1.1.tgz

Then import it in the AppModule:

`app.module.ts:`

    import { SmartTabGroupModule } from '@smartbit4all/tab-group';
    ...
    @NgModule({
        declarations: [...]
        imports: [
            ...
            SmartTabGroupModule,
        ],
        ...
    })

### Usage

`example.component.html:`

    <smart-tab-group [tabTiles]="tabs!" [route]="route">
        <router-outlet></router-outlet>
    </smart-tab-group>

`example.component.ts:`

    export class ExampleComponent {
        tabs?: TabTile[];

        constructor(public route: ActivatedRoute) {
            this.setTabs();
        }

        setTabs(): void {
            this.tabs = [
                {
                    tileName: 'Tab 1',
                    url: '../tab_1',
                    uuid: ''
                },
                {
                    tileName: 'Tab 2',
                    url: '../tab_2',
                    uuid: ''
                }
            ];
        }
    }

`app-routing.module.ts:`

    const routes: Routes = [
        {
            path: '',
            component: ExampleComponent,
            children: [
                {
                    path: 'tab_1',
                    component: ExampleTab1Component
                },
                {
                    path: 'tab_2',
                    component: ExampleTab2Component
                }
            ]
        },
    ]

---

## Version logs

## @smartbit4all/tab-group v0.1.1

**Type: Feature**

This version of the `SmartTabGroup` uses the `SmartNavigationService` package.

In order to use this package make sure that the proper version of the **@smartbit4all/navigation** is installed.

**Changes in the usage:**

There are two versions for setting up a `SmartTabGroup`. In _versionA_ the url gets a `'../'` prefix which helps the relative navigation. However, in some cases it simply does not work. In that case get rid of the prefix mentioned before, and set the url as it is shown in _versionB_.

    let versionA = [
        {
            tileName: 'Tab 1',
            url: '../tab_1',
            uuid: ''
        }
    ];

    let versionB = [
        {
            tileName: 'Tab 1',
            url: 'tab_1',
            uuid: ''
        }
    ];

## @smartbit4all/tab-group v0.0.2

**Type: Bugfix**

The **query parameters dissappeared** from the url **on navigating** to a new tab.

The code snippet that has been replaced:

    this.router.navigateByUrl(this.actualPath + "/" + this.tabTiles[$event.index].url);

The new code snippet which has solved the issue:

    this.router.navigate(
        [this.actualPath, this.tabTiles[$event.index].url],
        {
            queryParamsHandling: "preserve",
        });

## @smartbit4all/tab-group v0.0.1

**Type: Feature**

The basic smart tab group with its features.
