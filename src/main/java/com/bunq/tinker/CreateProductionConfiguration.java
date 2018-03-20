package com.bunq.tinker;

import com.bunq.sdk.context.ApiContext;
import com.bunq.sdk.context.ApiEnvironmentType;
import com.bunq.sdk.exception.BunqException;
import com.bunq.tinker.utils.ITinker;
import org.apache.commons.cli.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CreateProductionConfiguration implements ITinker {

    /**
     *
     * @param args
     * @throws ParseException
     */
    public void run(String[] args) throws ParseException, UnknownHostException {
        Options options = new Options();
        options.addOption(new Option("", "api-key", true, ""));
        CommandLineParser parser = new BasicParser();

        CommandLine allOption = parser.parse(options, args);

        if (allOption.hasOption("api-key")) {
            ApiContext.create(
                    ApiEnvironmentType.PRODUCTION,
                    allOption.getOptionValue("api-key"),
                    InetAddress.getLocalHost().getHostName()
            ).save("bunq-production.conf");
        } else {
            throw new BunqException("Missing mandatory option \"--api-key [API key]\"");
        }

    }
}
