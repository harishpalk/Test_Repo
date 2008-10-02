/*******************************************************************************
 * Copyright (c) 2004, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.forms.widgets.Section;

/**
 * A layout that uses the width hint or client area of a composite to recommend the width of its children, allowing
 * children to fill the width and specify their preferred height for a given width.
 * 
 * Intended for use with a composite that contains a single child that should fill available horizontal space.
 * 
 * @author David Green
 */
public class FillWidthLayout extends Layout {

	private final int marginLeft;

	private final int marginRight;

	private final int marginTop;

	private final int marginBottom;

	private int widthHintMargin = 15;

	private Composite layoutAdvisor;

	/**
	 * create with 0 margins
	 * 
	 */
	public FillWidthLayout() {
		this(0, 0, 0, 0);
	}

	/**
	 * create while specifying margins
	 * 
	 * @param marginLeft
	 *            the left margin in pixels, or 0 if there should be none
	 * @param marginRight
	 *            the right margin in pixels, or 0 if there should be none
	 * @param marginTop
	 *            the top margin in pixels, or 0 if there should be none
	 * @param marginBottom
	 *            the bottom margin in pixels, or 0 if there should be none
	 */
	public FillWidthLayout(int marginLeft, int marginRight, int marginTop, int marginBottom) {
		this(null, marginLeft, marginRight, marginTop, marginBottom);
	}

	/**
	 * create specifying margins and a {@link #getLayoutAdvisor() layout advisor}.
	 * 
	 * @param layoutAdvisor
	 *            the composite that is used to advise on layout based on its {@link Composite#getClientArea() client
	 *            area}.
	 * @param marginLeft
	 *            the left margin in pixels, or 0 if there should be none
	 * @param marginRight
	 *            the right margin in pixels, or 0 if there should be none
	 * @param marginTop
	 *            the top margin in pixels, or 0 if there should be none
	 * @param marginBottom
	 *            the bottom margin in pixels, or 0 if there should be none
	 */
	public FillWidthLayout(Composite layoutAdvisor, int marginLeft, int marginRight, int marginTop, int marginBottom) {
		this.layoutAdvisor = layoutAdvisor;
		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
		this.marginTop = marginTop;
		this.marginBottom = marginBottom;
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			widthHintMargin = 15;
		} else {
			widthHintMargin = 25;
		}
	}

	/**
	 * calculate the client area of the given container, accomodating for insets and margins.
	 */
	private int calculateWidthHint(Composite container) {
		return calculateWidthHint(container, layoutAdvisor == null);
	}

	/**
	 * calculate the client area of the given container, accomodating for insets and margins.
	 */
	private int calculateWidthHint(Composite container, boolean layoutAdvisorHit) {
		if (container == layoutAdvisor) {
			layoutAdvisorHit = true;
		}
		Rectangle clientArea = container.getClientArea();
		int horizontalMargin = 0;
		if (clientArea.width <= 1 || !layoutAdvisorHit) { // sometimes client area is incorrectly reported as 1
			clientArea.width = calculateWidthHint(container.getParent(), layoutAdvisorHit);
		}
		Layout bodyLayout = container.getLayout();
		if (bodyLayout instanceof GridLayout) {
			GridLayout gridLayout = (GridLayout) bodyLayout;
			horizontalMargin = (gridLayout.marginWidth * 2) + gridLayout.marginLeft + gridLayout.marginRight;
		} else if (bodyLayout instanceof FillLayout) {
			FillLayout fillLayout = (FillLayout) bodyLayout;
			horizontalMargin = fillLayout.marginWidth * 2;
		} else if (container instanceof Section) {
			horizontalMargin = ((Section) container).marginWidth * 2;
		} else if (container instanceof CTabFolder) {
			CTabFolder folder = (CTabFolder) container;
			horizontalMargin = folder.marginWidth * 2;
		}
		if (container instanceof ScrolledComposite) {
			ScrolledComposite composite = (ScrolledComposite) container;
			ScrollBar verticalBar = composite.getVerticalBar();
			if (verticalBar != null) {
				int verticalBarWidth = verticalBar.getSize().x;
				horizontalMargin += Math.max(15, verticalBarWidth);
			}
		}
		return clientArea.width - horizontalMargin;
	}

	@Override
	protected Point computeSize(Composite composite, int widthHint, int heightHint, boolean flushCache) {
		int resultX = 1;
		int resultY = 1;

		Control[] children = composite.getChildren();

		if (widthHint <= 0) {
			widthHint = calculateWidthHint(composite);
			if (widthHint < 300) {
				widthHint = 300;
			} else {
				widthHint -= widthHintMargin;
			}
		}

		int horizontalMargin = marginLeft + marginRight;

		for (Control control : children) {
			Point sz = control.computeSize(widthHint - horizontalMargin, -1, flushCache);

			resultX = Math.max(resultX, sz.x);
			resultY = Math.max(resultY, sz.y);
		}

		return new Point(resultX + horizontalMargin, resultY + marginTop + marginBottom);
	}

	@Override
	protected void layout(Composite composite, boolean flushCache) {

		Rectangle area = composite.getClientArea();
		if (area.width == 0) {
			area.width = calculateWidthHint(composite);
		}

		// account for margins
		area.x += marginLeft;
		area.y += marginTop;
		area.width -= (marginRight + marginLeft);
		area.height -= (marginBottom + marginTop);

		Control[] children = composite.getChildren();

		for (Control control : children) {
			control.setBounds(area);
		}
	}

	/**
	 * the composite that is used to advise on layout based on its {@link Composite#getClientArea() client area}.
	 * 
	 * @return the layout advisor, or null if there is none
	 */
	public Composite getLayoutAdvisor() {
		return layoutAdvisor;
	}

	/**
	 * the composite that is used to advise on layout based on its {@link Composite#getClientArea() client area}.
	 * 
	 * @param layoutAdvisor
	 *            the layout advisor, or null if there is none
	 */
	public void setLayoutAdvisor(Composite layoutAdvisor) {
		this.layoutAdvisor = layoutAdvisor;
	}

}