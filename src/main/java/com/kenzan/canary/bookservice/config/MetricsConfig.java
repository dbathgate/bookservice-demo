package com.kenzan.canary.bookservice.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;

@Configuration
public class MetricsConfig {

    @Value("${app.version:1.0}")
    private String appVersion;

    @Bean
    public ServletRegistrationBean<MetricsServlet> getMetricServlet(){
        DefaultExports.initialize();

        Gauge metadataGauge = Gauge.build()
            .name("app_metadata")
            .help("App Metadata")
            .labelNames("app_name", "version", "language")
            .register();

        metadataGauge.labels("book-service", appVersion, "java").set(1);

        return new ServletRegistrationBean<>(new MetricsServlet(), "/metrics");
    }

    @Bean("getHttpRequestsTotalCounter")
    public Counter getHttpRequestsTotalCounter() {
        return  Counter.build()
            .name("http_requests_total")
            .help("HTTP reuqest counts")
            .labelNames("api", "method", "status")
            .register();
    }
}