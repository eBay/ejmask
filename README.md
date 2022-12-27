# eJMask `{*!*}`

eJMask library provides a simple interface to make masking sensitive data sets before logging easier and simpler without impacting performance.

```java
public class EJMaskExample {

    static {
        EJMaskInitializer.addFilter(
                new BaseFilter(JsonFieldPatternBuilder.class, 4, "user_name", "first_name"),
                new BaseFilter(JsonFullValuePatternBuilder.class, 0, "password")
        );
    }

    public static void main(String[] args) {
        String input = "{\"user_name\":\"prasanth\",\"password\":\"masking@ejmask\"}";
        //mask
        String output = EJMask.mask(input);
        //print output
        System.err.println("result: " + output);
    }
}
```

> result: {"user_name":"pras-xxxx","password":"****"}

## Components

### IFilter

`IFilter` defines how a field should be masked. This includes the field name, the pattern builder need to be used, number of characters need to be visible at the end or beginning, etc.

### IPatternBuilder

`IPatternBuilder` implementations are responsible to generate the regular expression needed to replace data to be masked.
For many standard use cases you can make use of patten builders defined in `ejmask-extensions` module.

- HeaderFieldPatternBuilder
- XmlFieldPattenBuilder
- JsonFullValuePatternBuilder
- JsonFieldPatternBuilder
- JsonRelativeFieldPatternBuilder

### ContentProcessor

`ContentProcessor`(s) configured with the data masker will be invoked to process the data before and after actual masking operations getting invoked.
A few usecase we can use is to decode and encode the sting before masking or to reduce the size of a large string before performing the masking operation to improve performance.

### LogProvider

In case if you need to override the default logging library with the one you choose, just implement `ILogProvider`.

## Getting Started

### Supported Languages
eJMask is created as a Maven based Java project and can be used as a dependency in a Java based application or other JVM based languages such as Kotlin, Groovy, Scala etc.

### Manual configuration.

Using `EJMaskInitializer` we will be able to add masking pattern rules to EJMask at the time of your application start up code.
eJMask will internally dedupe the given set of filters and generate the most optimized set of regular expression to replace the sensitive data elements.

#### Adding Filters

Invoke `EJMaskInitializer.addFilters` with list of all Filter instances.

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
    <groupId>com.ebay.pmt2.ejmask</groupId>
    <artifactId>ejmask-spring-autoconfig</artifactId>
</dependency>
```

then simply add `com.ebay.pmt2.ejmask.spring.core` to your spring context scanning.

```xml
<context:component-scan base-package="com.ebay.pmt2.ejmask.spring.core"/>
```

#### AutoConfiguration

If your application is built on spring boot you can skip the above step by simply adding `ejmask-spring-boot` into dependency list.

```xml
<dependency>
    <groupId>com.ebay.pmt2.ejmask</groupId>
    <artifactId>ejmask-spring-boot</artifactId>
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

```xml
<dependency>
    <groupId>com.ebay.pmt2.ejmask</groupId>
    <artifactId>ejmask-bom</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Using in your Gradle Project.

```groovy
compile group: 'com.ebay.pmt2.ejmask', name: 'ejmask-bom', version: '1.0.0'
```

## Roadmap

- [x] eJMask extensions with ready to use common filters.
- [x] Spring Bean Support.
- [x] Spring Boot Starter Support.
- [x] Mask Operation with timeout.
- [ ] Users will should be able to configure data filters through `ejmas.ymal`.
- [ ] Users will be able to mask any given field by annotating with `@Filter` annotation.
