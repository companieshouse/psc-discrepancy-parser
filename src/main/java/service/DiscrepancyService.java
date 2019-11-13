package service;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.opencsv.bean.CsvToBeanBuilder;
import model.Discrepancy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class DiscrepancyService {

    public List<Discrepancy> readObjectContent(S3Object s3Object){
        S3ObjectInputStream stream = s3Object.getObjectContent();
        return (List<Discrepancy>) new CsvToBeanBuilder(
                new BufferedReader(new InputStreamReader(stream))).withType(Discrepancy.class)
                .build().parse();
    }

    public boolean validateCsv() {
        return true;
    }
}
