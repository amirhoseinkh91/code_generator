package ir.justro.commons.code_gen.ui.test.model.user;

import ir.viratech.commons.model.UIDAndDisplayStringProvider;

public class AuthUser implements UIDAndDisplayStringProvider {
	
	private String extuid;
	@Override
	public String getExtuid() {
		return this.extuid;
	}
	public void setExtuid(String extuid) {
		this.extuid = extuid;
	}
	
	private String username;
	public String getUsername() {
		return this.username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	private String password;
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	private boolean enabled;
	public boolean isEnabled() {
		return this.enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
	@Override
	public String getDisplayString() {
		return this.getUsername();
	}
}
