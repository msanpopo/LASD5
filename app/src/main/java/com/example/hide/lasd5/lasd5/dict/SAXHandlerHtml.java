/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.hide.lasd5.lasd5.dict;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;

/**
 *
 * @author hide
 */
public class SAXHandlerHtml extends DefaultHandler {

    private final Stack<String> stack = new Stack<String>();
    private final StringBuilder html = new StringBuilder();

    private int depth = 0;
    private boolean hwd = false;

    @Override
    public void startDocument() throws SAXException {
//        String jquery = getClass().getResource("jquery-2.1.0.min.js").toExternalForm();
//        String script = getClass().getResource("script.js").toExternalForm();
    }

    @Override
    public void endDocument() throws SAXException {
        html.append("\n");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
//        System.out.println("startElement()\tnamespace=" + uri);
//        System.out.println("\tlocal name=" + localName);
//        System.out.println("\tqualified name=" + qName);
//        System.out.println("startElement:qName:" + qName + " state:" + s);

//        insertTab();
        switch (qName) {
            case "HYPHENATION":
                if (atts.getValue("class") != null && atts.getValue("class").equals("activeword")) {
//                    System.out.println("\tattribute value=" + atts.getValue("class"));
                    html.append("<span class=\"word_activeword\">\n");
                } else {
                    html.append("<span class=\"word\">\n");
                }
                break;
            case "HOMNUM":
                if (atts.getValue("class") != null && atts.getValue("class").equals("activeword")) {
                    html.append("<span class=\"homnum_activeword\">\n");
                } else {
                    html.append("<span class=\"homnum\">\n");
                }
                break;
            case "HWD":
                hwd = true;
                break;
            case "i":
            case "STRONG":
            case "BADCOLLO":
            case "LINKWORD":
            case "POS":
                html.append("<span class=\"pos\">\n");
                break;

            case "COLLOINEXA":
                html.append("<span class=\"black_bold_italic\">\n");
                break;
            case "AC":
                html.append("<span class=\"ac\">\n");
                break;
            case "REGISTERLAB":
            case "GEO":
                html.append("<span class=\"purple_italic\">\n");
                break;
            case "DERIV":
                html.append("<span class=\"deriv\">");
                break;
            case "PROPFORM":
            case "ORTHVAR":
            case "PHRVBHWD":
            case "LEXVAR":
                html.append("<span class=\"blue_gothic\">\n");
                break;
            case "COLLO":
            case "LEXUNIT":
            case "PROPFORMPREP":
                html.append("<span class=\"blue_gothic\">\n");
                break;
            case "REFHOMNUM":
                html.append("<sup>\n");
                break;
            case "DEFBOLD":
            case "COLLOC":
            case "FULLFORM":
            case "EXPR":
            case "OPP":
            case "SYN":
            case "UNCLASSIFIED":
            case "RELATEDWD":
            case "EXP":
            case "THESPROPFORM":
            case "ABBR":
            case "PLURALFORM":
                html.append("<span class=\"bold\">\n");
                break;

            case "GOODCOLLO":
                html.append("<b><i>\n");
                break;
            case "HEADING":
            case "HEADBOLD":
                html.append("<span class=\"heading\">\n");
                break;
            case "GRAM":
                html.append("<span class=\"green\">\n");
                break;
            case "NonDV":
                html.append("<span class=\"nondv\">\n");
                break;
            case "SECHEADING":
                html.append("<span class=\"secheading\">\n");
                break;
            case "SIGNPOST":
                html.append("<span class=\"signpost\">\n");
                break;
            case "SENSENUMBER":
                html.append("<span class=\"sensenumber\">\n");
                break;
            case "span":
                String value = atts.getValue("class");
                if (value != null) {
                    if (value.equals("refsensenum")) {
                        html.append("<span\">\n");
                    } else if (value.equals("geo")) {
                        html.append("<span class=\"purple_italic\">\n");
                    } else if (value.equals("heading")) {
                        html.append("<span class=\"spanheading\">\n");
                    } else if (value.equals("infllab")) {
                        html.append("<span class=\"spanitalic\">\n");
                    } else if (value.equals("italic")) {
                        html.append("<span class=\"spanitalic\">\n");
                    } else if (value.equals("sensenum")) {
                        html.append("<span class=\"sensenum\">\n");
                    } else if (value.equals("synopp")) {
                        html.append("<span class=\"spansynopp\">\n");
                    } else if (value.equals("thesref")) {
                        html.append("<span class=\"spanthesref\">\n");
                    } else if (value.equals("warning")) {
                        html.append("<span class=\"spanwarning\">\n");
                    }
                } else {
                    html.append("<span>");
                }
                break;
            case "Audio":
                String resource = atts.getValue("resource");
                resource = resource.toLowerCase();

                if (resource != null) {
                    String topicFull = atts.getValue("topic");
                    String[] s = topicFull.split("/");
                    String topic = s[s.length - 1];
//                    System.out.println("full:" + topicFull + " topic:" + topic);
                    String icon = "";
                    String br = "";
                    if (resource.equals("gb_hwd_pron")) {
                        icon = "icon/pron_red.png";
                    } else if (resource.equals("us_hwd_pron")) {
                        icon = "icon/pron_blue.png";
                    } else if (resource.equals("exa_pron")) {
                        br = "<br>";
                        icon = "icon/pron_black.png";
                    } else if (resource.equals("sfx")) {
                        br = "<br>";
                        icon = "icon/pron_black.png";
                    } else {
                        System.out.println("sax: unknown audio resource:" + resource);
                    }

                    String str = "<a href=\"" + resource + "/" + topic + "\">";
                    str += "<img src=\"" + icon + "\" width=\"16\" height=\"16\"/></a>\n";
                   // sb.append("<a resource=\"");
                    //sb.append(resource);
                   // sb.append("\" topic=\"");
                   // sb.append(topic);
                   // sb.append("\">");
                   // sb.append("<img src=\"");
                   // sb.append(icon);
                   // sb.append("\" width=\"16\" height=\"16\"/>\n");
                   // sb.append("</a>");
                    html.append(str);
                }
                break;
            case "ILLUSTRATION":
                // TODO
                break;
            case "Crossref":
            case "Head":
            case "RunOn":
                html.append("<div>");
                break;
            case "EXAMPLE":
            case "COLLEXA":
            case "THESEXA":
            case "GramExa":
                html.append("<div class=\"example\">");
                break;
            case "ColloBox":
            case "LexuBox":
            case "Section":
            case "Tail":
            case "ThesBox":
                html.append("<br>\n");
                break;
            case "Exponent":
            case "Sense":
            case "EXPL":
                html.append("<div class=\"sense\">");
                break;
            case "PASTPART":
            case "PASTTENSE":
            case "PRESPART":
            case "PTandPP":
            case "T3PERSSING":
                html.append("<span class=\"inflection\">");
                break;
            case "INFLX":
            case "AMEQUIV":
            case "BREQUIV":
            case "AmEVariant":
            case "BrEVariant":
            case "PronCodes":
            case "PRON":
            case "AMEVARPRON":
            case "BADEXA":
            case "ColloExa":
            case "Ref":
            case "ErrorBox":
            case "Error":
            case "GramBox":
            case "Collocate":
            case "UsageBox":
            case "Fullformref":
            case "FREQ":
            case "Inflections":
            case "COMP":
            case "COMPX":
            case "SUPERL":
            case "SUPERLX":
            case "Subsense":
            case "Thesref":
            case "Variant":
            case "Entry":
            case "SE_EntryAssets":
            case "EntryAssets":
            case "EntryAsset":
            case "Refs":
            case "REFHWD":
            case "REFSENSENUM":
            case "LEXREF":
            case "CROSSREFTYPE":
            case "DEF":
            case "COLLGLOSS":
            case "GLOSS":
            case "OBJECT":
            case "PhrVbEntry":
            case "SUFFIX":
            case "BASE":
                // do nothing
                break;
            default:
                break;
        }

//        for (int i = 0; i < atts.getLength(); i++) {
//            System.out.println("\tattribute name=" + atts.getLocalName(i));
//            System.out.println("\tattribute qualified name=" + atts.getQName(i));
//            System.out.println("\tattribute value=" + atts.getValue(i));
//            sb.append(" ").append(atts.getQName(i)).append("=\"").append(atts.getValue(i)).append("\"");
//        }
        ++depth;
        stack.push(qName);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
//        System.out.println("endElement:qName:" + qName + " state:" + s);
//          System.out.println("endElement:head:" + html.toString() + " qName:" + qName);

//        insertTab();
        switch (qName) {
            case "HYPHENATION":
            case "HOMNUM":
            case "COLLOINEXA":
            case "AC":
            case "REGISTERLAB":
            case "COLLO":
            case "DERIV":
            case "HEADING":
            case "HEADBOLD":
            case "GEO":
            case "GRAM":
            case "PROPFORM":
            case "PROPFORMPREP":
            case "LEXUNIT":
            case "NonDV":
            case "ORTHVAR":
            case "PHRVBHWD":
            case "SIGNPOST":
            case "SENSENUMBER":
            case "LEXVAR":
            case "span":
            case "i":
            case "STRONG":
            case "BADCOLLO":
            case "LINKWORD":
            case "POS":
            case "DEFBOLD":
            case "COLLOC":
            case "FULLFORM":
            case "EXPR":
            case "OPP":
            case "SYN":
            case "UNCLASSIFIED":
            case "RELATEDWD":
            case "EXP":
            case "THESPROPFORM":
            case "ABBR":
            case "PLURALFORM":
            case "PASTPART":
            case "PASTTENSE":
            case "PRESPART":
            case "PTandPP":
            case "T3PERSSING":
                html.append("</span>\n");
                break;
            case "SECHEADING":
                html.append("</span><br>\n");
                break;
            case "HWD":
                hwd = false;
                break;
            case "REFHOMNUM":
                html.append("</sup>\n");
                break;
            case "GOODCOLLO":
                html.append("</b></i>\n");
                break;
            case "Audio":
                // TODO
                break;
            case "ILLUSTRATION":
                // TODO
                break;
            case "Head":
                html.append("<br></div>\n");
                break;
            case "Tail":
            case "ColloBox":
            case "ThesBox":
            case "Collocate":
                html.append("<br>\n");
                break;
            case "Crossref":
            case "Sense":
            case "EXPL":
            case "EXAMPLE":
            case "Exponent":
            case "GramExa":
            case "RunOn":
            case "COLLEXA":
            case "THESEXA":
                html.append("</div>\n");
                break;
            case "INFLX":
            case "AMEQUIV":
            case "BREQUIV":
            case "AmEVariant":
            case "BrEVariant":
            case "PronCodes":
            case "PRON":
            case "AMEVARPRON":
            case "BADEXA":
            case "ColloExa":
            case "Ref":
            case "ErrorBox":
            case "Error":
            case "GramBox":
            case "UsageBox":
            case "Fullformref":
            case "FREQ":
            case "LexuBox":
            case "Inflections":
            case "COMP":
            case "COMPX":
            case "SUPERL":
            case "SUPERLX":
            case "Section":
            case "Subsense":
            case "Thesref":
            case "Variant":
            case "Entry":
            case "SE_EntryAssets":
            case "EntryAssets":
            case "EntryAsset":
            case "Refs":
            case "REFHWD":
            case "REFSENSENUM":
            case "LEXREF":
            case "CROSSREFTYPE":
            case "DEF":
            case "COLLGLOSS":
            case "GLOSS":
            case "OBJECT":
            case "PhrVbEntry":
            case "SUFFIX":
            case "BASE":
                // do nothing
                break;
            default:
                break;
        }

        --depth;
        stack.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String text = new String(ch, start, length);

        String qName = stack.peek();

        switch (qName) {
            case "Error":
            case "HYPHENATION":
            case "HOMNUM":
            case "COLLOINEXA":
            case "STRONG":
            case "REGISTERLAB":
            case "BADEXA":
            case "COLLO":
            case "REFHWD":
            case "REFSENSENUM":
            case "REFHOMNUM":
            case "LEXREF":
            case "CROSSREFTYPE":
            case "DEF":
            case "DEFBOLD":
            case "DERIV":
            case "BADCOLLO":
            case "GOODCOLLO":
            case "COLLOC":
            case "COLLGLOSS":
            case "COMP":
            case "HEADING":
            case "HEADBOLD":
            case "FULLFORM":
            case "EXPR":
            case "GEO":
            case "GLOSS":
            case "PROPFORM":
            case "PROPFORMPREP":
            case "LINKWORD":
            case "NonDV":
            case "OPP":
            case "SYN":
            case "ORTHVAR":
            case "UNCLASSIFIED":
            case "PHRVBHWD":
            case "POS":
            case "RELATEDWD":
            case "SECHEADING":
            case "SIGNPOST":
            case "SENSENUMBER":
            case "SUFFIX":
            case "EXP":
            case "THESPROPFORM":
            case "LEXVAR":
            case "ABBR":
            case "span":
            case "PLURALFORM":
            case "EXPL":
            case "OBJECT":
            case "PASTPART":
            case "PASTTENSE":
            case "PRESPART":
            case "PTandPP":
            case "SUPERL":
            case "T3PERSSING":
                html.append(text);
                break;
            case "LEXUNIT":
                html.append(text);
                break;
            case "BASE":
                if (hwd == false) {
                    html.append(text);
                }
                break;
            case "AC":
                html.append("Ac");
                break;
            case "i":
            case "PRON":
            case "AMEVARPRON":
                html.append(replacePhonetic(text));
                break;
            case "Audio":
                // TODO
                break;
            case "ILLUSTRATION":
                // TODO
                break;
            case "GRAM":
                if(text.equals("countable")) {
                    html.append("C ");
                }else if(text.equals("uncountable")){
                    html.append("U ");
                }else if(text.equals("uncountable and countable")) {
                    html.append("C, U ");
                }else if(text.equals("transitive")) {
                    html.append("T ");
                }else if(text.equals("intransitive")){
                    html.append("I ");
                }else if(text.equals("intransitive and transitive")){
                    html.append("I, T ");
                }else{
                    html.append(text);
                }
                break;
            case "HWD":
            case "INFLX":
            case "AMEQUIV":
            case "BREQUIV":
            case "AmEVariant":
            case "BrEVariant":
            case "PronCodes":
            case "ColloExa":
            case "Crossref":
            case "Ref":
            case "ErrorBox":
            case "GramBox":
            case "ColloBox":
            case "Collocate":
            case "COLLEXA":
            case "UsageBox":
            case "Fullformref":
            case "EXAMPLE":
            case "FREQ":
            case "GramExa":
            case "LexuBox":
            case "Inflections":
            case "COMPX":
            case "SUPERLX":
            case "RunOn":
            case "Section":
            case "Subsense":
            case "ThesBox":
            case "Exponent":
            case "THESEXA":
            case "Thesref":
            case "Variant":
            case "Entry":
            case "SE_EntryAssets":
            case "EntryAssets":
            case "EntryAsset":
            case "Refs":
            case "PhrVbEntry":
            case "Head":
            case "Tail":
            case "Sense":
                // do nothing
                break;
            default:
                break;
        }
    }

    @Override
    public void warning(SAXParseException e) {
        System.out.println("warning: " + e.getLineNumber());
        System.out.println(e.getMessage());
    }

    @Override
    public void error(SAXParseException e) {
        System.out.println("error: " + e.getLineNumber());
        System.out.println(e.getMessage());
    }

    @Override
    public void fatalError(SAXParseException e) {
        System.out.println("fatalError: " + e.getLineNumber());
        System.out.println(e.getMessage());
    }

    @Override
    public String toString() {
        System.out.println("html:\n" + html.toString());

//        StringBuilder sb = new StringBuilder();
//        sb.append("<!DOCTYPE html><html><head>");
//        sb.append("<meta charset=\"UTF-8\">");
//        sb.append("</head><body>\n");
//        sb.append("<i>italic</i>");
//        
//        sb.append("</body></html>\n");
//        return sb.toString();
        return html.toString();
    }

    private void insertTab() {
        for (int i = 0; i < depth; ++i) {
            html.append("  ");
        }
    }

    private String replacePhonetic(String s) {
        int n = s.length();

        for (int i = 0; i < n; ++i) {
            char c = s.charAt(i);
//            System.out.println("replacePhonetic:n:" + n + " char:" + c + " int:" + (int) c);
        }

//        System.out.println("replacePhonetic before:\n" + s);
        s = s.replaceAll("\\uffce\\uffb8", "&#952;");
        s = s.replaceAll("\\uffc3\\uffb0", "&#240;");
        s = s.replaceAll("\\uffca\\uff83", "&#643;");
        s = s.replaceAll("\\uffca\\uff92", "&#658;");
        s = s.replaceAll("\\uffc5\\uff8b", "&#331;");
        s = s.replaceAll("\\uffc9\\uffaa", "&#618;");
        s = s.replaceAll("\\uffc3\\uffa6", "&#230;");
        s = s.replaceAll("\\uffc9\\uff92", "&#594;");
        s = s.replaceAll("\\uffca\\uff8c", "&#652;");
        s = s.replaceAll("\\uffca\\uff8a", "&#650;");
        s = s.replaceAll("\\uffc9\\uff99", "&#601;");
        s = s.replaceAll("\\uffcb\\uff90", "&#720;");
        s = s.replaceAll("\\uffc9\\uff91", "&#593;");
        s = s.replaceAll("\\uffc9\\uff94", "&#596;");
        s = s.replaceAll("\\uffc9\\uff9c", "&#604;");
        s = s.replaceAll("\\uffcb\\uff88", "&#712;");
        s = s.replaceAll("\\uffcb\\uff8c", "&#716;");
        s = s.replaceAll("\\uffe2\\uff97\\uff82", "");

        return s;
    }
}