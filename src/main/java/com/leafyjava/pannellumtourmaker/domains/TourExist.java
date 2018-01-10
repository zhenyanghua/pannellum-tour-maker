package com.leafyjava.pannellumtourmaker.domains;

public class TourExist {
    private boolean exists;

    public TourExist(final boolean exists) {
        this.exists = exists;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(final boolean exists) {
        this.exists = exists;
    }
}
