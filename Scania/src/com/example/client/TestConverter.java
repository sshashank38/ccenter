package com.example.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TestConverter {
    private static final String INPUT_DIR = "C:\\temp\\TestCompany\\input\\";
    private static final String OUTPUT_DIR = "C:\\temp\\TestCompany\\output\\";

    public static void main(String[] args) {
        try {
            Files.createDirectories(Paths.get(INPUT_DIR));
            Files.createDirectories(Paths.get(OUTPUT_DIR));
            File[] xmlFiles = new File(INPUT_DIR).listFiles((dir, name) -> name.endsWith(".xml"));

            if (xmlFiles == null || xmlFiles.length == 0) {
                System.out.println("No XML files found in input directory.");
                return;
            }

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            for (File xmlFile : xmlFiles) {
                System.out.println("Processing file: " + xmlFile.getName());
                if (!validateXML(xmlFile, saxParser)) {
                    System.out.println("Invalid XML file: " + xmlFile.getName() + ". Skipping...");
                    continue;
                }
                Data jsonData = extractDataFromXML(xmlFile, saxParser);
                saveToJsonFile(xmlFile.getName(), jsonData);
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean validateXML(File file, SAXParser saxParser) {
        try {
            saxParser.parse(file, new DefaultHandler());
            System.out.println("Validated XML file: " + file.getName());
            return true;
        } catch (SAXException | IOException e) {
            return false;
        }
    }

    private static Data extractDataFromXML(File file, SAXParser saxParser) {
        Data data = new Data();
        try {
            saxParser.parse(file, new XMLHandler(data));
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private static class XMLHandler extends DefaultHandler {
        private Data data;
        private StringBuilder elementValue = new StringBuilder();
        private PackageData currentPackage;
        private Part currentPart;
        private DocumentData currentDocument;
        private Map<String, String> attributes;
        
        public XMLHandler(Data data) {
            this.data = data;
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            elementValue.setLength(0);
            if ("BO".equals(qName)) {
                this.attributes = new HashMap<>();
                String type = attributes.getValue("type");
                if ("pim_package".equals(type)) {
                    currentPackage = new PackageData();
                } else if ("part".equals(type)) {
                    currentPart = new Part();
                } else if ("document".equals(type)) {
                    currentDocument = new DocumentData();
                }
            }
        }
        
        public void characters(char[] ch, int start, int length) {
            elementValue.append(ch, start, length);
        }
        
        public void endElement(String uri, String localName, String qName) {
            String value = elementValue.toString().trim();
            if (currentPackage != null) {
                if ("identifier".equals(qName)) currentPackage.setId(value);
                //else if ("title".equals(qName)) currentPackage.setTitle(value);
                else if ("state".equals(qName)) currentPackage.setState(value);
                else if ("BO".equals(qName)) {
                    currentPackage.setAttributes(attributes);
                    data.getPackages().add(currentPackage);
                    currentPackage = null;
                }
            } else if (currentPart != null) {
                if ("identifier".equals(qName)) currentPart.setId(value);
                else if ("name".equals(qName)) currentPart.setName(value);
                else if ("state".equals(qName)) currentPart.setState(value);
                else if ("partnumber".equals(qName)) currentPart.setPartNumber(value);
                else if ("BO".equals(qName)) {
                    currentPart.setAttributes(attributes);
                    data.getParts().add(currentPart);
                    currentPart = null;
                }
            } else if (currentDocument != null) {
                if ("identifier".equals(qName)) currentDocument.setId(value);
                else if ("name".equals(qName)) currentDocument.setName(value);
                else if ("created".equals(qName)) currentDocument.setCreated(value);
                else if ("modified".equals(qName)) currentDocument.setModified(value);
                //else if ("title".equals(qName)) currentDocument.setTitle(value);
                else if ("BO".equals(qName)) {
                    currentDocument.setAttributes(attributes);
                    data.getDocuments().add(currentDocument);
                    currentDocument = null;
                }
            } else if ("attr".equals(qName) || "ctrl".equals(qName)) {
                attributes.put(qName, value);
            }
        }
    }

    private static void saveToJsonFile(String xmlFileName, Data jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonFileName = xmlFileName.replace(".xml", ".json");
            File jsonFile = new File(OUTPUT_DIR, jsonFileName);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
