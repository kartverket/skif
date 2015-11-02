package no.statkart.skif.config;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SkifConfigConstants {

    public final static String SINGLE_VM_SERVER_INJECTOR = "skif.single_vm_serverInjector";
    /** @deprecated Single-VM er ikke lenger en true/false-greie. Se {@link #SERVICE_MODE} */
    public final static String SINGLE_VM = "skif.single_vm";
    public final static String SERVICE_MODE = "skif.service_mode";
    public static final String MODULE_CLASS = "skif.module_class";
    public static final String MODULE_EXT_CLASS = "skif.module_ext_class";
    public static final String MODULE_STRATEGY_FACTORY_CLASS = "skif.module_strategy_factory_class";
    public static final String EJB_SERVICE_CHAIN_EXT_CLASS = "skif.ejb_service_chain_ext_class";
    public static final String USE_SHARED_SERVER = "skif.shared_server";
    public static final String CONFIGURATION_PROPERTIES = "skif.configuration_properties";
    public static final String CONFIGURATION_FILENAME = "skif.configuration_filename";

    public static final String SINGLE_VM_SERVER_MODULE_CLASS = "skif.single_vm_server_module_class";
    public static final String SINGLE_VM_SERVER_MODULE_EXT_CLASS = "skif.single_vm_server_module_ext_class";
    public static final String SINGLE_VM_SERVER_MODULE_STRATEGY_FACTORY_CLASS = "skif.single_vm_server_module_strategy_factory_class";
    public static final String SINGLE_VM_SERVER_EJB_SERVICE_CHAIN_EXT_CLASS = "skif.single_vm_server_ejb_service_chain_ext_class";
    public static final String SINGLE_VM_SERVER_CONFIGURATION = "skif.single_vm_server_configuration_properties";
    public static final String SINGLE_VM_SERVER_CONFIGURATION_FILENAME = "skif.single_vm_server_configuration_filename";


    public static final String SERVER_USERNAME = "skif.server_username";
    public static final String SERVER_PASSWORD = "skif.server_password";
    public static final String SERVER_URL = "skif.server_url";

    public static final String DB_USERNAME = "skif.db_username";
    public static final String DB_PASSWORD = "skif.db_password";
    public static final String DB_HOSTNAME = "skif.db_hostname";
    public static final String DB_PORT = "skif.db_port";
    public static final String DB_SERVICE = "skif.db_service";
    public static final String DB_DATASOURCE = "skif.db_datasource";
    public static final String DB_DATASOURCE_OLD = "skif.db_datasource_old";

    public static final String HIBERNATE_MAPPRING_FILE_ROOT = "skif.hibernate_mapping_file_root";

    public static final String LOCK_TIMEOUT = "skif.lock_timeout";
    public static final String MAX_TRANSACTION_DURATION = "skif.max_transaction_duration";

    public static final String DB_LOCK_TABLENAME = "skif.db_lock_tablename";
    public static final String DB_SEQUENCE_TABLENAME = "skif.db_sequence_tablename";
    public static final String USE_DATABASE_EVENT_LISTENER = "skif.use_database_event_listener";

    public static final String ENDRINGSNUMMER_SEQUENCE_NAME = "skif.endringsnummer_sequence_name";
}
