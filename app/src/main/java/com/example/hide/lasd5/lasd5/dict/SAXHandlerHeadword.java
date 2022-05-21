package com.example.hide.lasd5.lasd5.dict;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import static com.example.hide.lasd5.lasd5.dict.LevelEnum.*;

public class SAXHandlerHeadword extends DefaultHandler {
    private final Stack<String> stack = new Stack<>();
    private ArrayList<Headword> list = new ArrayList<>();
    private int index;
    private Headword hw = null;
    private LevelEnum level;

    @Override
    public void startDocument() throws SAXException {
//        System.out.println("startDocument:index:" + index);
        level = TOP;
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
//        System.out.println("startElement()\tnamespace=" + uri);
//        System.out.println("\tlocal name=" + localName);
//        System.out.println("\tqualified name=" + qName);
//        System.out.println("startElement:qName:" + qName);

        if (TOP.equals(qName)) {
            level = TOP;
        } else if (HEAD.equals(qName)) {
            if (level == TOP) {
                level = HEAD;
            }
        } else if (SENSE.equals(qName)) {
            if (level == TOP) {
                level = SENSE;
            }
        } else if (TAIL.equals(qName)) {
            if (level == TOP) {
                level = TAIL;
            }

        }

//        System.out.println("level:" + level + " qName:" + qName);
        switch (level) {
            case HEAD:
                if (qName.equals("HWD")) {
//                    System.out.println("hwd");
                    hw = new Headword(index);
                    list.add(hw);
                } else if (qName.equals("HYPHENATION")) {
                    if (atts.getValue("class") != null && atts.getValue("class").equals("activeword")) {
                        hw.setActive(true);
                    } else {
                        hw.setActive(false);
                    }
                }

                break;
            case TAIL:
                if (qName.equals("DERIV")) {
//                    System.out.println("deriv");
                    hw = new Headword(index);
                    list.add(hw);
                }
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

        stack.push(qName);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
//        System.out.println("endElement:qName:" + qName + " state:" + s);
//          System.out.println("endElement:head:" + html.toString() + " qName:" + qName);

        if (HEAD.equals(qName) || SENSE.equals(qName) || TAIL.equals(qName)) {
            level = TOP;
        }

        stack.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String text = new String(ch, start, length);
        String qName = stack.peek();
        String qName2 = "";
        
        if(stack.size() >= 2){
            qName2 = stack.get(stack.size() - 2);
        }
        
        if (level == HEAD || level == TAIL) {
            if(qName2.equals("HWD") || qName2.equals("DERIV")){
                if(qName.equals("BASE")){
//                    System.out.println("characters:BASE:" + text + ":");
                    hw.setBase(text);
                }else if(qName.equals("INFLX")){
                    hw.addInflx(text);
                }
            }
            switch (qName) {
                case "HOMNUM":
                    hw.setHomnum(text);
                    break;
                case "POS":
                    if(text.equals("phrasal verb")){
                        ; // do nothing
                    }else {
                        hw.setPos(text);
                        break;
                    }
                default:
                    break;
            }
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

    public void setIndex(int i) {
        index = i;
    }

    public Collection<Headword> getHeadwordList() {
        return list;
    }
}
