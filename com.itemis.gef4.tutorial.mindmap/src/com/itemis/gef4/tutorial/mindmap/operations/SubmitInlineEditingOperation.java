package com.itemis.gef4.tutorial.mindmap.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.mvc.operations.ITransactionalOperation;

import com.itemis.gef4.tutorial.mindmap.models.IInlineEditableField;
import com.itemis.gef4.tutorial.mindmap.parts.IInlineEditablePart;

public class SubmitInlineEditingOperation extends AbstractOperation implements ITransactionalOperation {

	private IInlineEditableField field;
	private Object newValue;
	private Object oldValue;
	private IInlineEditablePart host;
	
	
	
	
	public SubmitInlineEditingOperation(IInlineEditablePart host, IInlineEditableField field) {
		super("Submit value for "+field.getPropertyName());
		this.host = host;
		this.field = field;
		this.newValue = field.getNewValue();
		this.oldValue = field.getOldValue();
	}

	@Override
	public boolean isContentRelevant() {
		return true;
	}

	@Override
	public boolean isNoOp() {
		return oldValue.equals(newValue);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		System.out.println("Ececuting submit: "+newValue);
		host.submitEditing(field, newValue);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		host.submitEditing(field, oldValue);
		return Status.OK_STATUS;
	}

}
