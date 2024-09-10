package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.thread_local_variables;

import lombok.AllArgsConstructor;

public class ThreadState {
    
    public static final ThreadLocal<StateHolder> statePerThread =
            ThreadLocal.withInitial(() -> new StateHolder("active"));

    public static StateHolder getState() {
        return statePerThread.get();
    }
}

@AllArgsConstructor
class StateHolder {

    private final String state;

    // standard constructors / getter
}