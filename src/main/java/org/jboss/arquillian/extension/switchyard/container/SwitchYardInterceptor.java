package org.jboss.arquillian.extension.switchyard.container;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.xml.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangeInterceptor;
import org.switchyard.HandlerException;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.security.SecurityMetadata;

@ApplicationScoped
@Named
public class SwitchYardInterceptor implements ExchangeInterceptor {

    private ServiceDomain serviceDomain;
    private final Set<ServiceActivator> activators = new HashSet<>();

    private static final Logger LOG = LoggerFactory.getLogger(SwitchYardInterceptor.class);

    private void activateService(ServiceActivator activator) {
        unregisterService(activator);

        LOG.info("Registering mock service for {}", activator.getServiceName());
        serviceDomain.registerService(activator.getServiceName(), activator.getServiceInterface(), activator.getServiceHandler());
        serviceDomain.registerServiceReference(activator.getServiceName(), activator.getServiceInterface());
    }

    private boolean unregisterService(ServiceActivator activator) {
        if (serviceDomain == null) {
            LOG.warn("Service domain is not defined yet");
            return false;
        }

        final List<Service> services = serviceDomain.getServices(activator.getServiceName());
        if (!services.isEmpty()) {
            LOG.info("Unregistering services for {}", activator.getServiceName());
            for (Service service : services) {
                service.unregister();
            }
        }

        final ServiceReference serviceReference = serviceDomain.getServiceReference(activator.getServiceName());
        if (serviceReference != null) {
            LOG.info("Unregistering service reference for {}", activator.getServiceName());
            serviceReference.unregister();
        }

        return true;
    }

    synchronized void registerService(QName serviceName, ExchangeHandler serviceHandler, ServiceInterface serviceInterface) {
        final ServiceActivator activator = new ServiceActivator(serviceName, serviceHandler, serviceInterface);
        if (activators.contains(activator)) {
            activators.remove(activator);
        }
        activators.add(activator);

        if (serviceDomain != null) {
            activateService(activator);
        }
    }

    synchronized void unregisterAll() {
        for (ServiceActivator activator : activators) {
            unregisterService(activator);
        }
        activators.clear();
    }

    @Override
    public void before(String target, Exchange exchange) throws HandlerException {
        if (serviceDomain == null) {
            synchronized (this) {
                serviceDomain = SecurityMetadata.getServiceDomain(exchange);
                LOG.info("SwithYard Service Domain found, starting to register mock services");
                for (ServiceActivator activator : activators) {
                    activateService(activator);
                }
            }
        }
    }

    @Override
    public void after(String target, Exchange exchange) throws HandlerException {
        // nop
    }

    @Override
    public List<String> getTargets() {
        return Arrays.asList(CONSUMER, PROVIDER);
    }

    private static class ServiceActivator {

        private final QName serviceName;
        private final ExchangeHandler serviceHandler;
        private final ServiceInterface serviceInterface;

        public ServiceActivator(QName serviceName, ExchangeHandler serviceHandler, ServiceInterface serviceInterface) {
            this.serviceName = serviceName;
            this.serviceHandler = serviceHandler;
            this.serviceInterface = serviceInterface;
        }

        public ExchangeHandler getServiceHandler() {
            return serviceHandler;
        }

        public QName getServiceName() {
            return serviceName;
        }

        public ServiceInterface getServiceInterface() {
            return serviceInterface;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 97 * hash + Objects.hashCode(this.serviceName);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ServiceActivator other = (ServiceActivator) obj;
            return Objects.equals(this.serviceName, other.serviceName);
        }

    }
}
