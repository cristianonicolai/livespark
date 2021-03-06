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

package org.livespark.formmodeler.service.impl.fieldProviders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;

import org.jboss.errai.common.client.api.Assert;
import org.livespark.formmodeler.model.FieldDefinition;
import org.livespark.formmodeler.model.FieldTypeInfo;
import org.livespark.formmodeler.service.FieldProvider;

/**
 * @author Pere Fernandez <pefernan@redhat.com>
 */
public abstract class BasicTypeFieldProvider<T extends FieldDefinition> implements FieldProvider<T> {

    protected List<Class> supportedTypes = new ArrayList<>();

    @PostConstruct
    protected void registerFields() {
        doRegisterFields();
    }

    public abstract int getPriority();

    protected abstract void doRegisterFields();

    public Class[] getSupportedTypes() {
        return supportedTypes.toArray( new Class[ supportedTypes.size() ] );
    }

    protected void registerPropertyType( Class type ) {
        Assert.notNull( "Type cannot be null", type );

        supportedTypes.add( type );
    }

    @Override
    public T getFieldByType( FieldTypeInfo typeInfo ) {
        if ( typeInfo.getType() == null ) {
            return getDefaultField();
        }
        if ( isSupported( typeInfo ) ) {
            return createFieldByType( typeInfo );
        }
        return null;
    }

    public abstract T createFieldByType( FieldTypeInfo typeInfo );

    @Override
    public boolean isCompatible( FieldDefinition field ) {
        Assert.notNull( "Field cannot be null", field );

        if ( field.getCode().equals( getProviderCode() ) ) {
            return true;
        }

        return isSupported( field.getFieldTypeInfo() );
    }

    protected boolean isSupported( FieldTypeInfo typeInfo ) {
        for ( Class type : supportedTypes ) {
            if ( type.getName().equals( typeInfo.getType() ) ) {
                return true;
            }
        }
        return false;
    }

    public boolean supports( Class clazz ) {
        return Arrays.asList( getSupportedTypes() ).contains( clazz );
    }
}
