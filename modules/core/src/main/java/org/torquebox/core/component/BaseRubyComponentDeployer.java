package org.torquebox.core.component;

import java.util.List;
import java.util.Set;

import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceName;
import org.torquebox.core.injection.analysis.Injectable;
import org.torquebox.core.injection.analysis.InjectionIndex;

public abstract class BaseRubyComponentDeployer implements DeploymentUnitProcessor {

    protected void addInjections(DeploymentPhaseContext phaseContext, ComponentResolver resolver, ServiceBuilder<ComponentResolver> builder)
            throws DeploymentUnitProcessingException {
        DeploymentUnit unit = phaseContext.getDeploymentUnit();
        InjectionIndex index = unit.getAttachment( InjectionIndex.ATTACHMENT_KEY );

        Set<Injectable> injectables = index.getInjectablesFor( getInjectionPathPrefixes( phaseContext ) );

        for (Injectable injectable : injectables) {
            try {
                ServiceName serviceName = injectable.getServiceName( phaseContext );
                builder.addDependency( serviceName, resolver.getInjector( injectable.getKey() ) );
            } catch (Exception e) {
                throw new DeploymentUnitProcessingException( e );
            }
        }
    }

    protected abstract List<String> getInjectionPathPrefixes(DeploymentPhaseContext phaseContext);

}