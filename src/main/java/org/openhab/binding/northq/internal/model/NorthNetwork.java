/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.northq.internal.model;

import java.util.ArrayList;

import org.openhab.binding.northq.internal.model.json.House;

/**
 * The {@link NorthNetwork} is a pojo which holds the Network variables and gateways which in turn holds the things
 *
 * @author Dan / Nicolaj - Initial contribution (from standalone java)
 */

public class NorthNetwork {
    private String token;
    private String userId;
    private House[] houses;
    private ArrayList<NGateway> gateways;

    public NorthNetwork(String token, String userId, House[] houses, ArrayList<NGateway> gateways) {
        super();
        this.token = token;
        this.userId = userId;
        this.houses = houses;
        this.gateways = gateways;
    }

    public House[] getHouses() {
        return houses;
    }

    public void setHouses(House[] houses) {
        this.houses = houses;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<NGateway> getGateways() {
        return gateways;
    }

    public void setGateways(ArrayList<NGateway> gateways) {
        this.gateways = gateways;
    }

    public String[] toStringArray() {
        String[] res = new String[gateways.size()];
        for (int i = 0; i < gateways.size(); i++) {
            res[i] = gateways.get(i).toString().substring(42, gateways.get(i).toString().length() - 1).split("@")[0]
                    + " " + (i + 1);
        }
        return res;
    }

    @Override
    public String toString() {
        return "token: " + token + "\ngateway id: " + gateways.get(0).getGatewayId() + "\nthings: "
                + gateways.get(0).getThings().size();

    }
}
