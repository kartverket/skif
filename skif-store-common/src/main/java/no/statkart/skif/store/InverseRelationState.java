package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 */
public enum InverseRelationState {
    UNREQUESTED,
    REQUESTED,
    MATERIALISED;

    public boolean isMaterialised() {
        return this==MATERIALISED;
    }

    public boolean isRequested() {
        return this !=UNREQUESTED;
    }
}
