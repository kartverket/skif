package no.statkart.skif.persistence.hibernate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.EntityComponent;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.persistence.hibernate.HibernateDetachedSupport;
import no.statkart.skif.store.persistence.hibernate.HibernateLazySupport;
import org.assertj.core.api.Assertions;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.Type;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.cfgxml.spi.LoadedConfig;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.collection.spi.PersistentSet;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EntityComponentChangeTypeTest {
    private static Object notMocked(InvocationOnMock invocationOnMock) {
        throw new RuntimeException("Not mocked");
    }

    private Metadata createMetadataOneToMany() {
        LoadedConfig loadedConfig = new LoadedConfig(null);
        loadedConfig.getConfigurationValues().put(AvailableSettings.DIALECT, "org.hibernate.dialect.H2Dialect");
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure(loadedConfig).build();
        return new MetadataSources(registry)
            .addAnnotatedClass(TestBubbleOneToMany.class)
            .addAnnotatedClass(TestBubbleOneToManyComponent.class)
            .addAnnotatedClass(TestBubbleOneToManyComponent1.class)
            .addAnnotatedClass(TestBubbleOneToManyComponent2.class)
            .buildMetadata();
    }

    @Test
    public void cantReuseIdForDifferentSubtype_collection() {
        Metadata metadata = createMetadataOneToMany();

        try (SessionFactory sessionFactory = metadata.buildSessionFactory()) {
            SessionImplementor session = Mockito.mock(SessionImplementor.class, EntityComponentChangeTypeTest::notMocked);
            Mockito.doReturn(sessionFactory).when(session).getFactory();
            Mockito.doReturn(sessionFactory).when(session).getSessionFactory();
            Mockito.doAnswer(invocation -> ((MetamodelImplementor) sessionFactory.getMetamodel()).entityPersister(TestBubbleOneToManyComponent.class)).when(session).getEntityPersister(Mockito.eq(TestBubbleOneToManyComponent.class.getName()), Mockito.any(TestBubbleOneToManyComponent.class));
            Mockito.doNothing().when(session).evict(Mockito.any());

            HibernateLazySupport hibernateLazySupport = new HibernateLazySupport(() -> session);
            HibernateDetachedSupport hibernateDetachedSupport = new HibernateDetachedSupport(hibernateLazySupport);

            TestBubbleOneToManyId bubbleId = new TestBubbleOneToManyId(1L);

            TestBubbleOneToManyComponent1 persistentBubbleComponent = new TestBubbleOneToManyComponent1(2L);
            TestBubbleOneToMany persistentBubble = new TestBubbleOneToMany(bubbleId);
            persistentBubble.getComponents().add(persistentBubbleComponent);
            persistentBubble.setComponents(new PersistentSet<>(session, persistentBubble.getComponents()));

            Mockito.doReturn(persistentBubble).when(session).get(TestBubbleOneToMany.class, bubbleId, LockMode.NONE);
            Mockito.doNothing().when(session).update(Mockito.any(TestBubbleOneToMany.class));

            TestBubbleOneToManyComponent2 updatedBubbleComponent = new TestBubbleOneToManyComponent2(2L);
            TestBubbleOneToMany updatedBubble = new TestBubbleOneToMany(bubbleId);
            updatedBubble.getComponents().add(updatedBubbleComponent);

            Assertions.assertThatThrownBy(() -> hibernateDetachedSupport.update(updatedBubble),
                "Persisting of entitycomponent with changed type"
            )
                .hasMessage("Attempted to change class from no.statkart.skif.persistence.hibernate.EntityComponentChangeTypeTest$TestBubbleOneToManyComponent1 to no.statkart.skif.persistence.hibernate.EntityComponentChangeTypeTest$TestBubbleOneToManyComponent2 for id 2");
        }
    }

    @Test
    public void canReplaceWithDifferentSubtypeWithoutId_collection() {
        Metadata metadata = createMetadataOneToMany();

        try (SessionFactory sessionFactory = metadata.buildSessionFactory()) {
            SessionImplementor session = Mockito.mock(SessionImplementor.class, EntityComponentChangeTypeTest::notMocked);
            Mockito.doReturn(sessionFactory).when(session).getFactory();
            Mockito.doReturn(sessionFactory).when(session).getSessionFactory();
            Mockito.doAnswer(invocation -> ((MetamodelImplementor) sessionFactory.getMetamodel()).entityPersister(TestBubbleOneToManyComponent.class)).when(session).getEntityPersister(Mockito.eq(TestBubbleOneToManyComponent.class.getName()), Mockito.any(TestBubbleOneToManyComponent.class));
            Mockito.doNothing().when(session).evict(Mockito.any());

            HibernateLazySupport hibernateLazySupport = new HibernateLazySupport(() -> session);
            HibernateDetachedSupport hibernateDetachedSupport = new HibernateDetachedSupport(hibernateLazySupport);

            TestBubbleOneToManyId bubbleId = new TestBubbleOneToManyId(1L);

            TestBubbleOneToManyComponent1 persistentBubbleComponent = new TestBubbleOneToManyComponent1(2L);
            TestBubbleOneToMany persistentBubble = new TestBubbleOneToMany(bubbleId);
            persistentBubble.getComponents().add(persistentBubbleComponent);
            persistentBubble.setComponents(new PersistentSet<>(session, persistentBubble.getComponents()));

            Mockito.doReturn(persistentBubble).when(session).get(TestBubbleOneToMany.class, bubbleId, LockMode.NONE);
            Mockito.doNothing().when(session).update(Mockito.any(TestBubbleOneToMany.class));

            TestBubbleOneToManyComponent2 updatedBubbleComponent = new TestBubbleOneToManyComponent2(null);
            TestBubbleOneToMany updatedBubble = new TestBubbleOneToMany(bubbleId);
            updatedBubble.getComponents().add(updatedBubbleComponent);

            hibernateDetachedSupport.update(updatedBubble);

            Mockito.verify(session).update(updatedBubble);
        }
    }

    private Metadata createMetadataOneToOne() {
        LoadedConfig loadedConfig = new LoadedConfig(null);
        loadedConfig.getConfigurationValues().put(AvailableSettings.DIALECT, "org.hibernate.dialect.H2Dialect");
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure(loadedConfig).build();
        return new MetadataSources(registry)
            .addAnnotatedClass(TestBubbleOneToOne.class)
            .addAnnotatedClass(TestBubbleOneToOneComponent.class)
            .addAnnotatedClass(TestBubbleOneToOneComponent1.class)
            .addAnnotatedClass(TestBubbleOneToOneComponent2.class)
            .buildMetadata();
    }

    @Test
    public void cantReuseIdForDifferentSubtype_oneToOne() {
        Metadata metadata = createMetadataOneToOne();

        try (SessionFactory sessionFactory = metadata.buildSessionFactory()) {
            SessionImplementor session = Mockito.mock(SessionImplementor.class, EntityComponentChangeTypeTest::notMocked);
            Mockito.doReturn(sessionFactory).when(session).getFactory();
            Mockito.doReturn(sessionFactory).when(session).getSessionFactory();
            Mockito.doAnswer(invocation -> ((MetamodelImplementor) sessionFactory.getMetamodel()).entityPersister(TestBubbleOneToOneComponent.class)).when(session).getEntityPersister(Mockito.eq(TestBubbleOneToOneComponent.class.getName()), Mockito.any(TestBubbleOneToOneComponent.class));
            Mockito.doNothing().when(session).evict(Mockito.any());

            HibernateLazySupport hibernateLazySupport = new HibernateLazySupport(() -> session);
            HibernateDetachedSupport hibernateDetachedSupport = new HibernateDetachedSupport(hibernateLazySupport);

            TestBubbleOneToOneId bubbleId = new TestBubbleOneToOneId(1L);

            TestBubbleOneToOneComponent1 persistentBubbleComponent = new TestBubbleOneToOneComponent1(2L);
            TestBubbleOneToOne persistentBubble = new TestBubbleOneToOne(bubbleId);
            persistentBubble.setComponent(persistentBubbleComponent);

            Mockito.doReturn(persistentBubble).when(session).get(TestBubbleOneToOne.class, bubbleId, LockMode.NONE);
            Mockito.doNothing().when(session).update(Mockito.any(TestBubbleOneToOne.class));

            TestBubbleOneToOneComponent2 updatedBubbleComponent = new TestBubbleOneToOneComponent2(2L);
            TestBubbleOneToOne updatedBubble = new TestBubbleOneToOne(bubbleId);
            updatedBubble.setComponent(updatedBubbleComponent);

            Assertions.assertThatThrownBy(() -> hibernateDetachedSupport.update(updatedBubble),
                "Persisting of entitycomponent with changed type"
            )
                .hasMessage("Attempted to change class from no.statkart.skif.persistence.hibernate.EntityComponentChangeTypeTest$TestBubbleOneToOneComponent1 to no.statkart.skif.persistence.hibernate.EntityComponentChangeTypeTest$TestBubbleOneToOneComponent2 for id 2");
        }
    }

    @Test
    public void canReplaceWithDifferentSubtypeWithoutId_oneToOne() {
        Metadata metadata = createMetadataOneToOne();

        try (SessionFactory sessionFactory = metadata.buildSessionFactory()) {
            SessionImplementor session = Mockito.mock(SessionImplementor.class, EntityComponentChangeTypeTest::notMocked);
            Mockito.doReturn(sessionFactory).when(session).getFactory();
            Mockito.doReturn(sessionFactory).when(session).getSessionFactory();
            Mockito.doAnswer(invocation -> ((MetamodelImplementor) sessionFactory.getMetamodel()).entityPersister(TestBubbleOneToOneComponent.class)).when(session).getEntityPersister(Mockito.eq(TestBubbleOneToOneComponent.class.getName()), Mockito.any(TestBubbleOneToOneComponent.class));
            Mockito.doNothing().when(session).evict(Mockito.any());

            HibernateLazySupport hibernateLazySupport = new HibernateLazySupport(() -> session);
            HibernateDetachedSupport hibernateDetachedSupport = new HibernateDetachedSupport(hibernateLazySupport);

            TestBubbleOneToOneId bubbleId = new TestBubbleOneToOneId(1L);

            TestBubbleOneToOneComponent1 persistentBubbleComponent = new TestBubbleOneToOneComponent1(2L);
            TestBubbleOneToOne persistentBubble = new TestBubbleOneToOne(bubbleId);
            persistentBubble.setComponent(persistentBubbleComponent);

            Mockito.doReturn(persistentBubble).when(session).get(TestBubbleOneToOne.class, bubbleId, LockMode.NONE);
            Mockito.doNothing().when(session).update(Mockito.any(TestBubbleOneToOne.class));

            TestBubbleOneToOneComponent2 updatedBubbleComponent = new TestBubbleOneToOneComponent2(null);
            TestBubbleOneToOne updatedBubble = new TestBubbleOneToOne(bubbleId);
            updatedBubble.setComponent(updatedBubbleComponent);

            hibernateDetachedSupport.update(updatedBubble);

            Mockito.verify(session).update(updatedBubble);
        }
    }

    public static class TestBubbleOneToManyId implements BubbleId<TestBubbleOneToMany> {
        private final long value;

        public TestBubbleOneToManyId(long value) {
            this.value = value;
        }

        @Override
        public Long getValue() {
            return value;
        }

        @Override
        public SnapshotVersion getSnapshotVersion() {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public TestBubbleOneToMany createTypeInstance() {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public Class<? extends BubbleObject> getBaseType() {
            return TestBubbleOneToMany.class;
        }

        @Override
        public Class<TestBubbleOneToMany> getType() {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public Class getValueType() {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public Class<? extends BubbleId<? super TestBubbleOneToMany>> getBaseIdType() {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public boolean equalsIgnoreSnapshotVersion(Object object) {
            return false;
        }

        @Override
        public BubbleId<? super TestBubbleOneToMany> asSnapshotVersion(SnapshotVersion snapshotVersion) {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public BubbleId<? super TestBubbleOneToMany> asSnapshotVersion(BubbleId<?> bubbleId) {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public BubbleId<? super TestBubbleOneToMany> asSnapshotVersionOld() {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public BubbleId<? super TestBubbleOneToMany> asSnapshotVersionCurrent() {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public BubbleId<? super TestBubbleOneToMany> asBase() {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestBubbleOneToManyId that = (TestBubbleOneToManyId) o;
            return value == that.value;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public int compareTo(Object o) {
            return 0;
        }
    }

    public static class TestBubbleOneToManyIdType extends BubbleIdType<TestBubbleOneToManyId> {
        @Override
        public Long getValue(Object id) {
            return ((TestBubbleOneToManyId) id).getValue();
        }

        @Override
        public TestBubbleOneToManyId createId(Long value) {
            return new TestBubbleOneToManyId(value);
        }

        @Override
        public Class<TestBubbleOneToManyId> returnedClass() {
            return TestBubbleOneToManyId.class;
        }
    }

    @Entity
    public static class TestBubbleOneToMany implements BubbleObject {
        @Id
        @Type(TestBubbleOneToManyIdType.class)
        private TestBubbleOneToManyId id;

        @OneToMany(cascade = CascadeType.ALL)
        private Set<TestBubbleOneToManyComponent> components = new HashSet<>();

        public TestBubbleOneToMany() {
        }

        public TestBubbleOneToMany(TestBubbleOneToManyId id) {
            this.id = id;
        }

        @Override
        public BubbleId<?> getBubbleId() {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public BubbleId<?> getId() {
            return id;
        }

        @Override
        public void setId(BubbleId<?> id) {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public Store store() {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public void register(Store store) {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public void setFlushed(boolean flushed) {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public boolean isFlushed() {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        public Set<TestBubbleOneToManyComponent> getComponents() {
            return components;
        }

        public void setComponents(Set<TestBubbleOneToManyComponent> components) {
            this.components = components;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestBubbleOneToMany that = (TestBubbleOneToMany) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }

    @Entity
    @Inheritance
    public abstract static class TestBubbleOneToManyComponent implements EntityComponent {
        @Id
        @GeneratedValue(strategy = GenerationType.TABLE)
        private Long id;

        public TestBubbleOneToManyComponent() {
        }

        public TestBubbleOneToManyComponent(Long id) {
            this.id = id;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestBubbleOneToManyComponent that = (TestBubbleOneToManyComponent) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }

    @Entity
    public static class TestBubbleOneToManyComponent1 extends TestBubbleOneToManyComponent {
        public TestBubbleOneToManyComponent1() {
        }

        public TestBubbleOneToManyComponent1(Long id) {
            super(id);
        }
    }

    @Entity
    public static class TestBubbleOneToManyComponent2 extends TestBubbleOneToManyComponent {
        public TestBubbleOneToManyComponent2() {
        }

        public TestBubbleOneToManyComponent2(Long id) {
            super(id);
        }
    }

    public static class TestBubbleOneToOneId implements BubbleId<TestBubbleOneToOne> {
        private final long value;

        public TestBubbleOneToOneId(long value) {
            this.value = value;
        }

        @Override
        public Long getValue() {
            return value;
        }

        @Override
        public SnapshotVersion getSnapshotVersion() {
            return null;
        }

        @Override
        public TestBubbleOneToOne createTypeInstance() {
            return null;
        }

        @Override
        public Class<? extends BubbleObject> getBaseType() {
            return TestBubbleOneToOne.class;
        }

        @Override
        public Class<TestBubbleOneToOne> getType() {
            return null;
        }

        @Override
        public Class getValueType() {
            return null;
        }

        @Override
        public Class<? extends BubbleId<? super TestBubbleOneToOne>> getBaseIdType() {
            return null;
        }

        @Override
        public boolean equalsIgnoreSnapshotVersion(Object object) {
            return false;
        }

        @Override
        public BubbleId<? super TestBubbleOneToOne> asSnapshotVersion(SnapshotVersion snapshotVersion) {
            return null;
        }

        @Override
        public BubbleId<? super TestBubbleOneToOne> asSnapshotVersion(BubbleId<?> bubbleId) {
            return null;
        }

        @Override
        public BubbleId<? super TestBubbleOneToOne> asSnapshotVersionOld() {
            return null;
        }

        @Override
        public BubbleId<? super TestBubbleOneToOne> asSnapshotVersionCurrent() {
            return null;
        }

        @Override
        public BubbleId<? super TestBubbleOneToOne> asBase() {
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestBubbleOneToOneId that = (TestBubbleOneToOneId) o;
            return value == that.value;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public int compareTo(Object o) {
            return 0;
        }
    }

    public static class TestBubbleOneToOneIdType extends BubbleIdType<TestBubbleOneToOneId> {
        @Override
        public Long getValue(Object id) {
            return ((TestBubbleOneToOneId) id).getValue();
        }

        @Override
        public TestBubbleOneToOneId createId(Long value) {
            return new TestBubbleOneToOneId(value);
        }

        @Override
        public Class<TestBubbleOneToOneId> returnedClass() {
            return TestBubbleOneToOneId.class;
        }
    }

    @Entity
    public static class TestBubbleOneToOne implements BubbleObject {
        @Id
        @Type(TestBubbleOneToOneIdType.class)
        private TestBubbleOneToOneId id;

        @SuppressWarnings({"FieldCanBeLocal", "unused"}) // JPA
        @OneToOne(cascade = CascadeType.ALL)
        private TestBubbleOneToOneComponent component;

        public TestBubbleOneToOne() {
        }

        public TestBubbleOneToOne(TestBubbleOneToOneId id) {
            this.id = id;
        }

        @Override
        public BubbleId<?> getBubbleId() {
            return null;
        }

        @Override
        public BubbleId<?> getId() {
            return id;
        }

        @Override
        public void setId(BubbleId<?> id) {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public Store store() {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public void register(Store store) {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public void setFlushed(boolean flushed) {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        @Override
        public boolean isFlushed() {
            throw new UnsupportedOperationException("Brukes ikke i testen");
        }

        public void setComponent(TestBubbleOneToOneComponent components) {
            this.component = components;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestBubbleOneToOne that = (TestBubbleOneToOne) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }

    @Entity
    @Inheritance
    public abstract static class TestBubbleOneToOneComponent implements EntityComponent {
        @Id
        @GeneratedValue(strategy = GenerationType.TABLE)
        private Long id;

        public TestBubbleOneToOneComponent() {
        }

        public TestBubbleOneToOneComponent(Long id) {
            this.id = id;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestBubbleOneToOneComponent that = (TestBubbleOneToOneComponent) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }

    @Entity
    public static class TestBubbleOneToOneComponent1 extends TestBubbleOneToOneComponent {
        public TestBubbleOneToOneComponent1() {
        }

        public TestBubbleOneToOneComponent1(Long id) {
            super(id);
        }
    }

    @Entity
    public static class TestBubbleOneToOneComponent2 extends TestBubbleOneToOneComponent {
        public TestBubbleOneToOneComponent2() {
        }

        public TestBubbleOneToOneComponent2(Long id) {
            super(id);
        }
    }
}
