package com.bunq.tinker;

import com.bunq.sdk.context.ApiEnvironmentType;
import com.bunq.sdk.exception.BunqException;
import com.bunq.sdk.model.generated.endpoint.UserCompany;
import com.bunq.sdk.model.generated.endpoint.UserLight;
import com.bunq.sdk.model.generated.endpoint.UserPerson;
import com.bunq.sdk.model.generated.object.NotificationFilter;
import com.bunq.tinker.libs.BunqLib;
import com.bunq.tinker.libs.SharedLib;
import com.bunq.tinker.utils.ITinker;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import java.util.ArrayList;
import java.util.List;

public class AddCallbackUrl implements ITinker {

    /**
     * Notification filter constants.
     */
    private static final String NOTIFICATION_DELIVERY_METHOD_URL = "URL";
    private static final String NOTIFICATION_CATEGORY_MUTATION = "MUTATION";

    /**
     * Error constants.
     */
    private static final String ERROR_UNKNOWN_USER_TYPE_RETURNED = "Unknown user type returned.";
    private static final String ERROR_CANNOT_UPDATE_NOTIFICATION_FILTER_FOR_USER_LIGHT = "Unknown user type returned.";

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

        String callbackUrl = SharedLib.determineCallbackUrlFromAllOptionOrStdIn(allOption);

        System.out.println();
        System.out.println("  | Adding Callback URL:    " + callbackUrl);
        System.out.println();
        System.out.println("    ...");
        System.out.println();

        List<NotificationFilter> allNotificationFilterCurrent;
        List<NotificationFilter> allNotificationFilterUpdated = new ArrayList<>();

        if (bunq.getUser().getReferencedObject() instanceof UserPerson) {
            allNotificationFilterCurrent = bunq.getUser().getUserPerson().getNotificationFilters();
        } else if (bunq.getUser().getReferencedObject() instanceof UserCompany) {
            allNotificationFilterCurrent = bunq.getUser().getUserCompany().getNotificationFilters();
        } else if (bunq.getUser().getReferencedObject() instanceof UserLight) {
            allNotificationFilterCurrent = bunq.getUser().getUserLight().getNotificationFilters();
        } else {
            throw new BunqException(ERROR_UNKNOWN_USER_TYPE_RETURNED);
        }

        for (NotificationFilter notificationFilter : allNotificationFilterCurrent) {
            if (callbackUrl.equals(notificationFilter.getNotificationTarget())) {
                allNotificationFilterUpdated.add(notificationFilter);
            }
        }

        allNotificationFilterUpdated.add(
                new NotificationFilter(NOTIFICATION_DELIVERY_METHOD_URL, callbackUrl, NOTIFICATION_CATEGORY_MUTATION)
        );

        if (bunq.getUser().getReferencedObject() instanceof UserPerson) {
            UserPerson.update(
                    null, /* firstName */
                    null, /* middleName */
                    null, /* lastName */
                    null, /* publicNickName */
                    null, /* addressMain */
                    null, /* addressPostal */
                    null, /* avatarUuid */
                    null, /* taxResident */
                    null, /* documentType */
                    null, /* documentNumber */
                    null, /* documentCountryOfIssuance */
                    null, /* documentFrontAttachmentId */
                    null, /* documentBackAttachmentId */
                    null, /* dataOfBirth */
                    null, /* placeOfBirth */
                    null, /* countryOfBirth */
                    null, /* nationality */
                    null, /* language */
                    null, /* region */
                    null, /* gender */
                    null, /* status */
                    null, /* subStatus */
                    null, /* legalGuardianAlias */
                    null, /* sessionTimeout */
                    null, /* DailyLimitWithoutConfirmationLogin */
                    allNotificationFilterUpdated
            );
        } else if (bunq.getUser().getReferencedObject() instanceof UserCompany) {
            UserCompany.update(
                    null, /* name */
                    null, /* publicNickName */
                    null, /* avatarUuid */
                    null, /* addressMain */
                    null, /* addressPostal */
                    null, /* language */
                    null, /* region */
                    null, /* country */
                    null, /* ubo */
                    null, /* chamberOfCommerce */
                    null, /* legalForm */
                    null, /* status */
                    null, /* subStatus */
                    null, /* sessionTimeout */
                    null, /* dailyLimitWithoutConfirmationLogin */
                    allNotificationFilterUpdated
            );
        } else if (bunq.getUser().getReferencedObject() instanceof UserLight) {
            throw new BunqException(ERROR_CANNOT_UPDATE_NOTIFICATION_FILTER_FOR_USER_LIGHT);
        } else {
            throw new BunqException(ERROR_UNKNOWN_USER_TYPE_RETURNED);
        }

        System.out.println();
        System.out.println("  | ✅  Callback URL added");
        System.out.println();
        System.out.println("  | ▶️  Check your changed overview");
        System.out.println();
        System.out.println();

        bunq.updateContext();
    }
}
