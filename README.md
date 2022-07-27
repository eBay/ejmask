# eJMask `{*!*}`

eJMask library provides a simple interface to make masking sensitive data sets before logging easier and simpler without impacting performance.

```
//input 
"{\"username\":\"prasanth\",\"password\":\"masking@ejmask\"}"
//mask
EJMask.mask(input);
//output
{"username":"pra****","password":"****"}
```

## Components

### IFilter
`IFilter` defines how a field should be masked. This includes the field name, the pattern builder need to be used, number of characters need to be visible at the end or beginning, etc.

### IPatternBuilder
`IPatternBuilder` implementations are responsible to generate the regular expression needed to replace data to be masked..
For many standard use cases you can make use of patten builders defined in ejmask extensions module.

### ContentProcessor
`ContentProcessor`(s) configured with the data masker will be invoked to process the data before and after actual masking operations getting invoked.
A few usecase we can use is to decode and encode the sting before masking or to reduce the size of a large string before performing the masking operation to improve performance.

### LogProvider

In case if you need to override the default logging library with the one you choose, just implement `ILogProvider`.

## Roadmap

- [ ] eJMask extensions with ready to use common filters. 
- [ ] Spring Bean Support.
- [ ] Spring Boot Starter Support.
- [ ] Users will be able to mask any given field by annotating with `@Filter` annotation.
- [ ] Users will should be able to configure data filters through `ejmas.ymal`.
- [ ] Mask Operation with timeout.

