/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.livespark.formmodeler.codegen.view.impl.html.inputs;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.livespark.formmodeler.model.impl.basic.slider.DoubleSliderDefinition;
import org.livespark.formmodeler.model.impl.basic.slider.IntegerSliderDefinition;
import org.livespark.formmodeler.model.impl.basic.slider.SliderBase;
import org.livespark.formmodeler.service.FieldManager;

/**
 * @author Pere Fernandez <pefernan@redhat.com>
 */
@Dependent
public class SliderTemplateProvider extends AbstractTemplateProvider {

    @Inject
    public SliderTemplateProvider( FieldManager fieldManager ) {
        super( fieldManager );
    }

    @Override
    protected String[] getSupportedFieldCodes() {
        return new String[]{ SliderBase.CODE };
    }

    @Override
    protected String getTemplateForFieldTypeCode( String fieldCode ) {
        return "/org/livespark/formmodeler/codegen/view/impl/html/templates/slider.mv";
    }
}
