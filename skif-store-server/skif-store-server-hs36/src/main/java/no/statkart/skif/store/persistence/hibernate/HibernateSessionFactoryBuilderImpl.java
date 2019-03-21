package no.statkart.skif.store.persistence.hibernate;


/**
 * // TODO: Denne klasse skal slette når SKIF-701 er inne på trunk op denne branch er rebaset mot trunk
 * Denne klasse inneholder Hibernate 3.2.6 specifikk kode. Den skal integreres i superklassen
 * når SKIF støtter bubbleref for seneste versjon av hibernate
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class HibernateSessionFactoryBuilderImpl extends HibernateSessionFactoryBuilder {
    public HibernateSessionFactoryBuilderImpl(String mappingFilesDirectory) {
        super(mappingFilesDirectory);
    }


    /*
    protected Configuration createConfiguration(Properties props, @Nullable Interceptor interceptor) {
        // Log databaseparametre. I singlevm mode brukes JDBCTransactionFactory (dvs url, bruker/password).
        // I servermode brukes JTATransactionFactory (dvs datasource)
        String connectionInfo;
        if (props.get("hibernate.transaction.factory_class").equals("org.hibernate.transaction.JDBCTransactionFactory")) {
            connectionInfo = props.getProperty("hibernate.connection.url") + " - " + props.getProperty("hibernate.connection.username");
        } else {
            // TODO: Dette blir feil for SnapshotVersion.OLD. Må bruke old datasource
            connectionInfo = props.getProperty("hibernate.connection.datasource");
        }
        logger.info("SKIF hibernatekonfigurasjon({}): {}", org.hibernate.Version.getVersionString(), connectionInfo);
        ClassLoader cl = HibernateSessionFactoryBuilder.class.getClassLoader();
        Configuration cfg = null;
        try {
            // NB: getBubbleClassDeleteOrder() definerer slette rekkefølgen for alle {@code BubbleObject} typer.
            // Metoden {@link #addResourceUsingAbsolutePath} legger automatisk {@code BubbleObject} klasser inn i listen i den rekkefølge
            // metoden blir kallt.
            cfg = new Configuration().setProperties(props);
            for (String hbm : hbmResource) {
                cfg.addResource(hbm, cl);
            }
            if (interceptor!=null) {
                cfg.setInterceptor(interceptor);
            }


            // Legg in patch for Hibernate 3.2.6
            DeleteEventListener[] deleteEventStack = {new BugFixDeleteEventListener()};
            cfg.getEventListeners().setDeleteEventListeners(deleteEventStack);


            // Konfigurerer Hibernate listeners for raskere initialisering av tomme collections. Listeners er aktive
            // for load og utvalgte update events. Klassen EmptyCollectionsOptimizer anvendes kun på bobler som har
            // angitt empty collections flagget i mapping filen. Den eneste måte å slå av denne feature er at
            // fjerne flagget fra mapping filen. Hvis flagget reintroduseres bør flagges settes til 0. Dette garanterer
            // at alle endringer på collections som gjøres via Hibernate holder flagget oppdatert (da featuren ikke kan
            // slås av). Hvis collections endres utenom Hibernate må flagget nullstilles samtidig. Neste oppdatering
            // av boblen via Hibernate vil automatisk gjenberegne flagget uavhengig av om collections har endret seg.
            // Bemerk at Hibernate eventtypene som anvendes her alene ikke er nok til å holde flagget oppdatert for
            // alle tilfeller. Se bruken av EmptyCollectionsFlagUpdater i HibernatePersistenceSessionMasterImpl.
            EmptyCollectionsOptimizerListener emptyCollectionOptimizerListener = new EmptyCollectionsOptimizerListener();
            PreLoadEventListener[] preLoadStack = {emptyCollectionOptimizerListener, new DefaultPreLoadEventListener()};
            PreCollectionUpdateEventListener[] preCollectionUpdateStack = {emptyCollectionOptimizerListener}; // Ingen default listener i Hibernate for denne type listener
            SaveOrUpdateEventListener[] saveStack = {emptyCollectionOptimizerListener, new DefaultSaveOrUpdateEventListener()}; // Ingen default listener i Hibernate for denne type listener
            cfg.getEventListeners().setPreLoadEventListeners(preLoadStack);
            cfg.getEventListeners().setPreCollectionUpdateEventListeners(preCollectionUpdateStack);
            cfg.getEventListeners().setSaveEventListeners(saveStack);

            // Legg på listeners for ta vare på event med id ifm databaseoperasjoner for insert, update og delete. Dette flagget setter det for alle sessioner.
            // Det er også mulig å sette det for en enkelt session via EventListenersUtil klassen. Dermed er det mulig å unngå listener overheaded som introduseres
            // nå dette flagget settes.
            if ("true".equalsIgnoreCase(props.getProperty(SkifConfigConstants.USE_DATABASE_EVENT_LISTENER))) {
                addCurrentDatabaseEventListener(cfg);
            }
        } catch (MappingException e) {
            throw new ConfigurationException("Error in Hibernate mapping files: " + e.getMessage(), e, logger);
        }
        return cfg;
    }

    private void addCurrentDatabaseEventListener(Configuration cfg) {
        CurrentDatabaseEventListener rememberCurrentEventListener = new CurrentDatabaseEventListener();
        PreInsertEventListener[] preInsertEventStack = {rememberCurrentEventListener};
        cfg.addgetEventListeners().setPreInsertEventListeners(preInsertEventStack);
        PreUpdateEventListener[] preUpdateEventStack = {rememberCurrentEventListener};
        cfg.getEventListeners().setPreUpdateEventListeners(preUpdateEventStack);
        PreDeleteEventListener[] preDeleteEventStack = {rememberCurrentEventListener};
        cfg.getEventListeners().setPreDeleteEventListeners(preDeleteEventStack);
        PostInsertEventListener[] PostInsertEventStack = {rememberCurrentEventListener};
        cfg.getEventListeners().setPostInsertEventListeners(PostInsertEventStack);
        PostUpdateEventListener[] PostUpdateEventStack = {rememberCurrentEventListener};
        cfg.getEventListeners().setPostUpdateEventListeners(PostUpdateEventStack);
        PostDeleteEventListener[] PostDeleteEventStack = {rememberCurrentEventListener};
        cfg.getEventListeners().setPostDeleteEventListeners(PostDeleteEventStack);
    }
*/
}
