/*******************************************************************************
 * Copyright (c) 2014 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.rest.tests.tck;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.mylyn.bugzilla.rest.tests.AbstractTckTest;
import org.eclipse.mylyn.bugzilla.rest.tests.TckFixture;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner.FixtureDefinition;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner.RunOnlyWhenProperty;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.Test;

@FixtureDefinition(fixtureClass = TckFixture.class, fixtureType = "bugzillaREST")
@RunOnlyWhenProperty(property = "default", value = "1")
public class AbstractRepositoryConnectorTest extends AbstractTckTest {

	public AbstractRepositoryConnectorTest(TckFixture fixture) {
		super(fixture);
	}

	@Test
	public void testApplyDefaultCategory() throws Exception {
		TaskRepository repository = fixture().createRepository();
		assertNull("repository.getCategory() should be null", repository.getCategory());
		fixture().connector().applyDefaultCategory(repository);
		assertNotNull("repository.getCategory() should not be null", repository.getCategory());
	}
}
