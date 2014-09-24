package se.cambio.cds.util.exporter.html;

import se.cambio.cds.gdl.model.Guide;
import se.cambio.cds.gdl.model.readable.ReadableGuide;
import se.cambio.cds.gdl.model.readable.rule.ReadableRule;
import se.cambio.cds.util.GuideImporter;
import se.cambio.openehr.util.exceptions.InternalErrorException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: iago.corbal
 * Date: 2014-09-12
 * Time: 13:31
 */
public class GuideHTMLExporter extends ClinicalModelHTMLExporter<Guide> {

    public GuideHTMLExporter(Guide guide, String lang) {
        super(guide, lang);
    }

    @Override
    public Map<String, Object> getEntityObjectsMap() throws InternalErrorException {
        Guide guide = getEntity();
        String lang = getLanguage();
        ReadableGuide readableGuide = GuideImporter.importGuide(guide, lang);
        Collection<String> htmlReadableRules = getHTMLReadableRules(readableGuide, lang);
        Map<String, Object> objectMap = new HashMap<String, Object>();
        objectMap.put("guide", guide);
        objectMap.put("guide_details", guide.getDescription().getDetails().get(lang));
        objectMap.put("guide_definitions", readableGuide);
        objectMap.put("guide_rules", htmlReadableRules);
        objectMap.put("guide_terms", guide.getOntology().getTermDefinitions().get(lang).getTerms());
        return objectMap;
    }

    private static Collection<String> getHTMLReadableRules(ReadableGuide readableGuide, String lang){
        Collection<String> htmlReadableRules = new ArrayList<String>();
        for(ReadableRule readableRule: readableGuide.getReadableRules().values()){
            htmlReadableRules.add(readableRule.toHTMLString(lang));
        }
        return htmlReadableRules;
    }


    @Override
    public Map<String, String> getEntityTextMap() {
        HashMap<String, String> textsMap = new HashMap<String, String>();
        addText(textsMap, "GuideDetails");
        addText(textsMap, "Preconditions");
        addText(textsMap, "RuleList");
        addText(textsMap, "Bindings");
        return textsMap;
    }

    @Override
    public InputStream getInputStreamTemplate() {
        return GuideHTMLExporter.class.getClassLoader().getResourceAsStream("gdl.ftl");
    }
}