package ir.justro.commons.code_gen.ui.test.model.user;

import java.util.Set;

import ir.justro.commons.code_gen.ui.test.model.user.role.UserRole;
import ir.viratech.commons.model.UIDAndDisplayStringProvider;

public class User implements UIDAndDisplayStringProvider {
	
	private String extuid;
	@Override
	public String getExtuid() {
		return this.extuid;
	}
	public void setExtuid(String extuid) {
		this.extuid = extuid;
	}
	
	private String firstName;
	public String getFirstName() {
		return this.firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	private String lastName;
	public String getLastName() {
		return this.lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	private AuthUser authUser;
	public AuthUser getAuthUser() {
		return this.authUser;
	}
	public void setAuthUser(AuthUser authUser) {
		this.authUser = authUser;
	}
	
	private Set<UserRole> roles;
	public Set<UserRole> getRoles() {
		return this.roles;
	}
	public void setRoles(Set<UserRole> roles) {
		this.roles = roles;
	}
	
	
	
	public String getUsername() {
		return this.getAuthUser().getUsername();
	}
	public void setUsername(String username) {
		this.getAuthUser().setUsername(username);
	}
	
	public String getPassword() {
		return this.getAuthUser().getPassword();
	}
	public void setPassword(String password) {
		this.getAuthUser().setPassword(password);
	}
	
	public Boolean isEnabled() {
		return this.getAuthUser().isEnabled();
	}
	public void setEnabled(Boolean enabled) {
		this.getAuthUser().setEnabled(enabled);
	}
	
	
	

	@Override
	public String getDisplayString() {
		return this.getFirstName() + " " + this.getLastName() + " (" + this.getUsername() + ")";
	}
	
}
