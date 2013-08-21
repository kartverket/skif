package no.statkart.skif.config;

/**
 * SkifConfiguration med standardoppsett for bruk på tjener.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public final class SkifServerConfiguration extends SkifConfiguration {
    public SkifServerConfiguration() {
        super("skif-default.properties", "skif-server.properties");
    }
}
