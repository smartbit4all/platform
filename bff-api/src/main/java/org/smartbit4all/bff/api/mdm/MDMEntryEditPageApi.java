package org.smartbit4all.bff.api.mdm;

import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;

/**
 * 
 * This is the default page api with dynamically generated layout and action. If we have an average
 * master data entry with normal properties and references to other entries then we can use this
 * page. It is activated when we do not set any specific view in the {@link MDMEntryDescriptor}. All
 * properties of the given object and its contained references appear on the editor page.
 * Additionally all the references let it be {@link ReferencePropertyKind#REFERENCE},
 * {@link ReferencePropertyKind#LIST} or {@link ReferencePropertyKind#MAP} will be selectors for the
 * given master data entry.
 * 
 * Right now the page doesn't manage the references point out from the MDM subsystem.
 * 
 * @author Peter Boros
 */
public interface MDMEntryEditPageApi {

}
