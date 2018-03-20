package com.bunq.tinker;

import com.bunq.sdk.context.ApiEnvironmentType;
import com.bunq.sdk.model.generated.endpoint.Card;
import com.bunq.tinker.libs.BunqLib;
import com.bunq.tinker.libs.SharedLib;
import com.bunq.tinker.utils.ITinker;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

public class LinkCard implements ITinker {

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

        String cardId = SharedLib.determineCardIdFromAllOptionOrStdIn(allOption);
        String accountId = SharedLib.determineAccountIdFromAllOptionOrStdIn(allOption);

        System.out.println();
        System.out.println("  | Link Card:    " + cardId);
        System.out.println("  | To Account:   " + accountId);
        System.out.println();
        System.out.println("    ...");
        System.out.println();

        Card.update(
                Integer.parseInt(cardId),
                null, /* pinCode */
                null, /* activationCode */
                null, /* status */
                null, /* limit */
                null, /* magStripePermissions */
                null, /* countryPermission */
                Integer.parseInt(accountId)
        );

        System.out.println();
        System.out.println("  | ✅  Account switched");
        System.out.println();
        System.out.println("  | ▶️  Check your changed overview");
        System.out.println();
        System.out.println();

        bunq.updateContext();
    }
}
