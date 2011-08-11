package no.statkart.skif.config;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class ConfigurationConstants {
    private ConfigurationConstants() {}

    public final static String SINGLE_VM_SERVER_INJECTOR = "skif.single_vm_serverInjector";
    public final static String SINGLE_VM = "skif.single_vm";
    public static final String MODULE_CLASS = "skif.module_class";
    public static final String USE_SHARED_SERVER = "skif.shared_server";
    public static final String CONFIGURATION_PROPERTIES = "skif.configuration_properties";
    public static final String CONFIGURATION_FILENAME = "skif.configuration_filename";
    public static final String MODULE_STRATEGY_FACTORY_CLASS = "skif.module_strategy_factory_class";

    public static final String SINGLE_VM_SERVER_MODULE_CLASS = "skif.single_vm_server_module_class";
    public static final String SINGLE_VM_SERVER_MODULE_STRATEGY_FACTORY_CLASS = "skif.singlevm_server_module_strategy_factory_class";
    public static final String SINGLEVM_SERVER_CONFIGURATION = "skif.single_vm_server_configuration_properties";
    public static final String SINGLE_VM_SERVER_CONFIGURATION_FILENAME = "skif.single_vm_server_configuration_filename";


    public static final String SERVER_USERNAME = "skif.server_username";
    public static final String SERVER_PASSWORD = "skif.server_password";
    public static final String SERVER_URL = "skif.server_url";

    public static final String DB_USERNAME = "skif.db_username";
    public static final String DB_PASSWORD = "skif.db_password";
    public static final String DB_HOSTNAME = "skif.db_hostname";
    public static final String DB_PORT = "skif.db_port";
    public static final String DB_SID = "skif.db_sid";
}
