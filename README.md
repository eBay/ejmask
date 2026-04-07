# eJMask `{*:*}`

eJMask is a JVM-based masking library that provides an easy-to-use API for masking sensitive data in your Java applications. With eJMask, you can quickly mask sensitive information like personal information, credit card numbers, and more. eJMask library is designed to provide a simple interface to make masking sensitive data sets before logging easier and simpler without impacting performance.

### Features
- Easy-to-use API for integration into your Java applications
- Support for multiple masking strategies, including character substitution and partial masking
- Custom masking strategies can be added easily to meet your specific needs
- Lightweight and efficient, with no external dependencies

### Getting Started

To get started with eJMask, you'll need to add the eJMask library to your project using your preferred build system, such as Maven or Gradle.

Here's an example of how to use eJMask in your code:

```java
public class EJMaskExample {

    static {
        // configure masking rules. 
        EJMaskInitializer.addFilter(
                new BaseFilter(JsonFieldPatternBuilder.class, 4, "user_name", "first_name"),
                new BaseFilter(JsonFullValuePatternBuilder.class, 0, "password")
        );
    }

    public static void main(String[] args) {
        String input = "{\"user_name\":\"prasanth\",\"password\":\"masking@ejmask\"}";
        // mask a sensitive piece of data
        String output = EJMask.mask(input);
        //Output {"user_name":"pras-xxxx","password":"****"}
        System.err.println("result: " + output);
    }
}
```

## Components

### IPatternBuilder

`IPatternBuilder` implementations are responsible to generate the regular expression needed to replace data to be masked. Pattern builder also have additional responsibility to optimize the regex by creating one expression to mask all list of field names for better performance.

#### eg: 
```java
public class JsonPatternBuilder implements IPatternBuilder {

    @Override
    public String buildPattern(int visibleCharacters, String... fieldName) {
        String fieldNames = Arrays.stream(fieldName).collect(Collectors.joining("|"));
        return String.format("\\\"(%s)(\\\\*\\\"\\s*:\\s*\\\\*\\\")([^\\\"]{1,%d})[^\\\"]*(\\\\?\\\"|)", fieldNames, visibleCharacters);
    }

    @Override
    public String buildReplacement(int visibleCharacters, String... fieldNames) {
        return "\"$1$2$3-xxxx$4";
    }
}
``` 
#### Extensions

For many standard use cases you can make use of pattern builders defined in the `ejmask-extensions` module.

##### JSON Builders

| Builder                                | Value Type   | Masking Style                                              |
|----------------------------------------|--------------|------------------------------------------------------------|
| `JsonFieldPatternBuilder`              | String       | First N chars visible, rest `→ -xxxx`                      |
| `JsonFullValuePatternBuilder`          | String       | Fully replaced with `****`                                 |
| `JsonRelativeFieldPatternBuilder`      | String       | First N chars visible; matched relative to a parent field  |
| `JsonNumericFieldPatternBuilder`       | Number       | Always fully replaced with `"xxxx"`                        |
| `JsonBooleanFieldPatternBuilder`       | Boolean      | Always fully replaced with `"xxxx"`                        |
| `JsonValueUnmaskFromEndPatternBuilder` | String       | Last N chars visible, start `→ xxxx-`                      |
| `JsonPathValuePatternBuilder`          | String       | First N chars visible; accepts full JSONPath expressions   |
| `JsonBodyPatternBuilder`               | Object       | Fully replaces a nested JSON object with `{"****":"****"}` |
| `JsonMiddleValuePatternBuilder`        | String       | Middle chars masked; first N/2 and last N/2 chars visible  |

###### JsonFieldPatternBuilder

Partially masks a JSON **string** field value, keeping the first N characters visible.

```java
// Input:  {"firstName":"sensitiveData","lastName":"anotherSecret"}
// Output: {"firstName":"sens-xxxx","lastName":"anot-xxxx"}
EJMaskInitializer.addFilter(
    new BaseFilter(JsonFieldPatternBuilder.class, 4, "firstName", "lastName")
);
```

###### JsonFullValuePatternBuilder

Fully masks a JSON **string** field value. `visibleCharacters` must be `0`.

```java
// Input:  {"password":"mySecret123","ssn":"123-45-6789"}
// Output: {"password":"****","ssn":"****"}
EJMaskInitializer.addFilter(
    new BaseFilter(JsonFullValuePatternBuilder.class, 0, "password", "ssn")
);
```

###### JsonRelativeFieldPatternBuilder

Masks a JSON **string** field whose identity depends on a parent field name. Useful when the same field name is sensitive only in a specific context (e.g. `buyer.name` but not `seller.name`).

```java
// Input:  {"buyer":{"name":"sensitiveData"},"seller":{"name":"publicName"}}
// Output: {"buyer":{"name":"sens-xxxx"},"seller":{"name":"publicName"}}
EJMaskInitializer.addFilter(
    new BaseFilter(JsonRelativeFieldPatternBuilder.class, 4, "buyer", "name")
);
```

###### JsonNumericFieldPatternBuilder

Masks a JSON **numeric** field value (integer, decimal, or scientific notation). The number is always fully replaced with `"xxxx"` — `visibleCharacters` must be `0`.

```java
// Input:  {"accountId":123456789,"zipCode":90210}
// Output: {"accountId":"xxxx","zipCode":"xxxx"}
EJMaskInitializer.addFilter(
    new BaseFilter(JsonNumericFieldPatternBuilder.class, 0, "accountId", "zipCode")
);
```

###### JsonBooleanFieldPatternBuilder

Masks a JSON **boolean** field value (`true`/`false`/`True`/`False`/`TRUE`/`FALSE`). Always fully replaced with `"xxxx"` — `visibleCharacters` must be `0`.

```java
// Input:  {"isVerified":true,"hasConsent":false}
// Output: {"isVerified":"xxxx","hasConsent":"xxxx"}
EJMaskInitializer.addFilter(
    new BaseFilter(JsonBooleanFieldPatternBuilder.class, 0, "isVerified", "hasConsent")
);
```

###### JsonValueUnmaskFromEndPatternBuilder

Masks a JSON **string** field value keeping the **last** N characters visible — ideal for card numbers, phone numbers, or account identifiers where the tail is needed for identification.

```java
// Input:  {"ccNumber":"1234567890123456","ssn":"123456789"}
// Output: {"ccNumber":"xxxx-3456","ssn":"xxxx-6789"}
EJMaskInitializer.addFilter(
    new BaseFilter(JsonValueUnmaskFromEndPatternBuilder.class, 4, "ccNumber", "ssn")
);
```


###### JsonPathValuePatternBuilder

Masks a JSON **string** field value using a full **JSONPath expression**. Supports filter-predicate paths and simple dotted field paths. Multiple expressions can be combined into a single filter for a single-pass replacement.

```java
// Filter predicate — mask a field in an array element matching all @.key=='value' conditions:
// Input:  {"source":"EBAY","type":"DEVICE_ID","value":"74f4ef092963b7439107285a8062c94a"}
// Output: {"source":"EBAY","type":"DEVICE_ID","value":"74f4****"}
EJMaskInitializer.addFilter(
    new BaseFilter(JsonPathValuePatternBuilder.class, 4,
        "$.deviceContext.fingerprints[?(@.source=='EBAY' && @.type=='DEVICE_ID')].value")
);

// Simple dotted path — mask by field name anywhere in the document:
// Input:  {"guid":"some-guid-value"}
// Output: {"guid":"some****"}
EJMaskInitializer.addFilter(
    new BaseFilter(JsonPathValuePatternBuilder.class, 4, "$.deviceContext.guid")
);

// Multiple expressions combined into one filter:
EJMaskInitializer.addFilter(
    new BaseFilter(JsonPathValuePatternBuilder.class, 4,
        "$.deviceContext.fingerprints[?(@.source=='EBAY' && @.type=='DEVICE_ID')].value",
        "$.deviceContext.guid")
);
```

###### JsonBodyPatternBuilder

Fully masks a nested JSON **object** field (single-level, no embedded objects). The entire object value is replaced with `{"****":"****"}`. `visibleCharacters` must be `0`.

```java
// Input:  {"address":{"street":"123 Main St","city":"Springfield"}}
// Output: {"address":{"****":"****"}}
EJMaskInitializer.addFilter(
    new BaseFilter(JsonBodyPatternBuilder.class, 0, "address", "paymentInfo")
);
```

###### JsonMiddleValuePatternBuilder

Masks the middle characters of a JSON **string** field, keeping the first N/2 and last N/2 characters visible. `visibleCharacters` must be even and ≥ 2.

```java
// Input:  {"cardNumber":"1234567890123456"}
// Output: {"cardNumber":"1234****3456"}
EJMaskInitializer.addFilter(
    new BaseFilter(JsonMiddleValuePatternBuilder.class, 8, "cardNumber")
);
```

##### Header Builder

###### HeaderFieldPatternBuilder

Masks HTTP **header** field values in a `key=value` query-string format. With `visibleCharacters = 0` the value is fully replaced with `******`; with `visibleCharacters > 0` the last N characters are kept after `xxxx-`.

```java
// Full mask:
// Input:  Authorization=Bearer secret-token-12345&Accept=application/json
// Output: Authorization=******&Accept=application/json
EJMaskInitializer.addFilter(
    new BaseFilter(HeaderFieldPatternBuilder.class, 0, "Authorization", "X-API-Key")
);

// Partial mask (shows last N chars):
// Input:  Authorization=Bearer secret-token-12345
// Output: Authorization=xxxx-2345
EJMaskInitializer.addFilter(
    new BaseFilter(HeaderFieldPatternBuilder.class, 4, "Authorization")
);
```

##### XML Builder

###### XmlFieldPattenBuilder

Partially masks an **XML** element's text content, keeping the first N characters visible.

```java
// Input:  <firstName>sensitiveData</firstName><lastName>anotherSecret</lastName>
// Output: <firstName>sens-xxxx</firstName><lastName>anot-xxxx</lastName>
EJMaskInitializer.addFilter(
    new BaseFilter(XmlFieldPattenBuilder.class, 4, "firstName", "lastName")
);
```

### IFilter

`IFilter` defines how a field should be masked. This includes the field name, the pattern builder need to be used, number of characters need to be visible at the end or beginning, etc.

eg:
```java

public class Sample implements IFilter {

    @Override
    public Class<? extends IPatternBuilder> getPatternBuilder() {
        return JsonPatternBuilder.class;
    }

    @Override
    public String[] getFieldNames() {
        return new String[]{"user_name", "first_name", "last_name", "address_1"};
    }
}
```
> Users can also override default values for `VisibleCharacters`,`Group`, `Order)` etc if needed.

### ContentProcessor

`ContentProcessor`(s) configured with the data masker will be invoked to process the data before and after actual masking operations getting invoked.
A few use case we can use is to decode and encode the string before masking and/or to reduce the size of a large string before performing the masking operation to improve performance.

#### Extensions
- ContentSlicerProcessor

### LogProvider

In case if you need to override the default logging library with the one you choose, just implement `ILogProvider`.

```java
LoggerUtil.register(new MyLogProvider());
```

## Getting Started

### Supported Languages
eJMask is created as a Maven based Java project and can be used as a dependency in a Java based application or other JVM based languages such as Kotlin, Groovy, Scala etc.

### Manual configuration.

Using `EJMaskInitializer` we will be able to add masking pattern rules to EJMask at the time of your application start up code.
eJMask will internally dedupe the given set of filters and generate the most optimized set of regular expression to replace the sensitive data elements.

#### Adding Filters

Invoke `EJMaskInitializer.addFilters` with list of all Filter instances. `EJMaskInitializer` Internally removes all duplicate and optimizes the MaskingPatterns by grouping similar patterns. 

#### Adding ContentProcessors

Invoke `EJMaskInitializer.addContentProcessors` to add ContentProcessors to eJMask Context.

#### Adding MaskingPatterns

If we don't want eJMask to dedupe and optimize the regular expression to mask use the bellow operations.

- `EJMaskInitializer.addMaskingPattern`
- `EJMaskInitializer.addMaskingPatterns`

> This is not recommended as this can cause copy-paste error and duplicates thus impacting performance.

#### Auto configuration

If you are using spring application ejamsk configurations can easily be auto wired using the following options.

#### Defining Beans.

##### Option 1:

- Extend directly from `BaseFilter` or implement `IFilter`.
- Annotate the class with spring `@Component` annotation.
- Add the Package to component scan.
- Done !!

```java
@Component("data-filter.add-address")
public class AddAddressFilter extends BaseFilter {
    AddAddressFilter() {
        super(JsonFieldPatternBuilder.class, "addressLine1", "addressLine2");
    }
}
```

> ideal for cases where all filed share one filter configuration.

##### Option 2:

- Create a new spring configuration class.
- Create and inject beans which extends `BaseFilter`
- We can add multiple groups for better readability.
- Annotate the class with spring `@Component` annotation.
    - Add to package to component class.
- ✔ Done !!

```java
@Configuration("data-filter.config.add-shareholder")
public class AddShareholderRequestFilterConfiguration {

    @Bean(name = "data-filter.add-shareholder")
    public IFilter getShareholder() {
        return new BaseFilter(JsonFieldPatternBuilder.class, "email");
    }

    @Bean(name = "data-filter.add-shareholder.personalInfo")
    public IFilter getShareholderPersonalInfo() {
        return new BaseFilter(JsonFieldPatternBuilder.class, "firstName", "lastName", "dateOfBirth");
    }
}
```

### Spring extensions

eJMask is a spring native library, spring eases the process of configuring eJMask and makes integration fun and secure.

#### WireOn

Fist add `ejmask-spring-core` to your dependency list.

```xml
<dependency>
  <groupId>com.ebay.ejmask</groupId>
  <artifactId>ejmask-spring-core</artifactId>
</dependency>
```

then simply add `com.ebay.ejmask.spring.core` to your spring context scanning.

```xml
<context:component-scan base-package="com.ebay.ejmask.spring.core"/>
```

#### AutoConfiguration

If your application is built on spring boot you can skip the above step by simply adding `ejmask-spring-boot` into dependency list.

```xml
<dependency>
  <groupId>com.ebay.ejmask</groupId>
  <artifactId>ejmask-spring-starter</artifactId>
</dependency>
```

**Properties**

| property                                   | description                                    | values               | default    |
|--------------------------------------------|------------------------------------------------|----------------------|------------|
| `ejmask.autoconfig`                        | Conditionally wire on flat                     | `enabled`,`disabled` | `enabled`  |
| `ejmask.processor.content-slicer`          | Conditionally wire on content slicer processor | `enabled`,`disabled` | `disabled` |
| `ejmask.processor.content-slicer.priority` | Content slicer priority                        | integer              | `50`       |
| `ejmask.processor.content-slicer.max-size` | Content slicer maximum allowed length          | integer              | `10000`    |
| `ejmask.processor.content-slicer.new-size` | Content slicer maximum new content size.       | integer              | `4000`     |

### Where can I get the latest release?

You can download source from our [release page](https://github.com/eBay/ejmask/releases).
Alternatively you can pull it from the central [Maven repositories](https://mvnrepository.com/artifact/com.ebay.ejmask):

### Using in your maven project.

```xml
<dependency>
  <groupId>com.ebay.ejmask</groupId>
  <artifactId>ejmask-bom</artifactId>
  <version>2.0.4</version>
</dependency>
```

### Using in your Gradle Project.

```groovy
compile group: 'com.ebay.ejmask', name: 'ejmask-bom', version: '2.0.4'
```

## Roadmap

- [x] eJMask extensions with ready to use common filters.
- [x] Spring Bean Support.
- [x] Spring Boot Starter Support.
- [x] Mask Operation with timeout.
- [ ] Users will should be able to configure data filters through `ejmask.ymal`.
- [ ] Users will be able to mask any given field by annotating with `@Mask` annotation.


## License Information
Copyright 2023 eBay Inc.

Author(s): [Prasanth Kaimattil Venu](https://github.com/prasanthkv), [Manikandan Perumal](https://github.com/tbd)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
