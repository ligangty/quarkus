/*
 * Copyright 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.shamrock.runtime.graal;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 */
public class ShutdownHookThread extends Thread {
    private final AtomicInteger shutdownState;
    private final Thread mainThread;

    public ShutdownHookThread(final AtomicInteger shutdownState, Thread mainThread) {
        super("Shutdown thread");
        this.shutdownState = shutdownState;
        this.mainThread = mainThread;
        setDaemon(false);
    }

    public void run() {
        shutdownState.set(1);
        LockSupport.unpark(mainThread);
        do {
            LockSupport.park(mainThread);
            Thread.interrupted();
        } while (shutdownState.get() != 2);
    }

    public String toString() {
        return getName();
    }
}