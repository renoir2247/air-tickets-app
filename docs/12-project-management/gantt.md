# Диаграмма Ганта

## Цель этапа

Визуализировать временные рамки выполнения каждой работы и их взаимосвязи для проекта «АвиаКасса».

## Результат

Построена диаграмма Ганта, отражающая календарный план разработки веб-приложения для бронирования авиабилетов в период с марта по май 2026 года. Диаграмма охватывает проектирование, разработку backend (Spring Boot), разработку frontend (React), тестирование и документирование.

## Диаграмма Ганта

![Диаграмма Ганта](./images/gantt.png)

## PlantUML код

```plantuml
@startgantt
title Диаграмма Ганта — АвиаКасса

Project starts 2026-03-03

[Анализ предметной области] lasts 7 days and is colored in LightBlue
[Проектирование архитектуры] lasts 7 days and is colored in LightBlue
[Проектирование базы данных] lasts 5 days and is colored in LightBlue
[Проектирование API и UI] lasts 5 days and is colored in LightBlue

[Проектирование архитектуры] starts at [Анализ предметной области]'s end
[Проектирование базы данных] starts at [Проектирование архитектуры]'s end
[Проектирование API и UI] starts at [Проектирование базы данных]'s end

[Настройка Spring Boot] lasts 3 days and is colored in LightGreen
[JPA-сущности] lasts 5 days and is colored in LightGreen
[Репозитории и миграции] lasts 5 days and is colored in LightGreen
[Сервисы бронирования] lasts 7 days and is colored in LightGreen
[REST-контроллеры] lasts 6 days and is colored in LightGreen
[JWT-аутентификация] lasts 5 days and is colored in LightGreen
[Отчёты и админ-API] lasts 4 days and is colored in LightGreen

[Настройка Spring Boot] starts at [Проектирование API и UI]'s end
[JPA-сущности] starts at [Настройка Spring Boot]'s end
[Репозитории и миграции] starts at [JPA-сущности]'s end
[Сервисы бронирования] starts at [Репозитории и миграции]'s end
[REST-контроллеры] starts at [Сервисы бронирования]'s end
[JWT-аутентификация] starts at [REST-контроллеры]'s end
[Отчёты и админ-API] starts at [JWT-аутентификация]'s end

[Настройка React + Vite] lasts 3 days and is colored in LightYellow
[Авторизация и маршрутизация] lasts 4 days and is colored in LightYellow
[Поиск авиарейсов] lasts 5 days and is colored in LightYellow
[Бронирование и выбор мест] lasts 5 days and is colored in LightYellow
[Оплата и подтверждение] lasts 4 days and is colored in LightYellow
[Личный кабинет] lasts 4 days and is colored in LightYellow
[Админ-панель] lasts 5 days and is colored in LightYellow

[Настройка React + Vite] starts at [JPA-сущности]'s end
[Авторизация и маршрутизация] starts at [Настройка React + Vite]'s end
[Поиск авиарейсов] starts at [Авторизация и маршрутизация]'s end
[Бронирование и выбор мест] starts at [Поиск авиарейсов]'s end
[Оплата и подтверждение] starts at [Бронирование и выбор мест]'s end
[Личный кабинет] starts at [Оплата и подтверждение]'s end
[Админ-панель] starts at [Личный кабинет]'s end

[Модульное тестирование] lasts 7 days and is colored in LightPink
[Анализ покрытия (JaCoCo)] lasts 3 days and is colored in LightPink

[Модульное тестирование] starts at [Отчёты и админ-API]'s end
[Анализ покрытия (JaCoCo)] starts at [Модульное тестирование]'s end

[Техническая документация] lasts 7 days and is colored in Plum
[Пользовательская документация] lasts 5 days and is colored in Plum

[Техническая документация] starts at [Анализ покрытия (JaCoCo)]'s end
[Пользовательская документация] starts at [Техническая документация]'s end

@endgantt
```
