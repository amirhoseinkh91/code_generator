package ir.viratech.commons.code_gen.ui.test.model.user;

import ir.viratech.commons.model.UIDAndDisplayStringProvider;

import org.apache.commons.lang.StringUtils;

public class Feature implements UIDAndDisplayStringProvider {
	
	
	private String extuid;
	@Override
	public String getExtuid() {
		return this.extuid;
	}
	public void setExtuid(String extuid) {
		this.extuid = extuid;
	}
	
	private String name;
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String description;
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	@Override
	public String getDisplayString() {
		if (StringUtils.isBlank(this.getDescription())) {
			return this.getName();
		}
		return this.getDescription();
	}
}
