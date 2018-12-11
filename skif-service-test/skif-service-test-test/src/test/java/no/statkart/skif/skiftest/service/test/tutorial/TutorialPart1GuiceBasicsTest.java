package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
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
 * @author Henrik Fredholm
 */
@Test(groups = "server-required")
public class TutorialPart1GuiceBasicsTest {

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

    public void alternativeWayOfSpecifyingClassesUsingTypeLiteral() {
        Injector injector = injectorWithXAsSingleton();
        X x1 = injector.getInstance(Key.get(new TypeLiteral<X>(){}));
        X x2 = injector.getInstance(X.class);
        assertThat(x1).isSameAs(x2);
    }

    public void dynamicSpecificationOfClassesUsingTypeLiteral() {
        Injector injector = injectorWithXAsSingleton();
        Class<?> xClass = X.class;
        X x1 = (X) injector.getInstance(Key.get(TypeLiteral.get(xClass)));
        X x2 = (X) injector.getInstance(xClass);
        assertThat(x1).isSameAs(x2);
    }

    // Generics
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


    private Injector injectorWithGenericsListInstance() {
        return Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(new TypeLiteral<List<String>>(){}).toInstance(Arrays.asList("A", "B", "C"));
            }
        });
    }

    public void bindingToGenericInstance() {
        Injector injector = injectorWithGenericsListInstance();
        List<String> listOfString = injector.getInstance(Key.get(new TypeLiteral<List<String>>(){}));
        assertThat(listOfString).isNotNull();
        assertThat(listOfString).hasSize(3);
    }

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

    private Injector injectorWithNamedInstance() {
        return Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(String.class).annotatedWith(Names.named("test1")).toInstance("Hello");
                bind(String.class).annotatedWith(Names.named("test2")).toInstance("Bye");
            }
        });
    }

    public void namedInstances() {
        Injector injector = injectorWithNamedInstance();
        String hello = injector.getInstance(Key.get(String.class, Names.named("test1")));
        String bye = injector.getInstance(Key.get(String.class, Names.named("test2")));
        assertThat(hello).isEqualTo("Hello");
        assertThat(bye).isEqualTo("Bye");
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