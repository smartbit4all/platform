# Smart tab group version log

[_@smartbit4all readme_](../../README.md)

## References

These packages must be updated in case of a new version:

-   There are no references yet

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
