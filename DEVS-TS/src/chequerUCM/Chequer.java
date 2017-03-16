package chequerUCM;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * 
 * @author: María Eva Villarreal Guzmán. E-mail: villarrealguzman@gmail.com
 *
 */
public class Chequer {

	String path;
	String message = "";
	Document doc;

	public Chequer(String pathName) {
		this.path = pathName;
	}

	public String getPathName() {
		return path;
	}

	public void setPathName(String pathName) {
		this.path = pathName;
	}

	/**
	 * Chequea que el UCM sea válido Si devuelve 0 es porque es válido Si
	 * devuelve 1 es porque existen elementos en el UCM que no están en el
	 * 'dibujo'; Si devuelve 2 es porque existe más de un startPoint o más de un
	 * endPoint; Si devuelve 3 es porque no es correcta la definición de los
	 * metadatos en las responsabilidades; Si devuelve 4 es porque existen
	 * elementos con nombres duplicados; Si devuleve 5 es porque no existen
	 * responsabilidades; Si devuelve 6 es porque no existen componentes; Si
	 * devuelve 7 es porque el dato del start point no esta bien ingresado; Si
	 * devuelve 8 es porque los datos del or fork no estan bien ingresados
	 * 
	 * @return
	 */
	public String isValid() {

		try {

			File fXmlFile = new File(path);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;

			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			isOneStartPointEndPoint();
			isResponsibilities();
			isComponents();
			isDuplicateName();
			isMetadataInStartPoint();
			isMetadataInResponsibilities();
			isMetadataInOrFork();
			isInDefinition();

			return message;

		} catch (FileNotFoundException e) {
			return "File "+ path + " not found";
		}catch (SAXException | IOException | ParserConfigurationException  e) {
			return e.toString();
		}

	}

	/**
	 * Chequea que exista solo un start point y un solo end point
	 */
	private void isOneStartPointEndPoint() {
		Boolean startPointFirst = false;
		Boolean endPointFirst = false;

		// Estás variables son evaluadas al final para saber si hay más de un
		// start point o más de un end point
		Boolean isMoreStartPoint = false;
		Boolean isMoreEndPoint = false;

		NodeList nodesList = doc.getElementsByTagName("nodes");

		for (int i = 0; i < nodesList.getLength(); i++) {

			Node node = nodesList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eNode = (Element) node;
				// chequea si existe más de un start point
				if (eNode.getAttribute("xsi:type").equals("ucm.map:StartPoint")) {
					if (startPointFirst == false) {
						startPointFirst = true;
					} else {
						isMoreStartPoint = true;
					}
				}
				// chequea si existe más de un end point
				if (eNode.getAttribute("xsi:type").equals("ucm.map:EndPoint")) {
					if (endPointFirst == false) {
						endPointFirst = true;
					} else {
						isMoreEndPoint = true;
					}
				}
			}
		}
		if (isMoreStartPoint) {
			message = message + "there is more than a start point \n";
		}
		if (isMoreEndPoint) {
			message = message + "there is more than a end point \n";
		}
	}

	/**
	 * Chequea si los metadatos de cada responsabilidad, están ingresados
	 * correctamente
	 * 
	 * @return
	 */
	private void isMetadataInResponsibilities() {

		Boolean isNotCountCorrect = false;
		Boolean isNotValueCorrect = false;

		NodeList responsibilitiesList = doc.getElementsByTagName("responsibilities");

		for (int i = 0; i < responsibilitiesList.getLength(); i++) {
			Boolean isMeanExecutionTime = false;
			Boolean isMeanRecoveryTime = false;
			Boolean isMeanDowntime = false;
			Boolean isMeanTimeBFail = false;

			int countMeanExecutionTime = 0;
			int countMeanRecoveryTime = 0;
			int countMeanDowntime = 0;
			int countMeanTimeBFail = 0;

			Node responsibility = responsibilitiesList.item(i);

			if (responsibility.getNodeType() == Node.ELEMENT_NODE) {
				Element eResponsibility = (Element) responsibility;

				NodeList metadataList = eResponsibility.getElementsByTagName("metadata");

				for (int k = 0; k < metadataList.getLength(); k++) {
					Node metadata = metadataList.item(k);
					Element eMetadata = (Element) metadata;
					String nameMetadata = eMetadata.getAttribute("name");

					if (nameMetadata.equals("MeanExecutionTime")) {
						try {
							countMeanExecutionTime++;
							String valueMetadata = eMetadata.getAttribute("value");
							double value = Double.parseDouble(valueMetadata);
							if (!isMeanExecutionTime) {
								isMeanExecutionTime = true;
							}
						} catch (Exception e) {
							// Si llega acá, es porque el valor ingresado no se
							// puede castear a double
							// por lo tanto, isMeanExecutionTime queda igual a
							// false
						}
					} else if (nameMetadata.equals("MeanRecoveryTime")) {
						try {
							countMeanRecoveryTime++;
							String valueMetadata = eMetadata.getAttribute("value");
							double value = Double.parseDouble(valueMetadata);
							if (!isMeanRecoveryTime) {
								isMeanRecoveryTime = true;
							}
						} catch (Exception e) {
							// Si llega acá, es porque el valor ingresado no se
							// puede castear a double
							// por lo tanto, isMeanRecoveryTime queda igual a
							// false
						}
					} else if (nameMetadata.equals("MeanDowntime")) {
						try {
							countMeanDowntime++;
							String valueMetadata = eMetadata.getAttribute("value");
							double value = Double.parseDouble(valueMetadata);
							if (!isMeanDowntime) {
								isMeanDowntime = true;
							}
						} catch (Exception e) {
							// Si llega acá, es porque el valor ingresado no se
							// puede castear a double
							// por lo tanto, isMeanDowntime queda igual a false
						}
					} else if (nameMetadata.equals("MeanTimeBFail")) {
						try {
							countMeanTimeBFail++;
							String valueMetadata = eMetadata.getAttribute("value");
							double value = Double.parseDouble(valueMetadata);
							if (!isMeanTimeBFail) {
								isMeanTimeBFail = true;
							}
						} catch (Exception e) {
							// Si llega acá, es porque el valor ingresado no se
							// puede castear a double
							// por lo tanto, isMeanTimeBFail queda igual a false
						}
					}
				}

			}
			if (!isMeanTimeBFail || !isMeanDowntime || !isMeanRecoveryTime || !isMeanExecutionTime) {
				isNotValueCorrect = true;
			}
			if (!(countMeanTimeBFail == 1) || !(countMeanDowntime == 1) || !(countMeanRecoveryTime == 1)
					|| !(countMeanExecutionTime == 1)) {
				isNotCountCorrect = true;
			}

		}

		if (isNotCountCorrect || isNotValueCorrect) {
			message = message + "responsibilities parameters were not entered correctly \n";
		}
	}

	/**
	 * Chequea si los metadatos del start point, están ingresador correctamente
	 * 
	 */
	private void isMetadataInStartPoint() {
		Boolean isMeanTimeBRequest = false;

		NodeList nodesList = doc.getElementsByTagName("nodes");

		for (int i = 0; i < nodesList.getLength(); i++) {

			Node node = nodesList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eNode = (Element) node;

				String typeNode = eNode.getAttribute("xsi:type");

				if (typeNode.equals("ucm.map:StartPoint")) {
					NodeList metadataList = eNode.getElementsByTagName("metadata");

					for (int k = 0; k < metadataList.getLength(); k++) {
						Node metadata = metadataList.item(k);
						Element eMetadata = (Element) metadata;
						String nameMetadata = eMetadata.getAttribute("name");

						if (nameMetadata.equals("MeanTimeBRequest")) {
							try {
								String valueMetadata = eMetadata.getAttribute("value");
								double value = Double.parseDouble(valueMetadata);
								if (!isMeanTimeBRequest) {
									isMeanTimeBRequest = true;
								}
							} catch (Exception e) {
								// Si llega acá, es porque el valor ingresado no
								// se
								// puede castear a double
								// por lo tanto, MeanTimeBRequest queda igual a
								// false
							}
						}

					}

				}
			}
		}

		if (!isMeanTimeBRequest) {
			message = message + "the parameter of the start point were not entered correctly \n";
		}

	}

	/**
	 * Chequea si los metadatos del Or fork están ingresador correctamente
	 */
	private void isMetadataInOrFork() {
		Boolean isNotOrForkCorrect = false;

		NodeList nodesList = doc.getElementsByTagName("nodes");

		for (int i = 0; i < nodesList.getLength(); i++) {

			Node node = nodesList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eNode = (Element) node;

				String typeNode = eNode.getAttribute("xsi:type");

				if (typeNode.equals("ucm.map:OrFork")) {
					// esta variable cuanta la cantidad de parámetros que tiene
					// el or fork
					int parameterCount = 0;
					// esta variable indica la cantidad de parámetros que
					// debería tener el or fork
					int pathParameter = 0;
					String idOrFork = "";

					idOrFork = eNode.getAttribute("id");
					NodeList metadataList = eNode.getElementsByTagName("metadata");

					for (int k = 0; k < metadataList.getLength(); k++) {
						Node metadata = metadataList.item(k);
						Element eMetadata = (Element) metadata;
						String nameMetadata = eMetadata.getAttribute("name");

						if (nameMetadata.equals("PathProbability")) {
							try {
								String valueMetadata = eMetadata.getAttribute("value");
								double value = Double.parseDouble(valueMetadata);
								parameterCount++;
							} catch (Exception e) {
								// Si llega acá, es porque el valor ingresado no
								// se
								// puede castear a double
								// por lo tanto, MeanTimeBRequest queda igual a
								// false
								isNotOrForkCorrect = true;
							}
						}

					}

					// Obtiene la cantidad de caminos que salen del Or Fork,
					// y por lo tanto, la cantidad de parámetros necesarios
					NodeList connectionsList = doc.getElementsByTagName("connections");

					for (int j = 0; j < connectionsList.getLength(); j++) {

						Node connectionNode = connectionsList.item(j);

						if (node.getNodeType() == Node.ELEMENT_NODE) {
							Element eConnectionNode = (Element) connectionNode;

							String sourceConnectionNode = eConnectionNode.getAttribute("source");
							if (sourceConnectionNode.equals(idOrFork)) {
								pathParameter++;
							}
						}
					}

					if (parameterCount != pathParameter) {
						isNotOrForkCorrect = true;
					}

				}
			}
		}
		if (isNotOrForkCorrect) {
			message = message + "the parameter of the or fork were not entered correctly \n";
		}
	}

	/**
	 * Chequea que existan no elementos en el UCM que no se muestren en el
	 * dibujo
	 * 
	 */
	private void isInDefinition() {
		Boolean IsNotInDefinition = false;

		NodeList responsibilitiesList = doc.getElementsByTagName("responsibilities");

		for (int i = 0; i < responsibilitiesList.getLength(); i++) {
			Node responsibility = responsibilitiesList.item(i);

			if (responsibility.getNodeType() == Node.ELEMENT_NODE) {
				Element eResponsibility = (Element) responsibility;

				String respRefs = eResponsibility.getAttribute("respRefs");
				if (respRefs.equals("")) {
					IsNotInDefinition = true;
				}

			}
		}

		NodeList componentsList = doc.getElementsByTagName("components");

		for (int i = 0; i < componentsList.getLength(); i++) {
			Node component = componentsList.item(i);

			if (component.getNodeType() == Node.ELEMENT_NODE) {
				Element eComponent = (Element) component;

				String contRefs = eComponent.getAttribute("contRefs");
				if (contRefs.equals("")) {
					IsNotInDefinition = true;
				}

			}
		}
		if (IsNotInDefinition) {
			message = message + "there are path elements are in the definition but not in the picture \n";
		}
	}

	/**
	 * Chequea que exista al menos una responsabilidad
	 */
	private void isResponsibilities() {
		NodeList responsibilitiesList = doc.getElementsByTagName("responsibilities");
		if (!(responsibilitiesList.getLength() > 0)) {
			message = message + "there are no responsabilities \n";
		}
	}

	/**
	 * Chequea que exista al menos un componente
	 */
	private void isComponents() {
		NodeList componentsList = doc.getElementsByTagName("components");
		if (!(componentsList.getLength() > 0)) {
			message = message + "there are no components \n";
		}
	}

	/**
	 * Chequea que no existan nombres duplicados de responsabilidades y de
	 * componentes
	 */
	private void isDuplicateName() {
		Boolean isDuplicateName = false;

		NodeList responsibilitiesList = doc.getElementsByTagName("responsibilities");

		for (int i = 0; i < responsibilitiesList.getLength(); i++) {
			Node responsibility = responsibilitiesList.item(i);

			if (responsibility.getNodeType() == Node.ELEMENT_NODE) {
				Element eResponsibility = (Element) responsibility;

				String respRefs = eResponsibility.getAttribute("respRefs");
				String[] respRefsList = respRefs.split(" ");
				if (respRefsList.length > 1) {
					isDuplicateName = true;
				}
			}
		}

		NodeList componentsList = doc.getElementsByTagName("components");

		for (int i = 0; i < componentsList.getLength(); i++) {
			Node component = componentsList.item(i);

			if (component.getNodeType() == Node.ELEMENT_NODE) {
				Element eComponent = (Element) component;

				String contRefs = eComponent.getAttribute("contRefs");
				String[] contRefsList = contRefs.split(" ");
				if (contRefsList.length > 1) {
					isDuplicateName = true;
				}

			}
		}

		if (isDuplicateName) {
			message = message + "there are duplicate names path elements \n";
		}
	}

}
