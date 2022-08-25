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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnRkaWFsb2cuc2VydmljZS5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3Byb2plY3RzL3NtYXJ0ZGlhbG9nL3NyYy9saWIvc21hcnRkaWFsb2cuc2VydmljZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFDQSxPQUFPLEVBQUUsVUFBVSxFQUFFLE1BQU0sZUFBZSxDQUFDOzs7O0FBUTNDLE1BQU0sT0FBTyxrQkFBa0I7SUFJM0IsWUFBb0IsTUFBaUIsRUFBVSxNQUFjO1FBQXpDLFdBQU0sR0FBTixNQUFNLENBQVc7UUFBVSxXQUFNLEdBQU4sTUFBTSxDQUFRO0lBQUcsQ0FBQztJQUVqRSxZQUFZLENBQUMsV0FBNEIsRUFBRSxTQUE2QjtRQUNwRSxJQUFJLENBQUMsVUFBVSxHQUFHLFdBQVcsQ0FBQztRQUU5QixJQUFJLENBQUMsU0FBUyxHQUFHLElBQUksQ0FBQyxNQUFNLENBQUMsSUFBSSxDQUFDLFNBQVMsRUFBRSxFQUFFLElBQUksRUFBRSxJQUFJLENBQUMsVUFBVSxFQUFFLENBQUMsQ0FBQztRQUV4RSxJQUFJLENBQUMsU0FBUyxDQUFDLFdBQVcsRUFBRSxDQUFDLFNBQVMsQ0FBQyxDQUFDLE1BQU0sRUFBRSxFQUFFO1lBQzlDLElBQUksQ0FBQyxpQkFBaUIsQ0FBQyxNQUFNLENBQUMsQ0FBQztZQUMvQixJQUFJLENBQUMsV0FBVyxFQUFFLENBQUM7UUFDdkIsQ0FBQyxDQUFDLENBQUM7SUFDUCxDQUFDO0lBRUQsaUJBQWlCLENBQUMsTUFBVztRQUN6QixNQUFNLElBQUksS0FBSyxDQUFDLGdEQUFnRCxDQUFDLENBQUM7SUFDdEUsQ0FBQztJQUVELEtBQUssQ0FBQyxXQUFXO1FBQ2IsSUFBSSxJQUFJLENBQUMsVUFBVSxFQUFFLE9BQU8sRUFBRTtZQUMxQixNQUFNLElBQUksQ0FBQyxNQUFNLENBQUMsUUFBUSxDQUFDO2dCQUN2QjtvQkFDSSxPQUFPLEVBQUUsSUFBSSxDQUFDLFVBQVUsQ0FBQyxPQUFPO2lCQUNuQzthQUNKLENBQUMsQ0FBQztTQUNOO1FBQ0QsSUFBSSxDQUFDLFNBQVMsRUFBRSxLQUFLLEVBQUUsQ0FBQztJQUM1QixDQUFDOzsrR0E5QlEsa0JBQWtCO21IQUFsQixrQkFBa0IsY0FGZixNQUFNOzJGQUVULGtCQUFrQjtrQkFIOUIsVUFBVTttQkFBQztvQkFDUixVQUFVLEVBQUUsTUFBTTtpQkFDckIiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBDb21wb25lbnRUeXBlIH0gZnJvbSBcIkBhbmd1bGFyL2Nkay9wb3J0YWxcIjtcclxuaW1wb3J0IHsgSW5qZWN0YWJsZSB9IGZyb20gXCJAYW5ndWxhci9jb3JlXCI7XHJcbmltcG9ydCB7IE1hdERpYWxvZywgTWF0RGlhbG9nUmVmIH0gZnJvbSBcIkBhbmd1bGFyL21hdGVyaWFsL2RpYWxvZ1wiO1xyXG5pbXBvcnQgeyBSb3V0ZXIgfSBmcm9tIFwiQGFuZ3VsYXIvcm91dGVyXCI7XHJcbmltcG9ydCB7IFNtYXJ0RGlhbG9nRGF0YSB9IGZyb20gXCIuL3NtYXJ0ZGlhbG9nLm1vZGVsXCI7XHJcblxyXG5ASW5qZWN0YWJsZSh7XHJcbiAgICBwcm92aWRlZEluOiBcInJvb3RcIixcclxufSlcclxuZXhwb3J0IGNsYXNzIFNtYXJ0ZGlhbG9nU2VydmljZSB7XHJcbiAgICBkaWFsb2dSZWY/OiBNYXREaWFsb2dSZWY8YW55LCBhbnk+O1xyXG4gICAgZGlhbG9nRGF0YT86IFNtYXJ0RGlhbG9nRGF0YTtcclxuXHJcbiAgICBjb25zdHJ1Y3Rvcihwcml2YXRlIGRpYWxvZzogTWF0RGlhbG9nLCBwcml2YXRlIHJvdXRlcjogUm91dGVyKSB7fVxyXG5cclxuICAgIGNyZWF0ZURpYWxvZyhzbWFydERpYWxvZzogU21hcnREaWFsb2dEYXRhLCBjb21wb25lbnQ6IENvbXBvbmVudFR5cGU8YW55Pik6IHZvaWQge1xyXG4gICAgICAgIHRoaXMuZGlhbG9nRGF0YSA9IHNtYXJ0RGlhbG9nO1xyXG5cclxuICAgICAgICB0aGlzLmRpYWxvZ1JlZiA9IHRoaXMuZGlhbG9nLm9wZW4oY29tcG9uZW50LCB7IGRhdGE6IHRoaXMuZGlhbG9nRGF0YSB9KTtcclxuXHJcbiAgICAgICAgdGhpcy5kaWFsb2dSZWYuYWZ0ZXJDbG9zZWQoKS5zdWJzY3JpYmUoKHJlc3VsdCkgPT4ge1xyXG4gICAgICAgICAgICB0aGlzLmhhbmRsZUFmdGVyQ2xvc2VkKHJlc3VsdCk7XHJcbiAgICAgICAgICAgIHRoaXMuY2xvc2VEaWFsb2coKTtcclxuICAgICAgICB9KTtcclxuICAgIH1cclxuXHJcbiAgICBoYW5kbGVBZnRlckNsb3NlZChyZXN1bHQ6IGFueSk6IHZvaWQge1xyXG4gICAgICAgIHRocm93IG5ldyBFcnJvcihcImhhbmRsZUFmdGVyQ2xvc2VkIGZ1bmN0aW9uIGlzIG5vdCBpbXBsZW1lbnRlZCFcIik7XHJcbiAgICB9XHJcblxyXG4gICAgYXN5bmMgY2xvc2VEaWFsb2coKTogUHJvbWlzZTx2b2lkPiB7XHJcbiAgICAgICAgaWYgKHRoaXMuZGlhbG9nRGF0YT8ub3V0bGV0cykge1xyXG4gICAgICAgICAgICBhd2FpdCB0aGlzLnJvdXRlci5uYXZpZ2F0ZShbXHJcbiAgICAgICAgICAgICAgICB7XHJcbiAgICAgICAgICAgICAgICAgICAgb3V0bGV0czogdGhpcy5kaWFsb2dEYXRhLm91dGxldHMsXHJcbiAgICAgICAgICAgICAgICB9LFxyXG4gICAgICAgICAgICBdKTtcclxuICAgICAgICB9XHJcbiAgICAgICAgdGhpcy5kaWFsb2dSZWY/LmNsb3NlKCk7XHJcbiAgICB9XHJcbn1cclxuIl19