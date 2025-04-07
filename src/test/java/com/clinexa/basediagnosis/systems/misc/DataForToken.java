package com.clinexa.basediagnosis.systems.misc;

import java.io.Serializable;
import java.util.Objects;

public final class DataForToken implements Serializable {

    private String ClientID;
    private String ClientSecret;

    public DataForToken() {}

    public DataForToken(String clientID, String clientSecret) {
        ClientID = clientID;
        ClientSecret = clientSecret;
    }

    public String getClientID() {
        return ClientID;
    }

    public void setClientID(String clientID) {
        ClientID = clientID;
    }

    public String getClientSecret() {
        return ClientSecret;
    }

    public void setClientSecret(String clientSecret) {
        ClientSecret = clientSecret;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DataForToken that)) return false;
        return Objects.equals(ClientID, that.ClientID) && Objects.equals(ClientSecret, that.ClientSecret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ClientID, ClientSecret);
    }
}
