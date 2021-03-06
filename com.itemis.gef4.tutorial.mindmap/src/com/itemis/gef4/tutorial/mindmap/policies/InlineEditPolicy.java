package com.itemis.gef4.tutorial.mindmap.policies;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.mvc.fx.policies.IFXOnClickPolicy;
import org.eclipse.gef.mvc.policies.AbstractInteractionPolicy;

import com.itemis.gef4.tutorial.mindmap.models.IInlineEditableField;
import com.itemis.gef4.tutorial.mindmap.models.InlineEditModel;
import com.itemis.gef4.tutorial.mindmap.operations.SubmitInlineEditingOperation;
import com.itemis.gef4.tutorial.mindmap.parts.IInlineEditablePart;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class InlineEditPolicy extends AbstractInteractionPolicy<Node> implements IFXOnClickPolicy {

	@Override
	public void click(MouseEvent e) {

		if (e.getClickCount() != 2) {
			return;
		}

		InlineEditModel editModel = getHost().getRoot().getViewer().getAdapter(InlineEditModel.class);

		IInlineEditablePart host = (IInlineEditablePart) getHost();

		Node target = (Node) e.getTarget();
		IInlineEditableField field = getEditableField(host, target);
		
		if (field!=null) {
			
			// store some information before editing
			editModel.setHost(getHost());
			editModel.setCurrentEditableField(field);
			
			// start editing
			host.startEditing(field);
			Node editorNode = field.getEditorNode();

			
			// add some listeners
			// they need to be somewhere else - where? 
			editorNode.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent event) {
					if (event.getCode() == KeyCode.ESCAPE) {
						host.endEditing(field);
					}

					if (event.getCode() == KeyCode.ENTER && event.isAltDown()) {
						SubmitInlineEditingOperation op = new SubmitInlineEditingOperation(host, field);
						
						try {
							getHost().getRoot().getViewer().getDomain().execute(op, new NullProgressMonitor());
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
						host.endEditing(field);
						
					}

				}

			});
			editorNode.focusedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

					if (!newValue) {
						Task<Boolean> task = new Task<Boolean>() {

							@Override
							protected Boolean call() throws Exception {
								Thread.sleep(200); // we wait if we get the
													// focus right back, like in
													// clicking inside the
													// textfield

								if (!editorNode.focusedProperty().getValue()) {
									Platform.runLater(new Runnable() {

										@Override
										public void run() {
											host.endEditing(field);
										}
									});

								}

								return true;
							}
						};
						new Thread(task).start();
					}
				}
			});
			editorNode.requestFocus();

		}

	}

	private IInlineEditableField getEditableField(IInlineEditablePart host, EventTarget target) {

		for (IInlineEditableField field : host.getEditableFields()) {
			if (field.isTarget(target))
				return field;
		}

		return null;
	}

}
