package com.synergy.bpms;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.test.JBPMHelper;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.*;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

public class ProcessMain {

	public static void main(String[] args) {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieHelper kieHelper = new KieHelper();
		KieBase kieBase = kieHelper
	    		.addResource(ResourceFactory.newClassPathResource("helloWorldbpms.bpmn2"))
	    		.build();

		RuntimeManager manager = createRuntimeManager(kieBase);
		RuntimeEngine engine = manager.getRuntimeEngine(null);
		KieSession ksession = engine.getKieSession();
		TaskService taskService = engine.getTaskService();

		ksession.startProcess("com.synergy.flow.helloWorldbpms");

		manager.disposeRuntimeEngine(engine);
		System.exit(0);
	}

	private static RuntimeManager createRuntimeManager(KieBase kbase) {
		JBPMHelper.startH2Server();
		JBPMHelper.setupDataSource();
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
		RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory.get()
			.newDefaultBuilder().entityManagerFactory(emf)
			.knowledgeBase(kbase);
		return RuntimeManagerFactory.Factory.get()
			.newSingletonRuntimeManager(builder.get(), "com.synergy.bpms.helloWorldbpms");
	}

}