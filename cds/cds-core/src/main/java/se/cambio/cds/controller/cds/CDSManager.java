package se.cambio.cds.controller.cds;

import org.apache.log4j.Logger;
import se.cambio.cds.controller.CDSSessionManager;
import se.cambio.cds.controller.guide.GuideManager;
import se.cambio.cds.model.facade.execution.vo.GeneratedArchetypeReference;
import se.cambio.cds.model.facade.execution.vo.GeneratedElementInstance;
import se.cambio.cds.model.facade.execution.vo.PredicateGeneratedElementInstance;
import se.cambio.cds.model.facade.execution.vo.PredicateGeneratedElementInstanceBuilder;
import se.cambio.cds.model.instance.ArchetypeReference;
import se.cambio.cds.model.instance.ElementInstance;
import se.cambio.cds.util.*;
import se.cambio.openehr.util.OpenEHRConstUI;
import se.cambio.openehr.util.exceptions.InternalErrorException;
import se.cambio.openehr.util.exceptions.PatientNotFoundException;

import java.util.*;

public class CDSManager {

    public static Collection<ElementInstance> getElementInstances(
            String ehrId,
            Collection<String> guideIds,
            Collection<ArchetypeReference> data,
            GuideManager guideManager,
            Calendar date)
            throws PatientNotFoundException, InternalErrorException{
        ElementInstanceCollection eic = new ElementInstanceCollection();
        if (data != null){
            eic.addAll(data, guideManager);
        }
        GeneratedElementInstanceCollection completeEIC = guideManager.getElementInstanceCollection(guideIds);
        //Search for EHR elements
        //Query EHR for elements
        if (ehrId != null){
            Collection<ArchetypeReference> ars = getEHRArchetypeReferences(completeEIC);
            Map<String, Collection<ElementInstance>> elementInstanceMap =
                    CDSSessionManager.getEHRFacadeDelegate().queryEHRElements(Collections.singleton(ehrId), ars, date);
            Collection<ElementInstance> elementInstances = elementInstanceMap.get(ehrId);
            if (elementInstances!=null){
                Set<ArchetypeReference> archetypeReferences = new HashSet<ArchetypeReference>();
                for (ElementInstance elementInstance: elementInstances){
                    archetypeReferences.add(elementInstance.getArchetypeReference());
                }
                eic.addAll(archetypeReferences, null);
            }
        }
        return getElementInstances(eic, completeEIC, guideManager, date);
    }

    public static Map<String,Collection<ElementInstance>> getElementInstancesForPopulation(
            Collection <String> ehrIds,
            Collection<String> guideIds,
            GuideManager guideManager,
            Calendar date) throws PatientNotFoundException, InternalErrorException{
        GeneratedElementInstanceCollection completeEIC = guideManager.getElementInstanceCollection(guideIds);
        Collection<ArchetypeReference> ars = getEHRArchetypeReferences(completeEIC);
        Map<String,Collection<ElementInstance>> ehrMap =
                CDSSessionManager.getEHRFacadeDelegate().queryEHRElements(ehrIds, ars, date);
        Map<String,Collection<ElementInstance>> cdsEIMap = new HashMap<String, Collection<ElementInstance>>();
        for (String ehrId : ehrIds) {
            ElementInstanceCollection eic = new ElementInstanceCollection();
            //TODO If the data existed in ehrData, it should not query for it again to EHR
            if (!ars.isEmpty()){
                Collection<ElementInstance> eis = ehrMap.get(ehrId);
                if (eis!=null){
                    eic.addAll(eis);
                }
            }
            cdsEIMap.put(ehrId, getElementInstances(eic, completeEIC, guideManager, date));
        }
        return cdsEIMap;
    }

    public static Collection<ArchetypeReference> getEHRArchetypeReferences(GeneratedElementInstanceCollection geic){
        Collection<ArchetypeReference> ars = new ArrayList<ArchetypeReference>();
        ars.addAll(geic.getAllArchetypeReferencesByDomain(Domains.EHR_ID));
        ars.addAll(geic.getAllArchetypeReferencesByDomain(ElementInstanceCollection.EMPTY_CODE));
        return getCompressedQueryArchetypeReferences(ars);
    }

    public static Collection<ArchetypeReference> getEHRArchetypeReferencesWithEventTimeElements(GeneratedElementInstanceCollection eic){
        Collection<ArchetypeReference> ars = getEHRArchetypeReferences(eic);
        addEventTimeElements(ars); //We add all the event time elements if they are not defined
        return ars;
    }

    private static Collection<ElementInstance> getElementInstances(ElementInstanceCollection eic, GeneratedElementInstanceCollection completeEIC, GuideManager guideManager, Calendar date)
            throws InternalErrorException{
        //Check for missing elements
        checkForMissingElements(eic, completeEIC, guideManager, date);
        return eic.getAllElementInstances();
    }

    public static void checkForMissingElements(
            ElementInstanceCollection ehrEIC,
            ElementInstanceCollection generatedEIC,
            GuideManager guideManager,
            Calendar date){
        checkForWholeMissingElements(ehrEIC, generatedEIC, guideManager, date);
        checkForMissingPathsInEHR(ehrEIC, generatedEIC, date);
    }

    private static void checkForWholeMissingElements(
            ElementInstanceCollection eic,
            ElementInstanceCollection generatedEIC,
            GuideManager guideManager,
            Calendar date){
        //Check for guide elements, if not present, create archetype reference
        List<ArchetypeReference> guideArchetypeReferences = new ArrayList<ArchetypeReference>(generatedEIC.getAllArchetypeReferences());
        Collections.sort(guideArchetypeReferences, new ARNonEmptyPredicateComparator());
        for (ArchetypeReference archetypeReference : guideArchetypeReferences) {
            GeneratedArchetypeReference gar = (GeneratedArchetypeReference)archetypeReference;
            boolean matches = eic.matches(gar, guideManager.getAllGuidesMap(), date);
            if (!matches){
                eic.add(archetypeReference, guideManager, date);
            }
        }
    }

    private static void checkForMissingPathsInEHR(
            ElementInstanceCollection ehrEIC,
            ElementInstanceCollection generatedEIC,
            Calendar date){
        //Add missing elements (as null) to the ehr data
        Map<String, ArchetypeReference> compressedARsMap = getCompressedQueryArchetypeReferencesMap(generatedEIC.getAllArchetypeReferences());
        for(ArchetypeReference ar: ehrEIC.getAllArchetypeReferences()){
            ArchetypeReference compressedAR = compressedARsMap.get(ar.getIdArchetype());
            if (compressedAR!=null){
                for (String elementId: compressedAR.getElementInstancesMap().keySet()){
                    if (!ar.getElementInstancesMap().containsKey(elementId)){
                        new ElementInstance(elementId, null, ar, null, OpenEHRConstUI.NULL_FLAVOUR_CODE_NO_INFO);
                    }
                }
            }
        }
    }

    private static Collection<ArchetypeReference> getCompressedQueryArchetypeReferences(Collection<ArchetypeReference> generatedArchetypeReferences){
        return new ArrayList<ArchetypeReference>(getCompressedQueryArchetypeReferencesMap(generatedArchetypeReferences).values());
    }

    private static Map<String, ArchetypeReference> getCompressedQueryArchetypeReferencesMap(Collection<ArchetypeReference> generatedArchetypeReferences){
        final Map<String, ArchetypeReference> archetypeReferencesMap = new HashMap<String, ArchetypeReference>();
        //Compress Archetype References with same archetype id
        for (ArchetypeReference arNew : generatedArchetypeReferences) {
            GeneratedArchetypeReference gar = (GeneratedArchetypeReference)arNew;
            ArchetypeReference arPrev = archetypeReferencesMap.get(arNew.getIdArchetype());
            if (arPrev!=null){
                compressQueryArchetypeReference(arPrev, arNew);
            }else{
                arNew = getCleanArchetypeReferenceWithElements(gar);
                archetypeReferencesMap.put(arNew.getIdArchetype(), arNew);
            }
        }
        return archetypeReferencesMap;
    }

    private static void compressQueryArchetypeReference(ArchetypeReference arPrev, ArchetypeReference arNew){
        for (ElementInstance newEI : arNew.getElementInstancesMap().values()) {
            ElementInstance eiAux = arPrev.getElementInstancesMap().get(newEI.getId());
            if (eiAux==null){
                //Missing elements
                cloneElementInstanceWithGTCodes(newEI, arPrev, false);
            }else{
                if (newEI instanceof PredicateGeneratedElementInstance){
                    PredicateGeneratedElementInstance pgeiNew = (PredicateGeneratedElementInstance)newEI;
                    ElementInstance prevEI = arPrev.getElementInstancesMap().get(pgeiNew.getId());
                    if (prevEI instanceof PredicateGeneratedElementInstance){
                        PredicateGeneratedElementInstance pgeiPrev = (PredicateGeneratedElementInstance)prevEI;
                        if (!pgeiNew.getOperatorKind().equals(pgeiPrev.getOperatorKind()) ||
                                DVUtil.compareDVs(pgeiNew.getDataValue(), pgeiPrev.getDataValue())!=0){
                            //TODO Find a predicate (if possible) that includes both
                            //Incompatible predicates found, we remove data value and operation
                            new PredicateGeneratedElementInstanceBuilder()
                                    .setId(pgeiPrev.getId())
                                    .setArchetypeReference(pgeiPrev.getArchetypeReference())
                                    .setNullFlavour(OpenEHRConstUI.NULL_FLAVOUR_CODE_NO_INFO)
                                    .createPredicateGeneratedElementInstance();
                        }
                    }
                }
                if (eiAux instanceof GeneratedElementInstance && newEI instanceof GeneratedElementInstance){
                    //Add new rule references
                    GeneratedElementInstance gei = (GeneratedElementInstance) eiAux;
                    GeneratedElementInstance gei2 = (GeneratedElementInstance) newEI;
                    gei.getRuleReferences().addAll(gei2.getRuleReferences());
                }
            }
        }
    }

    private static GeneratedArchetypeReference getCleanArchetypeReferenceWithElements(GeneratedArchetypeReference ar){
        GeneratedArchetypeReference arNew = ar.clone();
        for (ElementInstance ei : ar.getElementInstancesMap().values()) {
            cloneElementInstanceWithGTCodes(ei, arNew, true);
        }
        return arNew;
    }

    private static ElementInstance cloneElementInstanceWithGTCodes(ElementInstance ei, ArchetypeReference ar, boolean useGTCodes){
        ei = ei.clone();
        ei.setArchetypeReference(ar);
        if (!useGTCodes && ei instanceof GeneratedElementInstance){
            ((GeneratedElementInstance)ei).getRuleReferences().clear();
        }
        return ei;
    }

    private static class ARNonEmptyPredicateComparator implements Comparator<ArchetypeReference>{

        public int compare(ArchetypeReference o1, ArchetypeReference o2) {
            int count1 = getNumPredicates(o1);
            int count2 = getNumPredicates(o2);
            return count1-count2;
        }

        private int getNumPredicates(ArchetypeReference archetypeReference){
            int count = 0;
            for (ElementInstance elementInstance: archetypeReference.getElementInstancesMap().values()){
                if (elementInstance instanceof PredicateGeneratedElementInstance && elementInstance.getDataValue()!=null){
                    count++;
                }
            }
            return count;
        }
    }

    private static void addEventTimeElements(Collection<ArchetypeReference> queryARs){
        for(ArchetypeReference archetypeReference: queryARs){
            String eventTimePath = DateTimeARFinder.getEventTimePath(archetypeReference.getIdArchetype());
            if (eventTimePath!=null){
                String eventTimeElementId = archetypeReference.getIdArchetype()+eventTimePath;
                if (!archetypeReference.getElementInstancesMap().containsKey(eventTimeElementId)){
                    Logger.getLogger(CDSManager.class).info("Adding event path '"+eventTimeElementId+"' for archetype '"+archetypeReference.getIdArchetype()+"'!");
                    new GeneratedElementInstance(eventTimeElementId, null, archetypeReference, null, OpenEHRConstUI.NULL_FLAVOUR_CODE_NO_INFO);
                }
            }else{
                Logger.getLogger(CDSManager.class).warn("Could not find event path for archetype '"+archetypeReference.getIdArchetype()+"'!");
            }
        }
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