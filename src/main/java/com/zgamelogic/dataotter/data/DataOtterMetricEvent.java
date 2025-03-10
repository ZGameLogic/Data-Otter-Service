package com.zgamelogic.dataotter.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class DataOtterMetricEvent extends ApplicationEvent {
    private final long appId;
    private final String resource;
    private final String data;
    private final String unit;

    public DataOtterMetricEvent(Object source, long appId, String resource, String data, String unit) {
        super(source);
        this.appId = appId;
        this.resource = resource;
        this.data = data;
        this.unit = unit;
    }
}
