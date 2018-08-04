package com.rana.sahaj.awstest;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface MyAwsInterface {

    /**
     * Invoke the Lambda function "AndroidBackendLambdaFunction".
     * The function name is the method name.
     */
    @LambdaFunction
    ResponseClass test_rds_lambda(RequestClass request);

}
