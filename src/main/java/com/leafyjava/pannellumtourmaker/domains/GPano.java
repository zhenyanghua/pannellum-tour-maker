package com.leafyjava.pannellumtourmaker.domains;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class GPano {
    @JacksonXmlProperty(localName = "CroppedAreaImageHeightPixels")
    private int croppedAreaImageHeightPixels;
    @JacksonXmlProperty(localName = "CroppedAreaImageWidthPixels")
    private int croppedAreaImageWidthPixels;
    @JacksonXmlProperty(localName = "CroppedAreaLeftPixels")
    private int croppedAreaLeftPixels;
    @JacksonXmlProperty(localName = "CroppedAreaTopPixels")
    private int croppedAreaTopPixels;
    @JacksonXmlProperty(localName = "FullPanoHeightPixels")
    private int fullPanoHeightPixels;
    @JacksonXmlProperty(localName = "FullPanoWidthPixels")
    private int fullPanoWidthPixels;
    @JacksonXmlProperty(localName = "LargestValidInteriorRectHeight")
    private int largestValidInteriorRectHeight;
    @JacksonXmlProperty(localName = "LargestValidInteriorRectLeft")
    private int largestValidInteriorRectLeft;
    @JacksonXmlProperty(localName = "LargestValidInteriorRectTop")
    private int largestValidInteriorRectTop;
    @JacksonXmlProperty(localName = "LargestValidInteriorRectWidth")
    private int largestValidInteriorRectWidth;
    @JacksonXmlProperty(localName = "PoseHeadingDegrees")
    private float poseHeadingDegrees;
    @JacksonXmlProperty(localName = "ProjectionType")
    private String projectionType;
    @JacksonXmlProperty(localName = "UsePanoramaViewer")
    private boolean usePanoramaViewer;

    public int getCroppedAreaImageHeightPixels() {
        return croppedAreaImageHeightPixels;
    }

    public void setCroppedAreaImageHeightPixels(final int croppedAreaImageHeightPixels) {
        this.croppedAreaImageHeightPixels = croppedAreaImageHeightPixels;
    }

    public int getCroppedAreaImageWidthPixels() {
        return croppedAreaImageWidthPixels;
    }

    public void setCroppedAreaImageWidthPixels(final int croppedAreaImageWidthPixels) {
        this.croppedAreaImageWidthPixels = croppedAreaImageWidthPixels;
    }

    public int getCroppedAreaLeftPixels() {
        return croppedAreaLeftPixels;
    }

    public void setCroppedAreaLeftPixels(final int croppedAreaLeftPixels) {
        this.croppedAreaLeftPixels = croppedAreaLeftPixels;
    }

    public int getCroppedAreaTopPixels() {
        return croppedAreaTopPixels;
    }

    public void setCroppedAreaTopPixels(final int croppedAreaTopPixels) {
        this.croppedAreaTopPixels = croppedAreaTopPixels;
    }

    public int getFullPanoHeightPixels() {
        return fullPanoHeightPixels;
    }

    public void setFullPanoHeightPixels(final int fullPanoHeightPixels) {
        this.fullPanoHeightPixels = fullPanoHeightPixels;
    }

    public int getFullPanoWidthPixels() {
        return fullPanoWidthPixels;
    }

    public void setFullPanoWidthPixels(final int fullPanoWidthPixels) {
        this.fullPanoWidthPixels = fullPanoWidthPixels;
    }

    public int getLargestValidInteriorRectHeight() {
        return largestValidInteriorRectHeight;
    }

    public void setLargestValidInteriorRectHeight(final int largestValidInteriorRectHeight) {
        this.largestValidInteriorRectHeight = largestValidInteriorRectHeight;
    }

    public int getLargestValidInteriorRectLeft() {
        return largestValidInteriorRectLeft;
    }

    public void setLargestValidInteriorRectLeft(final int largestValidInteriorRectLeft) {
        this.largestValidInteriorRectLeft = largestValidInteriorRectLeft;
    }

    public int getLargestValidInteriorRectTop() {
        return largestValidInteriorRectTop;
    }

    public void setLargestValidInteriorRectTop(final int largestValidInteriorRectTop) {
        this.largestValidInteriorRectTop = largestValidInteriorRectTop;
    }

    public int getLargestValidInteriorRectWidth() {
        return largestValidInteriorRectWidth;
    }

    public void setLargestValidInteriorRectWidth(final int largestValidInteriorRectWidth) {
        this.largestValidInteriorRectWidth = largestValidInteriorRectWidth;
    }

    public float getPoseHeadingDegrees() {
        return poseHeadingDegrees;
    }

    public void setPoseHeadingDegrees(final float poseHeadingDegrees) {
        this.poseHeadingDegrees = poseHeadingDegrees;
    }

    public String getProjectionType() {
        return projectionType;
    }

    public void setProjectionType(final String projectionType) {
        this.projectionType = projectionType;
    }

    public boolean isUsePanoramaViewer() {
        return usePanoramaViewer;
    }

    public void setUsePanoramaViewer(final boolean usePanoramaViewer) {
        this.usePanoramaViewer = usePanoramaViewer;
    }

    @Override
    public String toString() {
        return "GPano{" +
            "croppedAreaImageHeightPixels=" + croppedAreaImageHeightPixels +
            ", croppedAreaImageWidthPixels=" + croppedAreaImageWidthPixels +
            ", croppedAreaLeftPixels=" + croppedAreaLeftPixels +
            ", croppedAreaTopPixels=" + croppedAreaTopPixels +
            ", fullPanoHeightPixels=" + fullPanoHeightPixels +
            ", fullPanoWidthPixels=" + fullPanoWidthPixels +
            ", largestValidInteriorRectHeight=" + largestValidInteriorRectHeight +
            ", largestValidInteriorRectLeft=" + largestValidInteriorRectLeft +
            ", largestValidInteriorRectTop=" + largestValidInteriorRectTop +
            ", largestValidInteriorRectWidth=" + largestValidInteriorRectWidth +
            ", poseHeadingDegrees=" + poseHeadingDegrees +
            ", projectionType='" + projectionType + '\'' +
            ", usePanoramaViewer=" + usePanoramaViewer +
            '}';
    }
}
