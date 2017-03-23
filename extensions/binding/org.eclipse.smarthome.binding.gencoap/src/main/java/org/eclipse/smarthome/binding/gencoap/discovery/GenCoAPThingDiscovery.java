/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.smarthome.binding.gencoap.discovery;

import static org.eclipse.smarthome.binding.gencoap.GenCoAPBindingConstants.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jacob.CborDecoder;

/**
 * The {@link GenCoAPThingDiscovery} is responsible for discovering CoAP-Servers
 * by listening for UDP-broadcasts containing a special payload-prefix.
 *
 * @author Stefan Oberdoerfer - Initial contribution
 * @author Tom Nordloh - Initial contribution
 */

public class GenCoAPThingDiscovery extends AbstractDiscoveryService {
    private Logger logger = LoggerFactory.getLogger(GenCoAPThingDiscovery.class);

    static boolean discoveryRunning = false;
    private final int UDP_PORT = 65527;
    private final String beduinoPrefix = "beduino";

    /** The refresh interval for discovery devices */
    private long refreshInterval = 60;
    private ScheduledFuture<?> discoveryJob;
    private Runnable discoveryRunnable = new Runnable() {
        @Override
        public void run() {
            receiveDiscoveryMessage();
        }
    };

    public GenCoAPThingDiscovery() throws IllegalArgumentException {
        super(SUPPORTED_THING_TYPES_UIDS, 10);
    }

    @Override
    protected void startBackgroundDiscovery() {
        if (discoveryJob == null || discoveryJob.isCancelled()) {
            discoveryJob = scheduler.scheduleAtFixedRate(discoveryRunnable, 0, refreshInterval, TimeUnit.SECONDS);
        }
    }

    @Override
    protected void stopBackgroundDiscovery() {
        if (discoveryJob != null && !discoveryJob.isCancelled()) {
            discoveryJob.cancel(true);
            discoveryJob = null;
        }
    }

    @Override
    protected void startScan() {
        scheduler.scheduleAtFixedRate(discoveryRunnable, 0, refreshInterval, TimeUnit.SECONDS);
    }

    private void receiveDiscoveryMessage() {

        DatagramSocket socket = null;

        try {
            discoveryRunning = true;
            socket = new DatagramSocket(UDP_PORT);
            socket.setReuseAddress(true);
            socket.setSoTimeout(30000);

            while (discoveryRunning) {
                // Wait for a response
                byte[] recvBuf = new byte[1500];
                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(receivePacket);

                // We have a response
                byte[] receivedData = receivePacket.getData();
                logger.debug("Broadcast response from {}", receivePacket.getAddress());

                discoverCoAP(receivePacket.getAddress(), receivedData);

            }
        } catch (SocketTimeoutException e) {
            logger.trace("No further response");
            discoveryRunning = false;
        } catch (IOException e) {
            logger.debug("IO error during discovery: {}", e.getMessage());
            discoveryRunning = false;
        } finally {
            // Close the socket!
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
                logger.debug(e.toString());
            }
        }
    }

    /**
     * Disassemble the received datagrampacket with cbor add check
     * if the payload matches the beduino format.
     *
     * If discovery was successful a generic CoAP thing with IP-address
     * and port as properties is added to the inbox.
     */
    private void discoverCoAP(InetAddress address, byte[] receivedData) {

        ByteArrayInputStream is = new ByteArrayInputStream(receivedData);

        String prefix = "";
        String mac = null;
        int port = 0;
        try {
            logger.debug("decoding cbor formatted message");
            CborDecoder decoder = new CborDecoder(new PushbackInputStream(is));
            decoder.readArrayLength();
            prefix = decoder.readTextString();
            mac = decoder.readTextString();
            port = decoder.readInt16();
            decoder.readBreak();
        } catch (IOException | IllegalArgumentException e) {
            logger.debug("ERROR: failed to decode cbor formatted message");
            e.printStackTrace();
        }

        // check if message is a coap message
        if (prefix.equals(beduinoPrefix)) {

            logger.debug("CoAP device found on network");
            logger.debug("Found at  : {}", address.getHostAddress());

            // test for values that are unset but needed
            if (mac != null && mac != "" && port != 0) {

                String ipAddress = address.getHostAddress();
                if (ipAddress.contains("%")) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf("%"));
                }

                ThingUID thingUID = new ThingUID(COAP_THING_TYPE, mac.replace(":", ""));

                Map<String, Object> properties = new HashMap<>(2);
                properties.put(PROPERTY_IPADDRESS, ipAddress);
                properties.put(PROPERTY_PORT, String.valueOf(port));

                DiscoveryResult result = DiscoveryResultBuilder.create(thingUID).withThingType(COAP_THING_TYPE)
                        .withProperties(properties).build();

                thingDiscovered(result);
            } else {
                logger.debug("ERROR: Mac-Adress or Port not set properly!");
            }
        } else {
            logger.debug("ERROR: unknown message received!");
        }
    }
}
