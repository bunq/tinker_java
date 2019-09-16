package com.bunq.tinker;

import com.bunq.sdk.context.ApiContext;
import com.bunq.sdk.context.ApiEnvironmentType;
import com.bunq.sdk.exception.BunqException;
import com.bunq.sdk.model.generated.object.Certificate;
import com.bunq.sdk.security.SecurityUtils;
import com.bunq.tinker.libs.SharedLib;
import com.bunq.tinker.utils.ITinker;
import org.apache.commons.cli.*;

import java.util.ArrayList;

public class CreatePsd2Configuration implements ITinker {
    /**
     * API constants.
     */
    protected static final String API_DEVICE_DESCRIPTION = "##### YOUR DEVICE DESCRIPTION #####";

    /**
     * Option constants.
     */
    protected static final String OPTION_ENVIRONMENT = "env";
    protected static final String OPTION_CERTIFICATE = "certificate";
    protected static final String OPTION_CERTIFICATE_CHAIN = "chain";
    protected static final String OPTION_PRIVATE_KEY = "key";

    /**
     * File constants.
     */
    protected static final String FILE_CONTEXT = "bunq-psd2.conf";

    /**
     * Error constants.
     */
    protected static final String ERROR_MISSING_MANDATORY_OPTION = "Missing mandatory option.";

    @Override
    public void run(String[] args) throws Exception {

        Options options = new Options();
        options.addOption(new Option("", OPTION_CERTIFICATE, true, ""));
        options.addOption(new Option("", OPTION_CERTIFICATE_CHAIN, true, ""));
        options.addOption(new Option("", OPTION_PRIVATE_KEY, true, ""));

        CommandLineParser parser = new BasicParser();
        CommandLine allOption = parser.parse(options, args);

        assertMandatoryOptions(allOption);

        ApiContext apiContext = ApiContext.createForPsd2(
            SharedLib.determineEnvironmentType(allOption),
            SecurityUtils.getCertificateFromFile(allOption.getOptionValue(OPTION_CERTIFICATE)),
            SecurityUtils.getPrivateKeyFromFile(allOption.getOptionValue(OPTION_PRIVATE_KEY)),
            new Certificate[]{
                    SecurityUtils.getCertificateFromFile(allOption.getOptionValue(OPTION_CERTIFICATE_CHAIN))
            },
            API_DEVICE_DESCRIPTION,
            new ArrayList<String>()
        );

        apiContext.save(FILE_CONTEXT);

        System.out.println(" | PSD2 configuration created. Saved as bunq-psd.conf!");
    }

    /**
     */
    private void assertMandatoryOptions(CommandLine allOption)
    {
        if (allOption.hasOption(OPTION_CERTIFICATE) &&
                allOption.hasOption(OPTION_CERTIFICATE_CHAIN) &&
                allOption.hasOption(OPTION_PRIVATE_KEY)) {
            return;
        }
        throw new BunqException(ERROR_MISSING_MANDATORY_OPTION);
    }
}
