package com.vinsguru.instrumentation;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.SimpleLogRecordProcessor;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class OpenTelemetryConfig {

    private static final Logger log = LoggerFactory.getLogger(OpenTelemetryConfig.class);

    private static final String COLLECTOR_ENDPOINT = "http://localhost:4317";
    private static final OpenTelemetry openTelemetry = initOpenTelemetry();

    private static OpenTelemetry initOpenTelemetry() {
        return OpenTelemetrySdk.builder()
                               .setTracerProvider(tracerProvider())
                               .setMeterProvider(meterProvider())
                               .setLoggerProvider(loggerProvider())
                               .build();
    }

    private static SdkTracerProvider tracerProvider() {
        var exporter = OtlpGrpcSpanExporter.builder()
                                           .setEndpoint(COLLECTOR_ENDPOINT)
                                           .build();

        // for production applications: BatchSpanProcessor.builder(exporter).build();
        var processor = SimpleSpanProcessor.create(exporter);
        return SdkTracerProvider.builder()
                                .setResource(resource())
                                .addSpanProcessor(processor)
                                .build();
    }

    private static SdkMeterProvider meterProvider() {
        var exporter = OtlpGrpcMetricExporter.builder()
                                             .setEndpoint(COLLECTOR_ENDPOINT)
                                             .build();
        var metricReader = PeriodicMetricReader.builder(exporter)
                                               .setInterval(Duration.ofSeconds(5))
                                               .build();
        return SdkMeterProvider.builder()
                               .setResource(resource())
                               .registerMetricReader(metricReader)
                               .build();
    }


    private static SdkLoggerProvider loggerProvider() {
        var exporter = OtlpGrpcLogRecordExporter.builder()
                                                .setEndpoint(COLLECTOR_ENDPOINT)
                                                .build();
        // for production applications: BatchLogRecordProcessor.builder(exporter).build();
        var processor = SimpleLogRecordProcessor.create(exporter);
        return SdkLoggerProvider.builder()
                                .setResource(resource())
                                .addLogRecordProcessor(processor)
                                .build();
    }

    private static Resource resource() {
        return Resource.create(Attributes.of(
                AttributeKey.stringKey("service.name"), "order-service"
        ));
    }

    public static Tracer tracer(Class<?> type) {
        return openTelemetry.getTracer(type.getName());
    }

    public static Meter meter(Class<?> type) {
        return openTelemetry.getMeter(type.getName());
    }

    public static void setupLoggingAppender(){
        OpenTelemetryAppender.install(openTelemetry);
    }

}
