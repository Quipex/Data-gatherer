package persist;

import exceptions.PersistenceException;
import lombok.extern.log4j.Log4j2;
import model.CSVable;
import model.Dateable;
import model.SummarizedTrendInfo;
import model.TrendInfo;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("Duplicates")
@Log4j2
public class LocalCsvStorage {
    private static final char CSV_DELIMITER = ';';
    private Path filePath;

    private LocalCsvStorage() {
    }

    public LocalCsvStorage(Path filePath) {
        this();
        this.filePath = filePath;
    }

    private static void logAndThrow(RuntimeException e) {
        log.error(e);
        throw e;
    }

    /**
     * Writes to a file. If file was empty, writes headers first. Does nothing if the list is empty.
     *
     * @param csvableList to write
     * @throws PersistenceException if file problems
     */
    public void append(List<CSVable> csvableList) {
        if (csvableList.size() == 0) {
            return;
        }

        log.debug("Writing to " + filePath.toString());
        boolean needHeader;
        try {
            long fileSize = Files.size(filePath);
            needHeader = fileSize == 0;
        } catch (IOException e) {
            throw new PersistenceException(e);
        }

        try (Writer writer = Files.newBufferedWriter(filePath, StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
            CSVFormat format = getCsvFormat();
            if (needHeader) {
                format = format.withHeader(csvableList.get(0).getCsvHeader());
            }
            CSVPrinter csvPrinter = new CSVPrinter(writer, format);
            for (CSVable csVable : csvableList) {
                csvPrinter.printRecord((Object[]) csVable.toCsv());
            }
            synchronized (this) {
                csvPrinter.flush();
            }
        } catch (IOException e) {
            throw new PersistenceException(e);
        }
        log.debug("Written");
    }

    /**
     * Reads all the lines from the file
     *
     * @return contents of the file
     */
    public synchronized List<CSVable> read() {
        List<CSVable> listToSend = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(filePath)) {
            CSVParser parser = new CSVParser(reader, getCsvFormat().withFirstRecordAsHeader());
            Map<String, Integer> header = parser.getHeaderMap();
            for (CSVRecord record : parser) {
                Optional<CSVable> extraction = extractCsvable(header, record);
                extraction.ifPresent(listToSend::add);
            }
        } catch (IOException e) {
            throw new PersistenceException(e);
        }

        return listToSend;
    }

    private Optional<CSVable> extractCsvable(Map<String, Integer> header, CSVRecord record) {
        CSVable info = CsvableFactory.fromRecord(record, header);

        if (info == null) {
            logAndThrow(new PersistenceException("Got null from record"));
            return Optional.empty();
        }
        if (info.getClass() == TrendInfo.class) {
            info = new SummarizedTrendInfo((TrendInfo) info);
        }
        if (info instanceof Dateable) {
            return Optional.of(info);
        } else {
            logAndThrow(new PersistenceException("Can't extract a class that is not Dateable: " + info.getClass()));
        }
        return Optional.empty();
    }

    /**
     * Reads from csv file and returns csvables that fit to dates inclusively.
     *
     * @param from date to fit
     * @param to   date to fit
     * @return csvables that fit
     */
    public synchronized List<CSVable> read(LocalDateTime from, LocalDateTime to) {
        List<CSVable> fullList = read();
        List<CSVable> filteredList = new ArrayList<>();
        for (CSVable csVable : fullList) {
            if (csVable instanceof Dateable) {
                if (((Dateable) csVable).fitsBetweenDates(from, to)) {
                    filteredList.add(csVable);
                }
            }
        }

        return filteredList;
    }

    private Optional<CSVable> extractCsvable(LocalDateTime from, LocalDateTime to, Map<String, Integer> header, CSVRecord record) {
        CSVable info = CsvableFactory.fromRecord(record, header);

        if (info == null) {
            logAndThrow(new PersistenceException("Got null from record"));
            return Optional.empty();
        }
        if (info.getClass() == TrendInfo.class) {
            info = new SummarizedTrendInfo((TrendInfo) info);
        }
        if (info instanceof Dateable) {
            if (((Dateable) info).fitsBetweenDates(from, to)) {
                return Optional.of(info);
            }
        } else {
            logAndThrow(new PersistenceException("Can't extract a class that is not Dateable: " + info.getClass()));
        }
        return Optional.empty();
    }

    private CSVFormat getCsvFormat() {
        return CSVFormat.DEFAULT.withDelimiter(CSV_DELIMITER);
    }

}
