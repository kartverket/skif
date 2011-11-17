package no.statkart.skif.service;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SingleVmRemoteCallMarker {
    private boolean isRemoteCall;

    public SingleVmRemoteCallMarker(boolean remoteCall) {
        isRemoteCall = remoteCall;
    }

    public boolean isRemoteCall() {
        return isRemoteCall;
    }
}
