# Smart tab group version log

[_@smartbit4all readme_](../../README.md)

## References

These packages must be updated in case of a new version:

-   There are no references yet

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
