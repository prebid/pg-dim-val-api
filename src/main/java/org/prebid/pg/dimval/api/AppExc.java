package org.prebid.pg.dimval.api;

public class AppExc extends Exception {

    private final String code;

    public AppExc(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
