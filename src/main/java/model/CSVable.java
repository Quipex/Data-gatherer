package model;

public interface CSVable {

    /**
     * @return a list of properties of a class
     */
    String[] toCsv();

    /**
     * Fill object's properties with arguments passed as list
     * @param params to be filled to the object
     */
    void fillFromCsv(String... params);

    /**
     * @return csv header that corresponds to class's properties
     */
    String[] getCsvHeader();
}
