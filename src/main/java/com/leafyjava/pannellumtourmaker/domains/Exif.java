package com.leafyjava.pannellumtourmaker.domains;

public class Exif {
    private double longitude;
    private double latitude;

    public Exif() {
    }

    public Exif(final double longitude, final double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }
}
