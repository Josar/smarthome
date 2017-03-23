/**
 * Copyright (c) 2014-2017 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.gencoap;

import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;

/**
 * The {@link GenCoAPBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Stefan Oberdoerfer - Initial contribution
 */
public class GenCoAPBindingConstants {

    public static final String BINDING_ID = "gencoap";

    // Generic CoAP thing type
    public final static ThingTypeUID COAP_THING_TYPE = new ThingTypeUID(BINDING_ID, "node");

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(COAP_THING_TYPE);

    // Thing Config Keys
    public final static String KEY_CONFIG_IPADDRESS = "ipAddress";
    public final static String KEY_CONFIG_PORT = "portNumber";

    // Thing Property Keys
    public final static String PROPERTY_IPADDRESS = "IP Adress";
    public final static String PROPERTY_PORT = "Port Number";
    public final static String PROPERTY_ITEMTYPE = "Item Type";

    // List of itemtypes
    public final static String IT_SWITCH = "Switch";
    public final static String IT_STRING = "String";
    public final static String IT_NUMBER = "Number";
    public final static String IT_COLOR = "Color";
    public final static String IT_CONTACT = "Contact";
    public final static String IT_PERCENT = "Dimmer";

    // List of channeltype ids
    public final static String SWITCH_CHANNEL = "switch";
    public final static String TEXT_SENSOR_CHANNEL = "text-sensor";
    public final static String COLOR_CHANNEL = "color";
    public final static String CONTACT_CHANNEL = "contact";
    public final static String PERCENT_CHANNEL = "percent";
    public final static String NUM_SENSOR_CHANNEL = "number-sensor";
    public final static String BUTTON_CHANNEL = "rawbutton";

    // List of channeltype uids
    public static final ChannelTypeUID CHANNEL_TYPE_SWITCH = new ChannelTypeUID(BINDING_ID, SWITCH_CHANNEL);
    public static final ChannelTypeUID CHANNEL_TYPE_TEXT_SENSOR = new ChannelTypeUID(BINDING_ID, TEXT_SENSOR_CHANNEL);
    public static final ChannelTypeUID CHANNEL_TYPE_COLOR = new ChannelTypeUID(BINDING_ID, COLOR_CHANNEL);
    public static final ChannelTypeUID CHANNEL_TYPE_CONTACT = new ChannelTypeUID(BINDING_ID, CONTACT_CHANNEL);
    public static final ChannelTypeUID CHANNEL_TYPE_PERCENT = new ChannelTypeUID(BINDING_ID, PERCENT_CHANNEL);
    public static final ChannelTypeUID CHANNEL_TYPE_NUM_SENSOR = new ChannelTypeUID(BINDING_ID, NUM_SENSOR_CHANNEL);
    public static final ChannelTypeUID CHANNEL_TYPE_BUTTON = new ChannelTypeUID(BINDING_ID, BUTTON_CHANNEL);

}
