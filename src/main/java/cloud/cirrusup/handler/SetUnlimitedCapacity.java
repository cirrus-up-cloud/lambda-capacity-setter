package cloud.cirrusup.handler;

import cloud.cirrusup.awsclient.AWSClientFactory;
import cloud.cirrusup.utils.SystemPropertiesUtils;
import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.autoscaling.model.Instance;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstanceCreditSpecificationsRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceCreditSpecificationsResult;
import com.amazonaws.services.ec2.model.InstanceCreditSpecification;
import com.amazonaws.services.ec2.model.InstanceCreditSpecificationRequest;
import com.amazonaws.services.ec2.model.ModifyInstanceCreditSpecificationRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collectors;


public class SetUnlimitedCapacity extends AbstractCapcitySetter {

    /**
     * {@inheritDoc}
     */
    @Override
    public Void handleRequest(Void aVoid, Context context) {

        context.getLogger().log("Starting <SetUnlimitedCapacity::handleRequest> Lambda functions.");
        validateInputParameters();

        AmazonAutoScaling asgClient = AWSClientFactory.getAutoScalingClient();
        AmazonEC2 amazonEC2 = AWSClientFactory.getAWSEc2Client();

        DescribeAutoScalingGroupsRequest request = new DescribeAutoScalingGroupsRequest();
        request.setAutoScalingGroupNames(ImmutableList.of(SystemPropertiesUtils.getPropertyValue(ASG_NAME)));
        DescribeAutoScalingGroupsResult result = asgClient.describeAutoScalingGroups(request);
        if (result.getAutoScalingGroups().size() > 0) {

            List<Instance> instances = result.getAutoScalingGroups().get(0).getInstances();
            List<String> instanceIds = instances.stream().map(Instance::getInstanceId).collect(Collectors.toList());
            DescribeInstanceCreditSpecificationsResult res = amazonEC2.
                    describeInstanceCreditSpecifications(
                            new DescribeInstanceCreditSpecificationsRequest().withInstanceIds(instanceIds));

            for (InstanceCreditSpecification ins : res.getInstanceCreditSpecifications()) {

                if (ins.getCpuCredits().equals(STANDARD)) {

                    InstanceCreditSpecificationRequest instanceRequest = new InstanceCreditSpecificationRequest()
                            .withInstanceId(ins.getInstanceId())
                            .withCpuCredits(UNLIMITED);
                    ModifyInstanceCreditSpecificationRequest modifyRequest = new ModifyInstanceCreditSpecificationRequest()
                            .withInstanceCreditSpecifications(instanceRequest);
                    amazonEC2.modifyInstanceCreditSpecification(modifyRequest);
                    context.getLogger().log("Modified credit for instance: " + ins.getInstanceId() + " to unlimited.");
                }
            }
        }

        context.getLogger().log("Done.");

        return null;
    }

}
