package com.example.hide.lasd5.lasd5.dict;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;

/**
 * @author hide
 */
public class SAXHandlerSimple extends DefaultHandler {
    private final Stack<String> stack = new Stack<String>();

    private final StringBuilder sb = new StringBuilder();
    private int depth = 0;
    private boolean afterStartElement = false;
    private boolean shortCharactars = false;

    private final StringBuilder sbElement = new StringBuilder(); //element の一覧表作成用

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
        sb.append("\n");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
//        System.out.println("startElement()");
//        System.out.println("\tnamespace=" + uri);
//        System.out.println("\tlocal name=" + localName);
//        System.out.println("\tqualified name=" + qName);

        if (afterStartElement) {
            sb.append(">\n");
        }

        insertTab();
        sb.append("<");
        sb.append(qName);

        for (int i = 0; i < atts.getLength(); i++) {
//            System.out.println("\tattribute name=" + atts.getLocalName(i));
//            System.out.println("\tattribute qualified name=" + atts.getQName(i));
//            System.out.println("\tattribute value=" + atts.getValue(i));

            sb.append(" ");
            sb.append(atts.getQName(i));
            sb.append("=\"");
            sb.append(atts.getValue(i));
            sb.append("\"");
        }

        ++depth;
        stack.push(qName);
        afterStartElement = true;
        shortCharactars = false;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
//        System.out.println("endElement():" + qName);

        --depth;

        if (afterStartElement) {
            sb.append("/>\n");
        } else {
            if (shortCharactars == false) {
                insertTab();
            }
            sb.append("</");
            sb.append(stack.peek());
            sb.append(">\n");
        }

        stack.pop();
        afterStartElement = false;
        shortCharactars = false;

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
//        System.out.println("characters()" + new String(ch, start, length));

        switch (stack.peek()) {
            case "span":
//            case "BASE":
            case "INFLX":
                shortCharactars = true;
                break;

            default:
                break;
        }

        if (afterStartElement) {
            if (length < 20) {
                shortCharactars = true;
            }

            sb.append(">");
            if (shortCharactars == false) {
                sb.append("\n");
            }
        }

        if (shortCharactars) {
            sb.append(new String(ch, start, length));
        } else {
            insertTab();
            sb.append(new String(ch, start, length));
            sb.append("\n");
        }

        afterStartElement = false;
    }

    // ErrorHandlerの実装
    @Override
    public void warning(SAXParseException e) {
        System.out.println("warning: " + e.getLineNumber() + " " + e.getMessage());
    }

    @Override
    public void error(SAXParseException e) {
        System.out.println("error: " + e.getLineNumber() + " " + e.getMessage());
    }

    @Override
    public void fatalError(SAXParseException e) {
        System.out.println("fatalError: " + e.getLineNumber() + " " + e.getMessage());
    }

    @Override
    public String toString() {
        return sb.toString();
    }

    private void insertTab() {
        for (int i = 0; i < depth; ++i) {
            sb.append("  ");
        }
    }

    public String getElementList() {
        return sbElement.toString();
    }
}
