package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.util.Types;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that demonstrates basic Guice functionality used in SKIF
 */
@Test(groups = "server-required")
public class TutorialPart1_GuiceBasicsTest {

    private Injector createEmptyInjector() {
        return Guice.createInjector(new AbstractModule() {
            protected void configure() {
            }
        });
    }

    public void createAnyObjectWithEmptyConstructorViaGuide() {
        Injector injector = createEmptyInjector();
        String emptyString = injector.getInstance(String.class);
        assertThat(emptyString).isEmpty();
        X x = injector.getInstance(X.class);
        assertThat(x).isNotNull();
    }

    public void createAnyObjectWithConstructorHavingInjectViaGuide() {
        Injector injector = createEmptyInjector();
        Y y = injector.getInstance(Y.class); // Y has @Inject constructor
        assertThat(y).isNotNull();
        assertThat(y.getX()).isNotNull();
    }

    public void injectorProducesNewInstacesByDefault() {
        Injector injector = createEmptyInjector();
        Y y1 = injector.getInstance(Y.class);
        Y y2 = injector.getInstance(Y.class);
        assertThat(y1).isNotSameAs(y2);
        assertThat(y1.getX()).isNotSameAs(y2.getX());
    }

    private Injector injectorWithXAsSingleton() {
        return Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(X.class).toInstance(new X());
            }
        });
    }

    public void injectorCanBeConfiguredToProduceSingletons() {
        Injector injector = injectorWithXAsSingleton();
        Y y1 = injector.getInstance(Y.class);
        Y y2 = injector.getInstance(Y.class);
        assertThat(y1).isNotSameAs(y2);
        assertThat(y1.getX()).isSameAs(y2.getX());
    }

    public void injectorsDoesNotShareSingletons() {
        Injector injector1 = injectorWithXAsSingleton();
        Injector injector2 = injectorWithXAsSingleton();
        Y y1 = injector1.getInstance(Y.class);
        Y y2 = injector2.getInstance(Y.class);
        assertThat(y1).isNotSameAs(y2);
        assertThat(y1.getX()).isNotSameAs(y2.getX());
    }

    /**
     * Ved bruk providers kan man forsinke opprettelsen av en instans til man kaller
     * {@code provider.get()}. Om man får en ny instans hver gang man kaller {@code  get()} avhenger av
     * hvordan underliggende klasser er konfigurert i Guice.
     */
    public void providersDelayInstanceCreation() {
        Injector injector = createEmptyInjector();
        Provider<Z> zProvider = injector.getProvider(Z.class);
        assertThat(Z.count).isEqualTo(0);
        Z z1 = zProvider.get();
        assertThat(z1).isNotNull();
        assertThat(Z.count).isEqualTo(1);
        Z z2 = zProvider.get();
        assertThat(z2).isNotSameAs(z1);
        assertThat(Z.count).isEqualTo(2);
    }

    /**
     * Alternativ måte å angi klasse via TypeLiteral.
     */
    public void alternativeWayOfSpecifyingClassesUsingTypeLiteral() {
        Injector injector = injectorWithXAsSingleton();
        X x1 = injector.getInstance(Key.get(new TypeLiteral<X>(){}));
        X x2 = injector.getInstance(X.class);
        assertThat(x1).isSameAs(x2);
    }

   /**
     * Noen ganger ønsker man å kunne angi klasse som Guice skal opprette som en parameter til Guice
     */
    public void dynamicSpecificationOfClassesUsingTypeLiteral() {
        Injector injector = injectorWithXAsSingleton();
        Class<?> xClass = X.class;
        X x1 = (X) injector.getInstance(Key.get(TypeLiteral.get(xClass)));
        X x2 = (X) injector.getInstance(xClass);
        assertThat(x1).isSameAs(x2);
    }

    // Generics

    /**
     * For generiske klasser må vi bruke TypeLiteral når man angir klasser til Guice
     */
    public void genericsCreateInstance() {
        Injector injector = createEmptyInjector();
        ArrayList<X> listOfX = injector.getInstance(Key.get(new TypeLiteral<ArrayList<X>>() {}));
        assertThat(listOfX).isNotNull();
        listOfX.add(new X());
        assertThat(listOfX).hasSize(1);
    }

    private Injector injectorWithSubtypeBinding() {
        return Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(List.class).to(LinkedList.class);
                bind(new TypeLiteral<List<X>>(){}).to(new TypeLiteral<ArrayList<X>>(){});
                bind(new TypeLiteral<List<Z>>(){}).to(new TypeLiteral<Vector<Z>>(){});
            }
        });
    }

    /**
     * Typeparameteren har betydning når man angir klasser til Guice
     */
    public void bindingToSubtype() {
        Injector injector = injectorWithSubtypeBinding();
        List listOfObject = injector.getInstance(Key.get(new TypeLiteral<List>(){}));
        assertThat(listOfObject).isNotNull();
        assertThat(listOfObject).isInstanceOf(LinkedList.class);

        List<X> listOfX = injector.getInstance(Key.get(new TypeLiteral<List<X>>() {}));
        assertThat(listOfX).isNotNull();
        assertThat(listOfX).isInstanceOf(ArrayList.class);

        List<Z> listOfZ = injector.getInstance(Key.get(new TypeLiteral<List<Z>>() {}));
        assertThat(listOfZ).isNotNull();
        assertThat(listOfZ).isInstanceOf(Vector.class);
    }


    /**
     * Det er mulig å binde opp spesifikke instanser. Disse blir singletons.
     */
    private Injector injectorWithGenericsListInstance() {
        return Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(new TypeLiteral<List<String>>(){}).toInstance(Arrays.asList("A", "B", "C"));
            }
        });
    }

    /**
     * Hvordan slå opp en generisk instans
     */
    public void bindingToGenericInstance() {
        Injector injector = injectorWithGenericsListInstance();
        List<String> listOfString = injector.getInstance(Key.get(new TypeLiteral<List<String>>(){}));
        assertThat(listOfString).isNotNull();
        assertThat(listOfString).hasSize(3);

        List<String> listOfString2 = injector.getInstance(Key.get(new TypeLiteral<List<String>>(){}));
        assertThat(listOfString2).isSameAs(listOfString); // singleton

    }

    /**
     * Hvordan slå opp en generisk instans via en variabel
     */
    public void dynamicBindingToGenericInstance() {
        Injector injector = injectorWithGenericsListInstance();
        Class<List> listClass = List.class;
        Class stringClass = String.class;
        Object instance = injector.getInstance(Key.get(Types.newParameterizedType(listClass, stringClass)));
        assertThat(instance).isNotNull();
        assertThat(instance).isInstanceOf(List.class);
        assertThat((List<String>)instance).hasSize(3);
        List<String> listOfString = injector.getInstance(Key.get(new TypeLiteral<List<String>>(){}));
        assertThat(instance).isSameAs(listOfString);
    }

    /**
     * Hvordan binde samme type til 2 forskjellige verdier. Man bruke en name qualifier.
     */
    private Injector injectorWithNamedInstance() {
        return Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(String.class).annotatedWith(Names.named("test1")).toInstance("Hello");
                bind(String.class).annotatedWith(Names.named("test2")).toInstance("Bye");
            }
        });
    }

    /**
     * Hvordan slå opp en kalle for en gitt name qualifier.
     */
    public void namedInstances() {
        Injector injector = injectorWithNamedInstance();
        String hello = injector.getInstance(Key.get(String.class, Names.named("test1")));
        String bye = injector.getInstance(Key.get(String.class, Names.named("test2")));
        assertThat(hello).isEqualTo("Hello");
        assertThat(bye).isEqualTo("Bye");
    }

    /**
     * Man kan bruke {@code @Provides} til å konstruere et objekt. Man kan bruke {@code @Singleton} til å
     * angi at instansen er en singleton.
     */
    public void brukAvProvides() {
        Injector injector =  Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(String.class).annotatedWith(Names.named("test1")).toInstance("Hello");
                bind(String.class).annotatedWith(Names.named("test2")).toInstance("Bye");
            }

            @Provides
            List<String> stringListProvider() {
                return Arrays.asList("A", "B", "C");
            }

            @Provides
            @Singleton
            List<Y> yListProvider(Y y) {
                ArrayList<Y> list = new ArrayList<Y>();
                list.add(y);
                return list;
            }

        });

        List<String> listOfString = injector.getInstance(Key.get(new TypeLiteral<List<String>>(){}));
        assertThat(listOfString).isNotNull();
        assertThat(listOfString).hasSize(3);
        List<String> listOfString2 = injector.getInstance(Key.get(new TypeLiteral<List<String>>(){}));
        assertThat(listOfString2).isNotSameAs(listOfString); // not singleton

        List<Y> listOfY = injector.getInstance(Key.get(new TypeLiteral<List<Y>>(){}));
        assertThat(listOfY).hasSize(1);
        List<Y> listOfY2 = injector.getInstance(Key.get(new TypeLiteral<List<Y>>(){}));
        assertThat(listOfY2).isSameAs(listOfY);

    }
}

class X {
}

class Y {
    private final X x;

    @Inject
    Y(X x) {
        this.x = x;
    }

    public X getX() {
        return x;
    }
}

class Z {
    static int count=0;
    public Z() {count++;}
}