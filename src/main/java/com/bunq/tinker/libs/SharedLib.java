package com.bunq.tinker.libs;

import com.bunq.sdk.context.ApiEnvironmentType;
import com.bunq.sdk.exception.BunqException;
import com.bunq.sdk.model.core.BunqModel;
import com.bunq.sdk.model.generated.endpoint.*;
import com.bunq.sdk.model.generated.object.Pointer;
import org.apache.commons.cli.*;

import java.util.List;
import java.util.Scanner;

public class SharedLib {

    /**
     * Option constants.
     */
    private static final String OPTION_PRODUCTION = "production";
    private static final String OPTION_AMOUNT = "amount";
    private static final String OPTION_DESCRIPTION = "description";
    private static final String OPTION_RECIPIENT = "recipient";
    private static final String OPTION_CARD_ID = "card-id";
    private static final String OPTION_ACCOUNT_ID = "account-id";
    private static final String OPTION_CALLBACK_URL = "callback-url";
    private static final String OPTION_NAME = "name";

    private static final String PROPERTY_LINE_SEPARATOR = "line.separator";

    private static final String EOL = System.getProperty(PROPERTY_LINE_SEPARATOR);
    private static final String ECHO_CARD = EOL + "   Cards" + EOL;
    private static final String ECHO_REQUEST = EOL + "   Requests" + EOL;
    private static final String ECHO_PAYMENT = EOL + "   Payments" + EOL;
    private static final String ECHO_MONETARY_ACCOUNT = EOL + "   Monetary Accounts" + EOL;
    private static final String ECHO_USER = EOL + "   User" + EOL;

    private static final String ECHO_AMOUNT_IN_EUR = EOL + "    Amount (EUR): ";
    private static final String ECHO_DESCRIPTION = "    Description:  ";
    private static final String ECHO_RECIPIENT = "    Recipient (%s): ";
    private static final String ECHO_NEW_NAME = EOL + "    New Name:        ";
    private static final String ECHO_CARD_ID = EOL + "    Card (ID):       ";
    private static final String ECHO_ACCOUNT_ID = "    Account (ID):    ";
    private static final String ECHO_CALLBACK_URL = EOL + "    Callback URL:    ";

    private static final String POINTER_TYPE_PHONE = "PHONE_NUMBER";

    private static ApiEnvironmentType environmentType;

    public static CommandLine parseAllOption(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption(new Option("", OPTION_PRODUCTION, false, ""));
        options.addOption(new Option("", OPTION_AMOUNT, true, ""));
        options.addOption(new Option("", OPTION_DESCRIPTION, true, ""));
        options.addOption(new Option("", OPTION_RECIPIENT, true, ""));
        options.addOption(new Option("", OPTION_CARD_ID, true, ""));
        options.addOption(new Option("", OPTION_ACCOUNT_ID, true, ""));
        options.addOption(new Option("", OPTION_CALLBACK_URL, true, ""));
        options.addOption(new Option("", OPTION_NAME, true, ""));
        CommandLineParser parser = new BasicParser();

        return parser.parse(options, args);
    }

    public static ApiEnvironmentType determineEnvironmentType(CommandLine allOption) {
        if (allOption.hasOption(OPTION_PRODUCTION)) {
            environmentType = ApiEnvironmentType.PRODUCTION;
        } else {
            environmentType = ApiEnvironmentType.SANDBOX;
        }

        return environmentType;
    }

    public static String determineAmountFromAllOptionOrStdIn(CommandLine allOption) {
        if (allOption.hasOption(OPTION_AMOUNT)) {
            return allOption.getOptionValue(OPTION_AMOUNT);
        } else {
            System.out.print(ECHO_AMOUNT_IN_EUR);
            return new Scanner(System.in).nextLine();
        }
    }

    public static String determineDescriptionFromAllOptionOrStdIn(CommandLine allOption) {
        if (allOption.hasOption(OPTION_DESCRIPTION)) {
            return allOption.getOptionValue(OPTION_DESCRIPTION);
        } else {
            System.out.print(ECHO_DESCRIPTION);
            return new Scanner(System.in).nextLine();
        }
    }

    public static String determineRecipientFromAllOptionOrStdIn(CommandLine allOption) {
        if (allOption.hasOption(OPTION_RECIPIENT)) {
            return allOption.getOptionValue(OPTION_RECIPIENT);
        } else {
            String exampleInput;

            if (ApiEnvironmentType.SANDBOX.equals(environmentType)) {
                exampleInput = "e.g. bravo@bunq.com";
            } else {
                exampleInput = "EMAIL";
            }

            System.out.print(String.format(ECHO_RECIPIENT, exampleInput));
            return new Scanner(System.in).nextLine();
        }
    }

    public static String determineCardIdFromAllOptionOrStdIn(CommandLine allOption) {
        if (allOption.hasOption(OPTION_CARD_ID)) {
            return allOption.getOptionValue(OPTION_CARD_ID);
        } else {
            System.out.print(ECHO_CARD_ID);
            return new Scanner(System.in).nextLine();
        }
    }

    public static String determineAccountIdFromAllOptionOrStdIn(CommandLine allOption) {
        if (allOption.hasOption(OPTION_ACCOUNT_ID)) {
            return allOption.getOptionValue(OPTION_ACCOUNT_ID);
        } else {
            System.out.print(ECHO_ACCOUNT_ID);
            return new Scanner(System.in).nextLine();
        }
    }

    public static String determineCallbackUrlFromAllOptionOrStdIn(CommandLine allOption) {
        if (allOption.hasOption(OPTION_CALLBACK_URL)) {
            return allOption.getOptionValue(OPTION_CALLBACK_URL);
        } else {
            System.out.print(ECHO_CALLBACK_URL);
            return new Scanner(System.in).nextLine();
        }
    }

    public static String determineNameFromAllOptionOrStdIn(CommandLine allOption) {
        if (allOption.hasOption(OPTION_NAME)) {
            return allOption.getOptionValue(OPTION_NAME);
        } else {
            System.out.print(ECHO_NEW_NAME);
            return new Scanner(System.in).nextLine();
        }
    }

    public static void printHeader() {
        if (ApiEnvironmentType.PRODUCTION.equals(environmentType)) {
            System.out.println("\033[93m");
            System.out.println("  ██████╗ ██████╗  ██████╗ ██████╗ ██╗   ██╗ ██████╗████████╗██╗ ██████╗ ███╗   ██╗");
            System.out.println("  ██╔══██╗██╔══██╗██╔═══██╗██╔══██╗██║   ██║██╔════╝╚══██╔══╝██║██╔═══██╗████╗  ██║");
            System.out.println("  ██████╔╝██████╔╝██║   ██║██║  ██║██║   ██║██║        ██║   ██║██║   ██║██╔██╗ ██║");
            System.out.println("  ██╔═══╝ ██╔══██╗██║   ██║██║  ██║██║   ██║██║        ██║   ██║██║   ██║██║╚██╗██║");
            System.out.println("  ██║     ██║  ██║╚██████╔╝██████╔╝╚██████╔╝╚██████╗   ██║   ██║╚██████╔╝██║ ╚████║");
            System.out.println("  ╚═╝     ╚═╝  ╚═╝ ╚═════╝ ╚═════╝  ╚═════╝  ╚═════╝   ╚═╝   ╚═╝ ╚═════╝ ╚═╝  ╚═══╝");
            System.out.println("\033[0m");
        } else {
            System.out.println("\033[94m");
            System.out.println("  ████████╗██╗███╗   ██╗██╗  ██╗███████╗██████╗ ██╗███╗   ██╗ ██████╗");
            System.out.println("  ╚══██╔══╝██║████╗  ██║██║ ██╔╝██╔════╝██╔══██╗██║████╗  ██║██╔════╝");
            System.out.println("     ██║   ██║██╔██╗ ██║█████╔╝ █████╗  ██████╔╝██║██╔██╗ ██║██║  ███╗");
            System.out.println("     ██║   ██║██║╚██╗██║██╔═██╗ ██╔══╝  ██╔══██╗██║██║╚██╗██║██║   ██║");
            System.out.println("     ██║   ██║██║ ╚████║██║  ██╗███████╗██║  ██║██║██║ ╚████║╚██████╔╝");
            System.out.println("     ╚═╝   ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚═╝╚═╝  ╚═══╝ ╚═════╝");
            System.out.println("\033[0m");
        }
    }

    public static void printUser(User user) {
        BunqModel userModel = user.getReferencedObject();
        String userName;
        int userId;

        if (userModel instanceof UserPerson) {
            userId = ((UserPerson) userModel).getId();
            userName = ((UserPerson) userModel).getDisplayName();
        } else if (userModel instanceof UserCompany) {
            userId = ((UserCompany) userModel).getId();
            userName = ((UserCompany) userModel).getDisplayName();
        } else if (userModel instanceof UserLight) {
            userId = ((UserLight) userModel).getId();
            userName = ((UserLight) userModel).getDisplayName();
        } else {
            throw new BunqException("Unexpected user type received.");
        }

        System.out.println(ECHO_USER);
        System.out.println("  ┌───────────────────┬────────────────────────────────────────────────────");
        System.out.println("  │ ID                │ " + userId);
        System.out.println("  ├───────────────────┼────────────────────────────────────────────────────");
        System.out.println("  │ Username          │ " + userName);
        System.out.println("  └───────────────────┴────────────────────────────────────────────────────");
    }

    public static void printAllMonetaryAccountBank(List<MonetaryAccountBank> allMonetaryAccountBank) {
        System.out.println(ECHO_MONETARY_ACCOUNT);

        for (MonetaryAccountBank monetaryAccountBank : allMonetaryAccountBank) {
            printMonetaryAccountBank(monetaryAccountBank);
            System.out.println();
        }
    }

    public static void printMonetaryAccountBank(MonetaryAccountBank monetaryAccountBank) {
        Pointer pointerIban = BunqLib.getPointerIbanForMonetaryAccountBank(monetaryAccountBank);

        System.out.println("  ┌───────────────────┬────────────────────────────────────────────────────");
        System.out.println("  │ ID                │ " + monetaryAccountBank.getId());
        System.out.println("  ├───────────────────┼────────────────────────────────────────────────────");
        System.out.println("  │ Description       │ " + monetaryAccountBank.getDescription());
        System.out.println("  ├───────────────────┼────────────────────────────────────────────────────");
        System.out.println("  │ IBAN              │ " + pointerIban.getValue());

        if (monetaryAccountBank.getBalance() == null) {
            // We don't have access to the balance of this account.
        } else {
            String currency = monetaryAccountBank.getBalance().getCurrency();
            String value = monetaryAccountBank.getBalance().getValue();
            System.out.println("  ├───────────────────┼────────────────────────────────────────────────────");
            System.out.println("  │ Balance           │ " + currency + " " + value);
        }

        System.out.println("  └───────────────────┴────────────────────────────────────────────────────");
    }

    public static void printAllPayment(List<Payment> allPayment) {
        System.out.println(ECHO_PAYMENT);

        for (Payment payment : allPayment) {
            printPayment(payment);
            System.out.println();
        }
    }

    public static void printPayment(Payment payment) {
        String currency = payment.getAmount().getCurrency();
        String value = payment.getAmount().getValue();
        String counterpartyDisplayName = payment.getCounterpartyAlias().getLabelMonetaryAccount().getDisplayName();

        System.out.println("  ┌───────────────────┬────────────────────────────────────────────────────");
        System.out.println("  │ ID                │ " + payment.getId());
        System.out.println("  ├───────────────────┼────────────────────────────────────────────────────");
        System.out.println("  │ Description       │ " + payment.getDescription());
        System.out.println("  ├───────────────────┼────────────────────────────────────────────────────");
        System.out.println("  │ Amount            │ " + currency + " " + value);
        System.out.println("  ├───────────────────┼────────────────────────────────────────────────────");
        System.out.println("  │ Recipient         │ " + counterpartyDisplayName);
        System.out.println("  └───────────────────┴────────────────────────────────────────────────────");
    }

    public static void printAllRequest(List<RequestInquiry> allRequest) {
        System.out.println(ECHO_REQUEST);

        for (RequestInquiry request : allRequest) {
            printRequest(request);
            System.out.println();
        }
    }

    public static void printRequest(RequestInquiry request) {
        String currency = request.getAmountInquired().getCurrency();
        String value = request.getAmountInquired().getValue();
        String counterpartyDisplayName = request.getCounterpartyAlias().getLabelMonetaryAccount().getDisplayName();

        System.out.println("  ┌───────────────────┬────────────────────────────────────────────────────");
        System.out.println("  │ ID                │ " + request.getId());
        System.out.println("  ├───────────────────┼────────────────────────────────────────────────────");
        System.out.println("  │ Description       │ " + request.getDescription());
        System.out.println("  ├───────────────────┼────────────────────────────────────────────────────");
        System.out.println("  │ Status            │ " + request.getStatus());
        System.out.println("  ├───────────────────┼────────────────────────────────────────────────────");
        System.out.println("  │ Amount            │ " + currency + " " + value);
        System.out.println("  ├───────────────────┼────────────────────────────────────────────────────");
        System.out.println("  │ Recipient         │ " + counterpartyDisplayName);
        System.out.println("  └───────────────────┴────────────────────────────────────────────────────");
    }

    public static void printAllCard(List<Card> allCard, List<MonetaryAccountBank> allMonetaryAccountBank) {
        System.out.println(ECHO_CARD);

        for (Card card : allCard) {
            printCard(card, allMonetaryAccountBank);
            System.out.println();
        }
    }

    public static void printCard(Card card, List<MonetaryAccountBank> allMonetaryAccountBank) {
        MonetaryAccountBank monetaryAccountBank = BunqLib.getMonetaryAccountBankFromLabel(
                card.getLabelMonetaryAccountCurrent().getLabelMonetaryAccount(),
                allMonetaryAccountBank
        );
        String iban = card.getLabelMonetaryAccountCurrent().getPointer().getValue();
        String cardDescription = card.getSecondLine() == null ? "bunq card" : card.getSecondLine();
        String monetaryAccountDescription =
                monetaryAccountBank == null ? "account description" : monetaryAccountBank.getDescription();

        System.out.println("  ┌───────────────────┬────────────────────────────────────────────────────");
        System.out.println("  │ ID                │ " + card.getId());
        System.out.println("  ├───────────────────┼────────────────────────────────────────────────────");
        System.out.println("  │ Type              │ " + card.getType());
        System.out.println("  ├───────────────────┼────────────────────────────────────────────────────");
        System.out.println("  │ Name on Card      │ " + card.getNameOnCard());
        System.out.println("  ├───────────────────┼────────────────────────────────────────────────────");
        System.out.println("  │ Description       │ " + cardDescription);
        System.out.println("  ├───────────────────┼────────────────────────────────────────────────────");
        System.out.println("  │ Linked Account    │ " + monetaryAccountDescription + " " + iban);
        System.out.println("  └───────────────────┴────────────────────────────────────────────────────");
    }

    public static void printAllUserAlias(List<Pointer> allUserAlias) {
        System.out.println("   You can use these login credentials to login in to the bunq sandbox app.");

        for (Pointer alias : allUserAlias) {
            System.out.println("  ┌───────────────────┬────────────────────────────────────────────────────");
            System.out.println("  │ Value             │ " + alias.getValue());
            System.out.println("  ├───────────────────┼────────────────────────────────────────────────────");
            System.out.println("  │ Type              │ " + alias.getType());
            System.out.println("  ├───────────────────┼────────────────────────────────────────────────────");
            if (alias.getType().equals(POINTER_TYPE_PHONE)) {
                System.out.println("  │ Confirmation Code │ 123456");
                System.out.println("  ├───────────────────┼────────────────────────────────────────────────────");
            }
            System.out.println("  │ Login Code        │ 000000");
            System.out.println("  └───────────────────┴────────────────────────────────────────────────────");
        }
    }
}
