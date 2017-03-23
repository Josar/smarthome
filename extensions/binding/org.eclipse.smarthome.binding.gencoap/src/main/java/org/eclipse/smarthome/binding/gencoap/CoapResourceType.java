package org.eclipse.smarthome.binding.gencoap;

import static org.eclipse.smarthome.binding.gencoap.GenCoAPBindingConstants.*;

import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;

/**
 * CoAP Resource Types enum used for discovery of thing features
 *
 * Specification used:
 * https://openconnectivity.org/specs/OIC_Resource_Type_Specification_v1.1.0.pdf
 *
 * @author Stefan Oberdörfer - Initial contribution
 */

public enum CoapResourceType {
    SENSOR_ACCELERATION("oic.r.sensor.acceleration", IT_NUMBER, CHANNEL_TYPE_NUM_SENSOR),
    BINARY_SWITCH("oic.r.switch.binary", IT_SWITCH, CHANNEL_TYPE_SWITCH),
    LOCK("oic.r.lock.status", IT_SWITCH, CHANNEL_TYPE_SWITCH),
    SENSOR_ILLUMINANCE("oic.r.sensor.illuminance", IT_NUMBER, CHANNEL_TYPE_NUM_SENSOR),
    SENSOR_COUNTER("oic.r.sensor.activity.count", IT_NUMBER, CHANNEL_TYPE_NUM_SENSOR),
    SENSOR_GENERIC("oic.r.sensor", IT_STRING, CHANNEL_TYPE_TEXT_SENSOR),
    LIGHT_DIMMER("oic.r.light.dimming", IT_PERCENT, CHANNEL_TYPE_PERCENT),
    PUSHBUTTON("oic.r.button", IT_SWITCH, CHANNEL_TYPE_SWITCH);

    /*
     * Other (unimplemented) resourcetypes:
     *
     * oic.r.altimeter
     * oic.r.sensor.atmosphericpressure
     * oic.r.airflowcontrol
     * oic.r.audio
     * oic.r.autofocus
     * oic.r.automaticdocumentfeeder
     * oic.r.colour.autowhitebalance
     * oic.r.energy.battery
     * oic.r.light.brightness
     * oic.r.sensor.carbondioxide
     * oic.r.sensor.carbonmonoxide
     * oic.r.clock
     * oic.r.colour.chroma
     * oic.r.colour.rgb
     * oic.r.colour.saturation
     * oic.r.sensor.contact
     * oic.r.energy.drlc
     * oic.r.light.dimming
     * oic.r.door
     * oic.r.energy.consumption
     * oic.r.energy.overload
     * oic.r.energy.usage
     * oic.r.sensor.geolocation
     * oic.r.sensor.glassbreak
     * oic.r.sensor.heart.zone
     * oic.r.height
     * oic.r.humidity
     * oic.r.icemaker
     * oic.r.lock.code
     * oic.r.sensor.magneticfielddirection
     * oic.r.media
     * oic.r.media.input
     * oic.r.media.output
     * oic.r.mode
     * oic.r.movement.linear
     * oic.r.sensor.motion
     * oic.r.nightmode
     * oic.r.openlevel
     * oic.r.operational.state
     * oic.r.ptz
     * oic.r.sensor.presence
     * oic.r.light.ramptime
     * oic.r.refrigeration
     * oic.r.signalstrength
     * oic.r.sensor.sleep
     * oic.r.sensor.smoke
     * oic.r.speech.tts
     * oic.r.temperature
     * oic.r.sensor.threeaxis
     * oic.r.time.period
     * oic.r.sensor.touch
     * oic.r.sensor.radiation.uv
     * oic.r.sensor.water
     * oic.r.weight
     */

    public String identifier;
    public String itemtype;
    public ChannelTypeUID channeltype;

    private static CoapResourceType[] values = values();

    private CoapResourceType(String identifier, String itemtype, ChannelTypeUID channeltype) {
        this.identifier = identifier;
        this.itemtype = itemtype;
        this.channeltype = channeltype;
    }

    public static CoapResourceType fromIdentifier(String identifier) {
        for (CoapResourceType type : values) {
            if (type.identifier.equals(identifier)) {
                return type;
            }
        }
        return null;
    }
}
