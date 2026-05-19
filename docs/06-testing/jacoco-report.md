# Отчёт JaCoCo

## Настройка плагина

В файле `pom.xml` добавлен плагин JaCoCo:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Результаты покрытия

![JaCoCo отчёт](images/test-coverage.png)

Целевой показатель >40% покрытия достигнут.
