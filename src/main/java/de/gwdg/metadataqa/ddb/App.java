package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.calculator.CalculatorFacade;
import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.configuration.MeasurementConfiguration;
import de.gwdg.metadataqa.api.interfaces.Calculator;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 */
public class App {
    private static final Logger logger = Logger.getLogger(App.class.getCanonicalName());
    public enum FORMAT {CSV, JSON};

    public static void main(String[] args) throws IOException, ParseException {
        Options options = new Options();
        options.addOption(new Option("c", "config", true, "Measurement configuration file"));
        options.addOption(new Option("s", "schema", true, "schema configuration file"));
        options.addOption(new Option("i", "input", true, "input file"));
        options.addOption(new Option("o", "output", true, "output file"));
        options.addOption(new Option("f", "format", true, "output format"));
        options.addOption(new Option("x", "indexing", false, "do indexing"));
        options.addOption(new Option("p", "path", true, "Solr path"));
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        logger.info("cmd: " + cmd.toString());

        String inputFile = cmd.getOptionValue("input");
        String schemaFile = cmd.getOptionValue("schema");
        String outputFile = cmd.getOptionValue("output");
        boolean indexing = cmd.hasOption("indexing");
        String solrPath = cmd.hasOption("path") ? cmd.getOptionValue("path") : null;
        FORMAT format = cmd.getOptionValue("format").toLowerCase(Locale.ROOT).equals("csv") ? FORMAT.CSV : FORMAT.JSON;
        CalculatorFacade calculator = initializeCalculator(schemaFile, indexing, solrPath);

        try {
            XPathBasedIterator iterator = new XPathBasedIterator(new File(inputFile), "//record");
            String line = null;
            try (var writer = Files.newBufferedWriter(Paths.get(outputFile))) {
                if (format.equals(FORMAT.CSV)) {
                    List<String> headers = new ArrayList<>();
                    for (String h : calculator.getHeader()) {
                        System.err.println(h);
                    }
                    line = StringUtils.join(calculator.getHeader(), ",");
                    writer.write(line + "\n");
                }
                while (iterator.hasNext()) {
                    if (format.equals(FORMAT.CSV))
                        line = calculator.measure(iterator.next());
                    else
                        line = calculator.measureAsJson(iterator.next());
                    writer.write(line + "\n");
                }
                calculator.shutDown();
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

    private static CalculatorFacade initializeCalculator(String schemaFile, boolean indexing, String solrPath) throws FileNotFoundException {
        logger.info("indexing: " + indexing);
        logger.info("solrPath: " + solrPath);
        Schema schema = ConfigurationReader.readSchemaYaml(schemaFile).asSchema();
        logger.info("RecordId: " + schema.getRecordId());

        MeasurementConfiguration configuration = null;
        if (indexing) {
            configuration = new MeasurementConfiguration()
              .disableCompletenessMeasurement()
              .disableFieldExistenceMeasurement()
              .disableFieldCardinalityMeasurement()
              .disableRuleCatalogMeasurement()
              .disableUniquenessMeasurement()
              .disableFieldExtractor()
              .withSolrConfiguration("localhost", "8983", solrPath)
              .enableIndexing()
            ;
        } else {
            configuration = new MeasurementConfiguration()
              .disableCompletenessMeasurement()
              .disableFieldExistenceMeasurement()
              .disableFieldCardinalityMeasurement()
              .enableRuleCatalogMeasurement()
              .enableFieldExtractor()
              .withOnlyIdInHeader(true)
              .withRuleCheckingOutputType(RuleCheckingOutputType.STATUS)
            ;
        }

        CalculatorFacade calculator = new CalculatorFacade(configuration)
          .setSchema(schema);
        calculator.configure();
        return calculator;
    }
}
