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

package org.livespark.formmodeler.renderer.backend.service.impl.processors;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.livespark.formmodeler.metaModel.Option;
import org.livespark.formmodeler.metaModel.RadioGroup;
import org.livespark.formmodeler.model.impl.basic.selectors.SelectorOption;
import org.livespark.formmodeler.model.impl.basic.selectors.StringSelectorOption;
import org.livespark.formmodeler.model.impl.basic.selectors.radioGroup.RadioGroupBase;
import org.livespark.formmodeler.renderer.backend.service.impl.FieldSetting;
import org.livespark.formmodeler.renderer.service.SelectorDataProviderManager;
import org.livespark.formmodeler.service.FieldManager;

@Dependent
public class RadioGroupFieldAnnotationProcessor extends AbstractFieldAnnotationProcessor {

    @Inject
    public RadioGroupFieldAnnotationProcessor( FieldManager fieldManager ) {
        super( fieldManager );
    }

    @Override
    public boolean supportsAnnotation( Annotation annotation ) {
        return annotation instanceof RadioGroup;
    }

    @Override
    public RadioGroupBase buildFieldDefinition( Annotation annotation, FieldSetting setting ) {

        if ( !supportsAnnotation( annotation )) {
            return null;
        }

        RadioGroupBase fieldDefinition = (RadioGroupBase) fieldManager.getFieldFromProvider( RadioGroupBase.CODE, setting.getTypeInfo() );

        RadioGroup radioGroupDef = (RadioGroup) annotation;

        if ( !radioGroupDef.provider().className().isEmpty() ) {
            String providerId =  radioGroupDef.provider().type().getCode()
                    + SelectorDataProviderManager.SEPARATOR
                    + radioGroupDef.provider().className();

            fieldDefinition.setDataProvider( providerId );
        } else {
            List<SelectorOption> options = new ArrayList<SelectorOption>();
            for ( Option option : radioGroupDef.options() ) {
                StringSelectorOption selectorOption = new StringSelectorOption();
                selectorOption.setValue( option.value() );
                selectorOption.setText( option.text() );
                selectorOption.setDefaultValue( option.isDefault() );
                options.add( selectorOption );
            }
            fieldDefinition.setOptions( options );
        }

        fieldDefinition.setInline( radioGroupDef.inline() );

        return fieldDefinition;
    }
}
