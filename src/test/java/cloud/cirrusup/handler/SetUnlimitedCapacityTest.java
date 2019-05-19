package test.java.cloud.cirrusup.handler;

import cloud.cirrusup.awsclient.AWSClientFactory;
import cloud.cirrusup.utils.SystemPropertiesUtils;
import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.autoscaling.model.Instance;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstanceCreditSpecificationsResult;
import com.amazonaws.services.ec2.model.InstanceCreditSpecification;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests for {@link SetUnlimitedCapacity} class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SystemPropertiesUtils.class, AWSClientFactory.class})
public class SetUnlimitedCapacityTest {

    private SetUnlimitedCapacity setter;
    private Context context;

    @Before
    public void init() {

        context = Mockito.mock(Context.class);
        setter = new SetUnlimitedCapacity();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidParams() {

        //setup
        LambdaLogger logger = Mockito.mock(LambdaLogger.class);
        when(context.getLogger()).thenReturn(logger);
        mockStatic(SystemPropertiesUtils.class);
        when(SystemPropertiesUtils.hasPropertySet("aws.accessKeyId")).thenReturn(true);
        when(SystemPropertiesUtils.hasPropertySet("aws.secretKey")).thenReturn(true);
        when(SystemPropertiesUtils.hasPropertySet("asgName")).thenReturn(false);

        //call
        setter.handleRequest(null, context);
    }

    @Test
    public void testComplete() {

        //setup
        LambdaLogger logger = Mockito.mock(LambdaLogger.class);
        when(context.getLogger()).thenReturn(logger);

        mockStatic(SystemPropertiesUtils.class);
        when(SystemPropertiesUtils.hasPropertySet(anyString())).thenReturn(true);
        when(SystemPropertiesUtils.getPropertyValue("asgName")).thenReturn("asg");

        mockStatic(AWSClientFactory.class);
        AmazonAutoScaling autoScaling = Mockito.mock(AmazonAutoScaling.class);
        AmazonEC2 amazonEC2 = Mockito.mock(AmazonEC2.class);
        when(AWSClientFactory.getAutoScalingClient()).thenReturn(autoScaling);
        when(AWSClientFactory.getAWSEc2Client()).thenReturn(amazonEC2);

        DescribeAutoScalingGroupsResult result = Mockito.mock(DescribeAutoScalingGroupsResult.class);
        DescribeInstanceCreditSpecificationsResult res = Mockito.mock(DescribeInstanceCreditSpecificationsResult.class);
        InstanceCreditSpecification specification = Mockito.mock(InstanceCreditSpecification.class);
        AutoScalingGroup autoScalingGroup = Mockito.mock(AutoScalingGroup.class);
        Instance instance = Mockito.mock(Instance.class);
        when(autoScaling.describeAutoScalingGroups(anyObject())).thenReturn(result);
        when(result.getAutoScalingGroups()).thenReturn(ImmutableList.of(autoScalingGroup));
        when(autoScalingGroup.getInstances()).thenReturn(ImmutableList.of(instance));
        when(instance.getInstanceId()).thenReturn("id1");
        when(amazonEC2.describeInstanceCreditSpecifications(anyObject())).thenReturn(res);
        when(res.getInstanceCreditSpecifications()).thenReturn(ImmutableList.of(specification));
        when(specification.getInstanceId()).thenReturn("id1");
        when(specification.getCpuCredits()).thenReturn("standard");

        //call
        setter.handleRequest(null, context);

        //verify
        verify(context, times(3)).getLogger();
        verify(logger, times(3)).log(anyString());
        verify(autoScaling, times(1)).describeAutoScalingGroups(anyObject());
        verify(result, times(2)).getAutoScalingGroups();
        verify(autoScalingGroup, times(1)).getInstances();
        verify(instance,times(1)).getInstanceId();
        verify(amazonEC2,times(1)).describeInstanceCreditSpecifications(anyObject());
        verify(res,times(1)).getInstanceCreditSpecifications();
        verify(specification,times(1)).getCpuCredits();
        verify(specification,times(2)).getInstanceId();
        verify(amazonEC2,times(1)).modifyInstanceCreditSpecification(anyObject());

        verifyNoMoreInteractions(context);
        verifyNoMoreInteractions(logger);
        verifyNoMoreInteractions(autoScaling);
        verifyNoMoreInteractions(result);
        verifyNoMoreInteractions(autoScalingGroup);
        verifyNoMoreInteractions(instance);
        verifyNoMoreInteractions(res);
        verifyNoMoreInteractions(specification);
        verifyNoMoreInteractions(amazonEC2);
        verifyStatic();
    }
}
