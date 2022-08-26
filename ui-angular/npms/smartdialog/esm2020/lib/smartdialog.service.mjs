import { Injectable } from "@angular/core";
import * as i0 from "@angular/core";
import * as i1 from "@angular/material/dialog";
import * as i2 from "@angular/router";
export class SmartdialogService {
    constructor(dialog, router) {
        this.dialog = dialog;
        this.router = router;
    }
    createDialog(smartDialog, component) {
        this.dialogData = smartDialog;
        this.dialogRef = this.dialog.open(component, { data: this.dialogData });
        this.dialogRef.afterClosed().subscribe((result) => {
            this.handleAfterClosed(result);
            this.closeDialog();
        });
    }
    handleAfterClosed(result) {
        throw new Error("handleAfterClosed function is not implemented!");
    }
    async closeDialog() {
        if (this.dialogData?.outlets) {
            await this.router.navigate([
                {
                    outlets: this.dialogData.outlets,
                },
            ]);
        }
        this.dialogRef?.close();
    }
}
SmartdialogService.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogService, deps: [{ token: i1.MatDialog }, { token: i2.Router }], target: i0.ɵɵFactoryTarget.Injectable });
SmartdialogService.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogService, providedIn: "root" });
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "13.2.7", ngImport: i0, type: SmartdialogService, decorators: [{
            type: Injectable,
            args: [{
                    providedIn: "root",
                }]
        }], ctorParameters: function () { return [{ type: i1.MatDialog }, { type: i2.Router }]; } });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnRkaWFsb2cuc2VydmljZS5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3Byb2plY3RzL3NtYXJ0ZGlhbG9nL3NyYy9saWIvc21hcnRkaWFsb2cuc2VydmljZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFDQSxPQUFPLEVBQUUsVUFBVSxFQUFFLE1BQU0sZUFBZSxDQUFDOzs7O0FBUTNDLE1BQU0sT0FBTyxrQkFBa0I7SUFJM0IsWUFBc0IsTUFBaUIsRUFBWSxNQUFjO1FBQTNDLFdBQU0sR0FBTixNQUFNLENBQVc7UUFBWSxXQUFNLEdBQU4sTUFBTSxDQUFRO0lBQUcsQ0FBQztJQUVyRSxZQUFZLENBQUMsV0FBNEIsRUFBRSxTQUE2QjtRQUNwRSxJQUFJLENBQUMsVUFBVSxHQUFHLFdBQVcsQ0FBQztRQUU5QixJQUFJLENBQUMsU0FBUyxHQUFHLElBQUksQ0FBQyxNQUFNLENBQUMsSUFBSSxDQUFDLFNBQVMsRUFBRSxFQUFFLElBQUksRUFBRSxJQUFJLENBQUMsVUFBVSxFQUFFLENBQUMsQ0FBQztRQUV4RSxJQUFJLENBQUMsU0FBUyxDQUFDLFdBQVcsRUFBRSxDQUFDLFNBQVMsQ0FBQyxDQUFDLE1BQU0sRUFBRSxFQUFFO1lBQzlDLElBQUksQ0FBQyxpQkFBaUIsQ0FBQyxNQUFNLENBQUMsQ0FBQztZQUMvQixJQUFJLENBQUMsV0FBVyxFQUFFLENBQUM7UUFDdkIsQ0FBQyxDQUFDLENBQUM7SUFDUCxDQUFDO0lBRUQsaUJBQWlCLENBQUMsTUFBVztRQUN6QixNQUFNLElBQUksS0FBSyxDQUFDLGdEQUFnRCxDQUFDLENBQUM7SUFDdEUsQ0FBQztJQUVELEtBQUssQ0FBQyxXQUFXO1FBQ2IsSUFBSSxJQUFJLENBQUMsVUFBVSxFQUFFLE9BQU8sRUFBRTtZQUMxQixNQUFNLElBQUksQ0FBQyxNQUFNLENBQUMsUUFBUSxDQUFDO2dCQUN2QjtvQkFDSSxPQUFPLEVBQUUsSUFBSSxDQUFDLFVBQVUsQ0FBQyxPQUFPO2lCQUNuQzthQUNKLENBQUMsQ0FBQztTQUNOO1FBQ0QsSUFBSSxDQUFDLFNBQVMsRUFBRSxLQUFLLEVBQUUsQ0FBQztJQUM1QixDQUFDOzsrR0E5QlEsa0JBQWtCO21IQUFsQixrQkFBa0IsY0FGZixNQUFNOzJGQUVULGtCQUFrQjtrQkFIOUIsVUFBVTttQkFBQztvQkFDUixVQUFVLEVBQUUsTUFBTTtpQkFDckIiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBDb21wb25lbnRUeXBlIH0gZnJvbSBcIkBhbmd1bGFyL2Nkay9wb3J0YWxcIjtcclxuaW1wb3J0IHsgSW5qZWN0YWJsZSB9IGZyb20gXCJAYW5ndWxhci9jb3JlXCI7XHJcbmltcG9ydCB7IE1hdERpYWxvZywgTWF0RGlhbG9nUmVmIH0gZnJvbSBcIkBhbmd1bGFyL21hdGVyaWFsL2RpYWxvZ1wiO1xyXG5pbXBvcnQgeyBSb3V0ZXIgfSBmcm9tIFwiQGFuZ3VsYXIvcm91dGVyXCI7XHJcbmltcG9ydCB7IFNtYXJ0RGlhbG9nRGF0YSB9IGZyb20gXCIuL3NtYXJ0ZGlhbG9nLm1vZGVsXCI7XHJcblxyXG5ASW5qZWN0YWJsZSh7XHJcbiAgICBwcm92aWRlZEluOiBcInJvb3RcIixcclxufSlcclxuZXhwb3J0IGNsYXNzIFNtYXJ0ZGlhbG9nU2VydmljZSB7XHJcbiAgICBkaWFsb2dSZWY/OiBNYXREaWFsb2dSZWY8YW55LCBhbnk+O1xyXG4gICAgZGlhbG9nRGF0YT86IFNtYXJ0RGlhbG9nRGF0YTtcclxuXHJcbiAgICBjb25zdHJ1Y3Rvcihwcm90ZWN0ZWQgZGlhbG9nOiBNYXREaWFsb2csIHByb3RlY3RlZCByb3V0ZXI6IFJvdXRlcikge31cclxuXHJcbiAgICBjcmVhdGVEaWFsb2coc21hcnREaWFsb2c6IFNtYXJ0RGlhbG9nRGF0YSwgY29tcG9uZW50OiBDb21wb25lbnRUeXBlPGFueT4pOiB2b2lkIHtcclxuICAgICAgICB0aGlzLmRpYWxvZ0RhdGEgPSBzbWFydERpYWxvZztcclxuXHJcbiAgICAgICAgdGhpcy5kaWFsb2dSZWYgPSB0aGlzLmRpYWxvZy5vcGVuKGNvbXBvbmVudCwgeyBkYXRhOiB0aGlzLmRpYWxvZ0RhdGEgfSk7XHJcblxyXG4gICAgICAgIHRoaXMuZGlhbG9nUmVmLmFmdGVyQ2xvc2VkKCkuc3Vic2NyaWJlKChyZXN1bHQpID0+IHtcclxuICAgICAgICAgICAgdGhpcy5oYW5kbGVBZnRlckNsb3NlZChyZXN1bHQpO1xyXG4gICAgICAgICAgICB0aGlzLmNsb3NlRGlhbG9nKCk7XHJcbiAgICAgICAgfSk7XHJcbiAgICB9XHJcblxyXG4gICAgaGFuZGxlQWZ0ZXJDbG9zZWQocmVzdWx0OiBhbnkpOiB2b2lkIHtcclxuICAgICAgICB0aHJvdyBuZXcgRXJyb3IoXCJoYW5kbGVBZnRlckNsb3NlZCBmdW5jdGlvbiBpcyBub3QgaW1wbGVtZW50ZWQhXCIpO1xyXG4gICAgfVxyXG5cclxuICAgIGFzeW5jIGNsb3NlRGlhbG9nKCk6IFByb21pc2U8dm9pZD4ge1xyXG4gICAgICAgIGlmICh0aGlzLmRpYWxvZ0RhdGE/Lm91dGxldHMpIHtcclxuICAgICAgICAgICAgYXdhaXQgdGhpcy5yb3V0ZXIubmF2aWdhdGUoW1xyXG4gICAgICAgICAgICAgICAge1xyXG4gICAgICAgICAgICAgICAgICAgIG91dGxldHM6IHRoaXMuZGlhbG9nRGF0YS5vdXRsZXRzLFxyXG4gICAgICAgICAgICAgICAgfSxcclxuICAgICAgICAgICAgXSk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIHRoaXMuZGlhbG9nUmVmPy5jbG9zZSgpO1xyXG4gICAgfVxyXG59XHJcbiJdfQ==