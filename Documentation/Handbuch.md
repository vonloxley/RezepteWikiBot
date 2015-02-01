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
java -jar RezepteWikiBot-1.0-full.jar run <scriptname>
```
Vor der ersten Benutzung muss eine Anmeldung am Wiki erfolgen:
``` 
java -jar RezepteWikiBot-1.0-full.jar login <Benutzername>
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
```java -jar RezepteWikiBot-1.0-full.jar run rs.wb``` getestet. Er wird vor jeder Änderung fragen,
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

