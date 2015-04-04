package org.jboss.arquillian.extension.switchyard.client;

import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.spi.LoadableExtension;

public class SwitchYardExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(AuxiliaryArchiveAppender.class, SwitchYardAppender.class);
    }
}
