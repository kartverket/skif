package no.statkart.skif;

/**
 * SKIF-266: Versjonsinformasjon for SKIF
 *
 * Det er viktig at byggesystemet legger inn manifest informasjon som behøves her.
 *
 * @author Leif Lislegård
 * @since 2.2
 */
public class SkifVersion {

    public static final SkifVersionImpl COMMON = forModule(SkifVersion.class);

    public static SkifVersionImpl forModule(Class forModuleClass) {
        return new SkifVersionImpl(forModuleClass);
    }

    public static String getTitle() {
        return COMMON.getTitle();
    }

    public static String getVersion() {
        return COMMON.getVersion();
    }



    public static class SkifVersionImpl {
        private final Class forModuleClass;

        SkifVersionImpl(Class forModule) {
            this.forModuleClass = forModule;
        }

        public String getTitle() {
            return forModuleClass.getPackage().getImplementationTitle();
        }

        public String getVersion() {
            return forModuleClass.getPackage().getImplementationVersion();
        }

    }





}
