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

package org.livespark.formmodeler.renderer.service;

import org.livespark.formmodeler.model.FormDefinition;
import org.livespark.formmodeler.renderer.service.impl.DynamicRenderingContext;

/**
 * @author Pere Fernandez <pefernan@redhat.com>
 */
public interface FormDefintionGenerator {
    FormDefinition generateFormDefinitionForModel( Object model, DynamicRenderingContext context );

    FormDefinition generateFormDefinitionForClass( Class clazz, DynamicRenderingContext context );
}
