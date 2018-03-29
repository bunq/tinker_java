package com.bunq.tinker.libs;

import com.bunq.sdk.context.ApiContext;
import com.bunq.sdk.context.ApiEnvironmentType;
import com.bunq.sdk.context.BunqContext;
import com.bunq.sdk.exception.BunqException;
import com.bunq.sdk.exception.ForbiddenException;
import com.bunq.sdk.http.Pagination;
import com.bunq.sdk.model.generated.endpoint.Card;
import com.bunq.sdk.model.generated.endpoint.MonetaryAccountBank;
import com.bunq.sdk.model.generated.endpoint.Payment;
import com.bunq.sdk.model.generated.endpoint.RequestInquiry;
import com.bunq.sdk.model.generated.endpoint.SandboxUser;
import com.bunq.sdk.model.generated.endpoint.User;
import com.bunq.sdk.model.generated.endpoint.UserCompany;
import com.bunq.sdk.model.generated.endpoint.UserPerson;
import com.bunq.sdk.model.generated.object.LabelMonetaryAccount;
import com.bunq.sdk.model.generated.object.Pointer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BunqLib {

  private static final int DEFAULT_FETCH_COUNT = 10;

  /**
   * Error constants.
   */
  private static final String ERROR_COULD_NOT_FIND_ALIAS_TYPE_IBAN = "Could not find alias with type IBAN for monetary account \"%s\"";
  private static final String ERROR_COULD_NOT_GENERATE_NEW_API_KEY = "Encountered error while retrieving new sandbox ApiKey.\nError message %s";
  private static final String ERROR_COULD_NOT_FIND_CONFIG_FILE = "Could not find a bunq configuration to load.";

  /**
   * FileName constatns.
   */
  private static final String FILE_NAME_BUNQ_CONF_PRODUCTION = "bunq-production.conf";
  private static final String FILE_NAME_BUNQ_CONF_SANDBOX = "bunq-sandbox.conf";

  /**
   * Field constatns.
   */
  private static final String FIELD_RESPONSE = "Response";
  private static final String FIELD_API_KEY = "ApiKey";

  /**
   */
  private static final String POINTER_TYPE_IBAN = "IBAN";
  private static final String MONETARY_ACCOUNT_STATUS_ACTIVE = "ACTIVE";
  private static final String DEVICE_SERVER_DESCRIPTION = "bunq Tinker java";

  /**
   * The index of the fist item in an array.
   */
  private static final int INDEX_FIRST = 0;

  /**
   * Http constatns.
   */
  private static final int HTTP_STATUS_OK = 200;
  private static final String ERROR_COULD_NOT_DETERMINE_USER_TYPE = "Could not determine user type";

  private ApiEnvironmentType environmentType;

  private User user;

  public BunqLib(ApiEnvironmentType environmentType) {
    this.environmentType = environmentType;

    this.setupContext();
    this.setupCurrentUser();
  }

  /**
   */
  private void setupContext() {
    this.setupContext(true);
  }

  /**
   */
  private void setupContext(boolean resetConfigIfNeeded) {
    if (new File(this.determineBunqConfigFileName()).exists()) {
      // Config is already present.
    } else if (ApiEnvironmentType.SANDBOX.equals(this.environmentType)) {
      SandboxUser sandboxUser = generateNewSandboxUser();
      ApiContext.create(ApiEnvironmentType.SANDBOX, sandboxUser.getApiKey(), DEVICE_SERVER_DESCRIPTION).save(this.determineBunqConfigFileName());
    } else {
      throw new BunqException(ERROR_COULD_NOT_FIND_CONFIG_FILE);
    }

    try {
      ApiContext apiContext = ApiContext.restore(this.determineBunqConfigFileName());

      apiContext.ensureSessionActive();
      apiContext.save(this.determineBunqConfigFileName());

      BunqContext.loadApiContext(apiContext);
    } catch (ForbiddenException forbiddenException) {
      if (resetConfigIfNeeded) {
        this.handleForbiddenException(forbiddenException);
      } else {
        throw forbiddenException;
      }
    }
  }

  public void updateContext() {
    BunqContext.getApiContext().save(this.determineBunqConfigFileName());
  }

  /**
   * @return String
   */
  private String determineBunqConfigFileName() {
    if (ApiEnvironmentType.PRODUCTION.equals(this.environmentType)) {
      return FILE_NAME_BUNQ_CONF_PRODUCTION;
    } else {
      return FILE_NAME_BUNQ_CONF_SANDBOX;
    }
  }

  /**
   */
  private void handleForbiddenException(ForbiddenException forbiddenException) {
    if (ApiEnvironmentType.SANDBOX.equals(this.environmentType)) {
      this.deleteOldConfig();
      this.setupContext(false);
    } else {
      throw forbiddenException;
    }
  }

  /**
   */
  private void deleteOldConfig() {
    try {
      Files.delete(Paths.get((this.determineBunqConfigFileName())));
    } catch (IOException e) {
      throw new BunqException(e.getMessage());
    }
  }

  /**
   */
  private void setupCurrentUser() {
    this.user = User.get().getValue();
  }

  public User getUser() {
    return this.user;
  }

  public List<MonetaryAccountBank> getAllMonetaryAccountBankActive() {
    return getAllMonetaryAccountBankActive(DEFAULT_FETCH_COUNT);
  }

  public List<MonetaryAccountBank> getAllMonetaryAccountBankActive(int count) {
    Pagination pagination = new Pagination();
    pagination.setCount(count);

    List<MonetaryAccountBank> allAccount = MonetaryAccountBank.list(pagination.getUrlParamsCountOnly()).getValue();
    List<MonetaryAccountBank> allAccountActive = new ArrayList<>();

    for (MonetaryAccountBank account : allAccount) {
      if (account.getStatus().equals(MONETARY_ACCOUNT_STATUS_ACTIVE)) {
        allAccountActive.add(account);
      } else {
        // Account is not active.
      }
    }

    return allAccountActive;
  }

  public List<Payment> getAllPayment(MonetaryAccountBank monetaryAccountBank) {
    return getAllPayment(monetaryAccountBank, DEFAULT_FETCH_COUNT);
  }

  public List<Payment> getAllPayment(MonetaryAccountBank monetaryAccountBank, int count) {
    Pagination pagination = new Pagination();
    pagination.setCount(count);

    return Payment.list(
        monetaryAccountBank.getId(),
        pagination.getUrlParamsCountOnly()
    ).getValue();
  }

  public List<RequestInquiry> getAllRequest(MonetaryAccountBank monetaryAccountBank) {
    return getAllRequest(monetaryAccountBank, DEFAULT_FETCH_COUNT);
  }

  public List<RequestInquiry> getAllRequest(MonetaryAccountBank monetaryAccountBank, int count) {
    Pagination pagination = new Pagination();
    pagination.setCount(count);

    return RequestInquiry.list(
        monetaryAccountBank.getId(),
        pagination.getUrlParamsCountOnly()
    ).getValue();
  }

  public List<Card> getAllCard() {
    return getAllCard(DEFAULT_FETCH_COUNT);
  }

  public List<Card> getAllCard(int count) {
    Pagination pagination = new Pagination();
    pagination.setCount(count);

    return Card.list(
        pagination.getUrlParamsCountOnly()
    ).getValue();
  }

  public static Pointer getPointerIbanForMonetaryAccountBank(MonetaryAccountBank monetaryAccountBank) {
    for (Pointer pointer : monetaryAccountBank.getAlias()) {
      if (pointer.getType().equals(POINTER_TYPE_IBAN)) {
        return pointer;
      }
    }

    throw new BunqException(
        String.format(
            ERROR_COULD_NOT_FIND_ALIAS_TYPE_IBAN,
            monetaryAccountBank.getDescription()
        )
    );
  }

  public static MonetaryAccountBank getMonetaryAccountBankFromLabel(
      LabelMonetaryAccount label,
      List<MonetaryAccountBank> allMonetaryAccountBank
  ) {
    String labelIban = label.getIban();

    for (MonetaryAccountBank monetaryAccountBank : allMonetaryAccountBank) {
      String monetaryAccountBankIban = getPointerIbanForMonetaryAccountBank(monetaryAccountBank).getValue();

      if (labelIban.equals(monetaryAccountBankIban)) {
        return monetaryAccountBank;
      } else {
        // Have not found the matching MonetaryAccountBank yet.
      }
    }

    return null;
  }

  public List<Pointer> getAllUserAlias() {
    if (this.getUser().getReferencedObject() instanceof UserPerson) {
      return ((UserPerson) this.getUser().getReferencedObject()).getAlias();
    } else if (this.getUser().getReferencedObject() instanceof UserCompany) {
      return ((UserCompany) this.getUser().getReferencedObject()).getAlias();
    } else {
      throw new BunqException(ERROR_COULD_NOT_DETERMINE_USER_TYPE);
    }
  }

  private SandboxUser generateNewSandboxUser() {
    OkHttpClient client = new OkHttpClient();

    Request request = new Request.Builder()
        .url("https://sandbox.public.api.bunq.com/v1/sandbox-user")
        .post(RequestBody.create(null, new byte[0]))
        .addHeader("x-bunq-client-request-id", "1234")
        .addHeader("cache-control", "no-cache")
        .addHeader("x-bunq-geolocation", "0 0 0 0 NL")
        .addHeader("x-bunq-language", "en_US")
        .addHeader("x-bunq-region", "en_US")
        .build();

    try {
      Response response = client.newCall(request).execute();
      if (response.code() == HTTP_STATUS_OK) {
        String responseString = response.body().string();
        JsonObject jsonObject = new Gson().fromJson(responseString, JsonObject.class);
        JsonObject apiKEy = jsonObject.getAsJsonArray(FIELD_RESPONSE).get(INDEX_FIRST).getAsJsonObject().get(FIELD_API_KEY).getAsJsonObject();

        return SandboxUser.fromJsonReader(new JsonReader(new StringReader(apiKEy.toString())));
      } else {
        throw new BunqException(String.format(ERROR_COULD_NOT_GENERATE_NEW_API_KEY, response.body().string()));
      }
    } catch (IOException e) {
      throw new BunqException(e.getMessage());
    }
  }
}
