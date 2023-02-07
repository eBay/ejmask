package com.ebay.ejmask.spring.core;

import com.ebay.ejmask.api.IContentProcessor;
import com.ebay.ejmask.api.IFilter;
import com.ebay.ejmask.api.MaskingPattern;
import com.ebay.ejmask.core.BaseFilter;
import com.ebay.ejmask.extenstion.builder.header.HeaderFieldPatternBuilder;
import com.ebay.ejmask.extenstion.builder.json.JsonFieldPatternBuilder;
import com.ebay.ejmask.extenstion.builder.json.JsonFullValuePatternBuilder;
import com.ebay.ejmask.extenstion.builder.json.JsonRelativeFieldPatternBuilder;
import com.ebay.ejmask.extenstion.processor.ContentSlicerProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.util.Arrays;
import java.util.List;

@Configuration("FilterContextConfiguration")
@ComponentScan("com.ebay.ejmask.spring.core")
public class FilterContextConfiguration {

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public IContentProcessor getContentSlicerPreProcessor() {
        return new ContentSlicerProcessor();
    }

    /**
     * This class creates bunch of duplicate filters and configuration. It's the
     * responsibility of the initializer to dedupe them.
     */
    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public IFilter getHeaders() {
        return new BaseFilter(HeaderFieldPatternBuilder.class, 10, 90, "X-API-Key", "Auth-Assertion", "Authorization");
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public IFilter getName() {
        return new BaseFilter(JsonFieldPatternBuilder.class, 3, 45, "lastName", "firstName");
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public IFilter getContent() {
        return new BaseFilter(JsonFieldPatternBuilder.class, 10, 1, "documentContent", "content");
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public IFilter getList1() {
        return new BaseFilter(JsonFieldPatternBuilder.class, 3, 45,
                "addressLine1", "addressLine2", "lastName", "firstName", "shopperEmail", "houseNumberOrName", "street", "city", "county", "postalCode", "phoneNumber", "fullPhoneNumber", "telephoneNumber"
        );
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public IFilter getList1_duplicate() {
        return new BaseFilter(JsonFieldPatternBuilder.class, 3, 43,
                "addressLine1", "addressLine2", "lastName", "firstName", "shopperEmail", "houseNumberOrName", "street", "city", "county", "postalCode", "phoneNumber", "fullPhoneNumber", "telephoneNumber"
        );
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public IFilter getList2() {
        return new BaseFilter(JsonFieldPatternBuilder.class, 3, 45,
                "email", "businessEmail", "accountNumber", "bankAccountName", "bankBicSwift", "branchCode", "checkCode", "iban", "ownerCity", "ownerHouseNumberOrName", "ownerName", "ownerPostalCode", "ownerStreet"
        );
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public IFilter getList2_duplicate() {
        return new BaseFilter(JsonFieldPatternBuilder.class, 3, 45,
                "email", "businessEmail", "accountNumber", "bankAccountName", "bankBicSwift", "branchCode", "checkCode", "iban", "ownerCity", "ownerHouseNumberOrName", "ownerName", "ownerPostalCode", "ownerStreet"
        );
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public IFilter getLest4() {
        return new BaseFilter(JsonFieldPatternBuilder.class, 3, 45,
                "registrationNumber", "errorDescription", "payment.token"
        );
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public IFilter getLest5() {
        return new BaseFilter(JsonFieldPatternBuilder.class, 3, 45,
                "holderName", "full_name", "address_line_1", "address_line_2", "postal_code", "given_name", "surname",
                "email_address", "payer_id", "national_number");
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public IFilter getLest6() {
        return new BaseFilter(JsonFieldPatternBuilder.class, 3, 45,
                "merchantAccount", "transferCode", "sourceAccountCode", "destinationAccountCode",
                "accountHolderCode", "accountCode", "bankAccountUUID", "account", "threeds2.threeDS2Result.authenticationValue",
                "emailAddress", "cavv");
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public IFilter getLest7() {
        return new BaseFilter(JsonFieldPatternBuilder.class, 3, 45, "mandateReferencNumber", "mandateId", "creditor", "debtor");
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public IFilter getLest9() {
        return new BaseFilter(JsonFieldPatternBuilder.class, 3, 45, "givenNames", "line1", "line2", "area1", "postcode");
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public IFilter getLest10() {
        return new BaseFilter(JsonFullValuePatternBuilder.class, 0, 10,
                "recipient", "idNumber", "cardNumber", "expiry", "cvc", "expiryMonth", "expiryYear", "number", "cardHolderName", "expiryDate", "dateOfBirth", "bankCity", "bankCode", "bankName", "ownerDateOfBirth", "ownerState", "primaryAccount");
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public IFilter getLest10_duplicate_1() {
        return new BaseFilter(JsonFullValuePatternBuilder.class, 0, 10, "recipient", "idNumber", "cardNumber", "expiry", "cvc", "expiryMonth", "expiryYear");
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public IFilter getLest10_duplicate_2() {
        return new BaseFilter(JsonFullValuePatternBuilder.class, 0, 15, "dateOfBirth", "bankCity", "bankCode", "bankName", "ownerDateOfBirth", "ownerState", "primaryAccount");
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public IFilter getLest100() {
        //all these should be ignored.
        return new BaseFilter(JsonFullValuePatternBuilder.class, 1000, 1,
                "recipient", "idNumber", "cardNumber", "expiry", "cvc", "expiryMonth", "expiryYear", "number", "cardHolderName", "expiryDate", "dateOfBirth", "bankCity", "bankCode", "bankName", "ownerDateOfBirth", "ownerState", "primaryAccount");
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public IFilter getBuyerNameFilter() {
        //all these should be ignored.
        return new BaseFilter(JsonRelativeFieldPatternBuilder.class, 2,
                "buyer", "name");
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.DEFAULT)
    public List<MaskingPattern> getMaskingPattern() {
        return Arrays.asList(
                new MaskingPattern(90, "\\\\?\"(SURNAME|GIVEN_NAME|PAYER_EMAIL_ADDRESS|PAYER_PROCESSOR_ID|PAYEE_EMAIL_ADDRESS)\",\\s*\"value\"\\s*:\\s*\"([^\"]{0,3})[^\"]*\\\\?\"", "\"$1\",\"value\":\"$2-xxxx\""),
                new MaskingPattern(90, "\\\\?\"(shipping|billing)\\\\?\"\\s*:\\s*\\{\\s*\"name\\\\?\"\\s*:\\s*\\\\?\"([^\"]{0,3})[^\"]*\\\\?\"", "\"$1\": { \"name\":\"$2-xxxx\""),
                new MaskingPattern(90, "(\\\\?\"paywithgoogle.token\\\\?\"\\s*:.*\\\\?\"data\\\\?\"\\s*:\\\\?\")([^\"]{0,3})[^\"]*(.*)}\"", "$1$2-xxxx\\\\$3}\""),
                new MaskingPattern(90, "\\\\?\"split.item({0,3}\\d).account\\\\?\"\\s*:\\s*\\\\?\"([^\"]{0,3})[^\"]*\\\\?\"", "\"split.item$1.account\":\"$2-xxxx\""),
                new MaskingPattern(90, "\\\\?\"raw_token\\\\?\"\\s*:\\s*\\\\?\"[^\"]*\\\\?\"", "\"raw_token\":\"$1-xxxx\"")
        );
    }
}
