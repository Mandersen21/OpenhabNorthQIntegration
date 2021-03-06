/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.internal.common;

import java.util.HashMap;
import java.util.Map;

import org.openhab.binding.northq.internal.mock.NorthQMockNetwork;
import org.openhab.binding.northq.internal.model.NorthNetwork;

/**
 * The {@link NorthQConfig} is responsible for keeping config variables to use in the binding.
 *
 * @author Mads / Mikkel - Initial contribution
 * @author Dan/Jakob/Philip/Nicolaj/Aslan - updated class
 */

public class NorthQConfig {

    // North network
    private static NorthNetwork NETWORK = null;

    private static String USERNAME;
    private static String PASSWORD;
    private static String HOMELOCATION;

    private static String SQL_USERNAME;
    private static String SQL_PASSWORD;

    private static String SECRET_KEY;

    private static boolean ISHOME = true;
    private static boolean POWERONLOCATION = false;

    private static String THERMOSTAT_TEMPERATURE;

    private static boolean HEATONLOCATION = false;
    private static float ISHOMETEMP = 23;
    private static float NOTHOMETEMP = 18;

    private static boolean TEMP_SCHEDULER = false;

    private static boolean MOCK = false;
    private static NorthQMockNetwork MOCK_NETWORK;

    private static Map<String, Boolean> PHONE_MAP = new HashMap<String, Boolean>();

    public static String getHOMELOCATION() {
        return HOMELOCATION;
    }

    public static void setHOMELOCATION(String hOMELOCATION) {
        HOMELOCATION = hOMELOCATION;
    }

    public static boolean isPOWERONLOCATION() {
        return POWERONLOCATION;
    }

    public static void setPOWERONLOCATION(boolean pOWERONLOCATION) {
        POWERONLOCATION = pOWERONLOCATION;
    }

    public static boolean isHEATONLOCATION() {
        return HEATONLOCATION;
    }

    public static void setHEATONLOCATION(boolean hEATONLOCATION) {
        HEATONLOCATION = hEATONLOCATION;
    }

    public static float getISHOMETEMP() {
        return ISHOMETEMP;
    }

    public static void setISHOMETEMP(float iSHOMETEMP) {
        ISHOMETEMP = iSHOMETEMP;
    }

    public static float getNOTHOMETEMP() {
        return NOTHOMETEMP;
    }

    public static void setNOTHOMETEMP(float nOTHOMETEMP) {
        NOTHOMETEMP = nOTHOMETEMP;
    }

    public static boolean ISHOME() {
        return ISHOME;
    }

    public static void setISHOME(boolean ishome) {
        ISHOME = ishome;
    }

    public static String getUSERNAME() {
        return USERNAME;
    }

    public static void setUSERNAME(String username) {
        USERNAME = username;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }

    public static void setPASSWORD(String password) {
        PASSWORD = password;
    }

    public static NorthNetwork getNETWORK() {
        return NETWORK;
    }

    public static void setNETWORK(NorthNetwork nETWORK) {
        NETWORK = nETWORK;
    }

    public static boolean isMOCK() {
        return MOCK;
    }

    public static void setMOCK(boolean mOCK) {
        MOCK = mOCK;
    }

    public static NorthQMockNetwork getMOCK_NETWORK() {
        return MOCK_NETWORK;
    }

    public static void setMOCK_NETWORK(NorthQMockNetwork mOCK_NETWORK) {
        MOCK_NETWORK = mOCK_NETWORK;
    }

    public static Map<String, Boolean> getPHONE_MAP() {
        return PHONE_MAP;
    }

    public static void setPHONE_MAP(Map<String, Boolean> pHONE_MAP) {
        PHONE_MAP = pHONE_MAP;
    }

    public static String getSQL_USERNAME() {
        return SQL_USERNAME;
    }

    public static void setSQL_USERNAME(String username) {
        SQL_USERNAME = username;
    }

    public static String getSQL_PASSWORD() {
        return SQL_PASSWORD;
    }

    public static void setSQL_PASSWORD(String password) {
        SQL_PASSWORD = password;
    }

    public static String getSECRET_KEY() {
        return SECRET_KEY;
    }

    public static void setSECRET_KEY(String key) {
        SECRET_KEY = key;
    }

    public static boolean isTEMP_SCHEDULER() {
        return TEMP_SCHEDULER;
    }

    public static void setTEMP_SCHEDULER(boolean tEMP_SCHEDULER) {
        TEMP_SCHEDULER = tEMP_SCHEDULER;
    }

    public static String getTHERMOSTAT_TEMPERATURE() {
        return THERMOSTAT_TEMPERATURE;
    }

    public static void setTHERMOSTAT_TEMPERATURE(String tHERMOSTAT_TEMPERATURE) {
        THERMOSTAT_TEMPERATURE = tHERMOSTAT_TEMPERATURE;
    }

}
