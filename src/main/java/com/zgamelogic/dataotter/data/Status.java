package com.zgamelogic.dataotter.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Getter
@NoArgsConstructor
@ToString
public class Status {
    @JsonProperty("date recorded")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;
    private long milliseconds;
    private boolean status;
    private int attempts;
    @JsonProperty("status code")
    private int statusCode;
}
