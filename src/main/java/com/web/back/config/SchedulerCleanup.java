package com.web.back.config;

import org.springframework.beans.factory.DisposableBean;
import reactor.core.scheduler.Schedulers;

public class SchedulerCleanup implements DisposableBean {

    @Override
    public void destroy() throws Exception {
        Schedulers.shutdownNow();
    }
}
