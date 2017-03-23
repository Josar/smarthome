/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.smarthome.binding.gencoap.handler;

import static org.eclipse.californium.core.coap.MediaTypeRegistry.TEXT_PLAIN;
import static org.eclipse.smarthome.binding.gencoap.GenCoAPBindingConstants.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
import org.eclipse.smarthome.binding.gencoap.CoapResourceType;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.builder.ChannelBuilder;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link CoAPHandler} is responsible for dynamically discovering
 * the features of a device running a CoAP server. It checks the resourcetypes
 * of the CoAP endpoints and adds corresponding channels to the Thing.
 * It subsequently handles refreshing data from sensor endpoints and sends user
 * commands to controllable/writeable endpoints.
 *
 * @author Stefan Oberdoerfer - Initial contribution
 */
public class GenCoAPHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(GenCoAPHandler.class);

    private CoapClient coapClient = new CoapClient();
    private ScheduledFuture<?> refreshSensorsJob;
    private BigDecimal refreshInterval = new BigDecimal(15);
    private String BASE_URI;

    public GenCoAPHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {

        // Get the device properties
        Map<String, String> deviceProperties = editProperties();

        // Device config overrides properties
        String configIP = (String) this.getConfig().get(KEY_CONFIG_IPADDRESS);
        String configPort = (String) this.getConfig().get(KEY_CONFIG_PORT);

        if (configIP != null && configPort != null && !configIP.isEmpty() && !configPort.isEmpty()) {
            deviceProperties.put(PROPERTY_IPADDRESS, configIP);
            deviceProperties.put(PROPERTY_PORT, configPort);
            updateProperties(deviceProperties);
        }

        String coap_server_ip_adr = deviceProperties.get(PROPERTY_IPADDRESS);
        String coap_server_port = deviceProperties.get(PROPERTY_PORT);

        if (coap_server_ip_adr == null || coap_server_ip_adr.isEmpty() || coap_server_port == null
                || coap_server_port.isEmpty()) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Can not access device as the ip address or port number is not set (NULL)");
            return;
        } else {
            // TODO check if ipv6 or ipv4
            BASE_URI = "coap://[" + coap_server_ip_adr + "]:" + coap_server_port;
        }

        updateStatus(ThingStatus.ONLINE);

        List<Channel> channelList = this.getThing().getChannels();
        if (channelList.isEmpty()) {
            // discover device functionality
            channelList = doChannelAutodiscovery();

            ThingBuilder thingBuilder = editThing();
            thingBuilder.withChannels(channelList);
            updateThing(thingBuilder.build());
        } else {
            // check found functionality
            for (Channel channel : channelList) {
                if (!channelIsAvailable(channel)) {
                    updateStatus(ThingStatus.OFFLINE);
                    break;
                }
            }
        }
    }

    @Override
    public void channelLinked(ChannelUID channelUID) {
        Channel channel = getThing().getChannel(channelUID.getId());
        switch (channel.getChannelTypeUID().getId()) {
            case SWITCH_CHANNEL:
                syncSwitchState(getThing().getChannel(channelUID.getId()));
                break;
            case PERCENT_CHANNEL:
                syncDimmerState(getThing().getChannel(channelUID.getId()));
                break;
            case TEXT_SENSOR_CHANNEL:
            case NUM_SENSOR_CHANNEL:
                if (refreshSensorsJob == null) {
                    startSensorRefresh();
                }
                break;

        }
    }

    @Override
    public void channelUnlinked(ChannelUID channelUID) {
        String typeID = getThing().getChannel(channelUID.getId()).getChannelTypeUID().getId();
        if (typeID.equals(TEXT_SENSOR_CHANNEL) || typeID.equals(NUM_SENSOR_CHANNEL)) {
            boolean foundSensorChannel = false;
            for (Channel channel : getThing().getChannels()) {
                if (channel.getUID().getId().equals(TEXT_SENSOR_CHANNEL)
                        || channel.getUID().getId().equals(NUM_SENSOR_CHANNEL)) {
                    foundSensorChannel = true;
                    break;
                }
            }

            if (!foundSensorChannel) {
                refreshSensorsJob.cancel(true);
                refreshSensorsJob = null;
            }
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        Channel channel = getThing().getChannel(channelUID.getId());
        logger.debug("handleCommand " + command + " for channelUID " + channel.getChannelTypeUID().getId());

        switch (channel.getChannelTypeUID().getId()) {
            case SWITCH_CHANNEL:
                if (command instanceof OnOffType) {
                    OnOffType onOffCommand = ((OnOffType) command);
                    setSwitch(onOffCommand, (String) channel.getConfiguration().get("path"));
                }
                break;
            case PERCENT_CHANNEL:
                if (command instanceof OnOffType) {
                    OnOffType onOffCommand = (OnOffType) command;
                    setDimmer(onOffCommand == OnOffType.ON ? new BigDecimal(100) : new BigDecimal(0),
                            (String) channel.getConfiguration().get("path"));
                } else if (command instanceof PercentType) {
                    PercentType percentCommand = (PercentType) command;
                    setDimmer(percentCommand.toBigDecimal(), (String) channel.getConfiguration().get("path"));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void dispose() {
        if (refreshSensorsJob != null) {
            refreshSensorsJob.cancel(true);
        }
        super.dispose();
    }

    private List<Channel> doChannelAutodiscovery() {
        coapClient.setURI(BASE_URI);

        List<Channel> results = new LinkedList<Channel>();
        List<String> channelNames = new ArrayList<>();
        Set<WebLink> weblinks = coapClient.discover();

        if (weblinks != null) {

            for (WebLink link : weblinks) {
                List<String> rts = link.getAttributes().getResourceTypes();

                // check for first supported resourceType
                for (String rt : rts) {
                    logger.debug("Found resource: " + rt);
                    CoapResourceType resourceType = CoapResourceType.fromIdentifier(rt);
                    if (resourceType != null) {

                        // create new channel
                        Configuration config = new Configuration();
                        config.put("path", link.getURI());

                        // RFC 5988: set channelname to human-readable title identifier
                        String uniqueChannelName = link.getAttributes().getTitle();
                        if (uniqueChannelName != null) {
                            uniqueChannelName = uniqueChannelName.contains(" ") ? uniqueChannelName.replace(" ", "")
                                    : uniqueChannelName;
                        } else {
                            uniqueChannelName = link.getURI().substring(link.getURI().lastIndexOf('/') + 1);
                        }

                        // check for duplicates
                        while (channelNames.contains(uniqueChannelName)) {
                            uniqueChannelName = uniqueChannelName + "-" + channelNames.size();
                        }

                        channelNames.add(uniqueChannelName);
                        Channel channel = ChannelBuilder
                                .create(new ChannelUID(this.getThing().getUID(), uniqueChannelName),
                                        resourceType.itemtype)
                                .withType(resourceType.channeltype).withConfiguration(config)
                                .withLabel(uniqueChannelName).build();
                        results.add(channel);
                        logger.debug("adding channel with Label " + uniqueChannelName);
                        break;
                    }
                }
            }
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Cannot identify the device functionality. Well-Known Core Discovery failed. Please remove the Thing and re-scan the device.");
        }
        return results;
    }

    private void syncSwitchState(Channel channel) {
        String path = (String) channel.getConfiguration().get("path");
        coapClient.setURI(BASE_URI + path);
        coapClient.get(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                if (response != null && response.isSuccess()) {
                    logger.debug("coap client syncing state: " + response.getResponseText());
                    switch (response.getResponseText()) {
                        case "0":
                            updateState(channel.getUID(), OnOffType.OFF);
                            break;
                        case "1":
                            updateState(channel.getUID(), OnOffType.ON);
                            break;
                    }
                }
            }

            @Override
            public void onError() {
                logger.error("coap client could not sync state");
            }
        });
    }

    private void syncDimmerState(Channel channel) {
        String path = (String) channel.getConfiguration().get("path");
        coapClient.setURI(BASE_URI + path);
        coapClient.get(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                if (response != null && response.isSuccess()) {
                    logger.debug("coap client syncing state: " + response.getResponseText());

                    String payload = response.getResponseText();
                    payload = payload.contains(",") ? payload.replace(",", ".") : payload;
                    State state = new PercentType(new BigDecimal(payload));
                    updateState(channel.getUID(), state);
                }
            }

            @Override
            public void onError() {
                logger.error("coap client could not sync state");
            }
        });
    }

    private boolean channelIsAvailable(Channel channel) {
        String path = (String) channel.getConfiguration().get("path");
        coapClient.setURI(BASE_URI + path);
        return coapClient.ping();
    }

    private void getSensorData() {
        for (Channel channel : getThing().getChannels()) {
            switch (channel.getChannelTypeUID().getId()) {
                case TEXT_SENSOR_CHANNEL:
                    updateSensorChannel(channel, false);
                    break;
                case NUM_SENSOR_CHANNEL:
                    updateSensorChannel(channel, true);
                    break;
                default:
                    break;
            }
        }
    }

    private void startSensorRefresh() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    getSensorData();
                } catch (Exception e) {
                    logger.debug("Exception occurred during execution: {}", e.getMessage(), e);
                }
            }
        };

        refreshSensorsJob = scheduler.scheduleAtFixedRate(runnable, 0, refreshInterval.intValue(), TimeUnit.SECONDS);
    }

    private void updateSensorChannel(Channel channel, boolean isNumeric) {
        String path = (String) channel.getConfiguration().get("path");
        coapClient.setURI(BASE_URI + path);
        coapClient.get(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                if (response != null && response.isSuccess()) {
                    String value = response.getResponseText();

                    if (value != null && !value.trim().isEmpty()) {
                        State state;
                        if (isNumeric) {
                            value = value.contains(",") ? value.replace(",", ".") : value;
                            state = new DecimalType(new BigDecimal(value));
                        } else {
                            state = new StringType(value);
                        }
                        updateState(channel.getUID(), state);
                    }
                }
            }

            @Override
            public void onError() {
                logger.error("could not get sensor value for " + channel.getUID().toString());

            }
        }, TEXT_PLAIN);

    }

    private void setSwitch(OnOffType command, String path) {
        coapClient.setURI(BASE_URI + path);

        StringBuilder payload = new StringBuilder();
        switch (command) {
            case ON:
                payload.append("1");
                break;
            case OFF:
                payload.append("0");
                break;
        }

        coapClient.post(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                if (response != null && response.isSuccess()) {
                    logger.debug("" + response.getResponseText());
                }
            }

            @Override
            public void onError() {
                logger.error("coap client got no success response");

            }
        }, payload.toString(), TEXT_PLAIN);
    }

    private void setDimmer(BigDecimal value, String path) {
        coapClient.setURI(BASE_URI + path);

        String payload = value.toString();

        coapClient.post(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                if (response != null && response.isSuccess()) {
                    logger.debug("" + response.getResponseText());
                }
            }

            @Override
            public void onError() {
                logger.error("coap client got no success response");
            }
        }, payload, TEXT_PLAIN);
    }
}
