# eJMask `{*:*}`

eJMask is a JVM-based masking library that provides an easy-to-use API for masking sensitive data in your Java applications. With eJMask, you can quickly mask sensitive information like personal information, credit card numbers, and more. eJMask library is designed to provide a simple interface to make masking sensitive data sets before
logging easier and simpler without impacting performance.

### Features

- Easy-to-use API for integration into your Java applications
- Support for multiple masking strategies, including character substitution and partial masking
- Custom masking strategies can be added easily to meet your specific needs
- Lightweight and efficient, with no external dependencies

### Dependencies

| JDK Version | Spring Version | Spring Boot Version |
|-------------|----------------|---------------------|
| 17          | 6.1.11         | 3.2.8               |

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

For many standard use cases you can make use of pattern builders defined in `ejmask-extensions` module.

- HeaderFieldPatternBuilder
- XmlFieldPattenBuilder
- JsonMiddleValuePatternBuilder
- JsonFullValuePatternBuilder
- JsonFieldPatternBuilder
- JsonRelativeFieldPatternBuilder

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

- ContentSlicerPreProcessor

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

If your application is built on spring boot you can skip the above step by simply adding `ejmask-spring-starter` into dependency list.

```xml

<dependency>
    <groupId>com.ebay.ejmask</groupId>
    <artifactId>ejmask-spring-starter</artifactId>
</dependency>
```

**Properties**

| property                                   | description                                    | values               | default   |
|--------------------------------------------|------------------------------------------------|----------------------|-----------|
| `ejmask.autoconfig`                        | Conditionally wire on flat                     | `enabled`,`disabled` | `enabled` |
| `ejmask.processor.content-slicer`          | Conditionally wire on content slicer processor | `enabled`,`disabled` | `enabled` |
| `ejmask.processor.content-slicer.priority` | Content slicer priority                        | integer              | `50`      |
| `ejmask.processor.content-slicer.max-size` | Content slicer maximum allowed length          | integer              | `10000`   |
| `ejmask.processor.content-slicer.new-size` | Content slicer maximum new content size.       | integer              | `4000`    |

### Where can I get the latest release?

You can download source and binaries from our [release page](https://github.com/eBay/ejmask/releases).
Alternatively you can pull it from the central Maven repositories:

### Using in your maven project.

> Please check the release version before adding to your project.

```xml

<dependency>
    <groupId>com.ebay.ejmask</groupId>
    <artifactId>ejmask-bom</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Using in your Gradle Project.

```groovy
compile group: 'com.ebay.ejmask', name: 'ejmask-bom', version: '2.0.0'
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

