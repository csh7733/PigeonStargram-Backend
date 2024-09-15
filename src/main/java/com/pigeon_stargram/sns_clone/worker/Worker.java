package com.pigeon_stargram.sns_clone.worker;

/**
 * 워커 작업을 처리하는 인터페이스입니다.
 *
 * 이 인터페이스는 다양한 작업을 처리하는 공통 메서드를 정의하며,
 * 각 워커는 작업을 받아들이고, 처리하며, 큐에 작업을 추가하는 로직을 구현해야 합니다.
 */
public interface Worker {

    /**
     * 큐에서 작업을 수락하고 처리할 준비를 하는 메서드입니다.
     * 각 워커는 이 메서드를 통해 작업을 수락하고 필요한 작업을 처리합니다.
     */
    void acceptTask();

    /**
     * 주어진 작업을 처리하는 메서드입니다.
     *
     * @param task 처리할 작업 객체
     */
    void work(Object task);

    /**
     * 작업을 큐에 추가하는 메서드입니다.
     *
     * @param task 큐에 추가할 작업 객체
     */
    void enqueue(Object task);
}
