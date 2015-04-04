package org.jboss.arquillian.extension.switchyard.container;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.namespace.QName;
import org.switchyard.ExchangeHandler;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;

@ApplicationScoped
@Named
public class SwitchYardMockRegister {

    @Inject
    private SwitchYardInterceptor mockHandler;

    public void registerInOutService(QName serviceName, ExchangeHandler serviceHandler) {
        mockHandler.registerService(serviceName, serviceHandler, new InOutService());
    }

    public void registerInOnlyService(QName serviceName, ExchangeHandler serviceHandler) {
        mockHandler.registerService(serviceName, serviceHandler, new InOnlyService());
    }

    public void unregisterAll() {
        mockHandler.unregisterAll();
    }

}
