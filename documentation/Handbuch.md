% Handbuch: RezepteWikiBot

Installation
------------
Benötigt wird Oracles [Java][Java], mindestens in Version 7.

Eine weitere Installation ist nicht notwendig, das Ziparchiv kann in einem
beliebigen Verzeichnis entpackt werden. 

Aufruf
------
Der allgemeine Aufruf lautet:
```
java -jar rwb.jar run <scriptname>
```
Vor der ersten Benutzung muss eine Anmeldung am Wiki erfolgen:
``` 
java -jar rwb.jar login <Benutzername>
```
Nach dem Druck auf die Entertaste fragt das Programm nach dem Passwort zum
Benutzernamen. Mit einem erfolgreichen Login wird eine Datei ```logindat.rwb```
erzeugt, die ein sogenanntes Security-Token enthält. Das Passwort wird nicht
gespeichert.

Scripte
-------
Ein Script ist eine Datei mit Anweisungen für den Bot. Anweisungen unterteilen
sich in drei Klassen:

Generatoren

:    Suchen Seiten im Wiki und liefern sie an Blockbefehle.

Blockbefehle

:    Arbeiten mit den von Generatoren zurückgelieferten Seiten.

Einfache Befehle

:    Nehmen Einstellungen vor oder benötigen keine von Generatoren gelieferten
     Seiten.

Generatoren und Befehle setzen sich aus dem Namen des Befehls gefolgt von
keinem, einem, oder mehreren Parametern in runden Klammern, die durch Kommata
getrennt werden. Die Parameter können von Anführungszeichen
umschlossene Zeichenketten, Zahlen oder Seitenlinks sein. Befehle enden mit
einem Semikolon, auf Generatoren folgt ein Anweisungsblock in geschweiften
Klammern.

Einfache Befehle können überall im Script vorkommen, Blockbefehle nur im
Anweisungsblock von Generatoren. Generatoren können geschachtelt werden. In
diesem Fall arbeiten die in den inneren Generatoren geschriebenen Befehle mit
der Schnittmenge der generierten Seiten.

```
BefehlOhneParamter();
BefehlMitEinemParamter("Parameter");
BefehlMitDreiParametern("Parameter1", "Paramter2", "Parameter3");

GeneratorMitEinemParameter("Parameter") {
	BlockbefehlMitEinemParameter("Parameter1");
}
```

Generatoren
----------

### Pagelist
Eine Liste von Seiten. Erwartet beliebig viele Parameter. Jeder Parameter
stellt eine Seite dar, auf der die Blockbefehle arbeiten sollen. Anstelle von
Zeichenketten dürfen auch Seitenverweise in Wikisyntax übergeben werden.

```
pagelist("Seite1", "Seite2"){}
pagelist([[Seite1]], [[Seite2]]){}
```

### Recent
Letzt Änderungen. Erwartet einen Parameter mit der Anzahl der letzten
Änderungen, die betrachtet werden sollen. Die Anzahl der zurückgelieferten
Seiten ist kleiner oder gleich der Anzahl der letzten Änderungen.

```
recent(500){}
```

### Search
Führt eine Volltextsuche im Wiki durch und liefert alle Seiten, auf denen sich
ein Treffer befindet. Die Suche berücksichtigt keine Groß-\ /\ Kleinschreibung.
Erwartet einen Parameter.

```
search("Suchtext"){}
```

### Linksto
Liefert alle Seiten, die auf eine als Parameter übergebene Seite verweisen. 
Entspricht dem Versweis „Was zeigt hierhin“.

```
linksto("Gekörnte Brühe"){}
```

Blockbefehle
------------
### Putintocat
Fügt die Seiten einer Kategorie hinzu. Erwartet eine Kategorie als Parameter.

```
putintocat("Rezepte");
```

### Removefromcat
Entfernt die Seiten aus einer Kategorie. Erwartet eine Kategorie als Parameter.

```
removefromcat("Rezepte");
```

### Printtitle
Gibt die Seitentitel auf der Standardausgabe aus. Keine Parameter.

### Replace
Führt ein Suchen und Ersetzen auf den Seiten aus. Erwartet zwei oder drei Parameter: Suchtext, Erstzungstext und optional die maximale Anzahl von Ersetzung, die durchgeführt werden soll.

Der Suchtext kann ein [Regulärer Ausdruck][JavaRe] sein, der Ersetzungstext kann sich auf Subausdrücke im Suchtext beziehen. Im Gegensatz zu Java entfällt das Verdoppeln von Backslashes ```\```.

```
search("am Besten"){
        replace("am Besten", "am besten");
}

recent(10){
        replace("(\s)([0-9]+)[ ]*-[ ]*([0-9]+)(\s)", "$1$2–$3$4");
        replace("(\d+)\s*&nbsp;[º°]([CF])", "{{Grad|$1}}");
        replace("(\d+)\s*[º°]([CF])", "{{Grad|$1}}");
}
```

### Replaceifnotcontains
Führt ein Suchen und Ersetzen auf den Seiten aus. Erwartet drei oder vier Parameter:

1. Suchtext
2. Erstzungstext
3. Text, der nicht auf der Seite vorkommen darf.
4. Maximale Anzahl von Ersetzung, die durchgeführt werden soll.


Der Suchtext kann ein [Regulärer Ausdruck][JavaRe] sein, der Ersetzungstext kann sich auf Subausdrücke im Suchtext beziehen. Im Gegensatz zu Java entfällt das Verdoppeln von Backslashes ```\```.


Einfache Befehle
-------
### Summary
Setzt die Logmeldung für die nächsten Befehle. Erwartet eine Zeichenkette als Parameter.

```
summary("Bot: Ändere Seiten.");
```

### Login
Wird momentan nicht benutzt.

### Setallgo
Ab diesem Befehl werden Änderungen ohne Nachfrage gespeichert. Hat keinen
Parameter.

```
setallgo();
```

### Commit
Normalerweise werden alle Änderungen gecached und erst am Ende des Scripts ins
Wiki geschrieben. Dieser Befehl ohne Parameter schreibt die Änderungen zwischendurch.

```
commit();
```

### Commitcompressed
Normalerweise wird pro Blockbefehl eine Änderung ins Wiki geschrieben. Der Befehl
```commitcompressed();``` fasst alle Änderungen an einer Seite zusammen und schreibt
sie mit dem Kommentar der letzten Änderung ins Wiki.

```
commitcompressed();
```

### Removeifinsubcat
Ein Parameter: Kategorie. Entfernt alle Seiten aus der übergebenen Kategorie,
falls sie sich auch in einer Unterkategorie befinden.

```
removeifinsubcat("Kategorie:Rezepte");
```

### Renamecat
Zwei Parameter: Von-Kategorie, Zu-Kategorie. Benennt eine Kategorie um.

### Addexclude
Erweitert die Liste von ignorierten Seitenbestandteilen um ein weiteres Element.
Alle folgenden Ersetzungsbefehle werden nicht innerhalb des Excludes durchgeführt.
Zwei Parameter: Beginn und Ende des auszuschließenden Bereichs

Beispiel: Keine Ersetzungen innerhalb der „Gallery“-Umgebung vornehmen.

```
addexclude("<gallery", "</gallery>");
```


### Resetexcludes
Setzt die auszuschließenden Seitenbestandteile auf den Standardwert zurück. 
Keine Parameter.

```
resetexcludes();
```


Ein Beispiel
------------
Ein Bot soll beliebte Rechtschreibfehler korrigieren. Dazu wird die Datei
```rs.rw``` angelegt und editiert.

Als erstes bekommt der Bot eine aussagekräftige Meldung:

```
summary("Bot: Korrigiere häufige Rechtschreibfehler.");

```

Anschließend wird ein Generator geschrieben, der alle Seiten mit dem zu behandelnden
Fehler sucht:

```
summary("Bot: Korrigiere häufige Rechtschreibfehler.");

search("Priese"){
}

```

Im Generator wird der Fehler mit ```replace``` berichtigt.

```
summary("Bot: Korrigiere häufige Rechtschreibfehler.");

search("Priese"){
    replace("Priese", "Prise");
}
```

Ein weiterer Fehler soll korrigiert werden:

```
summary("Bot: Korrigiere häufige Rechtschreibfehler.");

search("Priese"){
    replace("Priese", "Prise");
}

search("am Besten"){
    replace("am Besten", "am besten");
}
```

Zum Schluss werden alle Änderungen auf einmal geschrieben.

```
summary("Bot: Korrigiere häufige Rechtschreibfehler.");

search("Priese"){
    replace("Priese", "Prise");
}

search("am Besten"){
    replace("am Besten", "am besten");
}

commitcompressed();
```

Der Bot wird an der Kommandozeile mit dem Befehl
```java -jar rwb.jar run rs.wb``` getestet. Er wird vor jeder Änderung fragen, 
ob sie durchgeführt werden soll. Funktioniert er zufriedenstellend, 
wird mit ```setallgo();``` festgelegt, dass Änderungen automatisch im Wiki eingetragen 
werden.

```
summary("Bot: Korrigiere häufige Rechtschreibfehler.");
setallgo();

search("Priese"){
    replace("Priese", "Prise");
}

search("am Besten"){
    replace("am Besten", "am besten");
}

commitcompressed();
```



[Java]: http://www.java.com
[JavaRe]: http://www.straub.as/java/regex/regex.html
