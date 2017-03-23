/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.gencoap.internal;

import static org.eclipse.smarthome.binding.gencoap.GenCoAPBindingConstants.COAP_THING_TYPE;

import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.binding.gencoap.handler.GenCoAPHandler;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;

/**
 * The {@link GenCoAPHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Stefan Oberdörfer - Initial contribution
 */
public class GenCoAPHandlerFactory extends BaseThingHandlerFactory {

    private final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(COAP_THING_TYPE);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(COAP_THING_TYPE)) {
            return new GenCoAPHandler(thing);
        }

        return null;
    }
}
