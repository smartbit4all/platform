/**
 * This enum helps to define the action of a dialog.
 *
 * @author Roland Fényes
 */
export var SmartActionType;
(function (SmartActionType) {
    SmartActionType[SmartActionType["ADD"] = 0] = "ADD";
    SmartActionType[SmartActionType["CREATE"] = 1] = "CREATE";
    SmartActionType[SmartActionType["UPDATE"] = 2] = "UPDATE";
    SmartActionType[SmartActionType["REMOVE"] = 3] = "REMOVE";
    SmartActionType[SmartActionType["DELETE"] = 4] = "DELETE";
    SmartActionType[SmartActionType["SAVE"] = 5] = "SAVE";
    SmartActionType[SmartActionType["OK"] = 6] = "OK";
})(SmartActionType || (SmartActionType = {}));
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic21hcnRkaWFsb2cubW9kZWwuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi9wcm9qZWN0cy9zbWFydGRpYWxvZy9zcmMvbGliL3NtYXJ0ZGlhbG9nLm1vZGVsLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQWNBOzs7O0dBSUc7QUFDSCxNQUFNLENBQU4sSUFBWSxlQVFYO0FBUkQsV0FBWSxlQUFlO0lBQ3ZCLG1EQUFHLENBQUE7SUFDSCx5REFBTSxDQUFBO0lBQ04seURBQU0sQ0FBQTtJQUNOLHlEQUFNLENBQUE7SUFDTix5REFBTSxDQUFBO0lBQ04scURBQUksQ0FBQTtJQUNKLGlEQUFFLENBQUE7QUFDTixDQUFDLEVBUlcsZUFBZSxLQUFmLGVBQWUsUUFRMUIiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBTbWFydEZvcm0gfSBmcm9tIFwiQHNtYXJ0Yml0NGFsbC9mb3JtL2xpYi9zbWFydGZvcm0ubW9kZWxcIjtcclxuaW1wb3J0IHsgU21hcnRUYWJsZSB9IGZyb20gXCJAc21hcnRiaXQ0YWxsL3RhYmxlL2xpYi9zbWFydHRhYmxlLm1vZGVsXCI7XHJcblxyXG4vKipcclxuICogVGhpcyBpbnRlcmZhY2UgZGVmaW5lcyB0aGUgc2l6ZSBvZiBhIGRpYWxvZy5cclxuICpcclxuICogQGF1dGhvciBSb2xhbmQgRsOpbnllc1xyXG4gKi9cclxuZXhwb3J0IGludGVyZmFjZSBTbWFydERpYWxvZ1NpemUge1xyXG4gICAgd2lkdGg/OiBudW1iZXI7XHJcbiAgICBoZWlnaHQ/OiBudW1iZXI7XHJcbiAgICBmdWxsU2NyZWVuPzogYm9vbGVhbjtcclxufVxyXG5cclxuLyoqXHJcbiAqIFRoaXMgZW51bSBoZWxwcyB0byBkZWZpbmUgdGhlIGFjdGlvbiBvZiBhIGRpYWxvZy5cclxuICpcclxuICogQGF1dGhvciBSb2xhbmQgRsOpbnllc1xyXG4gKi9cclxuZXhwb3J0IGVudW0gU21hcnRBY3Rpb25UeXBlIHtcclxuICAgIEFERCxcclxuICAgIENSRUFURSxcclxuICAgIFVQREFURSxcclxuICAgIFJFTU9WRSxcclxuICAgIERFTEVURSxcclxuICAgIFNBVkUsXHJcbiAgICBPSyxcclxufVxyXG5cclxuLyoqXHJcbiAqIFRoaXMgaW50ZXJmYWNlIGRlc2NyaWJlcyB0aGUgY29udGVudCBvZiBhIGRpYWxvZy5cclxuICpcclxuICogQGF1dGhvciBSb2xhbmQgRsOpbnllc1xyXG4gKi9cclxuZXhwb3J0IGludGVyZmFjZSBTbWFydENvbnRlbnQge1xyXG4gICAgdGl0bGU6IHN0cmluZztcclxuICAgIGRlc2NyaXB0aW9uPzogc3RyaW5nO1xyXG59XHJcblxyXG4vKipcclxuICogV2l0aCB0aGlzIGludGVyZmFjZSBhbnkga2luZCBvZiBkaWFsb2dzIGNhbiBiZSBlYXNpbHkgY3JlYXRlZC5cclxuICpcclxuICogWW91IG11c3QgY2xvc2UgdGhlIGRpYWxvZyBtYW51YWxseSFcclxuICpcclxuICogQHBhcmFtIHNpemUgUmVxdWlyZWQuIFlvdSBjYW4gZGVmaW5lIHRoZSBkZXNpcmVkIHNpemUgb2YgdGhlIGRpYWxvZ1xyXG4gKiBAcGFyYW0gYWN0aW9uVHlwZSBSZXF1aXJlZC4gVGhlIG1haW4gYWN0aW9uIHR5cGUgb2YgdGhlIGRpYWxvZ1xyXG4gKiBAcGFyYW0gY29udGVudCBSZXF1aXJlZC4gRGVmaW5lcyB0aGUgdGl0bGUgYW5kIHRoZSBkZXNjcmlwdGlvbiBvZiB0aGUgZGlhbG9nXHJcbiAqIEBwYXJhbSBmb3JtIE5vdCByZXF1aXJlZC4gWW91IGNhbiBwcmVzZW50IGEgY3VzdG9tIGFuZCBkeW5hbWljIGZvcm0uXHJcbiAqIEBwYXJhbSB0YWJsZSBOb3QgcmVxdWlyZWQuIFlvdSBjYW4gcHJlc2VudCBhIGN1c3RvbSB0YWJsZS5cclxuICogQHBhcmFtIG9rQ2FsbGJhY2sgTm90IHJlcXVpcmVkLiBBIGN1c3RvbSBjYWxsYmFjayBmdW5jdGlvbi5cclxuICogQHBhcmFtIGNsb3NlQ2FsbGJhY2sgTm90IHJlcXVpcmVkLiBBIGN1c3RvbSBjYWxsYmFjayBmdW5jdGlvbi5cclxuICogQHBhcmFtIGFjdGlvbkNhbGxiYWNrIFJlcXVpcmVkLiBBIGN1c3RvbSBhY3Rpb24gY2FsbGJhY2sgZnVuY3Rpb24uXHJcbiAqIEBwYXJhbSBhY3Rpb25MYWJlbCBSZXF1aXJlZC4gVGhlIG5hbWUgb2YgdGhlIGFjdGlvbiBidXR0b24uXHJcbiAqXHJcbiAqIEBhdXRob3IgUm9sYW5kIEbDqW55ZXNcclxuICovXHJcbmV4cG9ydCBpbnRlcmZhY2UgU21hcnREaWFsb2dEYXRhIHtcclxuICAgIHNpemU6IFNtYXJ0RGlhbG9nU2l6ZTtcclxuICAgIGNvbnRlbnQ6IFNtYXJ0Q29udGVudDtcclxuICAgIGN1c3RvbUNvbXBvbmVudD86IGFueTtcclxuICAgIGN1c3RvbUNvbXBvbmVudElucHV0cz86IGFueTtcclxuICAgIGFjdGlvblR5cGU/OiBTbWFydEFjdGlvblR5cGU7XHJcbiAgICBmb3JtPzogU21hcnRGb3JtO1xyXG4gICAgdGFibGU/OiBTbWFydFRhYmxlPGFueT47XHJcbiAgICBva0NhbGxiYWNrPzogKCkgPT4gdm9pZDtcclxuICAgIGNhbmNlbENhbGxiYWNrPzogKGFyZ3M6IGFueVtdKSA9PiB2b2lkO1xyXG4gICAgY2xvc2VDYWxsYmFjaz86IChhcmdzOiBhbnlbXSkgPT4gdm9pZDtcclxuICAgIGFjdGlvbkNhbGxiYWNrPzogKGFyZ3M6IGFueVtdKSA9PiB2b2lkO1xyXG4gICAgYWN0aW9uTGFiZWw/OiBzdHJpbmc7XHJcbiAgICBvdXRsZXRzPzogYW55O1xyXG59XHJcbiJdfQ==