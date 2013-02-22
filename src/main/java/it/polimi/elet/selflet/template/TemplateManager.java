package it.polimi.elet.selflet.template;

import java.util.List;

import com.google.common.collect.Lists;

public class TemplateManager implements ITemplateManager {

	private static volatile ITemplateManager instance;
	private final List<String> templates;

	private TemplateManager() {
		templates = Lists.newArrayList();
		loadTemplates();
	}

	public static ITemplateManager getInstance() {
		if (instance == null) {
			instance = new TemplateManager();
		}
		return instance;
	}

	private void loadTemplates() {
		// TODO, just a mock
		templates.add("selflet1");
		templates.add("selflet2");
		templates.add("knitro");
		templates.add("video_provisioning");

	}

	@Override
	public List<String> getTemplates() {
		return templates;
	}

}
