package handler;

import com.opencsv.bean.CsvToBeanBuilder;
import model.Discrepancy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class CSVReader {
    public List<Discrepancy> readCSV(File csvFile) throws FileNotFoundException {
        List<Discrepancy> discrepancies = new CsvToBeanBuilder(new FileReader(csvFile)).withType(Discrepancy.class).build().parse();
        return discrepancies;
    }
}
