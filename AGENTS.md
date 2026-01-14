# AGENTS.md

## Repository Overview
- SKIF is a multi-module Gradle project (Java 11) with store/server/test/support modules.
- Persistence is centered in `skif-store-server` and uses Hibernate ORM with XML mappings (`*.hbm.xml`).
- Hibernate now comes from `org.hibernate.orm:hibernate-core` at `6.0.2.Final` (`gradle/libs.versions.toml`).

## Hibernate Integration (Current Behavior)
- Session factory bootstrap: `skif-store-server/src/main/java/no/statkart/skif/store/persistence/hibernate/HibernateSessionFactoryBuilder.java`
  - Scans mapping resources under a configurable directory (e.g., `no/statkart/skif/storetest/persistence/hibernate`).
  - Registers `EmptyCollectionOptimizerIntegrator` via `BootstrapServiceRegistryBuilder`.
  - Injects `SnapshotVersionSeed` into Hibernate config: property `no.statkart.skif.SnapshotVersionSeed`.
  - Forces `AvailableSettings.CONNECTION_HANDLING=DELAYED_ACQUISITION_AND_HOLD`.
- Session factory management:
  - `HibernateSessionFactoryManager` and `HibernateSessionFactoryDescriptor` build per-snapshot factories.
  - Snapshot switching can execute `snapshot_time.set_t(...)` using `OracleLocalTimestamp`.
  - `SessionSelector` reserves/releases `org.hibernate.internal.SessionImpl` per snapshot version.
- Session/persistence flow:
  - `HibernatePersistenceSessionMasterImpl` owns the Hibernate `Session` and implements load/update/delete.
  - Uses JPA Criteria (`jakarta.persistence.criteria.*`) for batched `id in (...)` lookups and other queries.
  - Accesses internal Hibernate classes (`SessionImpl`, `EntityPersister`, `PersistenceContext`, `EntityKey`).
- Interceptors/listeners:
  - `HibernateStoreInterceptor` extends `EmptyInterceptor` to validate snapshot version and mark flushes.
  - `EmptyCollectionsOptimizer` uses Hibernate event listeners and `PersistentCollection` internals.

## Custom Hibernate Types and Oracle Integration
- Core custom types in `skif-store-server/src/main/java/no/statkart/skif/store/persistence/hibernate/type`.
  - `BubbleIdType` implements the Hibernate 6 `UserType` API + `TypeConfigurationAware` and injects `SnapshotVersionSeed`.
  - `AnyBubbleIdType` and `AnyConcatenatedFieldsType` now use `CompositeUserType` for multi-column mappings.
- Oracle array and temporal types in `skif-store-server/src/main/java/no/statkart/skif/persistence/hibernate/type`.
  - `Oracle*Array*UserType`, `OracleLocalTimestamp`, `OraclePersistentLocalDate/Time`.
- HBM mappings reference these types across test and sample domains.

## Query Patterns
- JPA Criteria: `HibernatePersistenceSessionMasterImpl`, `EndringsloggServiceImpl`.
- Legacy Hibernate Criteria API removed; `NedlastningServiceImpl` now uses JPA Criteria and native SQL for Oracle array parameters.
- Native SQL + metadata: `EndringsloggServiceImpl` uses `ClassMetadata` and `AbstractEntityPersister`
  via `skif-store-server/src/main/java/no/statkart/skif/util/HibernateHelper.java`.

## Configuration and Environment
- Hibernate properties template: `skif-config/src/main/resources/no/statkart/skif/storetest/config/persistence/skiftest-hibernate.properties`.
- TomEE is used for embedded server/testing (`tomee` module).
- Test stack uses TestNG and standalone helpers (`skif-store-test` and `skif-testsupport-common`).

## Test/Mapping Assets
- HBM XML mappings live under `skif-store-test/skif-store-test-server/src/main/resources/.../persistence/hibernate`.
- Standalone tests build session factories with mappings and `skiftest-hibernate.properties`.

## Upgrade Risk Areas (Hibernate 6)
- Use of internal Hibernate classes (`SessionImpl`, `EntityPersister`, `PersistenceContext`).
- `org.hibernate.metadata.ClassMetadata` and `AbstractEntityPersister` APIs.
- Event listeners/integrator APIs and `PersistentCollection` internals.
- Custom `UserType`/`CompositeUserType` usage and query parameter binding with `CustomType`/`TypeConfiguration`.
