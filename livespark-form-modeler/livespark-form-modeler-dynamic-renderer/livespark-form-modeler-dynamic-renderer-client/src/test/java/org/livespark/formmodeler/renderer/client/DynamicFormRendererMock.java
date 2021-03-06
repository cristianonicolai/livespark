/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.livespark.formmodeler.renderer.client;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.livespark.formmodeler.model.FieldDefinition;
import org.livespark.formmodeler.processing.engine.handling.FormHandler;
import org.livespark.formmodeler.renderer.service.Model2FormTransformerService;

public class DynamicFormRendererMock extends DynamicFormRenderer {

    protected Object model;

    protected boolean binded = false;

    public DynamicFormRendererMock( DynamicFormRendererView view,
                                    Caller<Model2FormTransformerService> transformerService,
                                    FormHandler formHandler ) {
        super( view, transformerService, formHandler );
    }

    @Override
    protected void doBind( Widget input, FieldDefinition field ) {
    }
}
