package se.cambio.cds.gdl.model.readable.rule.lines;

import se.cambio.cds.gdl.model.expression.AssignmentExpression;
import se.cambio.cds.gdl.model.expression.CreateInstanceExpression;
import se.cambio.cds.gdl.model.expression.Variable;
import se.cambio.cds.gdl.model.readable.rule.lines.elements.CDSEntryRuleLineElement;
import se.cambio.cds.gdl.model.readable.rule.lines.elements.GTCodeRuleLineElement;
import se.cambio.cds.gdl.model.readable.rule.lines.elements.StaticTextRuleLineElement;
import se.cambio.cds.gdl.model.readable.rule.lines.interfaces.ActionRuleLine;
import se.cambio.cds.gdl.model.readable.rule.lines.interfaces.InstantiationRuleLine;
import se.cambio.cds.model.instance.ArchetypeReference;
import se.cambio.cds.util.misc.CDSLanguageManager;

import java.util.ArrayList;
import java.util.List;


public class CreateInstanceActionRuleLine extends AssignmentExpressionRuleLine implements ActionRuleLine, InstantiationRuleLine {

    private CDSEntryRuleLineElement cdsEntryRuleLineElement = null;

    public CreateInstanceActionRuleLine() {
        super(CDSLanguageManager.getMessage("CreateInstance"),
                CDSLanguageManager.getMessage("CreateInstanceDesc"));
        cdsEntryRuleLineElement = new CDSEntryRuleLineElement(this);
        getRuleLineElements().add(new StaticTextRuleLineElement(CDSLanguageManager.getMessage("CreateInstanceRLE")));
        getRuleLineElements().add(cdsEntryRuleLineElement);
    }

    public void setCDSEntryGTCodeRuleLineElementValue(GTCodeRuleLineElement value){
        if (cdsEntryRuleLineElement!=null){
            cdsEntryRuleLineElement.setValue(value);
        }
    }

    public ArchetypeReference getArchetypeReference() {
        return cdsEntryRuleLineElement.getArchetypeReference();
    }

    @Override
    public AssignmentExpression toAssignmentExpression() throws IllegalStateException {
        String name = getArchetypeReference().getIdArchetype();
        Variable var = new Variable(
                cdsEntryRuleLineElement.getValue().getValue(),
                null, name, CreateInstanceExpression.FUNCTION_CREATE_NAME);
        List<AssignmentExpression> assignmentExpressions = new ArrayList<AssignmentExpression>();
        if (!getChildrenRuleLines().getRuleLines().isEmpty()){
            for(RuleLine childRuleLine: getChildrenRuleLines().getRuleLines()){
                AssignmentExpressionRuleLine assignmentExpressionRuleLine = (AssignmentExpressionRuleLine)childRuleLine;
                assignmentExpressions.add(assignmentExpressionRuleLine.toAssignmentExpression());
            }
        }else{
            throw new IllegalStateException("No assignments set into '"+name+"'");
        }
        return new CreateInstanceExpression(
                var,
                assignmentExpressions);
    }

    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        for (RuleLine ruleLine : getChildrenRuleLines().getRuleLines()) {
            sb.append(ruleLine.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    public String toHTMLString(int level, String lang){
        StringBuffer sb = new StringBuffer();
        sb.append(toHTMLStringSingle(level, lang));
        for (RuleLine ruleLine : getChildrenRuleLines().getRuleLines()) {
            sb.append(ruleLine.toHTMLString(level+1, lang)+"<br>");
        }
        return sb.toString();
    }

    public String toHTMLStringSingle(int level, String lang){
        return super.toHTMLString(level, lang);
    }
}/*
 *  ***** BEGIN LICENSE BLOCK *****
 *  Version: MPL 2.0/GPL 2.0/LGPL 2.1
 *
 *  The contents of this file are subject to the Mozilla Public License Version
 *  2.0 (the 'License'); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *  http://www.mozilla.org/MPL/
 *
 *  Software distributed under the License is distributed on an 'AS IS' basis,
 *  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 *  for the specific language governing rights and limitations under the
 *  License.
 *
 *
 *  The Initial Developers of the Original Code are Iago Corbal and Rong Chen.
 *  Portions created by the Initial Developer are Copyright (C) 2012-2013
 *  the Initial Developer. All Rights Reserved.
 *
 *  Contributor(s):
 *
 * Software distributed under the License is distributed on an 'AS IS' basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 *  ***** END LICENSE BLOCK *****
 */