package com.example.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLToJsonFileConverter {
	private static final String INPUT_DIR = "C:\\temp\\TestCompany\\input\\";
	private static final String OUTPUT_DIR = "C:\\temp\\TestCompany\\output\\";

	public static Response convertXmlToJson(String fileKey) {
		Response response = new Response();
		String xmlFilePath = INPUT_DIR + fileKey + ".xml";
		String jsonFilePath = OUTPUT_DIR + fileKey + ".json";

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			File xmlFile = new File(xmlFilePath);
			if (!validateXML(xmlFile, saxParser)) {
				response.setError("Invalid XML file: " + fileKey + ".xml");
				return response;
			}

			Data jsonData = extractDataFromXML(xmlFile, saxParser);
			if (jsonData == null || (jsonData.getPackages().isEmpty() && jsonData.getParts().isEmpty()
					&& jsonData.getDocuments().isEmpty())) {
				response.setError("No valid data extracted from XML.");
				return response;
			}

			saveToJsonFile(jsonFilePath, jsonData);

			ObjectMapper objectMapper = new ObjectMapper();
			String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonData);
			response.setData(jsonString);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			response.setError("Error converting XML to JSON: " + e.getMessage());
		}
		return response;
	}

	public static List<String> listXmlFiles() {
		File dir = new File(INPUT_DIR);
		File[] files = dir.listFiles((d, name) -> name.endsWith(".xml"));
		List<String> fileNames = new ArrayList<>();
		if (files != null) {
			for (File file : files) {
				fileNames.add(file.getName());
			}
		}
		return fileNames;
	}

public static Response getPackageDetails() {
    Response response = new Response();
    File[] xmlFiles = new File(INPUT_DIR).listFiles((dir, name) -> name.endsWith(".xml"));

    if (xmlFiles == null || xmlFiles.length == 0) {
        response.setError("No XML files found in input directory.");
        return response;
    }

    StringBuilder csvData = new StringBuilder();
    csvData.append("\"package_id\",\"title\",\"state\",\"included_docs\",\"included_parts\"\n"); // CSV Header

    try {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        for (File xmlFile : xmlFiles) {
            if (!validateXML(xmlFile, saxParser)) {
                System.out.println("Skipping invalid XML: " + xmlFile.getName());
                continue;
            }

            Data data = new Data();
            saxParser.parse(xmlFile, new XMLHandler(data));

            // Process packages without duplicates
            for (PackageData pkg : data.getPackages()) {
                if (pkg == null) continue;

                // Extract required attributes
                String packageId = pkg.getAttributes().getOrDefault("package_id", "").trim();
                String title = pkg.getAttributes().getOrDefault("title", "").trim();
                String state = pkg.getAttributes().getOrDefault("cdb_status_txt", "").trim();
                String includedDocs = pkg.getAttributes().getOrDefault("included_docs", "").trim();
                String includedParts = pkg.getAttributes().getOrDefault("included_parts", "").trim();

                // **Skip Empty Entries**
                if (packageId.isEmpty() || state.isEmpty()) {
                    continue;
                }

                // Proper CSV formatting: Wrap each value in double quotes
                csvData.append("\"").append(packageId).append("\",")
                       .append("\"").append(title).append("\",")
                       .append("\"").append(state).append("\",")
                       .append("\"").append(includedDocs).append("\",")
                       .append("\"").append(includedParts).append("\"\n");
            }
        }
    } catch (ParserConfigurationException | SAXException | IOException e) {
        response.setError("Error processing XML files: " + e.getMessage());
        e.printStackTrace();
        return response;
    }

    response.setData(csvData.toString().trim());
    return response;
}

	

public static Response getPartDetails(String partNumber) {
    Response response = new Response();

        File[] xmlFiles = new File(INPUT_DIR).listFiles((dir, name) -> name.endsWith(".xml"));
        if (xmlFiles == null || xmlFiles.length == 0) {
            response.setError("No XML files found in input directory.");
            return response;
        }

        // ✅ CSV Header
        StringBuilder partDetails = new StringBuilder();
        partDetails.append("\"id\",\"name\",\"state\",\"cl_defining_document\",\"created\",\"modified\"\n");

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            for (File xmlFile : xmlFiles) {
                Data data = new Data();
                saxParser.parse(xmlFile, new XMLHandler(data));

                for (Part part : data.getParts()) {
                    if (part == null || !partNumber.equals(part.getPartNumber())) {
                        continue; // Skip parts that don't match input partnumber
                    }

                    // ✅ Extract attributes
                    String id = part.getId() != null ? part.getId() : "";
                    String name = part.getName() != null ? part.getName() : "";
                    String state = part.getState() != null ? part.getState() : "";
                    String definingDoc = part.getAttributes().getOrDefault("cl_defining_document", "");
                    String created = part.getCreated() != null ? part.getCreated() : "";
                    String modified = part.getModified() != null ? part.getModified() : "";

                    // ✅ Format values to handle commas properly by enclosing in quotes
                    partDetails.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                            id, name, state, definingDoc, created, modified));
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            response.setError("Error processing XML files: " + e.getMessage());
            e.printStackTrace();
        }

        // ✅ Print CSV Output to Console
        System.out.println("\n" + partDetails.toString());

        response.setData(partDetails.toString());
    //}  // ✅ Scanner auto-closes here

    return response;
}


public static Response getDocumentDetails(String getDocId) {
    Response response = new Response();

    File[] xmlFiles = new File(INPUT_DIR).listFiles((dir, name) -> name.endsWith(".xml"));
    if (xmlFiles == null || xmlFiles.length == 0) {
        response.setError("No XML files found in input directory.");
        return response;
    }

    // ✅ CSV Header
    StringBuilder documentDetails = new StringBuilder();
    documentDetails.append("\"id\",\"name\",\"revision\",\"created\",\"modified\",\"state\"\n");

    try {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        for (File xmlFile : xmlFiles) {
            Data data = new Data();
            saxParser.parse(xmlFile, new XMLHandler(data));

            for (DocumentData doc : data.getDocuments()) {
                if (doc == null || !getDocId.equals(doc.getDocId())) {
                    continue;
                }

                // ✅ Extract attributes
                String id = doc.getId() != null ? doc.getId() : "";
                String name = doc.getName() != null ? doc.getName() : "";
                String revision = doc.getRevision() != null ? doc.getRevision() : "";
                String created = doc.getCreated() != null ? doc.getCreated() : "";
                String modified = doc.getModified() != null ? doc.getModified() : "";
                String state = doc.getState() != null ? doc.getState() : "";

                // ✅ Format values to handle commas properly by enclosing in quotes
                documentDetails.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        id, name, revision, created, modified, state));
            }
        }
    } catch (ParserConfigurationException | SAXException | IOException e) {
        response.setError("Error processing XML files: " + e.getMessage());
        e.printStackTrace();
    }

    // ✅ Print CSV Output to Console
    System.out.println("\n" + documentDetails.toString());

    response.setData(documentDetails.toString());
    return response;
}


	private static boolean validateXML(File file, SAXParser saxParser) {
		try {
			saxParser.parse(file, new org.xml.sax.helpers.DefaultHandler());
			return true;
		} catch (SAXException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static Data extractDataFromXML(File file, SAXParser saxParser) {
		Data data = new Data();
		try {
			XMLHandler handler = new XMLHandler(data);
			saxParser.parse(file, handler);
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
		private String lastAttrName; // Store last processed attribute name
		private String currentBOType;

		public XMLHandler(Data data) {
			this.data = data;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) {
			elementValue.setLength(0); // Clear buffer

			if ("BO".equals(qName)) {
				this.attributes = new HashMap<>();
				currentBOType = null; // Reset BO type
			}
			if ("attr".equals(qName) || "ctrl".equals(qName)) {
				lastAttrName = null; // Reset attribute name
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) {
			elementValue.append(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String qName) {
			String value = elementValue.toString().trim(); // Keep original extracted text

			// Extract Type from <pk> tag
			if ("pk".equals(qName)) {
				currentBOType = extractTypeFromPK(value);
				//System.out.println("Extracted Type: " + currentBOType);

				// Initialize Object when `pk` is found
				if ("pim_package".equals(currentBOType)) {
					currentPackage = new PackageData();
				} else if ("part".equals(currentBOType)) {
					currentPart = new Part();
				} else if ("document".equals(currentBOType)) {
					currentDocument = new DocumentData();
				}
			}

		     //✅ Ensure `name` is only assigned from `<BO>`, not `<ctrl>` or `<attr>`
		    if ("name".equals(qName) && lastAttrName == null) {
		        if (currentPart != null && currentPart.getName() == null) {
		            currentPart.setName(value);
		            //System.out.println("✅ Assigned to Part: " + value);
		        }  else if (currentDocument != null && currentDocument.getName() == null) {
		            currentDocument.setName(value);
		            //System.out.println("✅ Assigned to Document: " + value);
		        }
		    }

		    // ✅ Process `attr` and `ctrl` correctly
		    if ("name".equals(qName) && !"BO".equals(currentBOType)) {
		        lastAttrName = value; // Store attribute name, but NOT for <BO>
		    } else if ("value".equals(qName) && lastAttrName != null) {
		        attributes.put(lastAttrName, value);
		        if ("partnumber".equals(lastAttrName) && currentPart != null) {
		            currentPart.setPartNumber(value);
		            //System.out.println("✅ Part Number Assigned: " + value);
		        }else if ("doc_id".equals(lastAttrName) && currentDocument != null) {
		            currentDocument.setDocId(value);
		            //System.out.println("✅ Part Number Assigned: " + value);
		        }
		        lastAttrName = null; // Reset after storing
		    }

			// Assign extracted values to the correct object
			if (currentPackage != null) {
				if ("type".equals(qName))
					currentPackage.setType(currentBOType);
				else if ("subtype".equals(qName))
					currentPackage.setSubtype(value);
				else if ("identifier".equals(qName))
					currentPackage.setId(value);
				else if ("created".equals(qName))
					currentPackage.setCreated(value);
				else if ("modified".equals(qName))
					currentPackage.setModified(value);
				else if ("state".equals(qName))
					currentPackage.setState(value);
			} else if (currentPart != null) {
				if ("type".equals(qName))
					currentPart.setType(currentBOType);
				else if ("subtype".equals(qName))
					currentPart.setSubtype(value);
				else if ("identifier".equals(qName))
					currentPart.setId(value);
				else if ("revision".equals(qName))
					currentPart.setRevision(value);
				else if ("created".equals(qName))
					currentPart.setCreated(value);
				else if ("modified".equals(qName))
					currentPart.setModified(value);
				else if ("state".equals(qName))
					currentPart.setState(value);
			} else if (currentDocument != null) {
				if ("type".equals(qName))
					currentDocument.setType(currentBOType);
				else if ("subtype".equals(qName))
					currentDocument.setSubtype(value);
				else if ("identifier".equals(qName))
					currentDocument.setId(value);
				else if ("revision".equals(qName))
					currentDocument.setRevision(value);
				else if ("created".equals(qName))
					currentDocument.setCreated(value);
				else if ("modified".equals(qName))
					currentDocument.setModified(value);
				else if ("state".equals(qName))
					currentDocument.setState(value);
			}

			// When `BO` closes, add to list and reset objects
			if ("BO".equals(qName)) {
				if (currentPackage != null) {
					currentPackage.setAttributes(attributes);
					data.getPackages().add(currentPackage);
					//System.out.println("Package added: " + currentPackage);
					currentPackage = null;
				} else if (currentPart != null) {
					currentPart.setAttributes(attributes);
					data.getParts().add(currentPart);
					//System.out.println("Part added: " + currentPart);
					currentPart = null;
				} else if (currentDocument != null) {
					currentDocument.setAttributes(attributes);
					data.getDocuments().add(currentDocument);
					//System.out.println("Document added: " + currentDocument);
					currentDocument = null;
				}
				currentBOType = null; // Reset for next BO
			}
		}

		private String extractTypeFromPK(String pkValue) {
			//System.out.println("Processing PK: " + pkValue);
			if (pkValue.contains("-type-:")) {
				String[] parts = pkValue.split("-type-:");
				if (parts.length > 1) {
					return parts[1].trim().split(" ")[0]; // Extract only the type
				}
			}
			return null;
		}
	}

	private static void saveToJsonFile(String jsonFilePath, Data jsonData) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.writeValue(new File(jsonFilePath), jsonData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
