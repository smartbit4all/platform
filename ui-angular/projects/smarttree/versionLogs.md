# Smart tree version log

[_@smartbit4all readme_](../../README.md)

## References

These packages must be updated in case of a new version:

-   There are no references yet

---

## How to use

### Installation

Go to your project, open the terminal and use the following command:

    npm i ../../platform/ui-angular/npms/smarttree/smartbit4all-tree-0.0.8.tgz

Then import it in the AppModule:

`app.module.ts:`

    import { SmarttreeModule } from '@smartbit4all/tree';
    ...
    @NgModule({
        declarations: [...]
        imports: [
            ...
            SmarttreeModule,
        ]
        ...
    })

### Usage

`any.component.html:`

    <router-outlet name="exampleNamedOutlet"></router-outlet>

`app-routing.module.ts:`

    ...
    {
    	path: 'example-url',
    	component: ExampleComponent,
    	outlet: 'exampleNamedOutlet'
    }

`example.component.ts:`

    ...
    navigationUrlsByNodeType: [
        {
            nodeType: 'example',
            navigationUrl: ['', { outlets: { exampleNamedOutlet: ['example-url'] } }]
        }
    ]

---

## Version logs

## @smartbit4all/smarttree v0.0.8

**Type: Feature**

This version contains a routing update, which allows you to navigate to a named outlet.

**The change:**

The `navigationUrl` property of the `SmartTreeNodeType` has changed from _string_ to _any[]_.

    export interface SmartTreeNodeType {
        nodeType: string;
        navigationUrl: any[];
    }

## @smartbit4all/smarttree v0.0.7

**Type: Bugfix**

A bug has been fixed which caused routing problems while navigating with objectUri.

The change itself: `this.router.navigate(['${navigationUrlByNodeType.navigationUrl}'], { queryParams: { uri: node.objectUri }});`

## @smartbit4all/smarttree v0.0.6

**Type: Bugfix**

The last deselected node could not be selected directly.

## @smartbit4all/smarttree v0.0.5

**Type: Bugfix**

There were no option to deselect an already selected tree node.

## @smartbit4all/smarttree v0.0.4

**Type: Bugfix**

Navigating on node click was not working properly.

`this.router.navigateByUrl` has been replaced with `this.router.navigate`.

## @smartbit4all/smarttree v0.0.3

**Type: Feature**

The generated TreeModel has been replaced with SmartTreeModel.

The new SmartTreeModel and SmartTreeNode have new features like setting the node type and its navigation target. Also some optional fields have been changed to required ones.
