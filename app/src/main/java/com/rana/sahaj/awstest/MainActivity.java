package com.rana.sahaj.awstest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.demo.posts.GetPostQuery;
import com.amazonaws.demo.posts.PutPostMutation;
import com.amazonaws.demo.posts.PutPostSubsSubscription;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.mobile.auth.core.IdentityHandler;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.auth.ui.SignInUI;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;

import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amazonaws.mobileconnectors.apigateway.ApiRequest;
import com.amazonaws.mobileconnectors.apigateway.ApiResponse;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.StringUtils;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.rana.sahaj.awstest.api.id8e11po8gee.TestrdslambdaapigatewayapiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

public class MainActivity extends AppCompatActivity {

    private Button mAwsBtn,mAwsBtnSave;
    private TextView mAwsText;
    private ProgressBar mAwsProg;
    private EditText mAwsName,mAwsClass;

    private String LOG_TAG="SahajLOG";
    private String TAG="SahajLOG";

    private AWSCredentialsProvider credentialsProvider;
    private AWSConfiguration configuration;

    private TestrdslambdaapigatewayapiClient apiClient;
    private AWSAppSyncClient mAWSAppSyncClient;

    private AppSyncSubscriptionCall subscriptionWatcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAwsBtn=findViewById(R.id.aws_btn);
        mAwsBtnSave=findViewById(R.id.aws_btn_sve);
        mAwsText=findViewById(R.id.aws_text);
        mAwsProg=findViewById(R.id.aws_prog);
        mAwsProg.setVisibility(View.INVISIBLE);
        mAwsName=findViewById(R.id.aws_name);
        mAwsClass=findViewById(R.id.aws_class);


        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.e("SahajLOG", "AWSMobileClient is instantiated and you are connected to AWS!");


                credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
                configuration = AWSMobileClient.getInstance().getConfiguration();

                Log.e("SahajLOG", "CredProv:>> " +credentialsProvider);
                Log.e("SahajLOG", "Config:>> " +configuration);


                apiClient = new ApiClientFactory()
                        .credentialsProvider(AWSMobileClient.getInstance().getCredentialsProvider())
                        .build(TestrdslambdaapigatewayapiClient.class);




                // Use IdentityManager#getUserID to fetch the identity id.
                IdentityManager.getDefaultIdentityManager().getUserID(new IdentityHandler() {
                    @Override
                    public void onIdentityId(String identityId) {
                        Log.e("SahajLOG", "Identity ID = " + identityId);

                        // Use IdentityManager#getCachedUserID to
                        //  fetch the locally cached identity id.
                        final String cachedIdentityId = IdentityManager.getDefaultIdentityManager().getCachedUserID();
                    }

                    @Override
                    public void handleError(Exception exception) {
                        Log.e("SahajLOG", "Error in retrieving the identity" + exception);
                    }
                });

                Log.e("SahajLOGOP", "Complete" +MainActivity.this+"  ");

                SignInUI signin = (SignInUI) AWSMobileClient.getInstance().getClient(MainActivity.this, SignInUI.class);
                if (MainActivity.this!=null)
                    signin.login(MainActivity.this, MainActivity.class).execute();

            }
        }).execute();


        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.e("SahajLOGOP", "Complete" +MainActivity.this+"  ");

                SignInUI signin = (SignInUI) AWSMobileClient.getInstance().getClient(MainActivity.this, SignInUI.class);
                if (MainActivity.this!=null)
                    signin.login(MainActivity.this, AfterLoginActivity.class).execute();

            }
        }).execute();

        mAwsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAwsProg.setVisibility(View.VISIBLE);
                mAwsText.setVisibility(View.GONE);
                //callCloudLogic();
                //postCloudLogic("YoME","YoClass");
                //cloudLambda();
                queryData();
            }
        });

        mAwsBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAwsProg.setVisibility(View.VISIBLE);
                mAwsText.setVisibility(View.GONE);
                saveData();
            }
        });

        //subscribe();
    }


    public void queryData() {
        if (mAWSAppSyncClient == null) {
            mAWSAppSyncClient = ClientFactory.getInstance(this);
        }
        mAWSAppSyncClient.query(GetPostQuery.builder().mName(mAwsName.getText().toString()).mClass(mAwsClass.getText().toString()).build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(postsCallback);
    }

    /*public void saveData() {
        if (mAWSAppSyncClient == null) {
            mAWSAppSyncClient = ClientFactory.getInstance(this);
        }
        mAWSAppSyncClient.query(GetPostQuery.builder().mName(mAwsName.getText().toString()).mClass(mAwsClass.getText().toString()).build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(postsCallback);
    }*/

    private GraphQLCall.Callback<GetPostQuery.Data> postsCallback = new GraphQLCall.Callback <GetPostQuery.Data>() {
        @Override
        public void onResponse(@Nonnull final Response<GetPostQuery.Data> response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAwsProg.setVisibility(View.INVISIBLE);
                    Log.e("SahajLOG", "Response>> " +response.data());
                    //PostsActivity.this.mAdapter.setPosts(response.data().listPosts().items());
                    //PostsActivity.this.mSwipeRefreshLayout.setRefreshing(false);
                    //PostsActivity.this.mAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onFailure(@Nonnull final ApolloException e) {
            Log.e(TAG, "Failed to perform AllPostsQuery", e);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("SahajLOG", "Error::>> " +e);
                    //PostsActivity.this.mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    };

    private void saveData() {

        PutPostMutation.Data expected = new PutPostMutation.Data(new PutPostMutation.PutPost(
                "Post",
                "2",
                mAwsName.getText().toString(),
                mAwsClass.getText().toString()
        ));

        PutPostMutation addPostMutation = PutPostMutation.builder()
                .mName(mAwsName.getText().toString())
                .mClass(mAwsClass.getText().toString())
                .build();
        ClientFactory.getInstance(this).mutate(addPostMutation, expected).enqueue(postsSaveCallback);
    }

    private GraphQLCall.Callback<PutPostMutation.Data> postsSaveCallback = new GraphQLCall.Callback<PutPostMutation.Data>() {
        @Override
        public void onResponse(@Nonnull final Response<PutPostMutation.Data> response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAwsProg.setVisibility(View.INVISIBLE);
                    Log.e("SahajLOG", "ResponseSave>> " +response.data());
                    //Toast.makeText(AddPostActivity.this, "Added post", Toast.LENGTH_SHORT).show();
                    //AddPostActivity.this.finish();
                }
            });
        }

        @Override
        public void onFailure(@Nonnull final ApolloException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("SahajLOG", "Error::>> " +e);
                    //Toast.makeText(AddPostActivity.this, "Failed to add post", Toast.LENGTH_SHORT).show();
                    //AddPostActivity.this.finish();
                }
            });
        }
    };

    private void subscribe() {
        PutPostSubsSubscription subscription = PutPostSubsSubscription.builder().build();

        subscriptionWatcher = ClientFactory.getInstance(this).subscribe(subscription);
        subscriptionWatcher.execute(subCallback);
    }

    private AppSyncSubscriptionCall.Callback subCallback = new AppSyncSubscriptionCall.Callback<PutPostSubsSubscription.Data>() {
        @Override
        public void onResponse(@Nonnull final Response<PutPostSubsSubscription.Data> response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "SubsResponded", Toast.LENGTH_SHORT).show();
                    Log.e("SahajLOG", "ResponseSUBSSSSS**>> " +response.data());
                }
            });

            // Further code can update UI or act upon this new comment
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("SahajLOG", "Error::>> " +e);
        }

        @Override
        public void onCompleted() {
            Log.d("Completed", "Completed");
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
//--        subscriptionWatcher.cancel();
    }

    public void cloudLambda(){
        // Create an instance of CognitoCachingCredentialsProvider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-south-1:b536d063-74b8-4878-9614-d05890c2b5a4", // Identity pool ID
                Regions.AP_SOUTH_1 // Region
        );
        // Create LambdaInvokerFactory, to be used to instantiate the Lambda proxy.
        LambdaInvokerFactory factory = new LambdaInvokerFactory(this.getApplicationContext(),
                Regions.AP_SOUTH_1, credentialsProvider);

        // Create the Lambda proxy object with a default Json data binder.
        // You can provide your own data binder by implementing
        // LambdaDataBinder.
        final MyAwsInterface myInterface = factory.build(MyAwsInterface.class);

        RequestClass request = new RequestClass(mAwsName.getText().toString(), mAwsClass.getText().toString(),"2");
        // The Lambda function invocation results in a network call.
        // Make sure it is not called from the main thread.

        new AsyncTask<RequestClass, Void, ResponseClass>() {
            @Override
            protected ResponseClass doInBackground(RequestClass... params) {
                // invoke "echo" method. In case it fails, it will throw a
                // LambdaFunctionException.
                try {
                    return myInterface.test_rds_lambda(params[0]);
                } catch (LambdaFunctionException lfe) {
                    Log.e("Tag", "Failed to invoke echo", lfe);
                    Log.e("SahajLOG", "Failed to invoke echo", lfe);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ResponseClass result) {
                if (result == null) {
                    return;
                }

                // Do a toast
                Toast.makeText(MainActivity.this, "done", Toast.LENGTH_LONG).show();
                mAwsProg.setVisibility(View.INVISIBLE);

                Log.e("SahajLOG", "Result>>> " +result.getmNameResponse()+ "  ***** "+result.getBody());
            }
        }.execute(request);
    }

    public void postCloudLogic(String mName,String mClass) {
        // Create components of api request
        final String method = "POST";

        final String path = "/test_rds_lambda/?name="+mName+"&class="+mClass;

        final String body = "";
        final byte[] content = body.getBytes(StringUtils.UTF8);

        final Map parameters = new HashMap<>();
        parameters.put("lang", "en_US");

        final Map headers = new HashMap<>();

        // Use components to create the api request
        ApiRequest localRequest =
                new ApiRequest(apiClient.getClass().getSimpleName())
                        .withPath(path)
                        .withHttpMethod(HttpMethodName.valueOf(method))
                        .withHeaders(headers)
                        .addHeader("Content-Type", "application/json")
                        .withParameters(parameters);
        // Only set body if it has content.
        if (body.length() > 0) {
            localRequest = localRequest
                    .addHeader("Content-Length", String.valueOf(content.length))
                    .withBody(content);
        }

        final ApiRequest request = localRequest;

        // Make network call on background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(LOG_TAG,
                            "Invoking API w/ Request : " +
                                    request.getHttpMethod() + ":" +
                                    request.getPath());

                    final ApiResponse response = apiClient.execute(request);

                    final InputStream responseContentStream = response.getContent();

                    if (responseContentStream != null) {
                        final String responseData = IOUtils.toString(responseContentStream);

                        Log.d(LOG_TAG, "Response : " + responseData);

                        Log.e("SahajLOG", "REs>> " +jsonToMap(responseData));
                        final HashMap<String,String> resultMap=jsonToMap(responseData);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                mAwsText.setText(resultMap.get("name"));
                                mAwsText.setVisibility(View.VISIBLE);
                                mAwsProg.setVisibility(View.INVISIBLE);
                            }
                        });

                    }

                    Log.d(LOG_TAG, response.getStatusCode() + " " + response.getStatusText());

                } catch (final Exception exception) {
                    Log.e(LOG_TAG, exception.getMessage(), exception);
                    exception.printStackTrace();
                }
            }
        }).start();
    }

    public void callCloudLogic() {
        // Create components of api request
        final String method = "GET";

        final String path = "/test_rds_lambda";

        final String body = "";
        final byte[] content = body.getBytes(StringUtils.UTF8);

        final Map parameters = new HashMap<>();
        parameters.put("lang", "en_US");

        final Map headers = new HashMap<>();

        // Use components to create the api request
        ApiRequest localRequest =
                new ApiRequest(apiClient.getClass().getSimpleName())
                        .withPath(path)
                        .withHttpMethod(HttpMethodName.valueOf(method))
                        .withHeaders(headers)
                        .addHeader("Content-Type", "application/json")
                        .withParameters(parameters);

        // Only set body if it has content.
        if (body.length() > 0) {
            localRequest = localRequest
                    .addHeader("Content-Length", String.valueOf(content.length))
                    .withBody(content);
        }

        final ApiRequest request = localRequest;

        // Make network call on background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(LOG_TAG,
                            "Invoking API w/ Request : " +
                                    request.getHttpMethod() + ":" +
                                    request.getPath());

                    final ApiResponse response = apiClient.execute(request);

                    final InputStream responseContentStream = response.getContent();

                    if (responseContentStream != null) {
                        final String responseData = IOUtils.toString(responseContentStream);

                        Log.d(LOG_TAG, "Response : " + responseData);

                        Log.e("SahajLOG", "REs>> " +jsonToMap(responseData));
                        final HashMap<String,String> resultMap=jsonToMap(responseData);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                mAwsText.setText(resultMap.get("name"));
                                mAwsText.setVisibility(View.VISIBLE);
                                mAwsProg.setVisibility(View.INVISIBLE);
                            }
                        });

                    }

                    Log.d(LOG_TAG, response.getStatusCode() + " " + response.getStatusText());

                } catch (final Exception exception) {
                    Log.e(LOG_TAG, exception.getMessage(), exception);
                    exception.printStackTrace();
                }
            }
        }).start();
    }

    public static HashMap jsonToMap(String t) throws JSONException {

        HashMap<String, String> map = new HashMap<String, String>();
        JSONObject jObject = new JSONObject(t);
        Iterator<?> keys = jObject.keys();

        while( keys.hasNext() ){
            String key = (String)keys.next();
            String value = jObject.getString(key);
            map.put(key, value);

        }

        //System.out.println("json : "+jObject);
        //System.out.println("map : "+map);
        return map;
    }

}
