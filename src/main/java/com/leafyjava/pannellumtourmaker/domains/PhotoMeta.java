package com.leafyjava.pannellumtourmaker.domains;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhotoMeta {

    private Exif exif;
    private GPano gPano;

    public Exif getExif() {
        return exif;
    }

    public void setExif(final Exif exif) {
        this.exif = exif;
    }

    public GPano getGPano() {
        return gPano;
    }

    public void setGPano(final GPano gPano) {
        this.gPano = gPano;
    }
}
