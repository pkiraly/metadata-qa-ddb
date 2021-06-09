package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.calculator.CalculatorFacade;
import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.configuration.MeasurementConfiguration;
import de.gwdg.metadataqa.api.configuration.schema.Rule;
import de.gwdg.metadataqa.api.schema.Schema;
import de.gwdg.metadataqa.api.util.FileUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws IOException, URISyntaxException {
        String path = App.class.getClassLoader().getResource("dc-schema.yaml").getPath();
        Schema schema = ConfigurationReader.readSchemaYaml(path).asSchema();

        Rule rule = schema.getPathByLabel("dc:title").getRules().get(0);

        MeasurementConfiguration configuration = new MeasurementConfiguration()
          .disableCompletenessMeasurement()
          .disableFieldExistenceMeasurement()
          .disableFieldCardinalityMeasurement()
          .enableRuleCatalogMeasurement()
          .enableFieldExtractor();

        CalculatorFacade calculator = new CalculatorFacade(configuration)
          .setSchema(schema);
        calculator.configure();

        String inputFile = "/home/kiru/git/metadata-qa-ddb/src/test/resources/dc/oai-dc-sample1.xml";
        String content = FileUtils.readFromUrl("file://" + inputFile);

        Map<String, Object> csv = calculator.measureAsMap(content);
        System.err.println(csv);
    }
}
