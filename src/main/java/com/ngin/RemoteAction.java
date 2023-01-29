package com.ngin;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import commander.Command.NEvent;

public class RemoteAction {
    private static int counter = 0;
    static int getNewSn() {
        counter++;
        return counter;
    }

    int sn;
    //private final Lock lock = new ReentrantLock();
    private final Semaphore semaphore = new Semaphore(1);
    NEvent event;

    RemoteAction() throws InterruptedException {
        sn = getNewSn();
        semaphore.acquire();
    }
    
    void lock() throws InterruptedException {
        semaphore.acquire();
    }

    void unlock() {
        semaphore.release();
    }
}
