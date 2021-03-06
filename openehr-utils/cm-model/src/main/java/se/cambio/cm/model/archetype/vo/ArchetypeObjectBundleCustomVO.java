package se.cambio.cm.model.archetype.vo;

import java.io.Serializable;
import java.util.Collection;

public class ArchetypeObjectBundleCustomVO implements Serializable {

    private static final long serialVersionUID = 25042012L;
    private Collection<ArchetypeElementVO> archetypeElementVOs = null;
    private Collection<ClusterVO> clusterVOs = null;
    private Collection<CodedTextVO> codedTextVOs = null;
    private Collection<OrdinalVO> ordinalVOs = null;
    private Collection<UnitVO> unitVOs = null;
    private Collection<ProportionTypeVO> proportionTypeVOs = null;

    public ArchetypeObjectBundleCustomVO(
            Collection<ArchetypeElementVO> archetypeElementVOs,
            Collection<ClusterVO> clusterVOs,
            Collection<CodedTextVO> codedTextVOs,
            Collection<OrdinalVO> ordinalVOs,
            Collection<UnitVO> unitVOs,
            Collection<ProportionTypeVO> proportionTypeVOs) {
        super();
        this.archetypeElementVOs = archetypeElementVOs;
        this.clusterVOs = clusterVOs;
        this.codedTextVOs = codedTextVOs;
        this.ordinalVOs = ordinalVOs;
        this.unitVOs = unitVOs;
        this.proportionTypeVOs = proportionTypeVOs;
    }

    public Collection<ArchetypeElementVO> getArchetypeElementVOs() {
        return archetypeElementVOs;
    }
    public Collection<ClusterVO> getClusterVOs() {
        return clusterVOs;
    }
    public Collection<CodedTextVO> getCodedTextVOs() {
        return codedTextVOs;
    }
    public Collection<OrdinalVO> getOrdinalVOs() {
        return ordinalVOs;
    }
    public Collection<UnitVO> getUnitVOs() {
        return unitVOs;
    }
    public void setUnits(Collection<UnitVO> unitVOs) {
        this.unitVOs = unitVOs;
    }
    public Collection<ProportionTypeVO> getProportionTypes() {
        return proportionTypeVOs;
    }
}
/*
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