# Smart expandable section version log

[_@smartbit4all readme_](../../README.md)

## References

These packages must be updated in case of a new version:

- There are no references yet

## @smartbit4all/expandable-section v0.1.2

**Type: Feature**

In this version the exapandable-section has been extended with an optional button feature.

**Modifications:**

ExpandableSection got a new ExpandableSectionButton property:

        export interface ExpandableSection {
            title: string;
            customComponent?: any;
            button?: ExpandableSectionButton;
            data?: T;
            inputName?: string;
        }

        export interface ExpandableSectionButton {
            label: string;
            icon?: string;
            onClick: (args?: any[]) => void;
        }

Html has been modified in order to render the button element by the given ExpandableSectionButton.

If the ExpandableSectionButton is not provided no button appears on the view. NO modification need in applications has the previous version of ExpendableSection.

## @smartbit4all/expandable-section v0.0.4

**Type: Feature**

The basic smart expandable section with its features.
