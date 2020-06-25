package co.cvdcc.brojekcustomer.model;

import io.realm.RealmObject;

/**
 * Created by bradhawk on 10/13/2016.
 */

public class FirebaseToken extends RealmObject {
    private String tokenId;

    public FirebaseToken(String tokenId) {
        this.tokenId = tokenId;
    }

    public FirebaseToken() {
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
}
