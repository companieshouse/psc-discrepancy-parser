package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.opencsv.bean.CsvToBeanBuilder;
import model.Discrepancy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;


public class Handler implements RequestHandler<S3Event, String> {


    public String handleRequest(S3Event s3event, Context context) {

        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
        S3Object returnObject = null;

        for (S3EventNotificationRecord record : s3event.getRecords()) {
            String s3Key = record.getS3().getObject().getKey();
            String s3Bucket = record.getS3().getBucket().getName();
            context.getLogger().log("found id: " + s3Bucket + " " + s3Key);
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(s3Bucket, s3Key));

            List<Discrepancy> discrepancies = readObjectContentAsString(s3Object);
            discrepancies.forEach(System.out::println);

            returnObject= s3Object;
        }


        return "ok";

    }


    private List<Discrepancy> readObjectContentAsString(S3Object s3Object){
        S3ObjectInputStream stream = s3Object.getObjectContent();
        return (List<Discrepancy>) new CsvToBeanBuilder(
                new BufferedReader(new InputStreamReader(stream))).withType(Discrepancy.class)
                .build().parse();
    }


}
