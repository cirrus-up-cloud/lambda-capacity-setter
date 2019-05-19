# README #

### What is this repository for? ###
Lambda function to set limited/unlimited capacity for T* instances.
* One function that sets capacity of all instances in an ASG to *unlimited* when *CPUCreditBalance* metric is under a 
given threshold.
* One function that sets capacity of all instances in an ASG to *standard* when *CPUCreditBalance* metric is under a
given threshold.

### How do I get set up? ###
#### Create Lambda functions ####

* Build with maven
mvn clean compile assembly:single

* Create Lambda functions using the jar generated above, having the following handlers:
    ** cloud.cirrusup.handler.SetUnlimitedCapacity::handleRequest -> for setting *unlimited* capacity
    ** cloud.cirrusup.handler.SetLimitedCapacity::handleRequest -> for setting *limited* capacity

* Set the following environment variable:
JAVA_TOOL_OPTIONS -> -DasgName=<asg_name> -Daws.accessKeyId=<> -Daws.secretKey=<> -DawsRegion=<>

* Create a new IAM user, having the following policy
```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "VisualEditor",
            "Effect": "Allow",
            "Action": [
                "ec2:ModifyInstanceCreditSpecification",
                "ec2:DescribeInstanceCreditSpecifications",
                "autoscaling:DescribeAutoScalingGroups"
            ],
            "Resource": "*"
        }
    ]
}
```

#### Create SNS topic ####
* Create a SNS topic and subscribe functions created above

#### Create alarms ####
* Add the SNS topics created above as actions when alarm are triggered

