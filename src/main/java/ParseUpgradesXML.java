import org.w3c.dom.*;
import java.util.*;

/**
 * Represents a class for parsing upgrade data from an XML file.
 * This class extends the AbstractParseXML class.
 * <p>
 * This class provides a method for reading upgrade data from the Document object.
 * It also provides a method for getting the parsed upgrades.
 */
public class ParseUpgradesXML extends AbstractParseXML {

    /**
     * Initializes a new ParseUpgradesXML object.
     */
    public ParseUpgradesXML() {
        super();
    }

    /**
     * Reads and returns a NodeList object of the <upgrade> elements from the office.
     * 
     * @param fileName The XML file name to read from.
     * @return the NodeList object of the <upgrade> elements
     * @throws Exception if parsing fails or <office> is missing.
     */
    @Override
    public NodeList readData(String fileName) throws Exception {
        Document d = getDocFromFile(fileName);
        Element root = d.getDocumentElement();

        // Locate the <office> element
        Element officeElement = (Element) root.getElementsByTagName("office").item(0);
        if (officeElement == null) {
            throw new Exception("Office element not found in the XML.");
        }

        // Retrieve all <upgrade> elements within <upgrades>
        NodeList upgradesList = officeElement.getElementsByTagName("upgrade");
        return upgradesList;
    }

    /**
     * Returns a list of Upgrade objects parsed from the given file.
     * 
     * @param fileName The XML file name to read from.
     * @return the list of parsed upgrades
     */
    public List<Upgrade> getUpgrades(String fileName) {
        List<Upgrade> upgrades = new ArrayList<>();
        try {
            NodeList upgradesNodeList = readData(fileName);
            upgrades = parseUpgrades(upgradesNodeList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return upgrades;
    }

    /**
     * Parses all upgrades from the given NodeList.
     * 
     * @param upgradesList NodeList containing <upgrade> elements.
     * @return List of Upgrade objects.
     */
    private List<Upgrade> parseUpgrades(NodeList upgradesList) {
        List<Upgrade> upgrades = new ArrayList<>();
        for (int i = 0; i < upgradesList.getLength(); i++) {
            Node upgradeNode = upgradesList.item(i);
            if (upgradeNode.getNodeType() == Node.ELEMENT_NODE) {
                Upgrade upgrade = parseUpgrade((Element) upgradeNode);
                upgrades.add(upgrade);
            }
        }
        return upgrades;
    }

    /**
     * Parses a single upgrade from the given Element object.
     * 
     * @param upgradeElement The XML Element representing the upgrade.
     * @return the parsed Upgrade object
     */
    private Upgrade parseUpgrade(Element upgradeElement) {
        int level = Integer.parseInt(upgradeElement.getAttribute("level"));
        String currency = upgradeElement.getAttribute("currency");
        int amt = Integer.parseInt(upgradeElement.getAttribute("amt"));
        Element areaElement = (Element) upgradeElement.getElementsByTagName("area").item(0);
        Area area = parseArea(areaElement);
        return new Upgrade(level, currency, amt, area);
    }
}
