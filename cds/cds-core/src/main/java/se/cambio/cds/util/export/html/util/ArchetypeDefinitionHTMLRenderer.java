package se.cambio.cds.util.export.html.util;

import org.openehr.am.archetype.Archetype;

import java.util.Collection;

public class ArchetypeDefinitionHTMLRenderer {
    private String iconPath = null;

    public ArchetypeDefinitionHTMLRenderer() {
    }

    public String generateHTML(Archetype archetype, String templateId, String lang){
        SimpleArchetypeNode rootNode = SimpleArchetypeNodeUtils.getSimpleArchetypeNode(archetype.getArchetypeId().getValue(), templateId, lang);
        StringBuffer htmlSB = new StringBuffer();
        htmlSB.append("<ul class=\"tree\">");
        htmlSB.append("<li>");
        htmlSB.append(getImageHTMLTag(rootNode));
        htmlSB.append(getNodeHTML(rootNode));
        htmlSB.append("</li>");
        htmlSB.append("</ul>");
        return htmlSB.toString();
    }

    protected String getNodeHTML(SimpleArchetypeNode rootSimpleArchetypeNode){
        StringBuffer htmlSB = new StringBuffer();
        htmlSB.append(rootSimpleArchetypeNode.getName());
        Collection<SimpleArchetypeNode> children = rootSimpleArchetypeNode.getChildren();
        if (!children.isEmpty()) {
            htmlSB.append("<ul>");
            for (SimpleArchetypeNode simpleArchetypeNode : children) {
                htmlSB.append("<li>");
                htmlSB.append("<div title='"+simpleArchetypeNode.getDescription()+"'>");
                htmlSB.append(getImageHTMLTag(simpleArchetypeNode));
                htmlSB.append(" ");
                htmlSB.append(getNodeHTML(simpleArchetypeNode));
                htmlSB.append("</div>");
                htmlSB.append("</li>");
            }
            htmlSB.append("</ul>");
        }
        return htmlSB.toString();
    }

    protected String getImageHTMLTag(SimpleArchetypeNode simpleArchetypeNode){
        String iconFileName = simpleArchetypeNode.getIconFileName();
        StringBuffer htmlSB = new StringBuffer();
        if (iconFileName!=null){
            htmlSB.append("<img src='");
            if (getIconPath()!=null) {
                htmlSB.append(getIconPath());
                if (!getIconPath().endsWith("/")){
                    htmlSB.append("/");
                }
            }
            htmlSB.append(iconFileName);
            htmlSB.append("'/>");
        }
        return htmlSB.toString();
    }
    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
}
