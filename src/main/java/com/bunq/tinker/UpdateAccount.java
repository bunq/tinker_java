package com.bunq.tinker;

import com.bunq.sdk.context.ApiEnvironmentType;
import com.bunq.sdk.model.generated.endpoint.MonetaryAccountBank;
import com.bunq.tinker.libs.BunqLib;
import com.bunq.tinker.libs.SharedLib;
import com.bunq.tinker.utils.ITinker;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

public class UpdateAccount implements ITinker {

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

        String name = SharedLib.determineNameFromAllOptionOrStdIn(allOption);
        String accountId = SharedLib.determineAccountIdFromAllOptionOrStdIn(allOption);

        System.out.println();
        System.out.println("  | Updating Name:      " + name);
        System.out.println("  | of Account:         " + accountId);
        System.out.println();
        System.out.println("    ...");
        System.out.println();

        MonetaryAccountBank.update(Integer.parseInt(accountId), name);
        System.out.println();
        System.out.println("  | ✅  Account updated");
        System.out.println();
        System.out.println("  | ▶️  Check your changed overview");
        System.out.println();
        System.out.println();

        bunq.updateContext();
    }
}
