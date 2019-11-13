package provider;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class AmazonS3Provider {
    public AmazonS3 provide() {
        return AmazonS3ClientBuilder.defaultClient();
    }
}
