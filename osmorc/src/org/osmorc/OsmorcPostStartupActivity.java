/*
 * Copyright (c) 2007-2009, Osmorc Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright notice, this list
 *       of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice, this
 *       list of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *     * Neither the name of 'Osmorc Development Team' nor the names of its contributors may be
 *       used to endorse or promote products derived from this software without specific
 *       prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.osmorc;

import aQute.bnd.build.Workspace;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.extensions.ExtensionPointUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.osgi.bnd.imp.BndProjectImporter;

import java.util.List;

/**
 * @author <a href="mailto:robert@beeger.net">Robert F. Beeger</a>
 */
public class OsmorcPostStartupActivity implements StartupActivity.DumbAware {
  @Override
  public void runActivity(@NotNull Project project) {
    Workspace workspace = BndProjectImporter.findWorkspace(project);
    if (workspace != null) {
      Disposable activityDisposable = ExtensionPointUtil.createExtensionDisposable(this, StartupActivity.POST_STARTUP_ACTIVITY);
      Disposer.register(project, activityDisposable);

      MessageBusConnection connection = project.getMessageBus().connect(activityDisposable);

      connection.subscribe(VirtualFileManager.VFS_CHANGES, new MyVfsListener(project));
    }
  }

  private static class MyVfsListener implements BulkFileListener {
    private final Project myProject;

    private MyVfsListener(Project project) {
      myProject = project;
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
      for (VFileEvent event : events) {
        if (event instanceof VFileContentChangeEvent) {
          VirtualFile file = event.getFile();
          if (file != null) {
            String name = file.getName();
            if (BndProjectImporter.BND_FILE.equals(name) || BndProjectImporter.BUILD_FILE.equals(name)) {
              OsmorcProjectComponent.getInstance(myProject).scheduleImportNotification();
              break;
            }
          }
        }
      }
    }
  }
}