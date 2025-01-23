package com.zgamelogic.dataotter.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Monitor {
    private long id;
    private String name;
    private String type;
    private String url;
    private String regex;
    private Status status;
}
