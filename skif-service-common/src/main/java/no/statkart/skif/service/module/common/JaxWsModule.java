package no.statkart.skif.service.module.common;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.OptionalBinder;
import no.statkart.skif.service.HttpRequestAuthenticationOverride;
import no.statkart.skif.service.ws.DefaultWebServiceExceptionMapper;
import no.statkart.skif.service.ws.JaxWsAuthenticationHandler;
import no.statkart.skif.service.ws.WebServiceExceptionMapper;

import javax.net.ssl.HostnameVerifier;
import javax.xml.namespace.QName;
import jakarta.xml.ws.handler.Handler;
import java.util.List;
import java.util.function.Function;

public interface JaxWsModule extends Module {

    final class Common extends AbstractModule {
        private static final Common INSTANCE = new Common();

        private Common() {}

        @Override
        protected void configure() {
            OptionalBinder.newOptionalBinder(binder(), new TypeLiteral<HttpRequestAuthenticationOverride>() {});
            OptionalBinder.newOptionalBinder(binder(), WebServiceExceptionMapper.class).setDefault().to(DefaultWebServiceExceptionMapper.class);
            OptionalBinder.newOptionalBinder(binder(), HostnameVerifier.class);
        }

        public static Common getInstance() {
            return INSTANCE;
        }
    }

    interface JaxWsModuleStrategyJEE {

        Function<String, String> DEFAULT_PORT_TYPE_TO_SERVICE_NAME_STRATEGY = portTypeName -> portTypeName + "WS";
        Function<QName, String> DEFAULT_SERVICE_NAME_TO_WSDL_LOCATION_STRATEGY = serviceName -> "/META-INF/wsdls/" + serviceName.getLocalPart() + ".wsdl";
        List<Class<? extends Handler>> DEFAULT_JAXWS_HANDLER_CLASSES = ImmutableList.of(
                JaxWsAuthenticationHandler.class
        );

        void setMaxIdlePooled(int maxIdlePooled);

        void setMaxTotalPooled(int maxTotalPooled);

        void setMinIdlePooled(int minIdlePooled);

        void setPortTypeToServiceNameStrategy(Function<? super String, String> portTypeToServiceNameStrategy);

        void setServiceNameToWsdlLocationStrategy(Function<? super QName, String> serviceNameToWsdlLocationStrategy);

        List<Class<? extends Handler>> getJaxwsHandlerClasses();
    }
}
