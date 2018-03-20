package com.bunq.tinker;

import com.bunq.sdk.context.ApiEnvironmentType;
import com.bunq.sdk.model.generated.endpoint.RequestInquiry;
import com.bunq.sdk.model.generated.object.Amount;
import com.bunq.sdk.model.generated.object.Pointer;
import com.bunq.tinker.libs.BunqLib;
import com.bunq.tinker.libs.SharedLib;
import com.bunq.tinker.utils.ITinker;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

public class MakeRequest implements ITinker {

    /**
     * Input constants.
     */
    private static final String CURRENCY_EURO = "EUR";
    private static final String POINTER_TYPE_EMAIL = "EMAIL";

    /**
     * @param args
     *
     * @throws ParseException
     */
    public void run(String[] args) throws ParseException {
        CommandLine allOption = SharedLib.parseAllOption(args);
        ApiEnvironmentType environmentType = SharedLib.determineEnvironmentType(allOption);

        SharedLib.printHeader();

        BunqLib bunq = new BunqLib(environmentType);

        String amount = SharedLib.determineAmountFromAllOptionOrStdIn(allOption);
        String description = SharedLib.determineDescriptionFromAllOptionOrStdIn(allOption);
        String recipient = SharedLib.determineRecipientFromAllOptionOrStdIn(allOption);

        System.out.println();
        System.out.println("  | Requesting:   € " + amount);
        System.out.println("  | From:         " + recipient);
        System.out.println("  | Description:  " + description);
        System.out.println();
        System.out.println("    ...");
        System.out.println();

        RequestInquiry.create(
                new Amount(amount, CURRENCY_EURO),
                new Pointer(POINTER_TYPE_EMAIL, recipient),
                description,
                true
        );

        System.out.println();
        System.out.println("  | ✅  Request sent");
        System.out.println();
        System.out.println("  | ▶️  Check your changed overview");
        System.out.println();
        System.out.println();

        bunq.updateContext();
    }
}
