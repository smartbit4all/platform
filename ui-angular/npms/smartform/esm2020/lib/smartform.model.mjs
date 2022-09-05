/**
 * This enum containes the available types of the form widgets.
 *
 * @author Roland Fényes
 */
export var SmartFormWidgetType;
(function (SmartFormWidgetType) {
    SmartFormWidgetType[SmartFormWidgetType["TEXT_FIELD"] = 0] = "TEXT_FIELD";
    SmartFormWidgetType[SmartFormWidgetType["TEXT_FIELD_NUMBER"] = 1] = "TEXT_FIELD_NUMBER";
    SmartFormWidgetType[SmartFormWidgetType["TEXT_FIELD_CHIPS"] = 2] = "TEXT_FIELD_CHIPS";
    SmartFormWidgetType[SmartFormWidgetType["TEXT_BOX"] = 3] = "TEXT_BOX";
    SmartFormWidgetType[SmartFormWidgetType["SELECT"] = 4] = "SELECT";
    SmartFormWidgetType[SmartFormWidgetType["SELECT_MULTIPLE"] = 5] = "SELECT_MULTIPLE";
    SmartFormWidgetType[SmartFormWidgetType["CHECK_BOX"] = 6] = "CHECK_BOX";
    SmartFormWidgetType[SmartFormWidgetType["CHECK_BOX_TABLE"] = 7] = "CHECK_BOX_TABLE";
    SmartFormWidgetType[SmartFormWidgetType["RADIO_BUTTON"] = 8] = "RADIO_BUTTON";
    SmartFormWidgetType[SmartFormWidgetType["DATE_PICKER"] = 9] = "DATE_PICKER";
    SmartFormWidgetType[SmartFormWidgetType["FILE_UPLOAD"] = 10] = "FILE_UPLOAD";
    SmartFormWidgetType[SmartFormWidgetType["ITEM"] = 11] = "ITEM";
    SmartFormWidgetType[SmartFormWidgetType["CONTAINER"] = 12] = "CONTAINER";
    SmartFormWidgetType[SmartFormWidgetType["LABEL"] = 13] = "LABEL";
    SmartFormWidgetType[SmartFormWidgetType["TIME"] = 14] = "TIME";
})(SmartFormWidgetType || (SmartFormWidgetType = {}));
export var SmartFormWidgetDirection;
(function (SmartFormWidgetDirection) {
    SmartFormWidgetDirection[SmartFormWidgetDirection["COL"] = 0] = "COL";
    SmartFormWidgetDirection[SmartFormWidgetDirection["ROW"] = 1] = "ROW";
})(SmartFormWidgetDirection || (SmartFormWidgetDirection = {}));
export var SmartFormWidgetWidth;
(function (SmartFormWidgetWidth) {
    SmartFormWidgetWidth[SmartFormWidgetWidth["SMALL"] = 150] = "SMALL";
    SmartFormWidgetWidth[SmartFormWidgetWidth["MEDIUM"] = 250] = "MEDIUM";
    SmartFormWidgetWidth[SmartFormWidgetWidth["LARGE"] = 350] = "LARGE";
    SmartFormWidgetWidth[SmartFormWidgetWidth["EXTRA_LARGE"] = 450] = "EXTRA_LARGE";
})(SmartFormWidgetWidth || (SmartFormWidgetWidth = {}));
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnRmb3JtLm1vZGVsLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vLi4vcHJvamVjdHMvc21hcnRmb3JtL3NyYy9saWIvc21hcnRmb3JtLm1vZGVsLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUVBOzs7O0dBSUc7QUFDSCxNQUFNLENBQU4sSUFBWSxtQkFnQlg7QUFoQkQsV0FBWSxtQkFBbUI7SUFDM0IseUVBQVUsQ0FBQTtJQUNWLHVGQUFpQixDQUFBO0lBQ2pCLHFGQUFnQixDQUFBO0lBQ2hCLHFFQUFRLENBQUE7SUFDUixpRUFBTSxDQUFBO0lBQ04sbUZBQWUsQ0FBQTtJQUNmLHVFQUFTLENBQUE7SUFDVCxtRkFBZSxDQUFBO0lBQ2YsNkVBQVksQ0FBQTtJQUNaLDJFQUFXLENBQUE7SUFDWCw0RUFBVyxDQUFBO0lBQ1gsOERBQUksQ0FBQTtJQUNKLHdFQUFTLENBQUE7SUFDVCxnRUFBSyxDQUFBO0lBQ0wsOERBQUksQ0FBQTtBQUNSLENBQUMsRUFoQlcsbUJBQW1CLEtBQW5CLG1CQUFtQixRQWdCOUI7QUFFRCxNQUFNLENBQU4sSUFBWSx3QkFHWDtBQUhELFdBQVksd0JBQXdCO0lBQ2hDLHFFQUFHLENBQUE7SUFDSCxxRUFBRyxDQUFBO0FBQ1AsQ0FBQyxFQUhXLHdCQUF3QixLQUF4Qix3QkFBd0IsUUFHbkM7QUFFRCxNQUFNLENBQU4sSUFBWSxvQkFLWDtBQUxELFdBQVksb0JBQW9CO0lBQzVCLG1FQUFXLENBQUE7SUFDWCxxRUFBWSxDQUFBO0lBQ1osbUVBQVcsQ0FBQTtJQUNYLCtFQUFpQixDQUFBO0FBQ3JCLENBQUMsRUFMVyxvQkFBb0IsS0FBcEIsb0JBQW9CLFFBSy9CIiwic291cmNlc0NvbnRlbnQiOlsiaW1wb3J0IHsgVmFsaWRhdG9yRm4gfSBmcm9tIFwiQGFuZ3VsYXIvZm9ybXNcIjtcclxuXHJcbi8qKlxyXG4gKiBUaGlzIGVudW0gY29udGFpbmVzIHRoZSBhdmFpbGFibGUgdHlwZXMgb2YgdGhlIGZvcm0gd2lkZ2V0cy5cclxuICpcclxuICogQGF1dGhvciBSb2xhbmQgRsOpbnllc1xyXG4gKi9cclxuZXhwb3J0IGVudW0gU21hcnRGb3JtV2lkZ2V0VHlwZSB7XHJcbiAgICBURVhUX0ZJRUxELFxyXG4gICAgVEVYVF9GSUVMRF9OVU1CRVIsXHJcbiAgICBURVhUX0ZJRUxEX0NISVBTLFxyXG4gICAgVEVYVF9CT1gsXHJcbiAgICBTRUxFQ1QsXHJcbiAgICBTRUxFQ1RfTVVMVElQTEUsXHJcbiAgICBDSEVDS19CT1gsXHJcbiAgICBDSEVDS19CT1hfVEFCTEUsXHJcbiAgICBSQURJT19CVVRUT04sXHJcbiAgICBEQVRFX1BJQ0tFUixcclxuICAgIEZJTEVfVVBMT0FELFxyXG4gICAgSVRFTSxcclxuICAgIENPTlRBSU5FUixcclxuICAgIExBQkVMLFxyXG4gICAgVElNRSxcclxufVxyXG5cclxuZXhwb3J0IGVudW0gU21hcnRGb3JtV2lkZ2V0RGlyZWN0aW9uIHtcclxuICAgIENPTCxcclxuICAgIFJPVyxcclxufVxyXG5cclxuZXhwb3J0IGVudW0gU21hcnRGb3JtV2lkZ2V0V2lkdGgge1xyXG4gICAgU01BTEwgPSAxNTAsXHJcbiAgICBNRURJVU0gPSAyNTAsXHJcbiAgICBMQVJHRSA9IDM1MCxcclxuICAgIEVYVFJBX0xBUkdFID0gNDUwLFxyXG59XHJcblxyXG4vKipcclxuICogVGhpcyBpbnRlcmZhY2UgZGVzY3JpYmVzIGEgd2lkZ2V0IGluIGEgZm9ybS5cclxuICpcclxuICogQHBhcmFtIGtleSBNdXN0IGJlIHVuaXF1ZVxyXG4gKiBAcGFyYW0gbGFiZWwgVGhlIGxhYmVsIG9mIHRoZSB3aWRnZXRcclxuICogQHBhcmFtIHZhbHVlIERlZmF1bHQgdmFsdWVcclxuICogQHBhcmFtIHR5cGUgVGhlIHR5cGUgb2YgdGhlIGZvcm0gd2lkZ2V0XHJcbiAqIEBwYXJhbSBjYWxsYmFjayBBY3Rpb24gY2FsbGJhY2tcclxuICogQHBhcmFtIHZhbHVlTGlzdCBVc2UgdGhpcyBpZiB5b3Ugd2FudCB0byBkZWZpbmUgc2VsZWN0YWJsZSB2YWx1ZXNcclxuICogQHBhcmFtIGlzUmVxdWlyZWRcclxuICogQHBhcmFtIGljb24gUGxlYXNlIHVzZSBBbmd1bGFyIE1hdGVyaWFsIGljb25zXHJcbiAqXHJcbiAqIEBhdXRob3IgUm9sYW5kIEbDqW55ZXNcclxuICovXHJcbmV4cG9ydCBpbnRlcmZhY2UgU21hcnRGb3JtV2lkZ2V0PFQ+IHtcclxuICAgIGtleTogc3RyaW5nO1xyXG4gICAgbGFiZWw6IHN0cmluZztcclxuICAgIHNob3dMYWJlbD86IGJvb2xlYW47XHJcbiAgICB2YWx1ZTogVDtcclxuICAgIHdpZGdldERlc2NyaXB0aW9uPzogc3RyaW5nO1xyXG4gICAgdHlwZTogU21hcnRGb3JtV2lkZ2V0VHlwZTtcclxuICAgIGNhbGxiYWNrPzogKGFyZ3M6IGFueVtdKSA9PiBhbnk7XHJcbiAgICBwbGFjZWhvbGRlcj86IHN0cmluZztcclxuICAgIG1pblZhbHVlcz86IG51bWJlcjtcclxuICAgIG1heFZhbHVlcz86IG51bWJlcjtcclxuICAgIGRpcmVjdGlvbj86IFNtYXJ0Rm9ybVdpZGdldERpcmVjdGlvbjtcclxuICAgIHZhbHVlTGlzdD86IFNtYXJ0Rm9ybVdpZGdldDxUPltdO1xyXG4gICAgaXNSZXF1aXJlZD86IGJvb2xlYW47XHJcbiAgICBpY29uPzogc3RyaW5nO1xyXG4gICAgbWluV2lkdGg/OiBTbWFydEZvcm1XaWRnZXRXaWR0aDtcclxuICAgIGlzRGlzYWJsZWQ/OiBib29sZWFuO1xyXG4gICAgcHJlZml4Pzogc3RyaW5nO1xyXG4gICAgc3VmZml4Pzogc3RyaW5nO1xyXG4gICAgdmFsaWRhdG9ycz86IFZhbGlkYXRvckZuW107XHJcbiAgICBlcnJvck1lc3NhZ2U/OiBzdHJpbmc7XHJcbn1cclxuXHJcbi8qKlxyXG4gKiBEeW5hbWljIGZvcm1zIGNhbiBiZSBlYXNpbHkgZGVmaW5lZCB3aXRoIHRoaXMgaW50ZXJmYWNlLlxyXG4gKlxyXG4gKiBAcGFyYW0gbmFtZSBUaGUgbmFtZSBvZiB0aGUgZm9ybVxyXG4gKiBAcGFyYW0gd2lkZ2V0cyBUaGUgd2lkZ2V0cyBwcmVzZW50ZWQgaW4gdGhlIGZvcm1cclxuICpcclxuICogQGF1dGhvciBSb2xhbmQgRsOpbnllc1xyXG4gKi9cclxuZXhwb3J0IGludGVyZmFjZSBTbWFydEZvcm0ge1xyXG4gICAgbmFtZTogc3RyaW5nO1xyXG4gICAgZGlyZWN0aW9uOiBTbWFydEZvcm1XaWRnZXREaXJlY3Rpb247XHJcbiAgICB3aWRnZXRzOiBTbWFydEZvcm1XaWRnZXQ8YW55PltdO1xyXG59XHJcbiJdfQ==