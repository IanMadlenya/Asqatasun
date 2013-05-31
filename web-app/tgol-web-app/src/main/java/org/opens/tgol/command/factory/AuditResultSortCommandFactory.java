/*
 * Tanaguru - Automated webpage assessment
 * Copyright (C) 2008-2013  Open-S Company
 *
 * This file is part of Tanaguru.
 *
 * Tanaguru is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact us by mail: open-s AT open-s DOT com
 */
package org.opens.tgol.command.factory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.opens.tgol.command.AuditResultSortCommand;
import org.opens.tgol.form.*;
import org.opens.tgol.form.builder.FormFieldBuilder;
import org.opens.tgol.form.parameterization.helper.FormFieldHelper;

/**
 *
 * @author jkowalczyk
 */
public final class AuditResultSortCommandFactory {

    private String themeKey;
    public String getThemeKey() {
        return themeKey;
    }

    public void setThemeKey(String themeKey) {
        this.themeKey = themeKey;
    }

    private static AuditResultSortCommandFactory auditResultSortCommandFactory;

    /**
     * Factory has default constructor
     */
    private AuditResultSortCommandFactory(){}

    public static synchronized AuditResultSortCommandFactory getInstance() {
        if (auditResultSortCommandFactory == null) {
            auditResultSortCommandFactory = new AuditResultSortCommandFactory();
        }
        return auditResultSortCommandFactory;
    }

    
    /**
     * 
     * @param webResourceId
     * @param displayScope
     * @param displayScopeChoice
     * @param formFieldList
     * @return 
     */
    public AuditResultSortCommand getInitialisedAuditResultSortCommand (
            Long webResourceId,
            String displayScope, 
            boolean displayScopeChoice,
            List<FormField> formFieldList) {

        AuditResultSortCommand auditResultSortCommand = new AuditResultSortCommand();
        auditResultSortCommand.setDisplayScope(displayScope);
        auditResultSortCommand.setDisplayScopeChoice(displayScopeChoice);
        
        for (FormField ff :formFieldList) {
            if (ff instanceof SelectFormField) {
                for (Map.Entry<String, List<SelectElement>> entry : ((SelectFormField)ff).getSelectElementMap().entrySet()) {
                    for (SelectElement se : entry.getValue()) {
                        if (se.getDefault() && se.getEnabled()) {
                            auditResultSortCommand.getSortOptionMap().put(entry.getKey(), se.getValue());
                        }
                    }
                }
            } else if (ff instanceof CheckboxFormField) {
                CheckboxFormField cff = ((CheckboxFormField)ff);
                String code = cff.getCode();
                List<String> selectedElements = new ArrayList<String>();
                for (CheckboxElement ce : cff.getCheckboxElementList()) {
                    if (ce.getEnabled() && ce.getSelected()) {
                        selectedElements.add(ce.getValue());
                    }
                }
                auditResultSortCommand.getSortOptionMap().put(code, selectedElements.toArray());
            }
        }
        auditResultSortCommand.setWebResourceId(webResourceId);
        return auditResultSortCommand;
    }

    /**
     *
     * @param referential
     * @param formFieldBuilderList
     * @return
     */
    public List<FormField> getFormFieldBuilderCopy(
        String referential,
        List<FormFieldBuilder> formFieldBuilderList) {
        // for each result page, only one referential and so only one theme list
        // is possible. We need to retrieve the appropriate theme list regarding
        // the referential
        // Copy the audit setup form field map from the builders
        List<FormField> initialisedFormFielList = new LinkedList<FormField>();
        boolean hasTheme = false;
        for (FormFieldBuilder formFieldBuilder : formFieldBuilderList) {
            if (!formFieldBuilder.getI18nKey().equals(themeKey)) {
                initialisedFormFielList.add(formFieldBuilder.build());
            } else if (!hasTheme && referential.equals(formFieldBuilder.getValue())){
                initialisedFormFielList.add(formFieldBuilder.build());
                hasTheme = true;
            }
        }
        return initialisedFormFielList;
    }

    /**
     * 
     * @param formFieldBuilderList
     * @param auditResultSortCommand
     * @return
     */
    public List<FormField> getFormFieldBuilderCopy(
        String referential,
        List<FormFieldBuilder> formFieldBuilderList,
        AuditResultSortCommand auditResultSortCommand) {
        // for each result page, only one referential and so only one theme list
        // is possible. We need to retrieve the appropriate theme list regarding
        // the referential
        // Copy the audit setup form field map from the builders
        List<FormField> initialisedFormFieldList = new LinkedList<FormField>();
        boolean hasTheme = false;
        for (FormFieldBuilder formFieldBuilder : formFieldBuilderList) {
            if (!formFieldBuilder.getI18nKey().equals(themeKey)) {
                initialisedFormFieldList.add(formFieldBuilder.build());
            } else if (!hasTheme && referential.equals(formFieldBuilder.getValue())){
                initialisedFormFieldList.add(formFieldBuilder.build());
                hasTheme = true;
            }
        }
        FormFieldHelper.setValueToFormField(
                initialisedFormFieldList, 
                auditResultSortCommand.getSortOptionMap());
        return initialisedFormFieldList;
    }

}