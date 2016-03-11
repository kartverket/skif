package no.statkart.skif.service.proxy;

/**
 * Hjelpestruktur for å returnere 2 verdier fra metoden
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class SnapshotVersionW2DResult {
    public final boolean mustAddSnapshotVersionAsLastArgument;
    public final int lastArg;


    public SnapshotVersionW2DResult(boolean mustAddSnapshotVersionAsLastArgument, int lastArg) {
        this.mustAddSnapshotVersionAsLastArgument = mustAddSnapshotVersionAsLastArgument;
        this.lastArg = lastArg;
    }
}
