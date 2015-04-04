package org.jboss.arquillian.extension.switchyard.client;

import org.jboss.arquillian.extension.switchyard.container.MockHandler;
import org.jboss.arquillian.extension.switchyard.container.SwitchYardInterceptor;
import org.jboss.arquillian.extension.switchyard.container.SwitchYardMockRegister;
import org.jboss.arquillian.container.test.spi.client.deployment.CachedAuxilliaryArchiveAppender;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class SwitchYardAppender extends CachedAuxilliaryArchiveAppender {

    @Override
    protected Archive<?> buildArchive() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "arquillian-switchyard.jar")
                .addClasses(SwitchYardInterceptor.class,
                        SwitchYardMockRegister.class,
                        MockHandler.class);
        return archive;
    }
}
