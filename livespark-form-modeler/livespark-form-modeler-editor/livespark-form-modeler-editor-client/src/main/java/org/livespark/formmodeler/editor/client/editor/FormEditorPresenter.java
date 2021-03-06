/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.livespark.formmodeler.editor.client.editor;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.livespark.formmodeler.editor.client.editor.rendering.EditorFieldLayoutComponent;
import org.livespark.formmodeler.editor.client.resources.i18n.FormEditorConstants;
import org.livespark.formmodeler.editor.client.type.FormDefinitionResourceType;
import org.livespark.formmodeler.editor.model.FormModelerContent;
import org.livespark.formmodeler.editor.service.FormEditorService;
import org.livespark.formmodeler.model.DataHolder;
import org.livespark.formmodeler.model.FieldDefinition;
import org.livespark.formmodeler.model.FormDefinition;
import org.livespark.formmodeler.renderer.client.rendering.FieldLayoutComponent;
import org.livespark.formmodeler.renderer.service.FormRenderingContext;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.ComponentDropEvent;
import org.uberfire.ext.layout.editor.client.api.ComponentRemovedEvent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.api.LayoutEditor;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.type.FileNameUtil;

/**
 * Created by pefernan on 7/7/15.
 */
@Dependent
@WorkbenchEditor(identifier = "LSFormEditor", supportedTypes = { FormDefinitionResourceType.class })
public class FormEditorPresenter extends KieEditor {

    public interface FormEditorView extends KieEditorView {
        public void init( FormEditorPresenter presenter );
        public void setupLayoutEditor( LayoutEditor layoutEditor );
    }

    public interface DataHolderAdminView extends IsWidget {

        public void init( FormEditorPresenter presenter );

        public void initView();

        void refreshView();

        public void addDataType(String dataType);
    }

    @Inject
    protected LayoutEditor layoutEditor;

    @Inject
    protected HTMLLayoutDragComponent htmlLayoutDragComponent;

    @Inject
    private Caller<MetadataService> metadataService;

    @Inject
    protected BusyIndicatorView busyIndicatorView;

    @Inject
    protected FormEditorHelper editorContext;

    @Inject
    protected SyncBeanManager beanManager;

    private FormEditorView view;
    private DataHolderAdminView objectsView;
    private FormDefinitionResourceType resourceType;
    private Caller<FormEditorService> editorService;
    private TranslationService translationService;

    @Inject
    public FormEditorPresenter(FormEditorView view,
                               DataHolderAdminView objectsView,
                               FormDefinitionResourceType resourceType,
                               Caller<FormEditorService> editorService,
                               SyncBeanManager beanManager,
                               TranslationService translationService ) {
        super( view );
        this.view = view;
        this.objectsView = objectsView;
        this.resourceType = resourceType;
        this.editorService = editorService;
        this.beanManager = beanManager;
        this.translationService = translationService;
    }

    @OnStartup
    public void onStartup( final ObservablePath path,
            final PlaceRequest place ) {

        init(path, place, resourceType);
    }

    @Override
    protected void loadContent() {
        editorService.call( new RemoteCallback<FormModelerContent>() {
            @Override
            public void callback( FormModelerContent content ) {
                doLoadContent( content );
            }
        }, getNoSuchFileExceptionErrorCallback() ).loadContent( versionRecordManager.getCurrentPath() );
    }

    @Override
    protected void save( String commitMessage ) {
        synchronizeFormLayout();
        editorService.call( getSaveSuccessCallback( editorContext.getContent().hashCode() )  ).save( versionRecordManager.getCurrentPath(),
                editorContext.getContent(),
                metadata,
                commitMessage );
    }

    protected void synchronizeFormLayout() {
        editorContext.getFormDefinition().setLayoutTemplate( layoutEditor.getLayout() );
    }

    public void doLoadContent( FormModelerContent content ) {
        busyIndicatorView.hideBusyIndicator();

        // TODO: fix this, this return avoids to reload the layout editor again
        if (editorContext.getContent() != null) return;

        editorContext.initHelper( content );

        layoutEditor.init(content.getDefinition().getName(), getLayoutComponents());

        if (content.getDefinition().getLayoutTemplate() == null) content.getDefinition().setLayoutTemplate( new LayoutTemplate(  ) );

        loadAvailableFields(content);

        layoutEditor.loadLayout(content.getDefinition().getLayoutTemplate());

        resetEditorPages(content.getOverview());

        view.init( this );

        objectsView.init( this );

        view.setupLayoutEditor( layoutEditor );
    }

    protected List<LayoutDragComponent> getLayoutComponents() {

        List<LayoutDragComponent>  list = new ArrayList<LayoutDragComponent>();
        list.add( htmlLayoutDragComponent );
        list.addAll( editorContext.getBaseFieldsDraggables() );

        return list;
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        String fileName = FileNameUtil.removeExtension( versionRecordManager.getCurrentPath(), resourceType );
        return translationService.format( FormEditorConstants.FormEditorPresenterTitle, fileName );
    }

    @WorkbenchMenu
    public Menus getMenus() {
        if ( menus == null ) {
            makeMenuBar();
        }
        return menus;
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    protected void makeMenuBar() {
        menus = menuBuilder
                .addSave(versionRecordManager.newSaveMenuItem(new Command() {
                    @Override
                    public void execute() {
                        onSave();
                    }
                }))
                .addCopy(versionRecordManager.getCurrentPath(),
                        fileNameValidator)
                .addRename(versionRecordManager.getPathToLatest(),
                        fileNameValidator)
                .addDelete(versionRecordManager.getPathToLatest())
                .addNewTopLevelMenu(versionRecordManager.buildMenu())
                .build();
    }

    public LayoutTemplate getFormTemplate() {
        return layoutEditor.getLayout();
    }

    public FormDefinition getFormDefinition() {
        return editorContext.getFormDefinition();
    }

    public void addDataHolder( final String name, final String type ) {
        if ( editorContext.addDataHolder(name, type) ) {
            editorService.call( new RemoteCallback<List<FieldDefinition>>() {
                @Override
                public void callback( List<FieldDefinition> fields ) {
                    addAvailableFields( name, fields );
                }
            } ).getAvailableFieldsForType( editorContext.getContent().getPath(), name, type );
        }
    }

    private void loadAvailableFields( FormModelerContent content ) {
        if ( content.getAvailableFields() == null ) return;

        for ( String holderName : content.getAvailableFields().keySet() ) {
            List<FieldDefinition> availableFields = content.getAvailableFields().get( holderName );
            addAvailableFields( holderName, availableFields );
        }
    }

    protected void addAvailableFields(String holderName, List<FieldDefinition> fields) {
        editorContext.addAvailableFields( fields );

        LayoutDragComponentGroup group = new LayoutDragComponentGroup( holderName );

        for ( FieldDefinition field : fields ) {
            EditorFieldLayoutComponent layoutFieldComponent = beanManager.lookupBean( EditorFieldLayoutComponent.class ).newInstance();
            if (layoutFieldComponent != null) {
                layoutFieldComponent.init( editorContext.getRenderingContext(), field );
                group.addLayoutDragComponent( field.getId(), layoutFieldComponent );
            }
        }

        layoutEditor.addDraggableComponentGroup( group );
    }

    public void onDropComponent(@Observes ComponentDropEvent event) {
        String formId = event.getComponent().getProperties().get( FieldLayoutComponent.FORM_ID );

        if ( editorContext.getFormDefinition().getId().equals( formId ) ) {
            String fieldId = event.getComponent().getProperties().get( FieldLayoutComponent.FIELD_ID );

            FieldDefinition field = editorContext.getDroppedField( fieldId );
            if (field != null) {
                layoutEditor.removeDraggableGroupComponent( field.getModelName(), field.getId() );
            }
        }
    }

    public void onRemoveComponent(@Observes ComponentRemovedEvent event) {
        String formId = event.getLayoutComponent().getProperties().get( FieldLayoutComponent.FORM_ID );

        if ( editorContext.getFormDefinition().getId().equals( formId ) ) {
            String fieldId = event.getLayoutComponent().getProperties().get( FieldLayoutComponent.FIELD_ID );
            FieldDefinition field = editorContext.removeField( fieldId );
            if (field != null) {
                EditorFieldLayoutComponent layoutFieldComponent = beanManager.lookupBean( EditorFieldLayoutComponent.class ).newInstance();
                if (layoutFieldComponent != null) {
                    layoutFieldComponent.init( editorContext.getRenderingContext(), field );
                    layoutEditor.addDraggableComponentToGroup( field.getModelName(), field.getId(), layoutFieldComponent );
                }
            }
        }
    }

    public void initDataObjectsTab() {
        objectsView.initView();
        editorService.call(new RemoteCallback<List<String>>() {
            @Override
            public void callback(List<String> availableDataObjects) {
                for ( String dataType : availableDataObjects ) {
                    objectsView.addDataType( dataType );
                }
            }
        }).getAvailableDataObjects(versionRecordManager.getCurrentPath());
    }

    public boolean hasBindedFields(DataHolder dataHolder) {
        return editorContext.hasBindedFields( dataHolder );
    }

    public void removeDataHolder( String holderName) {
        if ( getFormDefinition().getDataHolderByName( holderName ) != null ) {
            editorContext.removeDataHolder( holderName, layoutEditor.getLayout() );
            layoutEditor.removeDraggableComponentGroup( holderName );
            objectsView.refreshView();
        }
    }

    public FormRenderingContext getRenderingContext() {
        synchronizeFormLayout();
        return editorContext.getRenderingContext();
    }
}
