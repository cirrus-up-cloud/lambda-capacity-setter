package cloud.cirrusup.handler;

import cloud.cirrusup.utils.SystemPropertiesUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import static com.amazonaws.SDKGlobalConfiguration.ACCESS_KEY_SYSTEM_PROPERTY;
import static com.amazonaws.SDKGlobalConfiguration.SECRET_KEY_SYSTEM_PROPERTY;

public abstract class AbstractCapcitySetter implements RequestHandler<Void, Void> {

    static final String ASG_NAME = "asgName";
    static final String STANDARD = "standard";
    static final String UNLIMITED = "unlimited";

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract Void handleRequest(Void aVoid, Context context);

    void validateInputParameters() {

        if (!SystemPropertiesUtils.hasPropertySet(ACCESS_KEY_SYSTEM_PROPERTY)
                || !SystemPropertiesUtils.hasPropertySet(SECRET_KEY_SYSTEM_PROPERTY)) {

            throw new IllegalArgumentException("Missing AWS credentials.");
        }

        if (!SystemPropertiesUtils.hasPropertySet(ASG_NAME)) {

            throw new IllegalArgumentException("Missing ASG name.");
        }
    }
}
