package no.statkart.skif.service;

import java.io.Serializable;

public class StopRequest  implements Serializable {
    private final String ejbThreadName;
    private volatile boolean stop;
    private static ThreadLocal<StopRequest> stopRequests = new ThreadLocal<StopRequest>();

    public StopRequest() {
        ejbThreadName = Thread.currentThread().getName();
    }

    public void stop() {
        this.stop = true;
    }

    public static StopRequest create() {
        StopRequest s = new StopRequest();
        stopRequests.set(s);
        return s;
    }

    public static boolean stopRequestedForThread() {
        StopRequest s = stopRequests.get();
        return s!=null && s.stop;
    }

    @Override
    public String toString() {
        return "StopRequest[ejbTreadName="+ ejbThreadName + ", stop=" + stop + "]";
    }
}
