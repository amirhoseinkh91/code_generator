package ir.viratech.commons.code_gen.ui.test.model.user.role;

import ir.viratech.commons.code_gen.ui.test.model.user.Feature;
import ir.viratech.commons.util.synchronized_lazy.AbstractNotNullSynchronizedLazy;
import ir.viratech.commons.util.synchronized_lazy.SynchronizedLazy;

import java.util.ArrayList;
import java.util.List;

public class FeatureDAO {
	
	
	// ========== BEGIN singleton ==========
	private FeatureDAO() {
	}

	private static final SynchronizedLazy<FeatureDAO> SYNCH_LAZY_INSTANCE = new AbstractNotNullSynchronizedLazy<FeatureDAO>() {
		@Override
		public FeatureDAO create() {
			return new FeatureDAO();
		}
	};

	public static FeatureDAO getInstance() {
		return SYNCH_LAZY_INSTANCE.get();
	}
	// =========== END singleton ===========
	
	
	
	public List<Feature> findAll() {
		return new ArrayList<>();
	}
	
}
