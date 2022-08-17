# Smart tree version log

## References

These packages must be updated in case of a new version:

-   There are no references yet

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
