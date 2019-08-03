package nhlp3.scheduler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<Object, Object> {

    public Object handleRequest(final Object input, final Context context) {
        // TODO: Implement handler
        return new Object();
    }
}
