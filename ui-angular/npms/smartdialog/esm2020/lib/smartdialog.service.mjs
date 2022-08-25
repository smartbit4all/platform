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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnRkaWFsb2cuc2VydmljZS5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3Byb2plY3RzL3NtYXJ0ZGlhbG9nL3NyYy9saWIvc21hcnRkaWFsb2cuc2VydmljZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFDQSxPQUFPLEVBQUUsVUFBVSxFQUFFLE1BQU0sZUFBZSxDQUFDOzs7O0FBUTNDLE1BQU0sT0FBTyxrQkFBa0I7SUFJM0IsWUFBb0IsTUFBaUIsRUFBVSxNQUFjO1FBQXpDLFdBQU0sR0FBTixNQUFNLENBQVc7UUFBVSxXQUFNLEdBQU4sTUFBTSxDQUFRO0lBQUcsQ0FBQztJQUVqRSxZQUFZLENBQUMsV0FBNEIsRUFBRSxTQUE2QjtRQUNwRSxJQUFJLENBQUMsVUFBVSxHQUFHLFdBQVcsQ0FBQztRQUU5QixJQUFJLENBQUMsU0FBUyxHQUFHLElBQUksQ0FBQyxNQUFNLENBQUMsSUFBSSxDQUFDLFNBQVMsRUFBRSxFQUFFLElBQUksRUFBRSxJQUFJLENBQUMsVUFBVSxFQUFFLENBQUMsQ0FBQztRQUV4RSxJQUFJLENBQUMsU0FBUyxDQUFDLFdBQVcsRUFBRSxDQUFDLFNBQVMsQ0FBQyxDQUFDLE1BQU0sRUFBRSxFQUFFO1lBQzlDLElBQUksQ0FBQyxpQkFBaUIsQ0FBQyxNQUFNLENBQUMsQ0FBQztZQUMvQixJQUFJLENBQUMsV0FBVyxFQUFFLENBQUM7UUFDdkIsQ0FBQyxDQUFDLENBQUM7SUFDUCxDQUFDO0lBRUQsaUJBQWlCLENBQUMsTUFBVztRQUN6QixNQUFNLElBQUksS0FBSyxDQUFDLGdEQUFnRCxDQUFDLENBQUM7SUFDdEUsQ0FBQztJQUVELEtBQUssQ0FBQyxXQUFXO1FBQ2IsSUFBSSxJQUFJLENBQUMsVUFBVSxFQUFFLE9BQU8sRUFBRTtZQUMxQixNQUFNLElBQUksQ0FBQyxNQUFNLENBQUMsUUFBUSxDQUFDO2dCQUN2QjtvQkFDSSxPQUFPLEVBQUUsSUFBSSxDQUFDLFVBQVUsQ0FBQyxPQUFPO2lCQUNuQzthQUNKLENBQUMsQ0FBQztTQUNOO1FBQ0QsSUFBSSxDQUFDLFNBQVMsRUFBRSxLQUFLLEVBQUUsQ0FBQztJQUM1QixDQUFDOzsrR0E5QlEsa0JBQWtCO21IQUFsQixrQkFBa0IsY0FGZixNQUFNOzJGQUVULGtCQUFrQjtrQkFIOUIsVUFBVTttQkFBQztvQkFDUixVQUFVLEVBQUUsTUFBTTtpQkFDckIiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBDb21wb25lbnRUeXBlIH0gZnJvbSBcIkBhbmd1bGFyL2Nkay9wb3J0YWxcIjtcbmltcG9ydCB7IEluamVjdGFibGUgfSBmcm9tIFwiQGFuZ3VsYXIvY29yZVwiO1xuaW1wb3J0IHsgTWF0RGlhbG9nLCBNYXREaWFsb2dSZWYgfSBmcm9tIFwiQGFuZ3VsYXIvbWF0ZXJpYWwvZGlhbG9nXCI7XG5pbXBvcnQgeyBSb3V0ZXIgfSBmcm9tIFwiQGFuZ3VsYXIvcm91dGVyXCI7XG5pbXBvcnQgeyBTbWFydERpYWxvZ0RhdGEgfSBmcm9tIFwiLi9zbWFydGRpYWxvZy5tb2RlbFwiO1xuXG5ASW5qZWN0YWJsZSh7XG4gICAgcHJvdmlkZWRJbjogXCJyb290XCIsXG59KVxuZXhwb3J0IGNsYXNzIFNtYXJ0ZGlhbG9nU2VydmljZSB7XG4gICAgZGlhbG9nUmVmPzogTWF0RGlhbG9nUmVmPGFueSwgYW55PjtcbiAgICBkaWFsb2dEYXRhPzogU21hcnREaWFsb2dEYXRhO1xuXG4gICAgY29uc3RydWN0b3IocHJpdmF0ZSBkaWFsb2c6IE1hdERpYWxvZywgcHJpdmF0ZSByb3V0ZXI6IFJvdXRlcikge31cblxuICAgIGNyZWF0ZURpYWxvZyhzbWFydERpYWxvZzogU21hcnREaWFsb2dEYXRhLCBjb21wb25lbnQ6IENvbXBvbmVudFR5cGU8YW55Pik6IHZvaWQge1xuICAgICAgICB0aGlzLmRpYWxvZ0RhdGEgPSBzbWFydERpYWxvZztcblxuICAgICAgICB0aGlzLmRpYWxvZ1JlZiA9IHRoaXMuZGlhbG9nLm9wZW4oY29tcG9uZW50LCB7IGRhdGE6IHRoaXMuZGlhbG9nRGF0YSB9KTtcblxuICAgICAgICB0aGlzLmRpYWxvZ1JlZi5hZnRlckNsb3NlZCgpLnN1YnNjcmliZSgocmVzdWx0KSA9PiB7XG4gICAgICAgICAgICB0aGlzLmhhbmRsZUFmdGVyQ2xvc2VkKHJlc3VsdCk7XG4gICAgICAgICAgICB0aGlzLmNsb3NlRGlhbG9nKCk7XG4gICAgICAgIH0pO1xuICAgIH1cblxuICAgIGhhbmRsZUFmdGVyQ2xvc2VkKHJlc3VsdDogYW55KTogdm9pZCB7XG4gICAgICAgIHRocm93IG5ldyBFcnJvcihcImhhbmRsZUFmdGVyQ2xvc2VkIGZ1bmN0aW9uIGlzIG5vdCBpbXBsZW1lbnRlZCFcIik7XG4gICAgfVxuXG4gICAgYXN5bmMgY2xvc2VEaWFsb2coKTogUHJvbWlzZTx2b2lkPiB7XG4gICAgICAgIGlmICh0aGlzLmRpYWxvZ0RhdGE/Lm91dGxldHMpIHtcbiAgICAgICAgICAgIGF3YWl0IHRoaXMucm91dGVyLm5hdmlnYXRlKFtcbiAgICAgICAgICAgICAgICB7XG4gICAgICAgICAgICAgICAgICAgIG91dGxldHM6IHRoaXMuZGlhbG9nRGF0YS5vdXRsZXRzLFxuICAgICAgICAgICAgICAgIH0sXG4gICAgICAgICAgICBdKTtcbiAgICAgICAgfVxuICAgICAgICB0aGlzLmRpYWxvZ1JlZj8uY2xvc2UoKTtcbiAgICB9XG59XG4iXX0=