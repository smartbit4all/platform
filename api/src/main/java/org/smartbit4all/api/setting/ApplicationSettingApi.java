package org.smartbit4all.api.setting;

import java.util.Optional;

/**
 * This api is responsible for managing the application level settings. Every module can define one
 * or more api to expose setting options. This manager collects all of them and manages their life
 * cycle. The setting options could have different behavior. Some of them are just option values
 * that must be set to the given variable. Later on the application will use this variable from the
 * api instance. The value based option can be exposed by any api that extends the HasOption marker
 * interface. It will force the ApplicationSettingApi to analyze the api and collect the settings
 * from this api. The value based options can use the Validation annotations on the bean properties
 * to ensure the valid values and define the error messages. (The messages will be localized later
 * on!)
 * <ul>
 * <li><b>Value: Enable - disable</b> - we have a variable in our api and this variable is
 * modifiable by the user. Looks like a simple option. If it's {@link Optional} then it has a third
 * (unknown) state.</li>
 * <li><b>Value: Select from a value list</b> - we have a variable in our api and this variable is
 * modifiable by the user. Looks like a simple selection. If it's defined as an {@link Optional}
 * then it can be unselected else it must have value. --> The application fails to start without
 * this value.</li>
 * <li><b>Value: Numeric values</b> - we have a variable in our api (double, int or long) and this
 * variable is modifiable by the user. This is an input field. The optional is working here as
 * well.</li>
 * <li><b>Value: Local depending resource</b> - we have a variable in our api (double, int or long)
 * and this variable is modifiable by the user. This is an input field. The optional is working here
 * as well.</li>
 * </ul>
 * 
 * @author Peter Boros
 */
public interface ApplicationSettingApi {

}
