# Fanning

A desktop application for automatically controlling computer fan speeds based on values of temperature sensors. It features a GUI for defining mappings from temperatures to fan speeds.

NOTE: This is not a complete, working software. It is able to control fan speeds if a suitable backend is provided, but none exist currently. There was an effort to create [OpenHardwareMonitorWrapper](https://github.com/Tuupertunut/OpenHardwareMonitorWrapper) but I can't remember if it ever reached a functional state.

## Releases

[Latest release](https://github.com/Tuupertunut/Fanning/releases)

## Documentation

[Requirements specification](https://github.com/Tuupertunut/Fanning/blob/master/documentation/reqspec.md)

[Architecture](https://github.com/Tuupertunut/Fanning/blob/master/documentation/architecture.md)

[Known issues](https://github.com/Tuupertunut/Fanning/blob/master/documentation/todo.md)

## Development

### Run project

```
mvn package exec:java
```

### Run tests

```
mvn test
```

### Build executable jar

```
mvn package
```

### Generate test coverage report

```
mvn test jacoco:report
```

### Generate checkstyle report

```
mvn jxr:jxr checkstyle:checkstyle
```

[Työaikakirjanpito](https://github.com/Tuupertunut/Fanning/blob/master/documentation/tyoaikakirjanpito.md)
