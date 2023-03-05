package com.nepxion.discovery.plugin.strategy.opentelemetry.configuration;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import com.nepxion.discovery.common.constant.DiscoveryConstant;
import com.nepxion.discovery.common.util.StringUtil;
import com.nepxion.discovery.plugin.framework.context.PluginContextAware;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporterBuilder;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nepxion.banner.BannerConstant;
import com.nepxion.banner.Description;
import com.nepxion.banner.LogoBanner;
import com.nepxion.banner.NepxionBanner;
import com.nepxion.discovery.common.entity.TracingType;
import com.nepxion.discovery.plugin.strategy.constant.StrategyConstant;
import com.nepxion.discovery.plugin.strategy.monitor.StrategyTracer;
import com.nepxion.discovery.plugin.strategy.opentelemetry.monitor.OpenTelemetryStrategyTracer;
import com.taobao.text.Color;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Configuration
public class OpenTelemetryStrategyAutoConfiguration {
    static {
        LogoBanner logoBanner = new LogoBanner(OpenTelemetryStrategyAutoConfiguration.class, "/com/nepxion/opentelemetry/resource/logo.txt", "Welcome to Nepxion", 8, 5, new Color[] { Color.red, Color.green, Color.cyan, Color.blue, Color.yellow, Color.magenta, Color.red, Color.green }, true);

        NepxionBanner.show(logoBanner, new Description("Tracing:", TracingType.OPENTELEMETRY.toString(), 0, 1), new Description(BannerConstant.GITHUB + ":", BannerConstant.NEPXION_GITHUB + "/Discovery", 0, 1));
    }

    public static final String OTEL_EXPORTER_ENDPOINT = "otel.exporter.otlp.endpoint";
    public static final String OTEL_EXPORTER_ENDPOINT_TOKEN = "otel.exporter.otlp.endpoint.token";

    @Value("${"+OTEL_EXPORTER_ENDPOINT+"}")
    private String otlpEndpoint;

    @Value("${"+OTEL_EXPORTER_ENDPOINT_TOKEN+"}")
    private String otlpEndpointToken;

    @Value("${"+DiscoveryConstant.SPRING_APPLICATION_NAME+":discovery}")
    private String applicationName;

    @Value("${"+DiscoveryConstant.VERSION+":1.0.0}")
    private String version;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = StrategyConstant.SPRING_APPLICATION_STRATEGY_MONITOR_ENABLED, matchIfMissing = false)
    public OpenTelemetry openTelemetry() {
        OtlpGrpcSpanExporterBuilder builder = OtlpGrpcSpanExporter.builder();
        if (!StringUtils.isEmpty(this.otlpEndpoint)) {
            builder.setEndpoint(this.otlpEndpoint);
        }
        if (!StringUtils.isEmpty(this.otlpEndpointToken)) {
            builder.addHeader("Authentication", this.otlpEndpointToken);
        }

        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(builder.build()).build())
                .build();

        return OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = StrategyConstant.SPRING_APPLICATION_STRATEGY_MONITOR_ENABLED, matchIfMissing = false)
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer(this.applicationName, this.version);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = StrategyConstant.SPRING_APPLICATION_STRATEGY_MONITOR_ENABLED, matchIfMissing = false)
    public StrategyTracer strategyTracer() {
        return new OpenTelemetryStrategyTracer();
    }

}