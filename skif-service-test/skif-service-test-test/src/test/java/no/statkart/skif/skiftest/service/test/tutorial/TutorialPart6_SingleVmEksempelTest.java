package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Modules;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.service.annotation.Implementation;
import no.statkart.skif.service.annotation.WSServiceChain;
import no.statkart.skif.service.chain.CallServiceChainFactory;
import no.statkart.skif.service.provider.ServiceProvider;
import no.statkart.skif.service.proxy.D2WAdapterProxyHandler;
import no.statkart.skif.service.proxy.W2DAdapterProxyHandler;
import no.statkart.skif.skiftest.service.test.tutorial.ex.ExMapper;
import no.statkart.skif.skiftest.service.test.tutorial.ex.ExMapping;
import no.statkart.skif.skiftest.service.test.tutorial.ex.api.ExService;
import no.statkart.skif.skiftest.service.test.tutorial.ex.api.ExServiceImpl;
import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.A;
import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.B;
import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.C;
import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.server.ExServiceWSBean;
import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.server.ExServiceWSI;
import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.server.ExServiceWSIDummyImpl;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Denne tutorial viser det overordnede konseptet for hvordan SKIFs Service Rammeverk utfører remote kall over
 * Web Services. Eksemplet bruker ikke ekte Web Services, men har klasser som tilsvarer de som brukes for JAX-WS.
 * Eksemplet fokusere på mapping av services, domeneklasser og de servicekjeder som inngår. Andre
 * aspekter er uteladt, herunder mapping av ServiceContext parameter og RequestScope håndtering på server.
 * <p>
 * JAX-WS genererer ut fra WSDL-en følgende klasser:
 * <ul>
 *     <li>{@code ExService}: Interface for Web Service</li>
 *     <li>{@code ExServiceWS}: Klient subklasse for Web Service</li>
 * </ul>
 * Merk at {@code ExService} bruker samme klassenavn som vår egen api service men ligger i en annen pakke. Det gjør
 * at vi må skrive pakkenavnet helt ut for en av servicene.
 * <p>
 * Eksemplet viser hvordan en Java service kan mappes via en Web Service til Java service implementasjonen på serveren.
 * <ol>
 * <li>{@code api.ExService} på klienten mappes til {@code wsapi.klient.ExServiceWS}</li>
 * <li>Parametre til metode på klient må mappes fra {@code api}-pakken til {@code wsapi}-pakken</li>
 * <li>Returverdi på klient må mappes fra {@code wsapi}-pakken til {@code api}-pakken</li>
 * <li>Web Service kall utføres fra {@code wsapi.klient.ExServiceWS} til {@code wsapi.server.ExServiceWSBean} på server/li>
 * <li>{@code wsapi.server.ExServiceWSBean} på server mapper kall til {@code api.ExServiceImpl}</li>
 * <li>Parametre til Web Service på tjener mappes fra {@code wsapi}-pakken til {@code api}-pakken</li>
 * <li>Returverdi på server mappes fra {@code api}-pakken til {@code wsapi}-pakken</li>
 * </ol>
 *
 * For bedre å forstå koden så husk på følgende:
 * <ul>
 *     <li>Hvis en service skal ha en proxykjede så brukes en {@code ServiceProvider}</li>
 *     <li>Proxykjede konfigures vha Guice multibinder for {@code CallServiceChainFactory}</li>
 *     <li>Vi bruker {@code @Implementation} for å binne avsluttende proxykjedeledd {@code ToImplementationProxyHandler}
 *     til serviceimplementasjon som skal anvendes.</li>
 *     <li>Vi bruker {@code @WSServiceChain} for å angi ExServiceWSI  som har Web Service Proxykjede</li>
 * </ul>
 */
@Test(groups = "server-required")
public class TutorialPart6_SingleVmEksempelTest {

    /**
     * Binder opp {@code ExServiceWSI} til dummy implementasjon inntilvidere.
     */
    public void server_step1() {
        Injector serverInjector = Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(ExServiceWSI.class).annotatedWith(Implementation.class).to(ExServiceWSIDummyImpl.class);
                    }
                }
        );
        // Test at service kan hentes ut med annotation Implementation
        ExServiceWSI exServiceWSI = serverInjector.getInstance(Key.get(ExServiceWSI.class, Implementation.class));
        assertThat(exServiceWSI.doEx(new A(10), new B(5))).isEqualTo(new C(15,2));
    }


    /**
     * Binder opp {@code ExServiceWSBean}  til {@code ExServiceWSI} med proxykjede lagt på.
     */
    public void server_step2() {
        Injector serverInjector = Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(ExServiceWSI.class).annotatedWith(WSServiceChain.class).toProvider(new TypeLiteral<ServiceProvider<ExServiceWSI>>() {});
                        bind(ExServiceWSI.class).annotatedWith(Implementation.class).to(ExServiceWSIDummyImpl.class);
                    }
                },
                new ProxyHandlerModule(ToImplementationProxyHandler.class, 0, ExServiceWSI.class), // Denne bruker @Implementation versjonen av ExServiceWSI
                new ProxyHandlerModule(OnServerProxyHandler.class, 1, ExServiceWSI.class)
                );
        // Hent ut ExService implementasjonen uten proxy kjede
        ExServiceWSI exServiceWSIImpl = serverInjector.getInstance(Key.get(ExServiceWSI.class, Implementation.class));
        assertThat(exServiceWSIImpl).isInstanceOf(ExServiceWSIDummyImpl.class);
        assertThat(exServiceWSIImpl.doEx(new A(10), new B(5))).isEqualTo(new C(15,2));

        // Hent ut ExService med proxy kjede
        ExServiceWSI exServiceWSIProxy = serverInjector.getInstance(Key.get(ExServiceWSI.class, WSServiceChain.class));
        exServiceWSIProxy.doEx(new A(10), new B(5));

        // Kan nå også hente ut ExServiceBean, denne krever jo en ExServiceWSI med annotatsjon WSServiceChain
        ExServiceWSBean exServiceWSBean = serverInjector.getInstance(ExServiceWSBean.class);
        exServiceWSBean.doEx(new A(10), new B(5));
    }



    /**
     * Endre {@code ExServiceWSI} til ikke å bruke dummy implementasjon, men mappe kall via adapter til {@code api.ExServiceImpl}
     * <p>
     * <lo>
     *     <li>Fjerne bruken av {@code ToImplementationProxyHandler} for {@code ExServiceWSI}</li>
     *     <li>Bruke {@code W2DAdapterProxyHandler} for {@code ExServiceWSI} i stedet</li>
     *     <li>Bruke {@code ToImplementationProxyHandler} for {@code api.ExService}</li>
     *     <li>Vi må også ha en mapping som mapper mellom {@code api} og {@code wsapi}
     * </lo>
     */
    public void server_step3_mappingTest() {
        ExMapping mapping = new ExMapper().getMapping();
        assertThat(mapping.w2d(new A(5))).isEqualTo(new no.statkart.skif.skiftest.service.test.tutorial.ex.api.A(5));
        assertThat(mapping.w2d(new B(5))).isEqualTo(new no.statkart.skif.skiftest.service.test.tutorial.ex.api.B(5));
        assertThat(mapping.w2d(new C(10, 2))).isEqualTo(new no.statkart.skif.skiftest.service.test.tutorial.ex.api.C(10, 2));
        assertThat(mapping.d2w(new no.statkart.skif.skiftest.service.test.tutorial.ex.api.A(5))).isEqualTo(new A(5));
    }

    public void server_step4() {
        // Bindinger for ExService
        Module serviceApiModule = Modules.combine(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(ExService.class).annotatedWith(WSServiceChain.class).toProvider(new TypeLiteral<ServiceProvider<ExService>>() {});
                        bind(ExService.class).annotatedWith(Implementation.class).to(ExServiceImpl.class);
                    }
                },
                new ProxyHandlerModule(ToImplementationProxyHandler.class, 0, ExService.class),
                new ProxyHandlerModule(OnServerProxyHandler.class, 1, ExService.class)
        );
        // Bindinger for ExServiceWSI
        Module serviceWSModule = Modules.combine(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(Mapping.class).toInstance(new ExMapper().getMapping());
                        bind(ExServiceWSI.class).annotatedWith(WSServiceChain.class).toProvider(new TypeLiteral<ServiceProvider<ExServiceWSI>>() {});
                        Multibinder<CallServiceChainFactory<ExServiceWSI>> multibinder
                                = Multibinder.newSetBinder(binder(), new TypeLiteral<CallServiceChainFactory<ExServiceWSI>>() {});
                        multibinder.addBinding().toProvider(
                                new CallServiceChainFactoryProvider<>(
                                        new TypeLiteral<W2DAdapterProxyHandler<ExServiceWSI, ExService>>() {}, 0));
                    }
                },
                new ProxyHandlerModule(OnServerProxyHandler.class, 1, ExServiceWSI.class)
        );
        Injector serverInjector = Guice.createInjector(serviceApiModule, serviceWSModule);

        // Hent ut ExService
        System.out.println("Kaller ExService:");
        ExService exService = serverInjector.getInstance(Key.get(ExService.class, WSServiceChain.class));
        no.statkart.skif.skiftest.service.test.tutorial.ex.api.A a = new no.statkart.skif.skiftest.service.test.tutorial.ex.api.A(10);
        no.statkart.skif.skiftest.service.test.tutorial.ex.api.B b = new no.statkart.skif.skiftest.service.test.tutorial.ex.api.B(5);
        assertThat(exService.doEx(a, b)).isNotNull();

        // Henter ut ExServiceWSI med proxy kjede WSServiceChain
        System.out.println("Kaller ExServiceWSI:");
        ExServiceWSI exServiceWSIProxy = serverInjector.getInstance(Key.get(ExServiceWSI.class, WSServiceChain.class));
        exServiceWSIProxy.doEx(new A(10), new B(5));

        // Henter ut ExServiceWSBean
        System.out.println("Kaller ExserviceWSBean:");
        ExServiceWSBean exServiceWSBean = serverInjector.getInstance(ExServiceWSBean.class);
        exServiceWSBean.doEx(new A(10), new B(5));
    }

    /**
     * Dette er den endelige versjonen for server injectoren
     */
    Injector createServerInjector() {
        // Bindinger for ExService
        Module serviceApiModule = Modules.combine(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(ExService.class).annotatedWith(WSServiceChain.class).toProvider(new TypeLiteral<ServiceProvider<ExService>>() {});
                        bind(ExService.class).annotatedWith(Implementation.class).to(ExServiceImpl.class);
                    }
                },
                new ProxyHandlerModule(ToImplementationProxyHandler.class, 0, ExService.class),
                new ProxyHandlerModule(OnServerProxyHandler.class, 10, ExService.class)
        );
        // Bindinger for ExServiceWSI
        Module serviceWSModule = Modules.combine(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(Mapping.class).toInstance(new ExMapper().getMapping());
                        bind(ExServiceWSI.class).annotatedWith(WSServiceChain.class).toProvider(new TypeLiteral<ServiceProvider<ExServiceWSI>>() {});
                        Multibinder<CallServiceChainFactory<ExServiceWSI>> multibinder
                                = Multibinder.newSetBinder(binder(), new TypeLiteral<CallServiceChainFactory<ExServiceWSI>>() {});
                        multibinder.addBinding().toProvider(
                                new CallServiceChainFactoryProvider<>(
                                        new TypeLiteral<W2DAdapterProxyHandler<ExServiceWSI, ExService>>() {}, 0));
                    }
                },
                new ProxyHandlerModule(OnServerProxyHandler.class, 1, ExServiceWSI.class)
        );
        return Guice.createInjector(serviceApiModule, serviceWSModule);
    }

    /**
     * På klientsiden må følgende gjøres:
     * <ol>
     *     <li>Opprette {@code wsapi.klient.ExServiceWS} instans som kaller ExServiceWSBean på server</li>
     *     <li>Binde {@code wsapi.klient.ExService} implementasjon til {@code wsapi.klient.ExServiceWS} instans</li>
     *     <li>Opprette proxykjede for {@code wsapi.klient.ExService}
     *     <li>Konfigurere proxykjede for {@code wsapi.klient.ExService} til å kalle implementasjon av service
     * </ol>
     *
     */
    public void klient_Step1() {
        Injector serverInjector = createServerInjector();

        // Bindinger for ExService
        Module clientWsModule = Modules.combine(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.klient.ExService.class)
                                .toProvider(new TypeLiteral<ServiceProvider<no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.klient.ExService>>() {
                                });

                        // Merk: Her kobler vi klienten til serveren via serverInjector
                        ExServiceWSBean exServiceWSBean = serverInjector.getInstance(ExServiceWSBean.class);
                        bind(no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.klient.ExService.class)
                                .annotatedWith(Implementation.class)
                                .toInstance(new no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.klient.ExServiceWS(exServiceWSBean));

                    }
                },
                new ProxyHandlerModule(ToImplementationProxyHandler.class, 0, no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.klient.ExService.class),
                new ProxyHandlerModule(OnClientProxyHandler.class, 1, no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.klient.ExService.class)
        );



        Injector clientInjector = Guice.createInjector(clientWsModule);


        // Hent ut ExService
        System.out.println("Kaller wsapi.klient.ExService:");
        no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.klient.ExService exServiceWS =
                clientInjector.getInstance(Key.get(no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.klient.ExService.class));
        assertThat(exServiceWS.doEx(new A(10), new B(5))).isEqualTo(new C(15, 2));
    }

    /**
     * For å kunne bruke {@code api.ExService} på klient må følgende gjøres:
     * <ol>
     *     <li>Opprette mapping fra {@code api} domeneklasser til {@code wsapi}-domeneklasser</li>
     *     <li>Opprette proxykjede for {@code api.ExService}
     *     <li>Konfigurere proxykjede for {@code api.ExService} til å kalle D2WAdaptor som forwarder til {@code wsapi.klient.ExService}
     * </ol>
     *
     * Vi har nå et full oppsett av tjeneste fra klient til tjener.
     */
    public void klient_Step2() {
        Injector serverInjector = createServerInjector();

        // Bindinger for wsapi.klient.ExService
        Module clientWsModule = Modules.combine(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.klient.ExService.class)
                                .toProvider(new TypeLiteral<ServiceProvider<no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.klient.ExService>>() {
                                });
                        ExServiceWSBean exServiceWSBean = serverInjector.getInstance(ExServiceWSBean.class);
                        bind(no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.klient.ExService.class)
                                .annotatedWith(Implementation.class)
                                .toInstance(new no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.klient.ExServiceWS(exServiceWSBean));

                    }
                },
                new ProxyHandlerModule(ToImplementationProxyHandler.class, 0, no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.klient.ExService.class),
                new ProxyHandlerModule(OnClientProxyHandler.class, 1, no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.klient.ExService.class)
        );


        Module clientApiModule = Modules.combine(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(Mapping.class).toInstance(new ExMapper().getMapping());
                        bind(ExService.class).toProvider(new TypeLiteral<ServiceProvider<ExService>>() {});
                        Multibinder<CallServiceChainFactory<ExService>> multibinder
                                = Multibinder.newSetBinder(binder(), new TypeLiteral<CallServiceChainFactory<ExService>>() {});
                        multibinder.addBinding().toProvider(
                                new CallServiceChainFactoryProvider<>(
                                        new TypeLiteral<D2WAdapterProxyHandler<ExService, no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.klient.ExService>>() {}, 0));
                    }
                },
                new ProxyHandlerModule(OnClientProxyHandler.class, 1, ExService.class)
        );
        Injector clientInjector = Guice.createInjector(clientWsModule, clientApiModule);

        // Hent ut ExService
        System.out.println("Kaller api.klient.ExService:");
        ExService exService = clientInjector.getInstance(ExService.class);
        no.statkart.skif.skiftest.service.test.tutorial.ex.api.A a = new no.statkart.skif.skiftest.service.test.tutorial.ex.api.A(10);
        no.statkart.skif.skiftest.service.test.tutorial.ex.api.B b = new no.statkart.skif.skiftest.service.test.tutorial.ex.api.B(5);
        assertThat(exService.doEx(a, b)).isEqualTo(new no.statkart.skif.skiftest.service.test.tutorial.ex.api.C(15, 2));
    }
}
