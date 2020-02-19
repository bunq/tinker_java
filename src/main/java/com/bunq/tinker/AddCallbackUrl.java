package com.bunq.tinker;

import com.bunq.sdk.context.ApiEnvironmentType;
import com.bunq.sdk.model.core.NotificationFilterUrlUserInternal;
import com.bunq.sdk.model.generated.endpoint.NotificationFilterUrlUser;
import com.bunq.sdk.model.generated.object.NotificationFilterUrl;
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
    private static final String NOTIFICATION_CATEGORY_MUTATION = "MUTATION";

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

        List<NotificationFilterUrlUser> allNotificationFilterCurrent = NotificationFilterUrlUser.list().getValue();
        List<NotificationFilterUrl> allNotificationFilterUpdated = new ArrayList<>();

        for (NotificationFilterUrlUser notificationFilterUrlUser : allNotificationFilterCurrent) {
            for (NotificationFilterUrl notificationFilterUrl : notificationFilterUrlUser.getNotificationFilters()) {
                if (callbackUrl.equals(notificationFilterUrl.getNotificationTarget())) {
                    allNotificationFilterUpdated.add(notificationFilterUrl);
                }
            }
        }

        allNotificationFilterUpdated.add(
                new NotificationFilterUrl(NOTIFICATION_CATEGORY_MUTATION, callbackUrl)
        );

        NotificationFilterUrlUserInternal.createWithListResponse(allNotificationFilterUpdated);

        System.out.println();
        System.out.println("  | ✅  Callback URL added");
        System.out.println();
        System.out.println("  | ▶️  Check your changed overview");
        System.out.println();
        System.out.println();

        bunq.updateContext();
    }
}
