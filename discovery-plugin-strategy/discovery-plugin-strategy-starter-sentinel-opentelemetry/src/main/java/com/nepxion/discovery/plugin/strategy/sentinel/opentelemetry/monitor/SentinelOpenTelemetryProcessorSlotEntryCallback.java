package com.nepxion.discovery.plugin.strategy.sentinel.opentelemetry.monitor;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Weihua Wang
 * @author Haojun Ren
 * @version 1.0
 */

import com.nepxion.discovery.common.constant.DiscoveryConstant;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;

import io.opentelemetry.api.trace.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import com.nepxion.discovery.plugin.framework.context.PluginContextAware;
import com.nepxion.discovery.plugin.strategy.sentinel.monitor.callback.SentinelTracerProcessorSlotEntryCallback;
import com.nepxion.discovery.plugin.strategy.sentinel.monitor.constant.SentinelStrategyMonitorConstant;

public class SentinelOpenTelemetryProcessorSlotEntryCallback extends SentinelTracerProcessorSlotEntryCallback<Span> {

    private static final String TRACER_NAME = "sentinel";

    @Value("${"+DiscoveryConstant.SPRING_APPLICATION_NAME+"}")
    private String applicationName;

    @Autowired
    private OpenTelemetry openTelemetry;

    @Override
    protected Span buildSpan() {
        String instrumentationName = String.join(this.applicationName, "-", TRACER_NAME);
        return openTelemetry.getTracer(instrumentationName).spanBuilder(SentinelStrategyMonitorConstant.SPAN_NAME).startSpan();
    }

    @Override
    protected void outputSpan(Span span, String key, String value) {
        span.setAttribute(key, value);
    }

    @Override
    protected void finishSpan(Span span) {
        span.end();
    }
}