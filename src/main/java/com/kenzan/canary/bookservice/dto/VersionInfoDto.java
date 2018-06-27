package com.kenzan.canary.bookservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VersionInfoDto {
    private String version;

    @JsonCreator
    public VersionInfoDto(@JsonProperty("version") String version) {
        this.version = version;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }
}