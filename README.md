# BIF5D1 Softwareentwicklung 3 OR-Mapper
SWE3 Semester project OR-Mapper.<br>
JavaDoc unter [Dokumentation](https://if18b136.github.io/SWE3_OR_Mapper/) einsichtlich.

## Übersicht
- Architektur: Code First
- Metadatenkonfiguration: Annotationen
- Datenbankabbildung: Table per Type
- Datenbank: MySQL 8.0

## Funktionsübersicht

Das Framework ermöglicht Klassen für Datenbanktabellen zu erstellen, welche mit folgenden Annotationen versehen werden können:
- ```@Table```:
    - Tag, um eine Klasse als Datenbanktabelle zu annotieren. Erlaubt den Tabellennamen zu definieren und Kindtabellen anzugeben.
- ```@Column```: 
    - Tag, um ein Field als Tabellenspalte zu annotieren. Erlaubt primary key, autoIncrement, unique, nullable, variable length und ignore anzugeben.
    - Für length wird ein Datenbanktyp vorausgesetzt, welcher bei in der Datenbank legal eine Längenangabe beinhalten darf (zB. varchar).
    - Ignore ist keine Datenbankspezifische Angabe, sondern wird bei 1:n foreign keys angewandt, um die 1-zu-Relation zu einem Objekt anzugeben, welche in der Datenbank nicht extra angegeben wird.
- ```@ForeignKey```: 
    - Tag, um ein Field als Foreign Key zu annotieren.
- ```@MtoN```:
    - Tag, um ein Field als m:n Beziehung zu annotieren. Das Framework erstellt eine weitere m:n Tabelle, falls bereits beide Tabellen initialisiert wurden.

Um benutzeridentifizierte Objekte generalisiert verarbeiten zu können wird für jede Klasse ein Entity-Objekt erstellt, welches ein Grundgerüst für die Objekte darstellt. Jedes Klassenfeld, welches eine Spalte in der Datenbank darstellen soll, wir zudem als Field-Objekt in dem Entity-Objekt gespeichert.
    
Objekte, welche aus Klassen mit den gelisteten Annotationen erstellt werden, können mittels der statischen Manager-Klasse verschiedenen ORM-Operationen unterzogen werden.
- ```createTable()``` nimmt ein Objekt als Parameter und erstellt anhand der Metadaten einen Tabellen-Erstellungsquery für die Datenbank.
- ```get()``` Erstellt mit der angegebenen Klasse und den für das Objekt zur eindeutigen Identifikation benötigten Primärschlüsseln ein benutzeridentifiziertes Objekt, falls dieses in der Datenbank anzufinden ist. Hierbei werden sowohl Hierachien über joins, als auch 1:n und m:n Beziehungen als eigene Objekte erstellt und dem Objekt hinzugefügt. Bei m:n Beziehungen tritt ein temporärer Cache in Kraft, um einer rekursiven Anfrage nach denselben Objekten entgegenzuwirken.
- ```save()```  fügt ein Objekt mit einem Insert-Query in die Datenbank ein, unter Berücksichtigung von Klassenhierachie, 1:n und m:n Beziehungen.
- ```saveOrUpdate()``` fügt ein Objekt mit einem Upsert-Query in die Datenbank ein oder aktualisiert ein bereits in der Datenbank enthaltenes Objekt.
- ```delete()``` löscht einen zu einem Objekt gehörigen Datenbankeintrag aus der Datenbank.
- ```getEntity()``` durchsucht den Entity-Cache des Managers nach einem zu einem Objekt passenden Entity-Objekt oder erstellt ein neues Entity-Objekt, falls keines bislang erstellt wurde.
- Caching: Es ist möglich, erstellte oder abgerufene Objekte lokal zu speichern, um die Anzahl der Datenbankzugriffe zu verringern.
    - ```enableCaching()``` lässt den User die Caching-Funktion ein- oder ausschalten.
    - ```depleteCache()``` leert alle Einträge.

Bei diesem Projekt wurde bewusst auf die Möglichkeit einer kompletten Eingabe von SQL-Queries verzichtet und anstelle dessen pro CRUD query eine eigene Funktion zur Erstellung erschaffen.
- ```createTableQuery()``` erstellt einen create Table Query aus einer Entity.
- ```insertQuery()``` lässt den User zwischen insert und upsert wählen und erstellt dementsprechend einen Query aus einem Objekt.
- ```selectQuery()``` lässt den User Tabellen, Zielspalten und Bedingungen einstellen und erstellt daraus einen select Query.
- ```deleteQuery()``` erstellt einen delete Query aus einem Objekt.

Die Klassen leiten sich aus einem Query- und einem QueryLanguage-Interface ab. Hiermit können weitere Queries leicht erstellt werden.

## Vorzeigeprogramm

Zur Demonstration der Funktionalitäten des Frameworks wird über die Main-Methode ein Command line Interface aufgerufen, mittels dem der User verschiedene Funktionen testen kann.
Folgende Optionen werden gelistet:
- ```InitTables```: löscht und initiiert anschließend alle für die test-Entitäten benötigten Datenbanktabellen.
- ```dropTables```: löscht in korrekter Reihenfolge alle für die test-Entitäten benötigten Datenbanktabellen (Zusammen mit initTables wichtig, um beliebig Testprogramme laufen zu lassen, ohne Error-Benachrichtigungen zu bereits vorhandenen Einträgen zu erhalten).
- ```runAll```: Zeigt sämtliche oben genannten Funktionen her.
- ```runChoice```: Bietet eine Auswahl der einzelnen Funktionen, ohne das gesamtprogramm ausführen zu müssen.

ACHTUNG: Bei ```runAll``` werden alle Tabellen zuvor gelöscht, um auch die Funktionalität der Tabellenerstellung aus den verschiedenen Objekten zu demonstrieren. ```runChoice```Beispiele setzen bereits initialisierte Tabellen voraus, um die Beispiel-Funktionen simpler zu gestalten.

## Datenbankverbindung

Das Framework beinhaltet ein Config-File in welchem die Daten der Datenbankanbindung für die eigene Datenbank angepasst werden müssen.

Die Datenbankverbindung erfolgt automatisch durch den Manager und wird bei Programmstart getestet.

## ToDos

Folgende Funktionen sind zum Zeitpunkt der Abgabe des Projekts noch nicht enthalten:
- Intelligente bzw. sinnvolle Änderungsverfolgung
- Locking von Datenbankabschnitten
