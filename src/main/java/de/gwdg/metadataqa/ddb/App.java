package de.gwdg.metadataqa.ddb;

import de.gwdg.metadataqa.api.calculator.CalculatorFacade;
import de.gwdg.metadataqa.api.configuration.ConfigurationReader;
import de.gwdg.metadataqa.api.configuration.MeasurementConfiguration;
import de.gwdg.metadataqa.api.configuration.schema.Rule;
import de.gwdg.metadataqa.api.json.DataElement;
import de.gwdg.metadataqa.api.model.EdmFieldInstance;
import de.gwdg.metadataqa.api.rule.RuleCheckingOutputType;
import de.gwdg.metadataqa.api.schema.Schema;
import de.gwdg.metadataqa.api.xml.XPathWrapper;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

/**
 */
public class App {
    // private static final Logger logger = LoggerFactory.getLogger(App.class.getCanonicalName());
    private static Logger logger = Logger.getLogger(App.class.getName());

    static {
        try {
            InputStream stream = App.class.getClassLoader()
                .getResourceAsStream("logging.properties");
            LogManager.getLogManager().readConfiguration(stream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Writer writer;

    public enum FORMAT {CSV, JSON};
    public enum DATA_SOURCE {FILE, DIRECTORY};
    static Options options = new Options();
    private CommandLine cmd;
    private String inputFile;
    private String outputFile;
    private boolean indexing;
    private boolean storing;
    private FORMAT format;
    private SqliteManager sqliteManager;
    private MySqlManager mySqlManager;
    private boolean doSqlite = false;
    private boolean doMysql = false;
    private String idPath;
    private CalculatorFacade calculator;
    private String directory;
    private String fileMask;
    private DATA_SOURCE dataSource = DATA_SOURCE.FILE;
    private String fileNameInAnnotation;
    private String schemaFile;
    private String sqlitePath;
    private String recordAddress = "//oai:record";
    private Map<String, String> namespaces;
    private boolean recursive = false;
    private String rootDirectory;
    private String mysqlHost = "localhost";
    private String mysqlPort = "3306";
    private String solrPath;
    private String solrHost = "localhost";
    private String solrPort = "8983";
    private boolean skipDimension = false;
    private boolean skipContentType = false;
    private String schemaName;
    private int totalFiles = 0;
    private int totalRecords = 0;
    private int fileCount = 0;
    private int recordCount = 0;

    static {
        options.addOption(new Option("c", "config", true, "Measurement configuration file"));
        options.addOption(new Option("s", "schema", true, "schema configuration file"));
        options.addOption(new Option("i", "input", true, "input file"));
        options.addOption(new Option("o", "output", true, "output file"));
        options.addOption(new Option("f", "format", true, "output format"));
        options.addOption(new Option("x", "indexing", false, "do indexing"));
        options.addOption(new Option("t", "storing", false, "store XML into database"));
        options.addOption(new Option("p", "path", true, "Solr path"));
        options.addOption(new Option("q", "sqlitePath", true, "SQLite database file path"));
        options.addOption(new Option("d", "directory", true, "input direcotry"));
        options.addOption(new Option("m", "mask", true, "input file mask"));
        options.addOption(new Option("l", "record-address", true, "XPath expression to fetch the records out of XML"));
        options.addOption(new Option("r", "recursive", false, "recursive iteration of directories"));
        options.addOption(new Option("r", "rootDirectory", true, "root direactory, make file path relative to this directory"));
        options.addOption(new Option("D", "mysqlDatabase", true, "MySQL database"));
        options.addOption(new Option("U", "mysqlUser", true, "MySQL user name"));
        options.addOption(new Option("P", "mysqlPassword", true, "MySQL password"));
        options.addOption(new Option("A", "mysqlHost", true, "MySQL host"));
        options.addOption(new Option("B", "mysqlPort", true, "MySQL port"));
        options.addOption(new Option("C", "solrHost", true, "Apache Solr host"));
        options.addOption(new Option("D", "solrPort", true, "Apache Solr port"));
        options.addOption(new Option("E", "skipDimension", false, "skip Dimension check"));
        options.addOption(new Option("F", "skipContentType", false, "skip Content Type check"));
    }

    public App(String[] args) throws ParseException, IOException {
        readParameters(args);

        calculator = initializeCalculator();
        idPath = calculator.getSchema().getRecordId().getPath();
        namespaces = calculator.getSchema().getNamespaces();
        XPathWrapper.setXpathEngine(namespaces);
        schemaName = directory.substring(directory.lastIndexOf('/') + 1);
        totalFiles = mySqlManager.getFileCountBySchema(schemaName);
        totalRecords = mySqlManager.getRecordCountBySchema(schemaName);
        logger.info(String.format("schemaName: %s, total number of files: %d, total number of records: %d", schemaName, totalFiles, totalRecords));

        try {
            if (!indexing) {
                writer = Files.newBufferedWriter(Paths.get(outputFile));
                if (format.equals(FORMAT.CSV))
                    writer.write(StringUtils.join(calculator.getHeader(), ",") + "\n");
            }

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

        logger.info(String.format("number of processed files: %d, number of processed records: %d", fileCount, recordCount));
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
        storing = cmd.hasOption("storing");
        format = cmd.getOptionValue("format").toLowerCase(Locale.ROOT).equals("csv") ? FORMAT.CSV : FORMAT.JSON;
        recursive = cmd.hasOption("recursive");
        logger.info("recursive: " + recursive);
        if (cmd.hasOption("rootDirectory")) {
            rootDirectory = cmd.getOptionValue("rootDirectory");
            if (new File(rootDirectory).getAbsolutePath() != rootDirectory)
                rootDirectory = new File(rootDirectory).getAbsolutePath();
            if (!rootDirectory.endsWith("/"))
                rootDirectory = rootDirectory + "/";
        }
        logger.info("rootDirectory: " + rootDirectory);

        schemaFile = cmd.getOptionValue("schema");
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

        if (cmd.hasOption("mysqlDatabase")) {
            String mysqlDatabase = cmd.getOptionValue("mysqlDatabase");
            String mysqlUser = cmd.hasOption("mysqlUser") ? cmd.getOptionValue("mysqlUser") : null;
            String mysqlPassword = cmd.hasOption("mysqlPassword") ? cmd.getOptionValue("mysqlPassword") : null;
            if (cmd.hasOption("mysqlHost"))
                mysqlHost = cmd.getOptionValue("mysqlHost");
            if (cmd.hasOption("mysqlPort"))
                mysqlPort = cmd.getOptionValue("mysqlPort");
            if (StringUtils.isNotBlank(mysqlDatabase) && StringUtils.isNotBlank(mysqlUser) && StringUtils.isNotBlank(mysqlPassword)) {
                mySqlManager = new MySqlManager(mysqlHost, mysqlPort, mysqlDatabase, mysqlUser, mysqlPassword);
                doMysql = true;
            }
        }

        solrPath = cmd.hasOption("path") ? cmd.getOptionValue("path") : null;
        if (cmd.hasOption("solrHost"))
            solrHost = cmd.getOptionValue("solrHost");
        if (cmd.hasOption("solrPort"))
            solrPort = cmd.getOptionValue("solrPort");
        if (cmd.hasOption("skipDimension"))
            skipDimension = true;
        if (cmd.hasOption("skipContentType"))
            skipContentType = true;
    }

    private void processFile(String inputFile) throws IOException {
        fileCount++;
        String relativePath = getRelativePath(inputFile);
        logger.info(String.format("processing %d/%d: %s", fileCount, totalFiles, relativePath));
        // logger.info("processFile: {} -> {}", inputFile, relativePath);

        try {
            XPathBasedIterator iterator = new XPathBasedIterator(new File(inputFile), recordAddress, namespaces);
            String line = null;
            while (iterator.hasNext()) {
                String xml = iterator.next();
                if (++recordCount % 100 == 0) {
                    if (totalRecords == 0)
                        logger.info("  record #" + recordCount);
                    else
                        logger.info(String.format("  record #%d/%d", recordCount, totalRecords));
                }
                if (storing && (doSqlite || doMysql)) {
                    XPathWrapper xPathWrapper = new XPathWrapper(xml, namespaces);
                    List<EdmFieldInstance> idList = xPathWrapper.extractFieldInstanceList(idPath);
                    if (idList != null && !idList.isEmpty()) {
                        String id = idList.get(0).getValue();
                        if (doSqlite)
                            sqliteManager.insert(relativePath, id, xml);
                        if (doMysql)
                            mySqlManager.insertFileRecord(relativePath, id);
                    }
                }
                if (format.equals(FORMAT.CSV))
                    line = calculator.measure(xml);
                else
                    line = calculator.measureAsJson(xml);
                // logger.info(line);
                if (!indexing)
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
        if (skipDimension || skipContentType) {
            disableRules(schema);
        }

        MeasurementConfiguration configuration = null;
        if (indexing) {
            configuration = new MeasurementConfiguration()
              .disableCompletenessMeasurement()
              .disableFieldExistenceMeasurement()
              .disableFieldCardinalityMeasurement()
              .disableRuleCatalogMeasurement() // off
              .disableUniquenessMeasurement()  // off
              .disableFieldExtractor()         // off
              .withSolrConfiguration(solrHost, solrPort, solrPath)
              .enableIndexing()
            ;
        } else {
            configuration = new MeasurementConfiguration()
              .disableCompletenessMeasurement()
              .disableFieldExistenceMeasurement()
              .disableFieldCardinalityMeasurement()
              .enableRuleCatalogMeasurement()  // on
              .enableFieldExtractor()          // on
              .withSolrConfiguration(solrHost, solrPort, solrPath)
              // .enableUniquenessMeasurement()
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

    private void disableRules(Schema schema) {
        for (DataElement dataElement : schema.getPaths()) {
            List<Rule> rules = dataElement.getRules();
            if (rules == null)
                continue;
            for (Rule rule : rules) {
                disableRule(dataElement, rule);
            }
        }
    }

    private void disableRule(DataElement dataElement, Rule rule) {
        if (skipDimension && rule.getDimension() != null) {
            rule.setSkip(true);
            logger.info(String.format("skip %s [dimension check] for %s", rule.getId(), dataElement.getPath()));
        }
        if (skipContentType && rule.getContentType() != null) {
            rule.setSkip(true);
            logger.info(String.format("skip %s [content type] for %s", rule.getId(), dataElement.getPath()));
        }
        if (rule.getAnd() != null) {
            for (Rule child : rule.getAnd())
                disableRule(dataElement, child);
        }
        if (rule.getOr() != null) {
            for (Rule child : rule.getOr())
                disableRule(dataElement, child);
        }
        if (rule.getNot() != null) {
            for (Rule child : rule.getNot())
                disableRule(dataElement, child);
        }
    }

    private static String formatOptions(Option[] options) {
        List<String> items = new ArrayList<>();
        for (Option option : options) {
            if (option.hasArg())
                items.add(String.format("%s: %s", option.getLongOpt(), option.getValue()));
            else
                items.add(String.format("%s", option.getLongOpt()));
        }
        return StringUtils.join(items, ", ");
    }

    private void parseArguments(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        cmd = parser.parse(options, args);
        logger.info("cmd: " + formatOptions(cmd.getOptions()));
    }

    public static void main(String[] args) throws IOException, ParseException {
        App app = new App(args);
        logger.info("DONE");
    }
}
