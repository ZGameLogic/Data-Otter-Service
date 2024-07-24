package com.zgamelogic.dataotter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DataOtterService {
    @Value("${dataotter-appid}")
    private long appid;

    public void test(){
        log.info("{}", appid);
    }
}
