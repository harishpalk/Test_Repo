/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Set;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeAllTasksJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeQueriesJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeRepositoriesJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeTasksJob;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.SynchronizeJob;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.sync.IRepositorySynchronizationManager;
import org.eclipse.mylyn.tasks.ui.ITasksJobFactory;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Steffen Pingel
 */
public class TasksJobFactory implements ITasksJobFactory {

	private final TaskList taskList;

	private final IRepositorySynchronizationManager synchronizationManager;

	private final TaskRepositoryManager repositoryManager;

	public TasksJobFactory(TaskList taskList, IRepositorySynchronizationManager synchronizationManager,
			TaskRepositoryManager repositoryManager) {
		this.taskList = taskList;
		this.synchronizationManager = synchronizationManager;
		this.repositoryManager = repositoryManager;
	}

	public SynchronizeJob createSynchronizeTasksJob(AbstractRepositoryConnector connector, Set<AbstractTask> tasks) {
		SynchronizeAllTasksJob job = new SynchronizeAllTasksJob(taskList, synchronizationManager, repositoryManager,
				connector, tasks);
		job.setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
		job.setPriority(Job.LONG);
		return job;
	}

	public SynchronizeJob createSynchronizeTasksJob(AbstractRepositoryConnector connector,
			TaskRepository taskRepository, Set<AbstractTask> tasks) {
		SynchronizeTasksJob job = new SynchronizeTasksJob(taskList, synchronizationManager, connector, taskRepository,
				tasks);
		job.setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
		job.setPriority(Job.LONG);
		return job;
	}

	public SynchronizeJob createSynchronizeQueriesJob(AbstractRepositoryConnector connector, TaskRepository repository,
			Set<AbstractRepositoryQuery> queries) {
		SynchronizeJob job = new SynchronizeQueriesJob(taskList, synchronizationManager, connector, repository, queries);
		job.setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
		job.setPriority(Job.DECORATE);
		return job;
	}

	public SynchronizeJob createSynchronizeRepositoriesJob(Set<TaskRepository> repositories) {
		SynchronizeRepositoriesJob job = new SynchronizeRepositoriesJob(taskList, synchronizationManager,
				repositoryManager, repositories);
		job.setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
		job.setPriority(Job.DECORATE);
		return job;
	}

}