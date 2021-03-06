package com.itemis.gef4.tutorial.mindmap.parts;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef.mvc.parts.IVisualPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;
import com.itemis.gef4.tutorial.mindmap.visuals.ConnectionVisual;

import javafx.scene.Node;

public class ConnectionPart extends AbstractFXContentPart<Connection> {
	
	private static final String START_ROLE = "START";
	private static final String END_ROLE = "END";
	
	@Override
	public com.itemis.gef4.tutorial.mindmap.model.Connection getContent() {
		return (com.itemis.gef4.tutorial.mindmap.model.Connection) super.getContent();
	}
	
	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		SetMultimap<Object, String> anchorages = HashMultimap.create();

		anchorages.put(getContent().getSource(), START_ROLE);
		anchorages.put(getContent().getTarget(), END_ROLE);

		return anchorages;
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		return Collections.emptyList();
	}

	@Override
	protected Connection createVisual() {
		return new ConnectionVisual();
	}

	@Override
	protected void doRefreshVisual(Connection visual) {
		// nothing to do here
	}
	
	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		
		// find a anchor provider, which must be registered in the module
		// be aware to use the right interfaces (Proviser is used a lot)
		@SuppressWarnings("serial")
		Provider<? extends IAnchor> adapter = anchorage.getAdapter(AdapterKey.get(new TypeToken<Provider<? extends IAnchor>>() {}));
		if (adapter == null) {
			throw new IllegalStateException("No adapter  found for <" + anchorage.getClass() + "> found.");
		}
		IAnchor anchor = adapter.get();
		
		if (role.equals(START_ROLE)) {
			getVisual().setStartAnchor(anchor);
		} else if (role.equals(END_ROLE)) {
			getVisual().setEndAnchor(anchor);
		} else {
			throw new IllegalArgumentException("Invalid role: "+role);
		}
	}

	@Override
	protected void detachFromAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		if (role.equals(START_ROLE)) {
			getVisual().setStartPoint(getVisual().getStartPoint());
		} else if (role.equals(END_ROLE)) {
			getVisual().setEndPoint(getVisual().getEndPoint());
		} else {
			throw new IllegalArgumentException("Invalid role: "+role);
		}
	}
}
