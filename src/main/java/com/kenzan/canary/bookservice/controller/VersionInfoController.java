package com.kenzan.canary.bookservice.controller;

import com.kenzan.canary.bookservice.dto.VersionInfoDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.prometheus.client.Counter;

@RestController("/version")
public class VersionInfoController {

    @Value("${app.version:1.0}")
    private String appVersion;

    @Autowired
    @Qualifier("getHttpRequestsTotalCounter")
    private Counter httpRequestTotalCounter;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<VersionInfoDto> getVersionInfo() {
        httpRequestTotalCounter.labels("getVersion", "GET", "200").inc();

        VersionInfoDto versionInfoDto = new VersionInfoDto(appVersion);
        return ResponseEntity.ok(versionInfoDto);
    }
}