package com.bunq.tinker;

import com.bunq.sdk.context.ApiContext;
import com.bunq.sdk.context.ApiEnvironmentType;
import com.bunq.sdk.context.BunqContext;
import com.bunq.sdk.exception.BunqException;
import com.bunq.sdk.json.BunqGsonBuilder;
import com.bunq.sdk.model.core.OauthAuthorizationUri;
import com.bunq.sdk.model.core.OauthResponseType;
import com.bunq.sdk.model.generated.endpoint.OauthCallbackUrl;
import com.bunq.sdk.model.generated.endpoint.OauthClient;
import com.bunq.sdk.model.generated.object.Certificate;
import com.bunq.sdk.security.SecurityUtils;
import com.bunq.tinker.utils.ITinker;
import com.google.gson.stream.JsonReader;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class CreateOauthClient implements ITinker {
    /**
     * API constants.
     */
    protected static final String OPTION_CONTEXT = "context";
    protected static final String OPTION_REDIRECT_URI = "redirect";

    /**
     * File constants.
     */
    protected static final String FILE_OAUTH_CONFIGURATION = "oauth.conf";

    /**
     * Error constants.
     */
    protected static final String ERROR_MISSING_MANDATORY_OPTION = "Missing mandatory option.";

    @Override
    public void run(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(new Option("", OPTION_CONTEXT, true, ""));
        options.addOption(new Option("", OPTION_REDIRECT_URI, true, ""));

        CommandLineParser parser = new BasicParser();
        CommandLine allOption = parser.parse(options, args);

        assertMandatoryOptions(allOption);

        BunqContext.loadApiContext(
            ApiContext.restore(allOption.getOptionValue(OPTION_CONTEXT))
        );

        OauthClient oauthClient;
        File oauthFile = new File(FILE_OAUTH_CONFIGURATION);
        if (oauthFile.exists()) {
            oauthClient = createOauthClientFromFile(oauthFile.getPath());
        } else {
            Integer oauthClientId = OauthClient.create().getValue();

            OauthCallbackUrl.create(oauthClientId, allOption.getOptionValue(OPTION_REDIRECT_URI));
            oauthClient = OauthClient.get(oauthClientId).getValue();

            String serializedClient = BunqGsonBuilder.buildDefault().create().toJson(oauthClient);
            FileWriter fileWriter = new FileWriter(FILE_OAUTH_CONFIGURATION);

            fileWriter.write(serializedClient);
            fileWriter.close();
        }

        OauthAuthorizationUri authorizationUri = OauthAuthorizationUri.create(
            OauthResponseType.CODE,
            allOption.getOptionValue(OPTION_REDIRECT_URI),
            oauthClient
        );

        System.out.println(" | Created OAuth client!");
        System.out.println(" | Point your user to the following URL to obtain an auth code:");
        System.out.println(" | " + authorizationUri.getAuthorizationUri());
    }

    /**
     */
    private void assertMandatoryOptions(CommandLine allOption)
    {
        if (allOption.hasOption(OPTION_CONTEXT) &&
                allOption.hasOption(OPTION_REDIRECT_URI)) {
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
}
