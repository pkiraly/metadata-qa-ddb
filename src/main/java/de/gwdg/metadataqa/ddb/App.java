package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.calculator.CalculatorFacade;
import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.configuration.MeasurementConfiguration;
import de.gwdg.metadataqa.api.model.EdmFieldInstance;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputType;
import de.gwdg.metadataqa.api.schema.Schema;
import de.gwdg.metadataqa.api.xml.OaiPmhXPath;
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
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 */
public class App {
    private static final Logger logger = Logger.getLogger(App.class.getCanonicalName());
    private Writer writer;

    public enum FORMAT {CSV, JSON};
    public enum DATA_SOURCE {FILE, DIRECTORY};
    static Options options = new Options();
    private CommandLine cmd;
    private String inputFile;
    private String outputFile;
    private boolean indexing;
    private FORMAT format;
    private SqliteManager sqliteManager;
    private boolean doSqlite = false;
    private String idPath;
    private CalculatorFacade calculator;
    private String directory;
    private String fileMask;
    private DATA_SOURCE dataSource = DATA_SOURCE.FILE;
    private String fileNameInAnnotation;
    private String schemaFile;
    private String solrPath;
    private String sqlitePath;
    private String recordAddress = "//oai:record";
    private Map<String, String> namespaces;
    private boolean recursive = false;
    private String rootDirectory;

    static {
        options.addOption(new Option("c", "config", true, "Measurement configuration file"));
        options.addOption(new Option("s", "schema", true, "schema configuration file"));
        options.addOption(new Option("i", "input", true, "input file"));
        options.addOption(new Option("o", "output", true, "output file"));
        options.addOption(new Option("f", "format", true, "output format"));
        options.addOption(new Option("x", "indexing", false, "do indexing"));
        options.addOption(new Option("p", "path", true, "Solr path"));
        options.addOption(new Option("q", "sqlitePath", true, "SQLite database file path"));
        options.addOption(new Option("d", "directory", true, "input direcotry"));
        options.addOption(new Option("m", "mask", true, "input file mask"));
        options.addOption(new Option("l", "record-address", true, "XPath expression to fetch the records out of XML"));
        options.addOption(new Option("r", "recursive", false, "recursive iteration of directories"));
        options.addOption(new Option("r", "rootDirectory", true, "root direactory, make file path relative to this directory"));
    }

    public App(String[] args) throws ParseException, IOException {
        readParameters(args);

        calculator = initializeCalculator();
        idPath = calculator.getSchema().getRecordId().getJsonPath();
        namespaces = calculator.getSchema().getNamespaces();

        try {
            writer = Files.newBufferedWriter(Paths.get(outputFile));
            if (format.equals(FORMAT.CSV))
                writer.write(StringUtils.join(calculator.getHeader(), ",") + "\n");

            if (dataSource.equals(DATA_SOURCE.FILE))
                processFile(inputFile);
            else
                processDirectory(directory);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Some I/O issue happened", e);
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }

        calculator.shutDown();
    }

    private void processDirectory(String directory) throws IOException {
        logger.info("processDirectory: " + directory);
        File dir = new File(directory);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File file : directoryListing) {
                if (file.isFile() && file.exists()) {
                    if (fileMask == null || file.getName().matches(fileMask)) {
                        processFile(file.getAbsolutePath());
                    }
                } else if (file.isDirectory() && recursive) {
                    processDirectory(file.getAbsolutePath());
                }
            }
        } else {
            logger.info("Empty directory");
        }
    }

    private void readParameters(String[] args) throws ParseException {
        parseArguments(args);

        if (cmd.hasOption("input")) {
            inputFile = cmd.getOptionValue("input");
            fileNameInAnnotation = inputFile.substring(inputFile.lastIndexOf("/") + 1);
        }
        outputFile = cmd.getOptionValue("output");
        indexing = cmd.hasOption("indexing");
        format = cmd.getOptionValue("format").toLowerCase(Locale.ROOT).equals("csv") ? FORMAT.CSV : FORMAT.JSON;
        recursive = cmd.hasOption("recursive");
        logger.info(String.format("recursive: %s", recursive));
        if (cmd.hasOption("rootDirectory")) {
            rootDirectory = cmd.getOptionValue("rootDirectory");
            if (!rootDirectory.endsWith("/"))
                rootDirectory = rootDirectory + "/";
        }
        logger.info(rootDirectory);

        schemaFile = cmd.getOptionValue("schema");
        solrPath = cmd.hasOption("path") ? cmd.getOptionValue("path") : null;
        sqlitePath = cmd.hasOption("sqlitePath") ? cmd.getOptionValue("sqlitePath") : null;
        if (cmd.hasOption("directory")) {
            directory = cmd.getOptionValue("directory");
            dataSource = DATA_SOURCE.DIRECTORY;
            if (cmd.hasOption("mask")) {
                String rawMask = cmd.getOptionValue("mask");
                if (rawMask.startsWith("*"))
                    rawMask = "." + rawMask;
                fileMask = String.format("^%s$", rawMask);
            }
        }

        if (cmd.hasOption("record-address"))
            recordAddress = cmd.getOptionValue("record-address");

        sqliteManager = null;
        if (sqlitePath != null) {
            sqliteManager = new SqliteManager(sqlitePath);
            doSqlite = true;
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        App app = new App(args);
    }

    private void processFile(String inputFile) throws IOException {
        String relativePath = getRelativePath(inputFile);
        // logger.info("processFile: " + inputFile);

        try {
            XPathBasedIterator iterator = new XPathBasedIterator(new File(inputFile), recordAddress, namespaces);
            String line = null;
            while (iterator.hasNext()) {
                String xml = iterator.next();
                if (!indexing && doSqlite) {
                    OaiPmhXPath oaiPmhXPath = new OaiPmhXPath(xml, namespaces);
                    List<EdmFieldInstance> idList = oaiPmhXPath.extractFieldInstanceList(idPath);
                    if (idList != null && !idList.isEmpty()) {
                        String id = idList.get(0).getValue();
                        System.err.println(id);
                        sqliteManager.insert(relativePath, id, xml);
                    }
                }
                if (format.equals(FORMAT.CSV))
                    line = calculator.measure(xml);
                else
                    line = calculator.measureAsJson(xml);
                // logger.info(line);
                writer.write(line + "\n");
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    private String getRelativePath(String inputFile) {
        return (rootDirectory != null)  ? inputFile.replaceAll(rootDirectory, "") : inputFile;
    }

    private CalculatorFacade initializeCalculator() throws FileNotFoundException {
        Schema schema = ConfigurationReader.readSchemaYaml(schemaFile).asSchema();

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
              .withRuleCheckingOutputType(RuleCheckingOutputType.BOTH)
              // .withAnnotationColumns(String.format("{\"file\":\"%s\"}", fileNameInAnnotation))
            ;
        }

        CalculatorFacade calculator = new CalculatorFacade(configuration)
          .setSchema(schema);
        calculator.configure();
        return calculator;
    }

    private static String formatOptions(Option[] options) {
        List<String> items = new ArrayList<>();
        for (Option option : options) {
            items.add(String.format("%s: %s", option.getLongOpt(), option.getValue()));
        }
        return StringUtils.join(items, ", ");
    }

    private void parseArguments(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        cmd = parser.parse(options, args);
        logger.info("cmd: " + formatOptions(cmd.getOptions()));
    }
}
