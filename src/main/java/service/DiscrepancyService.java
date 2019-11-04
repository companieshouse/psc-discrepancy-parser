package service;

import com.amazonaws.services.s3.model.S3Object;
import model.Discrepancy;

import java.util.List;

public interface DiscrepancyService {
    boolean validateCsv();
    List<Discrepancy> readObjectContent(S3Object s3Object);
}
