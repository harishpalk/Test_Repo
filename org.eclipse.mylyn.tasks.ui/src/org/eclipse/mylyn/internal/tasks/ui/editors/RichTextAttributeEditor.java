/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Iterator;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.internal.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.internal.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.TaskTextViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.themes.IThemeManager;

/**
 * Text viewer generally used for displaying non-editable text. No annotation model or spell checking support. Supports
 * cut/copy/paste/etc..
 * 
 * For viewing and editing text. Spell checking w/ annotations supported One or two max per editor, any more and the
 * spell checker will bring the editor to a grinding halt.
 * 
 * @author Steffen Pingel
 */
// TODO EDITOR support setting values
public class RichTextAttributeEditor extends AbstractAttributeEditor {

	private RepositoryTextViewer viewer;

	// TODO EDITOR
	private boolean spellCheckingEnabled;

	private final int style;

	public RichTextAttributeEditor(AbstractAttributeEditorManager manager, RepositoryTaskAttribute taskAttribute) {
		this(manager, taskAttribute, SWT.V_SCROLL);
	}

	public RichTextAttributeEditor(AbstractAttributeEditorManager manager, RepositoryTaskAttribute taskAttribute,
			int style) {
		super(manager, taskAttribute);
		this.style = style;
		setLayoutHint(new LayoutHint(RowSpan.MULTIPLE, ColumnSpan.MULTIPLE));
	}

	private void configureAsTextEditor(Document document) {
		AnnotationModel annotationModel = new AnnotationModel();
		viewer.showAnnotations(false);
		viewer.showAnnotationsOverview(false);

		IAnnotationAccess annotationAccess = new DefaultMarkerAnnotationAccess();

		final SourceViewerDecorationSupport support = new SourceViewerDecorationSupport(viewer, null, annotationAccess,
				EditorsUI.getSharedTextColors());

		@SuppressWarnings("unchecked")
		Iterator e = new MarkerAnnotationPreferences().getAnnotationPreferences().iterator();
		while (e.hasNext()) {
			support.setAnnotationPreference((AnnotationPreference) e.next());
		}

		support.install(EditorsUI.getPreferenceStore());

		viewer.getTextWidget().setIndent(2);
		viewer.getTextWidget().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				support.uninstall();
			}
		});

		// !Do Not Delete! hover manager that shows text when we hover
		// AnnotationBarHoverManager fAnnotationHoverManager = new AnnotationBarHoverManager(fCompositeRuler,
		//     commentViewer, new AnnotationHover(fAnnotationModel), new AnnotationConfiguration());
		// fAnnotationHoverManager.install(annotationRuler.getControl());

		// !Do Not Delete! Sample debugging code
		// document.set("Here's some texst so that we have somewhere to show an error");
		//
		// // // add an annotation
		// ErrorAnnotation errorAnnotation = new ErrorAnnotation(1, "");
		// // lets underline the word "texst"
		// fAnnotationModel.addAnnotation(errorAnnotation, new Position(12, 5));

		// CoreSpellingProblem iProblem = new CoreSpellingProblem(12, 5, 1, 
		//    "problem message", "theword", false, false, document, "task editor");
		// editorInput.getName()
		//
		// fAnnotationModel.addAnnotation(new ProblemAnnotation(iProblem, null), new Position(12, 5));

		viewer.setDocument(document, annotationModel);
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		TaskRepository taskRepository = getAttributeEditorManager().getTaskRepository();
		viewer = new RepositoryTextViewer(taskRepository, parent, SWT.FLAT | SWT.MULTI | SWT.WRAP | style);

		MenuManager menuManager = viewer.getMenuManager();
		getAttributeEditorManager().configureContextMenuManager(menuManager);
		viewer.setMenu(menuManager.createContextMenu(viewer.getTextWidget()));

		// NOTE: configuration must be applied before the document is set in order for
		// hyper link coloring to work, the Presenter requires the document object up front
		TextSourceViewerConfiguration viewerConfig = new TaskTextViewerConfiguration(spellCheckingEnabled);
		viewer.configure(viewerConfig);

		Document document = new Document(getValue());
		if (getTaskAttribute().isReadOnly()) {
			viewer.setEditable(false);
			viewer.setDocument(document);
		} else {
			viewer.setEditable(true);
			configureAsTextEditor(document);
		}

		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		Font font = themeManager.getCurrentTheme().getFontRegistry().get(TaskListColorsAndFonts.TASK_EDITOR_FONT);
		viewer.getTextWidget().setFont(font);
		toolkit.adapt(viewer.getTextWidget(), true, true);

		getAttributeEditorManager().addTextViewer(viewer);
		decorate(viewer.getTextWidget());
		setControl(viewer.getTextWidget());
	}

	public String getValue() {
		return getAttributeMapper().getValue(getTaskAttribute());
	}

	public SourceViewer getViewer() {
		return viewer;
	}

	public boolean isSpellCheckingEnabled() {
		return spellCheckingEnabled;
	}

	public void setSpellCheckingEnabled(boolean spellCheckingEnabled) {
		this.spellCheckingEnabled = spellCheckingEnabled;
	}

	public void setValue(String value) {
		getAttributeMapper().setValue(getTaskAttribute(), value);
	}

}