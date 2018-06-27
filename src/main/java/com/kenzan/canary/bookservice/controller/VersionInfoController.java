package com.kenzan.canary.bookservice.controller;

import javax.annotation.PostConstruct;

import com.kenzan.canary.bookservice.dto.VersionInfoDto;

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

    private Counter getVersionCounter;

    @PostConstruct
    public void init() {
        this.getVersionCounter = Counter.build().name("getVersionCounter").help("Get Version Info").register();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<VersionInfoDto> getVersionInfo() {
        getVersionCounter.inc();;

        VersionInfoDto versionInfoDto = new VersionInfoDto(appVersion);
        return ResponseEntity.ok(versionInfoDto);
    }
}