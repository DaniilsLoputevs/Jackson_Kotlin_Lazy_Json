# Jackson_Kotlin_Lazy_Json

Description on ENG & RUS

## ENG
Situation:
- an object received synchronously/aynchronously/from_database is often confirmed by changes
- large object (up to 15k lines of json file pretty format)
- in the standard object schema there are many notnull fields (kotlin is used), which further complicates the automapping of a mutable object via Jackson
- automatic Jackson mapping, as well as Jackson automigrations, are not always possible, because the changes being made are not always known or are difficult/labor-intensive to track
- data from an object is not always needed in full
- no desire to work with pure Jakson API, using has/get etc...

Problem:
- crash when reading an object if it has been changed, and our current release does not provide for this
- crash when reading an object if the schema has been changed in the current release, and we are working with an old version of the object
- the crash causes complete non-execution of the logic

Solution:
- creating a json adapter that wraps pure json based on the Jakson API
- creation of a separate migration stage for major changes to the original object

Pros of the solution:
- increased speed of mapping and object processing, as it feels
- even if part of the data is missing, the business logic of the service can be executed

Disadvantages of the solution:
- large amount of JsonNode in JVM memory
- the transition to working with delegates did not make the object itself simpler
- you still need to partially monitor changes in the base object, at least old fields

## RUS
Ситуация:
- полученный синхронно/аинхронно/из_базы объект часто подтвержен изменениям
- объект крупный (до 15к строк json файла)
- в стандартной схеме объекта много notnull полей (используется kotlin), что еще сильнее усложняет автомаппинг изменяемого объекта через Jackson
- автоматический маппинг Jackson, а так же автомиграции Jackson не всегда возможны, т.к. проводимые изменения не всегда известны или сложно/трудоёмко отслеживаемы
- данные из объекта не всегда нужны в полном объёме
- нет желания работать с чистым АПИ Jakson, используя has/get etc...

Проблема:
- падение при чтении объекта, если он был изменён, а наш текущий релиз этого не предусматривает
- падение при чтении объекта, если схема была изменена в текущем релизе, а мы работаем со старой версией объекта
- падение вызывает полное не выполнение логики

Решение:
- создание json-адаптера, который оборачивает чистый json на базе АПИ Jakson
- создание отдельного этапа миграций, для крупных изменений оригинального объекта

Плюсы решения:
- прирост скорости маппинга и обработки объектов, по ощущениям
- даже при отсуствии части данных, может быть выполнена бизнес-логика сервиса

Минусы решения:
- большой объем JsonNode в памяти JVM
- переход на работу с делегатами не сделал сам объект проще
- всё равно нужно частично следить за изменениями базового объекта, как минимум за старыми полями
