# Smart Navbar

[_@smartbit4all readme_](../../README.md)

## References

These packages must be updated in case of a new version:

-   There are no references yet

---

## How to use

### Installation

Go to your project, open the terminal and use the following command:

    npm i ../../platform/ui-angular/npms/smart-navbar/smartbit4all-navbar-0.0.3.tgz

Then import it in the AppModule:

`app.module.ts:`

    import { SmartNavbarModule } from '@smartbit4all/navbar';
    ...
    @NgModule({
        declarations: [...]
        imports: [
            ...
            SmartNavbarModule,
        ],
        ...
    })

### Usage

`example.component.html:`

    <smart-navbar [smartNavbar]="exampleNavbar"></smart-navbar>

`example.component.css:`

    .navbar {
        height: 250px;
    }

    ::ng-deep .navbarHeight {
        background-color: transparent !important;
    }

`example.component.ts:`

    export class ExampleComponent {
        exampleNavbar: SmartNavbar;
        user: ExampleUser;

        constructor() {
            this.exampleNavbar = {
                icon: 'assets/icon.png',
                searchBar: {
                    placeholder: 'Search',
                    quickFilterLabel: 'Quick filters',
                    quickFilters: ['Quick filter 1'],
                    searchCallback: this.onSearch.bind(this)
                },
                iconCallback: this.onHome.bind(this),
                filterButtonLabel: 'Filters',
                filterButtonCallback: this.onFilter.bind(this),
                userSettings: {
                    label: user.name
                }
            };
        }
    }

---

## Version logs

## @smartbit4all/navbar v0.0.3

**Type: Feature**

The basic smart navbar with its features.
