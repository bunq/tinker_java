package com.bunq.tinker;

import com.bunq.sdk.context.ApiContext;
import com.bunq.sdk.context.ApiEnvironmentType;
import com.bunq.sdk.context.BunqContext;
import com.bunq.sdk.exception.BunqException;
import com.bunq.sdk.model.core.OauthAccessToken;
import com.bunq.sdk.model.core.OauthGrantType;
import com.bunq.sdk.model.generated.endpoint.OauthClient;
import com.bunq.tinker.libs.SharedLib;
import com.bunq.tinker.utils.ITinker;
import com.google.gson.stream.JsonReader;
import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestOauth implements ITinker {
    /**
     * Option constants.
     */
    private static final String OPTION_AUTH_CODE = "code";
    private static final String OPTION_CLIENT_CONFIGURATION = "configuration";
    private static final String OPTION_REDIRECT = "redirect";

    /**
     * API constants.
     */
    protected static final String API_DEVICE_DESCRIPTION = "##### YOUR DEVICE DESCRIPTION #####";

    /**
     * Error constants.
     */
    protected static final String ERROR_MISSING_MANDATORY_OPTION = "Missing mandatory option.";

    @Override
    public void run(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(new Option("", OPTION_AUTH_CODE, true, ""));
        options.addOption(new Option("", OPTION_CLIENT_CONFIGURATION, true, ""));
        options.addOption(new Option("", OPTION_REDIRECT, true, ""));

        CommandLineParser parser = new BasicParser();
        CommandLine allOption = parser.parse(options, args);

        assertMandatoryOptions(allOption);

        OauthAccessToken accessToken = OauthAccessToken.create(
            OauthGrantType.AUTHORIZATION_CODE,
            allOption.getOptionValue(OPTION_AUTH_CODE),
            allOption.getOptionValue(OPTION_REDIRECT),
            createOauthClientFromFile(allOption.getOptionValue(OPTION_CLIENT_CONFIGURATION))
        );

        ApiContext apiContext = createApiContextByOauthToken(
                accessToken,
                SharedLib.determineEnvironmentType(allOption)
        );
        BunqContext.loadApiContext(apiContext);

        (new UserOverview()).run(new String[]{});
    }

    /**
     */
    private void assertMandatoryOptions(CommandLine allOption)
    {
        if (allOption.hasOption(OPTION_CLIENT_CONFIGURATION) &&
                allOption.hasOption(OPTION_REDIRECT) &&
                allOption.hasOption(OPTION_AUTH_CODE)) {
            return;
        }
        throw new BunqException(ERROR_MISSING_MANDATORY_OPTION);
    }

    private OauthClient createOauthClientFromFile(String path) throws IOException
    {
        return OauthClient.fromJsonReader(
                new JsonReader(new InputStreamReader(new FileInputStream(path)))
        );
    }

    private static ApiContext createApiContextByOauthToken(OauthAccessToken token, ApiEnvironmentType environmentType)
    {
        return ApiContext.create(
            environmentType,
            token.getToken(),
            API_DEVICE_DESCRIPTION
        );
    }
}
