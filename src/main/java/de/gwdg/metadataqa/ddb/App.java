package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.calculator.CalculatorFacade;
import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.configuration.MeasurementConfiguration;
import de.gwdg.metadataqa.api.schema.Schema;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 */
public class App {
    public static void main(String[] args) throws IOException, URISyntaxException {
        String inputFile = args[0];
        String schemaFile = args[1];
        CalculatorFacade calculator = initializeCalculator(schemaFile);

        try {
            XPathBasedIterator iterator = new XPathBasedIterator(new File(inputFile), "//record");
            String csv = null;
            while (iterator.hasNext()) {
                csv = calculator.measureAsJson(iterator.next());
                System.err.println(csv);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    private static CalculatorFacade initializeCalculator(String schemaFile) throws FileNotFoundException {
        Schema schema = ConfigurationReader.readSchemaYaml(schemaFile).asSchema();

        MeasurementConfiguration configuration = new MeasurementConfiguration()
          .disableCompletenessMeasurement()
          .disableFieldExistenceMeasurement()
          .disableFieldCardinalityMeasurement()
          .enableRuleCatalogMeasurement()
          .enableFieldExtractor();

        CalculatorFacade calculator = new CalculatorFacade(configuration)
          .setSchema(schema);
        calculator.configure();
        return calculator;
    }
}
