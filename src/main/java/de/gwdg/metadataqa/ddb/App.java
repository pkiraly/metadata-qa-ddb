package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.calculator.CalculatorFacade;
import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.configuration.MeasurementConfiguration;
import de.gwdg.metadataqa.api.schema.Schema;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class App {
    private static final Logger logger = Logger.getLogger(App.class.getCanonicalName());

    public static void main(String[] args) throws IOException, ParseException {
        Options options = new Options();
        options.addOption(new Option("c", "config", true, "Measurement configuration file"));
        options.addOption(new Option("s", "schema", true, "schema configuration file"));
        options.addOption(new Option("i", "input", true, "input file"));
        options.addOption(new Option("o", "output", true, "output file"));
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);
        String inputFile = cmd.getOptionValue("input");
        String schemaFile = cmd.getOptionValue("schema");
        String outputFile = cmd.getOptionValue("output");
        CalculatorFacade calculator = initializeCalculator(schemaFile);

        try {
            XPathBasedIterator iterator = new XPathBasedIterator(new File(inputFile), "//record");
            String csv = null;
            try (var writer = Files.newBufferedWriter(Paths.get(outputFile))) {
                while (iterator.hasNext()) {
                    csv = calculator.measureAsJson(iterator.next());
                    writer.write(csv + "\n");
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Some I/O issue happened", e);
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
          .disableFieldExtractor();

        CalculatorFacade calculator = new CalculatorFacade(configuration)
          .setSchema(schema);
        calculator.configure();
        return calculator;
    }
}
