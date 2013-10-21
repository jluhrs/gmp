package edu.gemini.aspen.gmp.services.properties;

public enum GmpProperties {
    GMP_HOST_NAME("NO_VALID"),
    DHS_ANCILLARY_DATA_PATH("NO_VALID"),
    DHS_SCIENCE_DATA_PATH("NO_VALID"),
    DHS_PERMANENT_SCIENCE_DATA_PATH("NO_VALID"),
    DHS_INTERMEDIATE_DATA_PATH("NO_VALID"),
    DEFAULT("NO_VALID"),
    APPEND_FITS_EXTENSION("APPEND_FITS_EXTENSION");

    private String defaultValue;

    GmpProperties(String defaultVal) {
        defaultValue = defaultVal;
    }

    public String getDefault() {
        return defaultValue;
    }
}
