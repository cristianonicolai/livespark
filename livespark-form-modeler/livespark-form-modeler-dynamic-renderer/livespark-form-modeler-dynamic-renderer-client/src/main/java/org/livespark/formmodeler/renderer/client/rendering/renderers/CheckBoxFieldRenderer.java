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
package org.livespark.formmodeler.renderer.client.rendering.renderers;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.livespark.formmodeler.model.impl.basic.checkBox.CheckBoxFieldDefinition;
import org.livespark.formmodeler.renderer.client.rendering.FieldRenderer;

/**
 * Created by pefernan on 9/21/15.
 */
@Dependent
public class CheckBoxFieldRenderer extends FieldRenderer<CheckBoxFieldDefinition> {
    private CheckBox checkbox;

    @Override
    public String getName() {
        return "CheckBox";
    }

    @Override
    public void initInputWidget() {
        checkbox = new CheckBox(field.getLabel());
        checkbox.setEnabled(!field.getReadonly());
    }

    @Override
    public IsWidget getInputWidget() {
        return checkbox;
    }

    protected void addFormGroupContents( FormGroup group ) {
        group.add(checkbox);
        HelpBlock helpBlock = new HelpBlock();
        helpBlock.setId( getHelpBlokId( field ) );
        group.add(helpBlock);
    }

    @Override
    public String getSupportedCode() {
        return CheckBoxFieldDefinition.CODE;
    }
}
