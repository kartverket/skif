package no.statkart.skif.config;

/**
 * SkifConfiguration med standardoppsett for bruk på klient.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public final class SkifClientConfiguration extends SkifConfiguration {
    public SkifClientConfiguration() {
        super("skif-default.properties", "skif-client.properties");
    }
}
