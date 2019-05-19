package cloud.cirrusup.awsclient;

import cloud.cirrusup.utils.SystemPropertiesUtils;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;

/**
 * Class with factory methods for AWS Clients.
 * <p/>
 * This is useful for testing purpose.
 */
public class AWSClientFactory {

    private static final String AWS_REGION = "awsRegion";

    /**
     * Default region - Ireland/EU-WEST-1
     */
    private static final String DEFAULT_REGION = Regions.EU_WEST_1.getName();

    /**
     * Private constructor to avoid class init.
     */
    private AWSClientFactory() {
    }

    /**
     * Factory method for AWS AutoScaling client.
     *
     * @return the AWS AutoScaling client.
     */
    public static AmazonAutoScaling getAutoScalingClient() {

        AmazonAutoScalingClientBuilder standard = AmazonAutoScalingClientBuilder
                .standard()
                .withClientConfiguration(buildClientConfig())
                .withCredentials(new SystemPropertiesCredentialsProvider());

        if (SystemPropertiesUtils.hasPropertySet(AWS_REGION)) {

            standard.setRegion(SystemPropertiesUtils.getPropertyValue(AWS_REGION));
        } else {

            standard.setRegion(DEFAULT_REGION);
        }

        return standard.build();
    }

    /**
     * Factory method for AWS EC2 client.
     *
     * @return the AWS EC2 client.
     */
    public static AmazonEC2 getAWSEc2Client() {

        AmazonEC2ClientBuilder standard = AmazonEC2ClientBuilder.standard()
                .withClientConfiguration(buildClientConfig())
                .withCredentials(new SystemPropertiesCredentialsProvider());

        if (SystemPropertiesUtils.hasPropertySet(AWS_REGION)) {

            standard.setRegion(SystemPropertiesUtils.getPropertyValue(AWS_REGION));
        } else {

            standard.setRegion(DEFAULT_REGION);
        }

        return standard.build();
    }

    private static ClientConfiguration buildClientConfig() {

        return new ClientConfiguration()
                .withConnectionTimeout(3 * 1000)
                .withSocketTimeout(3 * 1000);
    }
}
