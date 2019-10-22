package handler;

import model.Discrepancy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class CSVParser {

    public static void main(String[] args) throws FileNotFoundException {

        File csvFile = new File(CSVParser.class.getClassLoader().getResource("discrepancy.csv").getFile());
        CSVReader csvReader = new CSVReader();
        final List<Discrepancy> discrepancyList = csvReader.readCSV(csvFile);
        discrepancyList.stream().forEach(discrepancy -> System.out.println(discrepancy.getCompanyName()));
    }
}
