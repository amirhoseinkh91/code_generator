package ir.viratech.commons.code_gen.ui.test.model.user.role;

import java.util.HashSet;
import java.util.Set;

import ir.viratech.commons.code_gen.ui.test.model.user.Feature;
import ir.viratech.commons.model.UIDAndDisplayStringProvider;
import ir.viratech.commons.util.relation_map.RelationMap;
import ir.viratech.commons.util.synchronized_lazy.AbstractNotNullSynchronizedLazy;
import ir.viratech.commons.util.synchronized_lazy.SynchronizedLazy;

public class UserRole implements UIDAndDisplayStringProvider {
	
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
	
	private Set<Feature> availableFeatures;
	public Set<Feature> getAvailableFeatures() {
		return this.availableFeatures;
	}
	public void setAvailableFeatures(Set<Feature> availableFeatures) {
		this.availableFeatures = availableFeatures;
	}
	public Set<Feature> getCreatedAvailableFeatures() {
		if (this.availableFeatures == null)
			this.availableFeatures = new HashSet<>();
		return this.availableFeatures;
	}
	
	
	
	
	@Override
	public String getDisplayString() {
		return this.getName();
	}
	
	private final transient SynchronizedLazy<RelationMap<Feature>> syn_featuresRelationMap = new AbstractNotNullSynchronizedLazy<RelationMap<Feature>>() {
		@Override
		public RelationMap<Feature> create() {
			return new RelationMap<>(FeatureDAO.getInstance().findAll(), UserRole.this.getCreatedAvailableFeatures());
		}
	};

	/**
	 * Gets the features relation map.
	 *
	 * @return the features relation map
	 */
	public RelationMap<Feature> getFeaturesRelationMap() {
		return this.syn_featuresRelationMap.get();
	}
}
