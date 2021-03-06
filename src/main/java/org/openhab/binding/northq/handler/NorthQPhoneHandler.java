/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.handler;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.northq.NorthQBindingConstants;
import org.openhab.binding.northq.NorthQStringConstants;
import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link NorthQPhoneHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Dan & Nicolaj - Initial contribution
 */

@NonNullByDefault
public class NorthQPhoneHandler extends BaseThingHandler {

    @SuppressWarnings("null")
    private final Logger logger = LoggerFactory.getLogger(NorthQPhoneHandler.class);
    private boolean phoneEnabledStatus;
    public String location = "0";
    public String locationStatus = "home";
    private final byte[] keyValue;

    private ScheduledFuture<?> pollingJob;
    private Runnable pollingRunnable = new Runnable() {

        @Override
        public void run() {
            scheduleCode();
        }
    };

    /**
     * Constructor
     */
    @SuppressWarnings("null")
    public NorthQPhoneHandler(Thing thing) {
        super(thing);
        keyValue = NorthQConfig.getSECRET_KEY().getBytes();
        phoneEnabledStatus = false;
        pollingJob = scheduler.scheduleWithFixedDelay(pollingRunnable, 1, 5, TimeUnit.SECONDS);
    }

    /**
     * Abstract method overwritten
     * Requires:
     * Returns: Initialisation method
     */
    @Override
    public void initialize() {
        updateStatus(ThingStatus.ONLINE);
        createDbUser();
    }

    /**
     * Abstract method overwritten
     * Requires: a channelId and a command
     * Returns: Updates the state of the device
     */
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(NorthQBindingConstants.CHANNEL_QPHONE)) {
            if (command.toString().equals(NorthQStringConstants.ON)) {
                phoneEnabledStatus = true;
            } else if (command.toString().equals(NorthQStringConstants.OFF)) {
                NorthQConfig.setISHOME(true);
                phoneEnabledStatus = false;
            }
        }
    }

    /**
     * Abstract method overwritten
     * Requires:
     * Returns: Scheduled jobs and removes thing
     */
    @Override
    public void handleRemoval() {
        if (pollingJob != null && !pollingJob.isCancelled()) {
            pollingJob.cancel(true);
        }

        removeUserFromDb();
        updateStatus(ThingStatus.REMOVED);
    }

    /**
     * Requires: A cipher text string
     * Returns: The decrypted plain text of text string
     */
    public @Nullable String decrypt(String cipherText) {
        try {
            Cipher AesCipher;
            AesCipher = Cipher.getInstance("AES");
            AesCipher.init(Cipher.DECRYPT_MODE, generateKey());

            return new String(AesCipher.doFinal(Base64.getDecoder().decode(cipherText.getBytes())));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Requires:
     * Returns: generates the AES key
     */
    private Key generateKey() {
        Key key = new SecretKeySpec(keyValue, "AES");
        return key;
    }

    /**
     * Requires:
     * Returns: updates the thing, when run
     */
    private void scheduleCode() {
        logger.debug("Polling data for phone");

        try {
            if (phoneEnabledStatus) {
                String raw = "";
                raw = getGpsDataFromDb(raw);
                if (this.getThing().getStatus() == ThingStatus.OFFLINE) {
                    return;
                }

                if (raw.equals("") && !NorthQConfig.isMOCK()) {
                    return;
                }

                String locationStatus = "0";
                String location = "home";

                if (!NorthQConfig.isMOCK()) {
                    // decrypt and split data
                    String decrypted = decrypt(raw);
                    // 1 or 0 ; home | work
                    String[] data = decrypted.split(";");
                    locationStatus = data[0];
                    location = data[1];
                } else {
                    String[] data = { "1", "Home" };
                    locationStatus = data[0];
                    location = data[1];
                }

                // Set phones status
                boolean resBol;
                if (!NorthQPhoneHandler.this.location.equals(NorthQStringConstants.HOME)
                        && NorthQPhoneHandler.this.locationStatus.equals("1")
                        && !location.equals(NorthQStringConstants.HOME) && locationStatus.equals("0")) {
                    resBol = true;
                } else if (location.equals(NorthQStringConstants.HOME) && locationStatus.equals("1")) {
                    resBol = true;
                } else {
                    resBol = false;
                }

                NorthQPhoneHandler.this.location = location;
                NorthQPhoneHandler.this.locationStatus = locationStatus;

                Boolean isHome = new Boolean(resBol);
                // Updated displayed name
                updateState(NorthQBindingConstants.CHANNEL_QPHONE_GPSLOCATION,
                        StringType.valueOf(isHome ? NorthQStringConstants.HOME : NorthQStringConstants.OUT));

                NorthQConfig.getPHONE_MAP().put(getThing().getConfiguration().get("name").toString(), isHome);
                Boolean[] phoneHome = new Boolean[NorthQConfig.getPHONE_MAP().values().toArray().length];
                NorthQConfig.getPHONE_MAP().values().toArray(phoneHome);

                // Update overall phone status
                boolean allAway = true;
                for (Boolean b : phoneHome) {
                    boolean bol = b.booleanValue();
                    if (bol) {
                        allAway = false;
                    }
                }
                if (phoneEnabledStatus && allAway) {
                    // turn off device
                    NorthQConfig.setISHOME(false);
                } // If home
                else if (phoneEnabledStatus && !allAway) {
                    NorthQConfig.setISHOME(true);
                }
            } else {
                updateState(NorthQBindingConstants.CHANNEL_QPHONE_GPSLOCATION,
                        StringType.valueOf(NorthQStringConstants.INACTIVE));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Requires:
     * Returns: registers a user in the database
     */
    private void createDbUser() {
        Connection conn;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:Mysql://localhost:3306", NorthQConfig.getSQL_USERNAME(),
                    NorthQConfig.getSQL_PASSWORD());
            PreparedStatement createStatement = null;
            createStatement = conn.prepareStatement(
                    "insert ignore into gpsapp.registeredgpsusers (`username`, `homelocation`) values (?,?);");
            createStatement.setString(1, getThing().getConfiguration().get("name").toString());
            createStatement.setString(2, NorthQConfig.getHOMELOCATION());
            createStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Requires:
     * Returns: Get the newest encrypted gps data for the phone
     */
    private String getGpsDataFromDb(String raw) {
        Connection conn;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:Mysql://localhost:3306", NorthQConfig.getSQL_USERNAME(),
                    NorthQConfig.getSQL_PASSWORD());
            PreparedStatement createStatement = null;
            createStatement = conn.prepareStatement(
                    "select * from `gpsapp`.`gpsdata` where  `user` = ? ORDER BY stamp DESC LIMIT 1;");
            createStatement.setString(1, getThing().getConfiguration().get("name").toString());
            ResultSet rs = null;
            rs = createStatement.executeQuery();
            while (rs.next()) {
                raw = rs.getString("gpscords");
                // if older then 30 set offline and update to being home as no data is avaliable
                if (rs.getTimestamp("stamp").after(Timestamp.valueOf(LocalDateTime.now().minusMinutes(30)))) {
                    updateStatus(ThingStatus.ONLINE);
                } else {
                    updateStatus(ThingStatus.OFFLINE);
                    NorthQConfig.getPHONE_MAP().put(getThing().getConfiguration().get("name").toString(), true);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            updateStatus(ThingStatus.OFFLINE);
        }
        return raw;
    }

    /**
     * Requires:
     * Returns: removes a user from the database
     */
    private void removeUserFromDb() {
        Connection conn;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:Mysql://localhost:3306", NorthQConfig.getSQL_USERNAME(),
                    NorthQConfig.getSQL_PASSWORD());
            PreparedStatement createStatement = null;
            createStatement = conn
                    .prepareStatement("delete from gpsapp.registeredgpsusers where registeredgpsusers.Username = ?;");
            createStatement.setString(1, getThing().getConfiguration().get("name").toString());
            createStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
