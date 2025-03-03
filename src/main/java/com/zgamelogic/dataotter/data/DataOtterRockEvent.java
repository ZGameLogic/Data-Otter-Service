package com.zgamelogic.dataotter.data;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class DataOtterRockEvent extends ApplicationEvent {
    private final Object pebble;

    public DataOtterRockEvent(Object source, Object pebble) {
        super(source);
        this.pebble = pebble;
    }
}
