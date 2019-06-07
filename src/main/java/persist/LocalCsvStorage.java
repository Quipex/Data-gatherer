package persist;

import exceptions.PersistenceException;
import lombok.extern.log4j.Log4j2;
import model.CSVable;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class LocalCsvStorage {
    private static final char CSV_DELIMITER = ';';
    private Path filePath;

    private LocalCsvStorage() {}

    public LocalCsvStorage(Path filePath) {
        this();
        this.filePath = filePath;
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
            CSVFormat format = CSVFormat.DEFAULT.withDelimiter(CSV_DELIMITER);
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
    public List<String> read() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)){
            String line = reader.readLine();
            lines.add(line);
        } catch (IOException e) {
            throw new PersistenceException(e);
        }
        return lines;
    }

    public List<CSVable> read(LocalDateTime from, LocalDateTime to) {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            final CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withDelimiter(';');
            CSVParser parser = new CSVParser(reader, csvFormat);
            for (CSVRecord record : parser) {
                System.out.println(record.getRecordNumber());
                for (String col : record) {
                    System.out.println(col);
                }
                System.out.println("-------------");
            }
        } catch (IOException e) {
            throw new PersistenceException(e);
        }

        throw new UnsupportedOperationException();
    }

    /**
     * Clears the file writing 0 bytes
     */
    public void clear() {
        try {
            log.debug("Cleared " + filePath.toString());
            Files.write(filePath, new byte[]{}, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new PersistenceException(e);
        }
    }

}
