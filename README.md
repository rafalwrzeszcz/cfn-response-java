[![Build Status](https://travis-ci.org/SunRun/cfn-response-java.svg?branch=master)](https://travis-ci.org/SunRun/cfn-response-java)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.sunrun/cfn-response/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.sunrun/cfn-response)
[![License](https://img.shields.io/github/license/SunRun/cfn-response-java.svg)](LICENSE)
# cfn-response-java
A utility for using Cloudformation with Java8-based AWS lambdas as custom resources

You may find it is convenient to run **Java 8** AWS Lambdas as a step in your Cloudformation infrastructure provisioning. For example it may be useful to run a Java-based tool such as [Liquibase](http://www.liquibase.org/) to fully provision a schema in a SQL database. Why limit yourself to Python or Node.JS when Java8 is a supported platform on [AWS Lambda](https://aws.amazon.com/lambda/)? 
This project was inspired by another Github project for use with Python lambdas: https://github.com/jorgebastida/cfn-response

This project provides the same support for Java that AWS includes for Javascript. If you are using Javascript to write AWS Lambdas, you can use the code available from:

http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-lambda-function-code.html

# Usage

To use, include the following Maven dependency in your project
```xml
    <dependency>
      <groupId>com.sunrun</groupId>
      <artifactId>cfn-response</artifactId>
      <version>1.1.0</version>
    </dependency>
```

Next, write a handler for your Lambda as usual, but before returning, call CfnResponseSender.send(): 
```java
private final CfnResponseSender sender = new CfnResponseSender();

public MyCustomResponse myHandler(final CfnRequest<MyCustomRequest> request, final Context context) {
    // Do some stuff...
    final MyCustomResponse response = new MyCustomResponse();
    response.setFoo("bar");
    
    final boolean result = sender.send(request, Status.SUCCESS, context, null, response, null);
    LOGGER.info("outcome sent to Cloudformation successfully: " + result);
    
    return response;
}
```

If you wish to enable NoEcho to mask the output of the custom resource, when retrieved by using the Fn::GetAtt function, then set the NoEcho paramter to true.
```java
sender.send(request, Status.SUCCESS, context, null, response, null, true);
```

# Debugging

To make debugging easier and provide more helpful outputs even in production, make sure your Java 8 Lambda also has a runtime for the slf4j logging interface. For example you might include the following in your POM in order to get log messages sent to CloudWatch:

```xml
    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-classic</artifactId>
      <version>1.1.6</version>
    </dependency>
```
If running your lambda within a VPC, make sure you add a VPC endpoint as even public subnets do not seem to have access to the S3 endpoint necessary to communicate with Cloudformation. You probably want an unrestrictive policy as this policy will be the policy used for all traffic to S3 in the affected subnets.  

```json
{
  "Resources":{
    "VpcEndpoint":{
      "Type":"AWS::EC2::VPCEndpoint",
      "Properties":{
        "PolicyDocument":{
          "Statement":[
            {
              "Action":"*",
              "Effect":"Allow",
              "Resource":"*",
              "Principal":"*"
            }
          ]
        },
        "RouteTableIds":{
          "Ref":"YourRouteTableIdsWhereLambdaWillExecute"
        },
        "ServiceName":{
          "Fn::Join":[
            "",
            [
              "com.amazonaws.",
              {
                "Ref":"AWS::Region"
              },
              ".s3"
            ]
          ]
        },
        "VpcId":{
          "Ref":"YourVpcId"
        }
      }
    }
  }
}
```

# Contributing

Please make sure all tests are passing & add unit test coverage if necessary.

# References 
- https://github.com/jorgebastida/cfn-response
- http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-lambda-function-code.html
- http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/template-custom-resources.html
- http://www.jayway.com/2015/07/04/extending-cloudformation-with-lambda-backed-custom-resources/
