package com.pigeon_stargram.sns_clone.worker;

import reactor.core.scheduler.Scheduler;

public interface Worker {
    void acceptTask();
    void work(Object task);
    void enqueue(Object task);
}