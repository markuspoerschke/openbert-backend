---
description: |
  Spezifikation einer einheitlichen Schnittstelle zum Abruf von
  maschinenlesbaren Informationen aus Ratsinformationssystemen.
detailed-version: 'v1.1'
language: 'de-DE'
rights: 'CC BY-SA 4.0'
title: 'OParl-Spezifikation 1.1'
toc-title: Inhaltsverzeichnis
---

- [Einleitung](#einleitung)
  - [Was ist OParl?](#was_ist_oparl)
  - [Zielsetzung von OParl](#zielsetzung-von-oparl)
  - [Transparenz und Beteiligung durch Open
    Data](#transparenz-und-beteiligung-durch-open-data)
  - [Nutzungsszenarien](#nutzungsszenarien)
  - [Nomenklatur](#nomenklatur)
  - [Datenschutz](#datenschutz)
  - [OParl Governance](#oparl-governance)
  - [Autoren](#oparl-autoren)
- [Prinzipien und Funktionen der
  Schnittstelle](#prinzipien-und-funktionen-der-schnittstelle)
  - [Designprinzipien](#designprinzipien)
  - [Zukunftssicherheit](#zukunftssicherheit)
  - [URLs](#urls)
  - [JSON-Ausgabe](#json-ausgabe)
  - [Objektlisten und Paginierung](#objektlisten-und-paginierung)
  - [Cross-Origin Resource Sharing (CORS)](#cors)
  - [Dateizugriffe](#dateizugriff)
  - [GelÃ¶schte Objekte](#geloeschte-objekte)
  - [Ausnahmebehandlung](#ausnahmebehandlung)
  - [OParl Endpunkt](#oparl-endpunkt)
- [Schema](#schema)
  - [Die Objekte](#objekttypen)
  - [Ãœbergreifende Aspekte](#uebergreifende-aspekte)
  - [Eigenschaften mit Verwendung in mehreren
    Objekttypen](#eigenschaften-mit-verwendung-in-mehreren-objekttypen)
  - [System](#entity-system)
  - [Body](#entity-body)
  - [LegislativeTerm](#entity-legislativeterm)
  - [Organization](#entity-organization)
  - [Person](#entity-person)
  - [Membership](#entity-membership)
  - [Meeting](#entity-meeting)
  - [AgendaItem](#entity-agendaitem)
  - [Paper](#entity-paper)
  - [Consultation](#entity-consultation)
  - [File](#entity-file)
  - [Location](#entity-location)
- [Ã„nderungen und Migration](#Ã¤nderungen-und-migration)
  - [OParl 1.1](#oparl-1-1)

# Einleitung

Dieses Dokument enthÃ¤lt die Spezifikation des OParl
Schnittstellen-Standards fÃ¼r parlamentarische Informationssysteme[^1].
Es dient damit als Grundlage fÃ¼r die Implementierung von OParl-konformen
Server- und Clientanwendungen.

## Was ist OParl? {#was_ist_oparl}

OParl ist die Gruppierung, die Initiator und Herausgeber der
vorliegenden Spezifikation ist. An OParl wirken VerbÃ¤nde,
zivilgesellschaftliche Organisationen und Initiativen und
Softwareanbieter sowie interessierte Einzelpersonen mit.

Die vorliegende Spezifikation beschreibt den OParl-Standard. Dieser
definiert eine Webserviceschnittstelle, die den anonymen, lesenden
Zugriff auf Ã¶ffentliche Inhalte aus parlamentarischen
Informationssystemen ermÃ¶glicht. Wie der Name "Webservice" ausdrÃ¼ckt,
setzt diese Schnittstelle auf dem World Wide Web auf. Sie ermÃ¶glicht,
dass parlamentarische Informationen maschinenlesbar als offene Daten
(Open Data) verÃ¶ffentlicht werden.

Die vorliegende Version ist die erste verabschiedete Version der
Spezifikation des OParl-Standards.

## Zielsetzung von OParl

OParl richtet sich an verschiedene Nutzergruppen und Stakeholder:

- Verwaltungen und andere politische Gremien in GebietskÃ¶rperschaften
- BÃ¼rger, politische Parteien und Organisationen
- Open-Data-Initiativen
- Wissenschaftler
- Anbieter von Server- und Softwareprodukten im Umfeld von
  parlamentarischen Informationssystemen und
  Ã–ffentlichkeitsbeteiligung

Die GrÃ¼nde, warum Betreiber von parlamentarischen Informationssystemen
den Zugriff darauf Ã¼ber eine standardisierte Schnittstelle ermÃ¶glichen
sollten oder mÃ¶chten, kÃ¶nnen vielfÃ¤ltig und je nach Nutzergruppe
unterschiedlich sein.

Ein zentrales Argument fÃ¼r Verwaltung und politische Gremien, sei es in
GebietskÃ¶rperschaften oder auf Landes- oder Bundesebene, ist die
Verpflichtung der Parlamente gegenÃ¼ber der BevÃ¶lkerung, diese Ã¼ber die
Fortschritte der parlamentarischen Arbeit zu informieren und auf dem
Laufenden zu halten. Ein erster Schritt, der BevÃ¶lkerung Einblicke in
die Arbeit und Zugriff auf Dokumente zu gewÃ¤hren, ist vielerorts in den
letzten Jahren durch EinfÃ¼hrung von Ratsinformationssystemen mit
anonymem, lesendem Zugriff Ã¼ber das World Wide Web gemacht worden.

Die damit eingeschlagene Richtung konsequent weiter zu gehen, bedeutet,
die Daten der parlamentarischen Informationssysteme soweit offen zu
legen, wie die Inhalte es erlauben. Es bedeutet, die Daten und Inhalte
so universell weiterverwendbar und so barrierearm wie mÃ¶glich
anzubieten, dass jegliche weitere Verwendung durch Dritte technisch
mÃ¶glich ist. Der seit einiger Zeit etablierte Begriff fÃ¼r dieses Prinzip
heiÃŸt "Open Data".

Open-Data-Initiativen kÃ¶nnen unter RÃ¼ckgriff auf Ratsinformationssysteme
(RIS) mit OParl-Schnittstelle einfacher Dokumente und Daten aus
unterschiedlichen GebietskÃ¶rperschaften in Open-Data-Katalogen
verzeichnen und so einfacher auffindbar machen fÃ¼r die Weiterverwendung
durch Dritte.

BÃ¼rgerinnen und BÃ¼rger, politische Parteien und zivilgesellschaftliche
Organisationen kÃ¶nnen einfacher auf Inhalte parlamentarischer
Informationssysteme zugreifen und diese entsprechend ihren Interessen
aufbereiten. Dies kÃ¶nnen beispielsweise Visualisierungen von enthaltenen
Daten, die Anreicherung von Informationsangeboten fÃ¼r spezielle
Nutzergruppen oder die Schaffung von BenutzeroberflÃ¤chen mit besonderen
Funktionen fÃ¼r verschiedene EndgerÃ¤te sein.

Das Interesse an parlamentarischen Informationen und an Anwendungen, die
diese nutzbar und auswertbar machen, ist offensichtlich vorhanden. Die
Entwickler der alternativen Ratsinformationssysteme wie Frankfurt
Gestalten[^2], Offenes KÃ¶ln oder der OpenRuhr:RIS-Instanzen (die letzten
beiden wurden zusammengefÃ¼hrt in Politik Bei Uns[^3]) wissen zu
berichten, wie viel Interesse den Projekten gerade aus Orten entgegen
gebracht wird, in denen derartige Systeme noch nicht verfÃ¼gbar sind.

Die AnwendungsmÃ¶glichkeiten fÃ¼r parlamentarische Informationen, wenn sie
Ã¼ber eine Schnittstelle schnell und einfach abgerufen werden kÃ¶nnen,
sind vielfÃ¤ltig. Beispiele sind:

- Apps fÃ¼r den Abruf auf mobilen EndgerÃ¤ten
- MÃ¶glichkeiten zur Wiedergabe fÃ¼r Nutzerinnen und Nutzer mit
  BeeintrÃ¤chtigung des SehvermÃ¶gens
- Alternative und erweiterte SuchmÃ¶glichkeiten in Inhalten
- Auswertung und Analyse von Themen, Inhalten, Sprache etc.
- Benachrichtigungsfunktionen beim Erscheinen bestimmter Inhalte

Die Standardisierung dieses Zugriffs Ã¼ber die Grenzen einzelner Systeme
hinweg erlaubt zudem, diese Entwicklungen auch geographisch und
politisch grenzÃ¼berschreitend zu denken. Damit steigt nicht nur die
potenzielle Nutzerschaft einzelner Entwicklungen. Auch das Potenzial fÃ¼r
Kooperationen zwischen Anwendungsentwicklern wÃ¤chst.

FÃ¼r Wissenschaftler, die z. B. an vergleichenden Untersuchungen zu
VorgÃ¤ngen in verschiedenen GebietskÃ¶rperschaften interessiert sind,
ergeben sich ebenso vielfÃ¤ltige MÃ¶glichkeiten Ã¼ber mehrere RIS-Instanzen
hinweg auf entsprechende Informationen zuzugreifen und diese so
einfacher in ihre Analysen einzubeziehen.

DarÃ¼ber hinaus sind auch Motivationen innerhalb von Organisationen und
KÃ¶rperschaften erkennbar. So sollen parlamentarische Informationssysteme
vielerorts in verschiedenste Prozesse und heterogene Systemlandschaften
integriert werden. Durch eine einheitliche Schnittstelle bieten sich
effiziente MÃ¶glichkeiten zur Integration der Daten in anderen Systeme,
wie beispielsweise Webportale.

Anbieter von Softwareprodukten, die RIS-LÃ¶sungen anbieten, kÃ¶nnen ihren
Kunden mit der Implementation der OParl-Schnittstelle eine entsprechende
einheitliche Schnittstelle anbieten.

AusfÃ¼hrlichere Beschreibungen einiger mÃ¶glicher Anwendungsszenarien
finden sich im Kapitel [Nutzungsszenarien](#nutzungsszenarien).

## Transparenz und Beteiligung durch Open Data

Ã–ffentliche Stellen verfÃ¼gen Ã¼ber vielfÃ¤ltige Informationen und Daten.
Seit einigen Jahren sind zivilgesellschaftliche Organisationen sowie
Politik und Verwaltung unter dem Schlagwort _Open Data_ international
und auch in Deutschland um eine stÃ¤rkere Ã–ffnung dieser Daten
bemÃ¼ht[^4]. Bei dem Ansatz Open Data[^5] geht es darum, diese Daten so
bereitzustellen, dass Dritte diese einfacher finden und weiterverwenden
kÃ¶nnen.

Die zehn Open-Data-Prinzipien der Sunlight-Foundation[^6] beschreiben
die Offenheit von DatensÃ¤tzen. Wesentlich dabei sind vor allem die
einfache rechtliche und die technische Offenheit. Bei ersterer geht es
darum, dass DatensÃ¤tze unter Nutzungsbestimmungen bereitgestellt werden,
die kurz und verstÃ¤ndlich formuliert sind und mindestens jegliche
weitere Verwendung inklusive der kommerziellen erlauben, unter der
Voraussetzung, dass bei der Weiterverwendung die Quelle benannt wird.
Bei der technischen Offenheit steht die Bereitstellung von DatensÃ¤tzen
in mÃ¶glichst maschinenlesbaren Formaten im Vordergrund. Dies bedeutet,
stÃ¤rker strukturierte DatensÃ¤tze sind in der Bereitstellung zu
bevorzugen. Liegen Daten innerhalb einer Organisation in einer Datenbank
vor, so bietet es sich an, diese Ã¼ber eine Programmierschnittstelle
(API) fÃ¼r AuÃŸenstehende bereitzustellen.

Die ErfÃ¼llung dieser rechtlichen und technischen Offenheit erlaubt es im
Falle von OParl Dritten -- dies kÃ¶nnen BÃ¼rgerinnen und BÃ¼rger,
Unternehmen, Forschungseinrichtungen oder auch andere
Verwaltungseinheiten sein -- die Verwaltungsdaten wesentlich
unkomplizierter fÃ¼r eigene Vorhaben wie Anwendungen oder
Visualisierungen einzusetzen. Mit dem Ansatz offener Verwaltungsdaten
soll so erstens mehr Transparenz Ã¼ber Prozesse und Entscheidungen in
Politik und Verwaltung erreicht werden. Zweitens kÃ¶nnen Dritte auf
Grundlage dieser Daten leichter eigene GeschÃ¤ftsmodelle verfeinern oder
neue entwickeln. Drittens wird es auch Ã¶ffentlichen Stellen selbst
erleichtert bereits im Ã¶ffentlichen Sektor existierende Daten zu finden
und weiterzuverwenden.

Das Prinzip offener Daten bzw. offener Verwaltungsdaten Ã¼ber die
Minimalprinzipien rechtlicher und technischer Offenheit hinaus in die
Tat umzusetzen, erfordert im Einzelfall hÃ¤ufig eine Zusammenarbeit von
Datenbereitstellern und potentiellen Datennutzern. Die bloÃŸe
Bereitstellung einer OParl-konformen API wird weder die Einhaltung der
technischen Prinzipien, noch der weiteren Open-Data-Prinzipien
vollstÃ¤ndig garantieren. Viele Bestandteile der OParl-Spezifikation, die
einen weitgehend barrierearmen Zugang zu Informationen[^7] ermÃ¶glichen
sollen, sind in der vorliegenden Version noch optional (Beispiel:
Volltexte von Dokumenten Ã¼ber die API abrufbar machen). Andere
Bestandteile, die von Interesse wÃ¤ren, sind noch gar nicht von OParl
abgedeckt (Beispiel: Abstimmungsergebnisse). Grund dafÃ¼r ist, dass sich
OParl in einem frÃ¼hen Stadium befindet und primÃ¤r am Status Quo der
parlamentarischen Informationssysteme ausgerichtet ist. Es liegt also
auch weiterhin an Verwaltung und Politik, durch einen
verantwortungsvollen Umgang mit den Systemen die maximal erreichbare
Transparenz zu bieten. Das fÃ¤ngt bei verfÃ¼gbaren Dokumentformaten an
(ein PDF mit digitalem Text weist weit weniger Barrieren auf, als ein
gescannter Brief, der ebenfalls als PDF gespeichert wurde) und hÃ¶rt bei
der verwendeten Sprache auf[^8].

## Nutzungsszenarien

FÃ¼r OParl sind verschiedene Nutzungsszenarien denkbar. Die Nachfolgende
Auflistung soll einen kleinen Ãœberblick geben, erhebt aber bei weitem
keinen Anspruch auf VollstÃ¤ndigkeit:

### Mobile Anwendung {#szenario-mobile-anwendung}

Eine Anwendung fÃ¼r mobile EndgerÃ¤te wie Smartphones und Tablets,
nachfolgend "App" genannt, kÃ¶nnte das Ziel verfolgen, Nutzern unterwegs,
sowie abseits vom Desktop-PC optimierten Zugriff auf ein
parlamentarisches Informationssystem zu ermÃ¶glichen. Dies kÃ¶nnte auch
zur Vereinfachung der bisherigen Prozesse beitragen, da Nutzerinnen z.B.
die MÃ¶glichkeit gegeben werden kann, auf Einladungen zu reagieren, oder
Protokolle zu lesen.

### Integration in ein Webportal {#szenario-webportal}

PortallÃ¶sungen bieten den Betreibern die MÃ¶glichkeit, Inhalte auf einer
einheitlichen WeboberflÃ¤che zu verÃ¶ffentlichen, die aus verschiedensten
Quellen und Plattformen bereitgestellt werden. Ein Beispiel fÃ¼r die
Realisierung eines solchen Integrations-Ansatzes wÃ¤re eine Kommune, die
fÃ¼r ihre allgemeine Website eine PortallÃ¶sung einsetzt und hier auch
Inhalte aus dem kommunalen Ratsinformationssystem einspeisen und
darstellen mÃ¶chte. Vorteil einer solchen Einbindung, also der
kontextbezogenen Darstellung von parlamentarischen Informationen im
Gegensatz zu einem monolithischen parlamentarischen Informationssystem
kÃ¶nnte sein, dass Nutzer in einer gewohnten und akzeptierten OberflÃ¤che
jeweils die relevanten Informationen erhalten, ohne sich an die
ungewohnte Umgebung eines parlamentarischen Informationssystems gewÃ¶hnen
zu mÃ¼ssen.

### Meta-Suche {#szenario-meta-suche}

Die ErmÃ¶glichung einer nutzerfreundlichen Suche, die damit verbundene
Indexierung von verschiedensten Dokumenteninhalten und die
Kategorisierung von Inhalten kann eine sowohl konzeptionell als auch
technisch anspruchsvolle Aufgabe sein. Angelehnt an das seit den
AnfÃ¤ngen des Webs etablierte Modell der externen Web-Suchmaschine sind
spezielle Suchmaschinen fÃ¼r OParl-konforme parlamentarische
Informationssysteme denkbar. Solche Plattformen treten gegenÃ¼ber dem
OParl-Server als Client auf und rufen bestimmte oder sÃ¤mtliche
Informationen, die das System bereithÃ¤lt, ab. Vorbild sind die Robots
oder Spider von Web-Suchmaschinen. Die abgerufenen Informationen kÃ¶nnen
dann indexiert und je nach Anforderungen fÃ¼r eine gezielte Suche
weiterverarbeitet werden.

### Forschungsprojekt "Themenanalyse" {#szenario-forschung}

In einem Forschungsprojekt sollen Pro- und Contra-Argumentationen bei
Ratsdiskussionen zum Ausbau von Stromtrassen identifiziert werden. Dazu
nutzen die Mitarbeitenden des Forschungsprojektes die
OParl-Schnittstellen der parlamentarischen Informationssysteme aller
Kommunen entlang der geplanten Ã¼berregionalen Trassen. Ãœber diese
einheitlichen Schnittstellen kÃ¶nnen sie insbesondere die relevanten
Wortprotokolle abrufen und zum Beispiel in einem Werkzeug zur
qualitativen Datenanalyse lokal verarbeiten.

## Nomenklatur

### Zwingende, empfohlene und optionale Anforderungen {#muss_sollte_darf}

Diese Spezifikation nutzt **mÃ¼ssen**, **kÃ¶nnen** und **sollten** in
einer eindeutig definierten Art und Weise. Diese ist angelehnt an die
Definitionen der Begriffe MUST, SHOULD und MAY (bzw. MUST NOT, SHOULD
NOT und MAY NOT) aus RFC2119.[^9]

Die Bedeutung im Einzelnen:

**mÃ¼ssen**/**muss** bzw. **zwingend**:

: Die ErfÃ¼llung einer so gekennzeichneten Anforderung ist zwingend
erforderlich.

    Die Entsprechung in RFC2119 lautet "MUST", "REQUIRED" oder "SHALL".

**nicht dÃ¼rfen**/**darf nicht**:

: Dieses Stichwort kennzeichnet ein absolutes Verbot.

    Die Entsprechung in RFC2119 lautet "MUST NOT" oder "SHALL NOT".

**sollten**/**sollte** bzw. **empfohlen**:

: Mit dem Wort **sollten** bzw. **sollte** sind empfohlene
Anforderungen gekennzeichnet, die von jeder Implementierung erfÃ¼llt
werden sollten. Eine NichterfÃ¼llung ist als Nachteil zu verstehen,
beispielsweise weil die Nutzerfreundlichkeit dadurch EinbuÃŸen
erleidet, und sollte daher sorgfÃ¤ltig abgewogen werden.

    Die Entsprechung in RFC2119 lautet "SHOULD" oder "RECOMMENDED".

**sollten nicht**/**sollte nicht** bzw. **nicht empfohlen**:

: Diese Formulierung wird verwendet, wenn unter gewissen UmstÃ¤nden
GrÃ¼nde existieren kÃ¶nnen, die ein bestimmtes Verhalten akzeptabel
oder sogar nÃ¼tzlich erscheinen lassen, jedoch die Auswirkung des
Verhaltens vor einer entsprechenden Implementierung verstanden und
abgewogen werden sollten.

    Die Entsprechung in RFC2119 lautet "SHOULD NOT" oder "NOT
    RECOMMENDED".

**dÃ¼rfen**/**darf** bzw. **optional**:

: Mit dem Wort **dÃ¼rfen** bzw. **darf** oder **optional** sind
optionale Bestandteile gekennzeichnet. Ein Anbieter kÃ¶nnte sich
entscheiden, den entsprechenden Bestandteil aufgrund besonderer
Kundenanforderungen zu unterstÃ¼tzen, wÃ¤hrend andere diesen
Bestandteil ignorieren kÃ¶nnten. Implementierer von Clients oder
Servern **dÃ¼rfen** in solchen FÃ¤llen **nicht** davon ausgehen, dass
der jeweilige Kommunikationspartner den entsprechenden, optionalen
Anteil unterstÃ¼tzt.

    Die Entsprechung in RFC2119 lautet "MAY".

### Geschlechterspezifische Begrifflichkeiten

Um bei Begriffen wie Nutzer, Anwender, Betreiber etc. die sonst Ã¼bliche
Dominanz der mÃ¤nnlichen Variante zu vermeiden, werden in diesem Dokument
mÃ¤nnliche und weibliche Varianten gemischt. Gemeint sind in allen FÃ¤llen
Personen jeglichen Geschlechts.

### Codebeispiele

Die in diesem Dokument aufgefÃ¼hrten Codebeispiele dienen der
Veranschaulichung der beschriebenen Prinzipien. Es handelt sich um frei
erfundene Daten.

Codebeispiele erheben insbesondere bei JSON-Code nicht den Anspruch auf
syntaktische Korrektheit und VollstÃ¤ndigkeit. Dementsprechend kÃ¶nnen in
Codebeispielen Auslassungen vorkommen, die mit `...` gekennzeichnet
werden.

### Namespace-PrÃ¤fixe fÃ¼r Objekt- und Datentypen {#namespace-praefixe-fuer-objekt-und-datentypen}

Bei der ErwÃ¤hnung von Objekttypen, die in dieser Spezifikation
beschrieben werden, wird in der Regel ein PrÃ¤fix `oparl:` vor den Namen
gesetzt, z. B. "oparl:Organization". Damit soll verdeutlicht werden,
dass der Objekttyp innerhalb der OParl-Spezifikation gemeint ist.

Das PrÃ¤fix `oparl:` steht hierbei fÃ¼r die folgende Namespace-URL:

    https://schema.oparl.org/1.1/

Dadurch kann eine Typenangabe wie `oparl:Organization` eindeutig in die
folgende URL Ã¼bersetzt werden:

    https://schema.oparl.org/1.1/Organization

## Datenschutz

GemÃ¤ÃŸ der Grundlage "Ã¶ffentliche Daten nutzen, private Daten schÃ¼tzen"
hat Datenschutz auch bei OParl eine hohe PrioritÃ¤t. Hierbei ist die
deutsche Datenschutz-Gesetzgebung zu beachten.

Um personenbezogene Daten zu verÃ¶ffentlichen, ist Ã¼blicherweise eine
explizite Zustimmung der betroffenen Person erforderlich. Dies gilt fÃ¼r
die bestehende WeboberflÃ¤che des Ratsinformationssystems ebenso wie fÃ¼r
die OParl- Schnittstelle. Besonders zu beachten sind hierbei unter
anderem E-Mail- Adressen, Anschriften, Fotos und Anwesenheitslisten. Es
wird empfohlen, vor VerÃ¶ffentlichung Ã¼ber die Schnittstelle den
zustÃ¤ndigen Datenschutzbeauftragten zu kontaktieren.

## OParl Governance

Im Verlauf der Weiterentwicklung kÃ¶nnen wie bei jedem
Standardisierungsprozess Konflikte Ã¼ber die Ausrichtung und die
Implementierung entstehen. Ist dies der Fall, so sollte als erstes der
[Issue Tracker auf Github](https://github.com/OParl/spec/issues/) fÃ¼r
eine offene Diskussion und eine konstruktive LÃ¶sung verwendet werden.

Sollte es auf Github wider Erwarten keine LÃ¶sung geben, wird die
Entscheidung an das OParl-Schlichtungsgremium weitergegeben. In diesem
Gremium vertreten sind Entwickler, Anwender und Datenbereitsteller, so
dass eine ausgewogene Weiterentwicklung im Interesse aller Akteure
gewahrt bleibt.

Es ist natÃ¼rlich unabhÃ¤ngig davon jederzeit erlaubt, einen Fork der
OParl-Schnittstelle zu erstellen und dort neue zunÃ¤chst nicht
mehrheitsfÃ¤hige Konzepte, Features und Funktionen auszuprobieren.

## Autoren {#oparl-autoren}

### Kernteam

Stefan Graupner, Ernesto Ruge, Konstantin SchÃ¼tze

### OParl 1.0

Folgende Personen haben an OParl 1.0 mitgewirkt:

Jayan Areekadan, Jan Erhardt, Lucas Jacob, Jens Klessmann (\*), Andreas
Kuckartz (\*\*), Babett Schalitz, Tim Scheuermann, Christine Siegfried
(\*), Ralf Sternberg, Marian Steinbach (\*), Bernd Thiem, Thomas
Tursics, Jakob Voss, Marianne Wulff(\*)

### OParl 1.1

Folgende Personen haben an OParl 1.1 mitgewirkt:

grindhold, Simeon Maxein, Sami Mussbach, Ralf Sternberg

(\*): Initiator(in), (\*\*): bis 4.7.2014

# Prinzipien und Funktionen der Schnittstelle

## Designprinzipien

### Aufbauen auf gÃ¤ngiger Praxis {#aufbauen-auf-gaengiger-praxis}

Grundlage fÃ¼r die Erarbeitung der OParl-Spezifikation in der
vorliegenden Version ist eine Analyse von aktuell (2012 bis 2016) in
Deutschland etablierten parlamentarischen Informationssystemen und ihrer
Nutzung. ErklÃ¤rtes Ziel fÃ¼r diese erste Version ist es, mit mÃ¶glichst
geringem Entwicklungsaufwand auf Seite der Softwareanbieter und ebenso
geringem Migrationsaufwand auf Seite der Betreiber zu einer
Bereitstellung von parlamentarischen Informationen Ã¼ber eine OParl-API
zu gelangen. Hierbei war es von entscheidender Bedeutung, dass sich die
Informationsmodelle der einschlÃ¤gigen Softwareprodukte stark Ã¤hneln. FÃ¼r
die OParl-Spezifikation wurde sozusagen ein Datenmodell als "gemeinsamer
Nenner" auf Basis der gÃ¤ngigen Praxis konstruiert.

### Verbesserung gegenÃ¼ber dem Status Quo wo mÃ¶glich {#verbesserung-gegenueber-status-quo}

Dort, wo es dem Ziel der einfachen Implementierbarkeit und der einfachen
Migration nicht im Weg steht, erlauben sich die Autoren dieser
Spezifikation, auch Funktionen aufzunehmen, die noch nicht als gÃ¤ngige
Praxis im Bereich der Ratsinformationssysteme bezeichnet werden kÃ¶nnen
oder welche nur von einzelnen Systemen unterstÃ¼tzt werden. Solche
Funktionen sind dann so integriert, dass sie nicht als zwingende
Anforderung gelten.

Ein Beispiel fÃ¼r eine derartige Funktion ist die Abbildung von Geodaten
im Kontext von Drucksachen (`oparl:Paper`), um beispielsweise die Lage
eines Bauvorhabens, das in einer Beschlussvorlage behandelt wird, zu
beschreiben. Zwar ist den Autoren nur ein einziges parlamentarisches
Informationssystem[^10] in Deutschland bekannt, das Geoinformationen --
und zwar in Form von Punktdaten, also einer Kombination aus LÃ¤ngen- und
Breitengradangaben -- mit Dokumenten verknÃ¼pft. Der Vorteil dieser
Funktion ist jedoch anhand zahlreicher Anwendungsszenarien, wie z.B. dem
Bauinformationssystem "BÃ¼rger baut Stadt"[^11], belegbar. Somit ist in
der vorliegenden OParl-Spezifikation die MÃ¶glichkeit beschrieben,
Geodaten-Objekte einzubetten.

Die Angabe eines einzelnen Punktes ist dabei der einfachste Fall. Die
Spezifikation erlaubt auch die Kodierung von mehreren Objekten, die
Punkte, Linien oder Polygone reprÃ¤sentieren kÃ¶nnen. Vgl. dazu
`oparl:Location`.

Auch die Ausgabe einer Nur-Text-Version im Kontext der Datei
(`oparl:File`), das den barrierefreien Zugriff auf Inhalte oder
Indexierung fÃ¼r Volltextsuchfunktionen deutlich vereinfacht, ist eine
MÃ¶glichkeit, die in der gÃ¤ngigen Praxis noch nicht zu finden ist. Ebenso
die MÃ¶glichkeit, Beziehungen zwischen einzelnen Dateien herzustellen, um
so z.B. von einer Datei zu anderen Dateien mit identischem Inhalt, aber
in anderen technischen Formaten zu verweisen, etwa von einer ODT-Datei
zu einer PDF-Version.

### SelbstbeschreibungsfÃ¤higkeit {#selbstbeschreibungsfaehigkeit}

Ausgaben des Servers sollten so beschaffen sein, dass sie fÃ¼r
menschliche Nutzerinnen weitgehend selbsterklÃ¤rend sein kÃ¶nnen. Dies
betrifft besonders die Benennung von Objekten und Objekteigenschaften.

Um den Kreis der Entwicklerinnen und Entwickler, die mit einer OParl-API
arbeiten kÃ¶nnen, nicht unnÃ¶tig einzuschrÃ¤nken, wird hierbei
grundsÃ¤tzlich und soweit sinnvoll auf englischsprachige
Begrifflichkeiten gesetzt.

### Erweiterbarkeit

Implementierer sollen in der Lage sein, Ã¼ber eine OParl-konforme
Schnittstelle auch solche Informationen auszugeben, die nicht im Rahmen
des OParl-Schemas abgebildet werden kÃ¶nnen. Dies bedeutet zum einen,
dass ein System Objekttypen unterstÃ¼tzen und ausliefern darf, die nicht
(oder noch nicht) im OParl-Schema beschrieben sind. Das bedeutet auch,
dass Objekttypen so um eigene Eigenschaften erweitert werden kÃ¶nnen, die
nicht im OParl Schema beschrieben sind.

Ein weiterer Aspekt betrifft die AbwÃ¤rtskompatibilitÃ¤t, also die
KompatibilitÃ¤t von OParl-Clients mit zukÃ¼nftigen Schnittstellen. So
kÃ¶nnen beispielsweise zukÃ¼nftige Erweiterungen des OParl-Schemas, etwa
um neue Objekttypen, genauso durchgefÃ¼hrt werden, wie die Erweiterungen
um herstellerspezifische Objekttypen. Ein Client muss diese Anteile
nicht auswerten, sofern sie nicht fÃ¼r die Aufgabe des Clients relevant
sind. Es bedeutet im Umkehrschluss allerdings auch, dass ein Client
nicht fehlschlagen darf, falls derartige Erweiterungen vorhanden sind.

### Browseability/Verlinkung {#browseability_verlinkung}

Klassische Webservice-Schnittstellen erfordern von den Entwicklern
vollstÃ¤ndige Kenntnis der angebotenen Einstiegspunkte und
Zugriffsmethoden, gepaart mit sÃ¤mtlichen unterstÃ¼tzten URL-Parametern,
um den vollen Funktionsumfang der Schnittstelle ausschÃ¶pfen zu kÃ¶nnen.

Parlamentarische Informationen sind weitgehend in Form von Graphen
aufgebaut. Das bedeutet, dass Objekte hÃ¤ufig mit einer Vielzahl anderer
Objekte verknÃ¼pft sind. So ist eine Person beispielsweise Mitglied in
mehreren Gremien, das Gremium hat mehrere Sitzungen abgehalten und zu
diesen Sitzungen gibt es jeweils zahlreiche Drucksachen, die ihrerseits
wieder zahlreiche Dokumente enthalten.

Eine OParl-Schnittstelle gibt jedem einzelnen Objekt eine eindeutige
Adresse, eine URL. Somit kann die Schnittstelle den Verweis von einem
Objekt, beispielsweise einem Gremium, auf ein anderes Objekt, etwa ein
Mitglied des Gremiums, dadurch ausgeben, dass im Kontext des Gremiums
die URL des Mitglieds ausgeben wird. Der Client kann somit ausgehend von
einem bestimmten Objekt die zugehÃ¶rigen Objekte im System finden, indem
er einfach den angebotenen URLs folgt. Dieses Prinzip wird auch "Follow
Your Nose"[^12] genannt.

## Zukunftssicherheit

Sollte in Zukunft eine zu OParl 1.0 inkompatible Version 2.0 erscheinen,
kann ein Server beide Versionen gleichzeitig unterstÃ¼tzen, um mit OParl
1.0 Clients kompatibel zu bleiben. Dazu muss der Server die OParl
2.0-Schnittstelle unter einer eigenen URL parallel zur bestehenden OParl
1.0-Schnittstelle anbieten, siehe Kapitel [System](#system).

## URLs

![Aufbau einer URL](build/src/images/url.png)

Den URLs (fÃ¼r _Uniform Resource Locators_) kommt eine besondere
Bedeutung zu und es werden deshalb eine Reihe von Anforderungen an deren
Aufbau und Eigenschaften gestellt. Die allgemeine Funktionsweise von
URLs ist in RFC 3986 beschrieben[^13].

GrundsÃ¤tzlich **mÃ¼ssen** alle Zugriffe zustandslos erfolgen kÃ¶nnen, also
ohne Sessioninformationen wie Cookies. Das bedeutet, dass alle
Informationen, die zum Abrufen eines Objekts nÃ¶tig sind, in der URL
vorhanden sein mÃ¼ssen.

### URL-Kanonisierung {#url_kanonisierung}

Um Objekte eindeutig identifizieren zu kÃ¶nnen ist es notwendig, dass ein
Server fÃ¼r ein Objekt genau eine unverÃ¤nderliche URL benutzt. Diese
Festlegung auf genaue eine eindeutige URL wird Kanonisierung genannt.
Ein Server **muss** deshalb fÃ¼r jedes seiner Objekte eine kanonische URL
bestimmen kÃ¶nnen.

Es wird empfohlen keine IP-Adressen in URLs zu benutzen, sondern einen
mit Bedacht gewÃ¤hlten Hostnamen einzusetzen. Das ist vor allem im
Hinblick auf die Langlebigkeit der URLs wichtig.

Um die Kanonisierung zu gewÃ¤hrleisten **sollten** OParl-Server so
konfiguriert werden, dass sie nur Ã¼ber eine bestimmte Domain erreichbar
sind. OParl-Server **sollten** dagegen mÃ¶glichst **nicht** nur Ã¼ber eine
IP-Addresse sowieso mÃ¶glichst auch **nicht** Ã¼ber weitere, nicht
kanonische URLs erreichbar sein.

Wenn ein Server auch durch eine nicht-kanonische URL erreichbar ist,
dann **sollte** eine entsprechende HTTP-Anfrage mit einer Weiterleitung
auf die entsprechende kanonische URL und HTTP-Status-Code 301
beantwortet werden. Zur ÃœberprÃ¼fung kann z.B. der `Host`-Header einer
HTTP-Anfrage verwendet werden.

Beim Pfad-Bestandteil der URL **mÃ¼ssen** Server-Implementierer darÃ¼ber
hinaus beachten, dass zur kanonischen Schreibweise auch die GroÃŸ- und
Kleinschreibung, die Anzahl von SchrÃ¤gstrichen als Pfad-Trennzeichen und
die Anzahl von fÃ¼hrenden Nullen vor numerischen URL-Bestandteilen
gehÃ¶rt.

Die Kanonisierung umfasst auch den Query-String-Bestandteil der URL. Wie
auch beim Pfad gilt, dass fÃ¼r jeden Parameter und jeden Wert im
Query-String genau eine kanonische Schreibweise gelten **muss**.

DarÃ¼ber hinaus **sollte** der Server-Implementierer darauf achten,
Query-String-Parameter immer nach demselben Prinzip zu sortieren. Als
Beispiel: Die beiden URLs

    https://oparl.example.org/members?body=1&committee=2
    https://oparl.example.org/members?committee=2&body=1

unterscheiden sich lediglich in der Reihenfolge der
Query-String-Parameter. Da sie jedoch nicht identisch sind, kÃ¶nnten
Clients annehmen, dass beide URLs verschiedene Objekte reprÃ¤sentieren.

Clients **sollen** die vom Server gelieferten URLs bei Anzeige,
Speicherung und Weiterverarbeitung nicht verÃ¤ndern.

### HTTP und HTTPS

Der Einsatz des verschlÃ¼sselten HTTPS wird empfohlen. Bei Verwendung von
HTTPS wird allen URLs "https://" voran gestellt, ansonsten beginnen URLs
mit "http://".

Aus GrÃ¼nden der URL-Kanonisierung ist es **zwingend** notwendig, dass
ein Server-Betreiber sich entweder fÃ¼r HTTP oder fÃ¼r HTTPS entscheidet.
Es jedoch mÃ¶glich, eine Weiterleitung (HTTP Status-Code 301)
einzurichten. Eine Weiterleitung von HTTPS auf HTTP wird **nicht
empfohlen**.

### Langlebigkeit {#url_langlebigkeit}

Weiterhin sollen URLs langlebig sein, sodass sie mÃ¶glichst lange zur
Abfrage des dazugehÃ¶rigen Objekts verwendet werden kÃ¶nnen.

In URLs **sollten** deshalb nur Eigenschaften des Objekts aufgenommen
werden, die nicht verÃ¤ndert werden. Ã„ndert sich beispielsweise die
Kennung einer Drucksache im Verlauf ihrer Existenz, dann scheidet sie
fÃ¼r die Bildung der URL aus.

Des Weiteren sollen Eigenschaften der Implementierung nicht sichtbar
sein. Ist ein OParl-Server beispielsweise in PHP geschrieben, **sollte**
dies **nicht** dazu fÃ¼hren, dass im Pfad ein Bestandteil wie
"oparl.php/" erscheint.

Weitere Empfehlungen fÃ¼r langlebige URLs liefern Tim Berners-Lee[^14]
sowie die EuropÃ¤ische Kommission[^15].

## JSON-Ausgabe

Ein OParl-Server **muss** Objekte in Form von JSON ausgeben. Die
AbkÃ¼rzung JSON steht fÃ¼r "JavaScript Object Notation". Das JSON-Format
ist in RFC 7159[^16] beschrieben.

SÃ¤mtliche JSON-Ausgabe **muss** in UTF-8 ohne Byte Order Mark (BOM)
geschehen. Dies entspricht RFC 7159 Section 8.1[^17]. GemÃ¤ÃŸ RFC 7159
Section 7[^18] **darf** UTF-8 String-Escaping verwendet werden.
XML-/HTML-String-Escaping **darf nicht** verwendet werden.

Eine SyntaxÃ¼bersicht und weitere Implementierungshinweise finden sich
auf [json.org](http://json.org/).

Es ist gestattet, weitere zur JSON-Ausgabe semantisch identische
Formate[^19] anzubieten. Da diese jedoch nicht Bestandteil der
Spezifikation sind, **sollten** sich Clients nicht auf deren
Vorhandensein verlassen.

### In OParl verwendete Datentypen

In OParl werden alle in JSON definierten Dateitypen verwendet:

object:
: Objects entsprechen der Definition des Objects in RFC 7159 Section 4

array:
: Arrays entsprechen der Definition des Arrays in RFC 7159 Section 5

integer:
: Integers entsprechen der Definition des Integer-Parts der Number aus
RFC 7159 Section 6

boolean:
: Booleans entsprechen der Definition von Boolean in RFC 7159 Section
3

string:
: Strings entsprechen der Definition der Unicode-Strings aus RFC 7159
Section 7

In OParl werden verschiedene String-Typen verwendet. Wenn von diesen
Typen gesprochen wird, so wird automatisch ein JSON-String
vorausgesetzt:

url:
: Eine URL ist ein String, der entsprechend des [URL-Kapitels](#urls)
formatiert wurde.

url (Object):
: Eine URL mit in Klammern angehÃ¤ngtem Objektname beschreibt eine URL
auf eben diesen Objekttypus.

date:
: Entspricht einem Datum ohne Uhrzeit und ohne Zeitzone, wie sie im
folgenden Abschnitt beschrieben werden.

date-time:
: Entspricht einem Datum und einer Uhrzeit mit Zeitzone, wie sie im
folgenden Abschnitt beschrieben werden.

### Datums- und Zeitangaben {#datum_zeit}

FÃ¼r Datums- und Zeitangaben wird eine Spezielisierung der in ISO 8601
beschriebenen Formate verwendet. Ein Datum (date) **muss** muss die Form
`yyyy-mm-dd` besitzen und ein Zeitpunkt (date-time) **muss** in der Form
`yyyy-mm-ddThh:mm:ssÂ±hh:mm` angegeben werden.

Beispiel fÃ¼r ein Datum: `1969-07-21`

Beispiel fÃ¼r einen Zeitpunkt: `1969-07-21T02:56:00+00:00`

### `null`-Werte und leere Listen

JSON erlaubt es grundsÃ¤tzlich, Eigenschaften mit dem Wert `null` zu
versehen. Eigenschaften **sollten** nicht mit dem Wert `null` ausgegeben
werden, wenn zu einer Eigenschaft keine Daten vorliegen. Obligatorische
Eigenschaften **dÃ¼rfen nicht** den Wert `null` haben.

Im Fall von Arrays erlaubt JSON grundsÃ¤tzlich die Ausgabe von `[]` fÃ¼r
leere Arrays. Wie bei `null` wird auch hier **empfohlen**, auf die
Ausgabe einer Eigenschaft mit dem Wert `[]` zu verzichten, wenn zu einer
Eigenschaft keine Daten vorliegen. Bei obligatorischen Eigenschaften
**muss** jedoch eine leere Liste ausgegeben werden.

Bei nicht obligatorischen Eigenschaften sollte gleichermaÃŸen auf die
Ausgabe eines leeren Strings `""` verzichtet werden.

## Objektlisten und Paginierung

Oft wird fÃ¼r ein Attribut kein Wert ausgegeben, sondern ein anderes
Objekt oder eine Liste von Objekten. Dabei kann eine Referenz auf das
Objekt bzw. die Objektliste angegeben werden, oder das Objekt bzw. die
Objektlist wird intern ausgegeben. Beide Verfahren sollen im Folgenden
erklÃ¤rt werden. Zu beachten ist, dass fÃ¼r jedes Listenattribut
festgelegt ist, welches dieser Verfahren jeweils zu verwenden ist. Diese
Information ist den [Schemadefinitionen](#schema) zu entnehmen.

### Referenzierung von Objekten via URL

Bei der Referenzierung einzelner Objekte wird eine URL angegeben, welche
auf das entsprechende Objekt verweist. Der Typ ist hierbei ein
`string (url: Objekt-ID)`. Ein Beispiel hierfÃ¼r ist `subOrganizationOf`
in `Organization`:

```{#objektlisten_ex1 .json}
{
  "id": "https://oparl.example.org/organization/1",
  "type": "https://schema.oparl.org/1.1/Organization",
  "subOrganizationOf": "https://oparl.example.org/organization/2"
  ...
}
```

Es kann auch eine Liste von Referenzen ausgegeben werden. Der Typ ist in
diese Fall `array of string (url: Objekt-ID)`.

Ein Beispiel hierfÃ¼r ist `meeting` in `Organization`:

```{#objektlisten_ex2 .json}
{
  "id": "https://oparl.example.org/organization/1",
  "type": "https://schema.oparl.org/1.1/Organization",
  "meeting": [
    "https://oparl.example.org/meeting/1",
    "https://oparl.example.org/meeting/2",
    "https://oparl.example.org/meeting/3",
  ]
  ...
}
```

### Interne Ausgabe von Objekten

Objekte kÃ¶nnen auch intern ausgegeben werden. Dabei wird das gesamte
Objekt als Wert eines Attributs angegeben. Ein Beispiel fÃ¼r ein internes
Objekt ist `location` in `oparl:Body`:

```{#objektlisten_ex3 .json}
{
  "id": "https://oparl.example.org/body/1",
  "type": "https://schema.oparl.org/1.1/Body",
  "location": {
    "id": "https://oparl.example.org/location/1",
    "type": "https://schema.oparl.org/1.1/Location",
    "description": "Ratshausvorplatz 1, 12345 Beispielstadt"
  },
  ...
}
```

Ebenso kann eine Liste von Objekten intern ausgegeben werden. Hier das
Beispiel des Attributes `membership` in `oparl:Person`.

```{#objektlisten_ex4 .json}
{
  "id": "https://oparl.example.org/person/1",
  "type": "https://schema.oparl.org/1.1/Person",
  "membership": [
    {
      "id": "https://oparl.example.org/memberships/385",
      "organization": "https://oparl.example.org/organizations/5",
      "role": "Vorsitzende",
      "votingRight": true,
      "startDate": "2013-12-03"
    },
    {
      "id": "https://oparl.example.org/memberships/693",
      "organization": "https://oparl.example.org/organizations/9",
      "role": "Sachkundige BÃ¼rgerin",
      "votingRight": false,
      "startDate": "2013-12-03",
      "endDate": "2014-07-28"
    }
  ],
  ...
}
```

Bei der internen Ausgabe von Objekten **darf** der Server keine
gelÃ¶schten Objekte ausgeben.

### Externe Objektlisten

Es kÃ¶nnen auch Referenzen zu sogenannten externen Objektlisten angegeben
werden. Die externe Liste enthÃ¤lt dann die betreffenden Objekte in Form
einer Listenausgabe. Ein Beispiel dafÃ¼r ist `organization` in
`oparl:Body`.

`oparl:Body`:

```{#objektlisten_ex5a .json}
{
  "id": "https://oparl.example.org/body/1",
  "type": "https://schema.oparl.org/1.1/Body",
  "organization": "https://oparl.example.org/body/1/organization"
  ...
}
```

Die externe Objektliste:

```{#objektlisten_ex5b .json}
{
    "data": [
      {
        "id": "https://oparl.example.org/organization/1",
        "type": "https://schema.oparl.org/1.1/Organization",
        "name": "Organisation Nummer 1",
        ...
      },
      {
        "id": "https://oparl.example.org/organization/2",
        "type": "https://schema.oparl.org/1.1/Organization",
        "name": "Organisation Nummer 2",
        ...
      },
      {
        "id": "https://oparl.example.org/organization/3",
        "type": "https://schema.oparl.org/1.1/Organization",
        "name": "Organisation Nummer 3",
        ...
      },
    ],
    ...
}
```

### Paginierung

FÃ¼r externe Objektlisten ist eine Aufteilung sogenannte _Listenseiten_
vorgesehen, wobei jede Listenseite eine eigene URL erhÃ¤lt. Das dient
dazu, die bei der jeweiligen Anfrage Ã¼bertragenen Datenmengen und
Antwortzeiten zu begrenzen.

Die Entscheidung, ob eine externe Objektliste mit Paginierung ausgegeben
wird, liegt allein beim Server. Bei Listen mit mehr als 100 EintrÃ¤gen
wird dies **empfohlen**.

Ein Server **muss** fÃ¼r eine stabile Sortierung von ListeneintrÃ¤gen
sorgen. Das heiÃŸt, dass die Sortierung der EintrÃ¤ge einem konstanten
Prinzip folgt und sich nicht von Abfrage zu Abfrage Ã¤ndert. Das kann
z.B. durch die Sortierung von Objekten nach einer eindeutigen und
unverÃ¤nderlichen ID erreicht werden.

Jede Listenseite **muss** die Attribute folgenden Attribute enthalten:

- **data** (Array der intern ausgegebenen Objekte)

- **pagination** (Object)

- **links** (Object)

FÃ¼r `pagination` sind die folgenden Attribute festgelegt, die alle
**optional** sind:

- `totalElements`: Gibt die Gesamtanzahl der Objekte in der Liste an.
  Diese Zahl kann sich unter UmstÃ¤nden bis zum Aufruf der nÃ¤chsten
  Listenseiten Ã¤ndern.

- `elementsPerPage`: Gibt die Anzahl der Objekte pro Listenseite an.
  Dieser Wert muss auf allen Listenseiten bis auf die letzte gleich
  sein.

- `currentPage`: Gibt die aktuelle Seitenzahl in der Liste an.

- `totalPages`: Gibt die Gesamtanzahl der Seiten in der Liste an.

FÃ¼r `links` sind folgende Attribute festgelegt, die bis auf `next` alle
**optional** sind:

- `first`: URL der ersten Listenseite

- `prev`: URL der vorherigen Listenseite

- `self`: Die kanonische URL dieser Listenseite

- `next`: URL der nÃ¤chsten Listen. FÃ¼r alle Seiten bis auf die letzte
  ist die Angabe dieser URL **zwingend**.

- `last`: URL der letzten Listenseite

- `web`: s. [web](#web). Neu in OParl 1.1

```{#paginierung_ex1 .json}
{
    "data": [
        {...},
        {...},
        ...
    ],
    "pagination": {
        "totalElements": 50000,
        "elementsPerPage": 100,
        "currentPage": 3,
        "totalPages":500
    },
    "links": {
        "first": "https://oparl.example.org/organization/",
        "prev": "https://oparl.example.org/organization/?page=2",
        "self": "https://oparl.example.org/organization/?page=3",
        "next": "https://oparl.example.org/organization/?page=4",
        "last": "https://oparl.example.org/organization/?page=500",
        "web": "https://web.example.org/organization/?page=500"
    }
}
```

### Filter

Externe Objektlisten kÃ¶nnen mit den URL-Parametern `created_since`,
`created_until`, `modified_since` und `modified_until` eingeschrÃ¤nkt
werden. Diese Parameter beziehen sich auf die entsprechenden Attribute
der jeweiligen Objekte, wobei reservierte Zeichen URL-Kodiert werden
mÃ¼ssen. Ein Server muss diese Parameter bei allen externen Objektlisten
unterstÃ¼tzen.

Neu in OParl 1.1: Wenn ein Client den Parameter `omit_internal` mit dem
Wert `true` angibt, dann **soll** der Server auf die Ausgabe von
internen Listen verzichten. Konkret bedeutet das, dass die folgenden
Attribute nicht ausgegeben werden mÃ¼ssen:

- `auxiliaryFile` in `AgendaItem`
- `auxiliaryFile` in `Meeting`
- `auxiliaryFile` in `Paper`
- `location` in `Paper`
- `membership` in `Person`
- `agendaItem` in `Meeting`
- `legislativeTerm` in `OparlBodyDto`

Die Filter werden vom Client benutzt, indem die gewÃ¼nschten
URL-Parameter an die URL der ersten Listenseite angehÃ¤ngt werden. Bei
allen weiteren Seiten, genauer gesagt bei den Werten von `links`,
**muss** der Server sicherzustellen, dass die verwendeten Filter
erhalten bleiben.

Neu in OParl 1.1: Ein Server **muss** fÃ¼r den im nÃ¤chsten Abschnitt
beschrieben Aktualisierungsmechanismus auch die den Filtern
entsprechenden gelÃ¶schten Objekte ausgeben, wenn der Parameter
`modified_since` gesetzt ist (s. [OParl 1.1](#oparl-1-1)). Wenn
`modified_since` nicht gesetzt ist, dann **dÃ¼rfen** die gelÃ¶schten
Objekte **nicht** ausgegeben werden. Dadurch kann sich ein Client
effizient darÃ¼ber informieren, welche der Objekte in seinem lokalen
Bestand gelÃ¶scht wurden.

Lautet die URL fÃ¼r eine Liste von Drucksachen wie folgt:

    https://oparl.example.org/papers/

kann der Client die folgende URL bilden, um die Ausgabe der Liste auf
Drucksachen einzuschrÃ¤nken, die seit dem 1. Januar 2014 verÃ¶ffentlicht
wurden:

    https://oparl.example.org/papers/?created_since=2014-01-01T00%3A00%3A00%2B01%3A00

Mehrere Parameter kÃ¶nnen auch gemeinsam verwendet werden. So kann man
z.B. eine EinschrÃ¤nkung vom 1.1.2014 bis zum 31.1.2014 vornehmen:

    https://oparl.example.org/papers/?created_since=2014-01-01T00%3A00%3A00%2B01%3A00&created_until=2014-01-31T23%3A59%3A59%2B01%3A00

Die genannten URL-Parameter erwarten grundsÃ¤tzlich eine vollstÃ¤ndige
[`date-time`-Angabe](#datum_zeit).

Des Weiteren kann ein Client die Anzahl der Objekte pro Listenseite
durch den URL-Parameter `limit` begrenzen, der sich auf das gleichnamige
Attribut bezieht. Ein Client **darf nicht** erwarten, dass sich ein
Server an seine `limit`-Anfrage hÃ¤lt.

### Der Aktualisierungsmechanismus {#aktualisierungsmechanismus}

Dieser Abschnitt ist neu in OParl 1.1.

Der Hauptnutzen der Filter ist die MÃ¶glichkeit, einen lokalen
Datenbestand inkrementell zu aktualisieren.

Ein Client kÃ¶nnte z.B. am 1.1.2014 um 2:00 Uhr deutscher Zeit die Liste
aller Drucksachen herunterladen und in einer Datenbank speichern.

    https://oparl.example.org/papers/

Um den Datenbestand am nÃ¤chsten Tag zu aktualisieren, ruft der Client
dieselbe URL auf, diesmal jedoch mit dem Parameter `modified_since` mit
dem Wert `2014-01-01T02:00:00+01:00` und mit `omit_internal`.

    https://oparl.example.org/papers/?modified_since=2014-01-01T02%3A00%3A00%2B01%3A00&omit_internal=true

Diese Liste ist in der Regel deutlich kÃ¼rzer als die Liste aller
Objekte, sodass die Aktualisierung bedeutend schneller ist als der erste
Abruf. Der Client muss auÃŸerdem nur noch eine deutlich kleinere Menge an
Objekten in die Datenbank einfÃ¼gen, aktualisieren oder lÃ¶schen, um den
gleichen Datenstand wie der Server zu haben.

## Cross-Origin Resource Sharing (CORS) {#cors}

Wenn Webbrowser mittels Skript auf JSON-Ressourcen zugreifen sollen
unterliegen diese Zugriffe Ã¼blicherweise einer _Same-Origin-Policy_
(SOP). Das heiÃŸt, eine Anfrage ist nur an den Server zulÃ¤ssig, der auch
das initiierende Skript ausgeliefert hat. Anfragen an andere Server
werden vom Browser blockiert. Diese EinschrÃ¤nkung dient im Allgemeinen
der Sicherheit von Webbrowsern.[^20]

Um die Daten von OParl-Servern auch im Kontext von Webanwendungen
flexibel nutzen zu kÃ¶nnen, ist die Ãœberwindung der SOP nÃ¶tig. Hierzu
dient _Cross-Origin Resource Sharing_ (CORS)[^21]. Mittels CORS kann ein
Server mitteilen, dass bestimmte von ihm ausgelieferte Ressourcen auch
innerhalb von Webapplikationen genutzt werden dÃ¼rfen, die nicht vom
selben Server ausgeliefert werden. Technisch wird dies durch Ausgabe
zusÃ¤tzlicher HTTP-Header erreicht.

OParl-Server **mÃ¼ssen** fÃ¼r jegliche Anfrage, die mit der Ausgabe von
JSON-Daten beantwortet wird (das sind alle Anfragen auÃŸer
[Dateizugriffe](#dateizugriff)) den folgenden HTTP-Antwort-Header
senden:

    Access-Control-Allow-Origin: *

Der HTTP-Antwort-Header `Access-Control-Allow-Methods` **sollte**
darÃ¼ber hinaus **nicht** gesetzt sein, oder **muss** die Methode `GET`
beinhalten.

Entwicklerinnen von Webanwendungen sollten sich darÃ¼ber bewusst sein,
dass durch die direkte Einbindung von Skripten Dritter in ihre
Anwendungen mÃ¶gliche Sicherheitsrisiken entstehen. FÃ¼r den Fall, dass
ein OParl-Server, etwa in Folge einer Manipulation, Schadcode
ausliefert, kÃ¶nnte dieser unmittelbar von Skripten im Browser ausgefÃ¼hrt
werden.

## Dateizugriffe {#dateizugriff}

Mit dem Begriff "Datei" sind im Sinne dieser Spezifikation alle
Ressourcen gemeint, die von einem OParl-Server zur VerfÃ¼gung gestellt
werden und deren Metadaten Ã¼ber die JSON-API als
[`oparl:File`](#entity-file) abgerufen werden kÃ¶nnen. Es handelt sich
dabei beispielsweise um Textdokumente im PDF-Format oder Abbildungen im
JPEG- oder PNG-Format.

Jede Datei **muss** dabei mit einer HTTP-GET-Anfrage abrufbar sein.

### Empfehlungen fÃ¼r Dateizugriffe

- Ein Server **sollte** die Verwendung von Kompression gemÃ¤ÃŸ dem
  HTTP-Standard unterstÃ¼tzen.

- Ein Server **sollte** "Conditional GET", insbesondere
  `If-Modified-Since` und `If-None-Match` sowie "Chunked GET"
  unterstÃ¼tzen.

- Die Ausgabe der HTTP-Header `Last-Modified`, `Content-Length` und
  `ETag` ist **empfohlen**.

- Bei gelÃ¶schten Dateien **sollte** der HTTP-Statuscode `410`
  verwendet werden.

### Allgemeiner Zugriff und expliziter Download

Mit der im `oparl:File` **zwingend** anzugebenden Eigenschaft
`accessUrl` liefert der Server dem Client eine URL, die dem allgemeinen
Zugriff auf die Datei dient. Beim Zugriff auf dieser URL **darf** der
Server **nicht** den `Content-Disposition`-Header mit dem Parameter
`attachment` senden. [^22]

Es wird daher **empfohlen**, zusÃ¤tzlich eine Eigenschaft `downloadUrl`
anzubieten. Beim Zugriff auf die Download-URL **muss** der Server in der
HTTP-Antwort einen `Content-Disposition`-Header senden, der als ersten
Parameter den Typ `attachment` enthÃ¤lt und mit dem `filename`-Parameter
den Namen der Datei angibt.

Beispiel:

    Content-Disposition: attachment; filename="2014-08-22 Rat Wortprotokoll.pdf"

## GelÃ¶schte Objekte {#geloeschte-objekte}

In OParl **dÃ¼rfen** Objekte **nicht** einfach gelÃ¶scht werden, sodass
unter der betreffenden URL kein gÃ¼ltiges Objekt ausgeliefert wird.
Stattdessen wird ein sogenanntes _soft delete_ verwendet.

Hintergrund ist, dass OParl-Clients bei der Aktualisierung ihres
Datenbestandes, z.B. mit den [Filtern](#filter) `modified_since` bzw.
`created_since`, erfahren kÃ¶nnen mÃ¼ssen, welche Objekte gelÃ¶scht wurden.

Dies wird durch die folgenden Regeln gewÃ¤hrleistet.

Wenn ein Objekt gelÃ¶scht wird,

- **muss** das Objekt das zusÃ¤tzliche Attribut `deleted` mit dem Wert
  `true` bekommen
- **muss** das Attribut `modified` auf den Zeitpunkt der LÃ¶schung
  setzen
- **mÃ¼ssen** die Attribute `id`, `type` und `created` erhalten bleiben
- **dÃ¼rfen** alle weiteren Attribute entfernt werden

Als HTTP-Statuscode **muss** weiterhin 200 verwendet werden.

Neu in OParl 1.1: Die Objekte _LegislativeTerm_, _Membership_,
_AgendaItem_ und _Consultation_ dÃ¼rfen nicht mehr einfach gelÃ¶scht
werden. Um KompatibilitÃ¤t zu OParl 1.0 zu gewÃ¤hrleisten muss weiterhin
der Wert `modified` aller Objekte aktualisiert werden, in die dieses
Objekt eingebettet war.

## Ausnahmebehandlung

Wenn ein Server eine Anfrage nicht bearbeiten kann, z.B. weil die URL
ungÃ¼ltig ist oder das angefragte Objekt nicht existiert, dann **sollte**
er mit dem entsprechenden HTTP-Statuscode antworten.

Ein Server **sollte** in diesem Fall ein Objekt ausgeben, das die
folgenden 3 Attribute enthÃ¤lt:

- `type`: EnthÃ¤lt als Wert die URL
  `https://schema.oparl.org/1.1/Error`
- `message`: Eine Fehlermeldung, die zur Anzeige fÃ¼r einen Nutzer
  gedacht ist. Die Fehlermeldung sollte deshalb in der Sprache der
  durch die Schnittstelle ausgelieferten Inhalte verfasst sein
- `debug`: ZusÃ¤tzliche Informationen Ã¼ber den Fehler

Wenn ein Server ein solches Objekt ausgibt, dann **muss** er dazu einen
HTTP-Statuscode senden, der einen Fehler anzeigt.

Ein Client **darf nicht** voraussetzen, dass er im Fall eines Fehlers
verwertbare Informationen wie das oben beschriebene Fehlerobjekt erhÃ¤lt.

## OParl Endpunkt

Als OParl Endpunkt bzw. Einsprungspunkt zur Schnittstelle wird ein
`OParl:System` Objekt genutzt. Falls auf einem HTTP-Host mehrere
OParl-Schnittstellen oder mehrere OParl Versionen parallel installiert
sind, **mÃ¼ssen** diese eindeutige und voneinander unabhÃ¤ngige
OParl-Endpunkte anbieten. Es ist allerdings mÃ¶glich, eine Liste von
`OParl:System`-Objekten auszugeben, die z.B. auf verschiedene
OParl-Versionen einer Schnittstelle verweisen.

# Schema

Dieses Kapitel beschreibt das Schema von OParl. Das Schema definiert die
Objekttypen und ihre Eigenschaften. DarÃ¼ber hinaus ist im Schema auch
festgelegt, in welcher Beziehung verschiedene Objekttypen zu einander
stehen.

![OParl Objekttypen: Ein Ãœberblick. Die Zahl an den Verbindungslinien
entspricht der Anzahl der Attribute, die eine oder mehrere VerknÃ¼pfungen
herstellen.](build/src/images/objekttypen_graph.png)

## Die Objekte {#objekttypen}

OParl nutzt folgenden Objekte:

- oparl:System
- oparl:Body
- oparl:LegislativeTerm
- oparl:Organization
- oparl:Person
- oparl:Membership
- oparl:Meeting
- oparl:AgendaItem
- oparl:Paper
- oparl:Consultation
- oparl:File
- oparl:Location

Einige Objekte werden intern in anderen Objekten ausgegeben:

- oparl:LegislativeTerm wird intern in oparl:Body ausgegeben
- oparl:Membership wird intern in oparl:Person ausgegeben
- oparl:AgendaItem wird intern in oparl:Meeting ausgegeben
- oparl:Consultation wird intern in Paper ausgegeben
- oparl:File wird intern in oparl:Meeting, oparl:AgendaItem und
  oparl:Paper ausgegeben
- oparl:Location wird intern in oparl:Body, oparl:Organization,
  oparl:Meeting und oparl:Paper ausgegeben

GrundsÃ¤tzlich muss jedes Objekt unter seiner ID abrufbar sein - auch
dann, wenn das Objekt in anderen Objekten intern ausgegeben wird. Bei
der internen Ausgabe wird beim internen Objekt auf die RÃ¼ckreferenz auf
das Elternobjekt verzichtet.

Als Beispiel hier eine Ausgabe von `oparl:Meeting`, in welchem ein
`oparl:File` enthalten ist:

```{#objekte_example1 .json}
{
    "id": "https://oparl.example.org/meeting/281",
    "type": "https://schema.oparl.org/1.1/Meeting",
    "name": "4. Sitzung des Finanzausschusses",
    "start": "2013-01-04T08:00:00+01:00",
    "end": "2013-01-04T12:00:00+01:00",
    "invitation": {
        "id": "https://oparl.example.org/files/57739",
        "name": "Einladung",
        "fileName": "einladung.pdf",
        "mimeType": "application/pdf",
        "date": "2012-01-08",
        "modified": "2012-01-08T14:05:27+01:00",
        "sha1Checksum": "da39a3ee5e6b4b0d3255bfef95601890afd80709",
        "size": 82930,
        "accessUrl": "https://oparl.example.org/files/57739.pdf",
        "downloadUrl": "https://oparl.example.org/files/download/57739.pdf"
    }
    [...]
}
```

Das enthaltene `oparl:File` muss auch einzeln abgerufen werden kÃ¶nnen.
Dabei kommt dann das Eltern-Objekt als zusÃ¤tzliches Attribut hinzu.:

```{#objekte_example2 .json}
{
    "id": "https://oparl.example.org/files/57739",
    "type": "https://schema.oparl.org/1.1/File",
    "name": "Einladung",
    "fileName": "einladung.pdf",
    "mimeType": "application/pdf",
    "date": "2012-01-08",
    "modified": "2012-01-08T14:05:27+01:00",
    "sha1Checksum": "da39a3ee5e6b4b0d3255bfef95601890afd80709",
    "size": 82930,
    "accessUrl": "https://oparl.example.org/files/57739.pdf",
    "downloadUrl": "https://oparl.example.org/files/download/57739.pdf",
    "meeting": [
        "https://oparl.example.org/meeting/281"
    ]
}
```

Das zusÃ¤tzliche Attribut ist ein Array, da es auch mÃ¶glich ist, dass
Dateien von mehreren Hauptobjekten aus genutzt werden. Das kann z.B. bei
`oparl:Location` vorkommen:

```{#objekte_example2 .json}
{
    "id": "https://oparl.example.org/locations/29856",
    "type": "https://schema.oparl.org/1.1/File",
    "description": "HonschaftsstraÃŸe 312, KÃ¶ln",
    "geojson": {
        "type": "Feature",
        "geometry": {
            "type": "Point",
            "coordinates": [
                7.03291,
                50.98249
            ]
        }
    },
    "meeting": [
        "https://oparl.example.org/meeting/281",
        "https://oparl.example.org/meeting/766",
        "https://oparl.example.org/meeting/1002"
    ],
    "paper": [
        "https://oparl.example.org/paper/749",
        "https://oparl.example.org/paper/861",
        "https://oparl.example.org/paper/1077"
    ]
}
```

## Ãœbergreifende Aspekte {#uebergreifende-aspekte}

### VollstÃ¤ndigkeit {#schema-vollstaendigkeit}

Alle regulÃ¤r Ã¶ffentlich abrufbaren Informationen **sollten** auch in
OParl ausgegeben werden, solange dies nicht den Datenschutzbestimmungen
widerspricht. Daher sind sÃ¤mtliche Felder im Schema als **empfohlen** zu
behandeln, wenn nicht explizit etwas anderes angegeben wurde.

### Herstellerspezifische Erweiterungen

In OParl kÃ¶nnen zusÃ¤tzliche, herstellerspezifische Eigenschaften
hinzugefÃ¼gt werden. Dazu wird diesen Eigenschaften ein Herstellerprefix
vorangestellt. So kÃ¶nnte man z.B. `oparl:Person` um eine Faxnummer
erweitern:

    "BeispielHersteller:faxNumber": "012345678",

### URL-Pfade in den Beispielen

OParl-Clients wissen nichts vom Aufbau von Pfaden innerhalb von URLs,
mÃ¼ssen dies nicht wissen, und es gibt deshalb in der OParl-Spezifikation
keine Festlegungen dazu. Die in den Beispielen verwendeten URLs zeigen
einen mÃ¶glichen Weg zur Umsetzungen der Empfehlungen in URLs.

## Eigenschaften mit Verwendung in mehreren Objekttypen

### `id` {#eigenschaft-id}

Die Eigenschaft `id` enthÃ¤lt den eindeutigen Bezeichner des Objekts,
nÃ¤mlich seine URL. Dies ist ein **zwingendes** Merkmal fÃ¼r jedes Objekt.

### `type` {#eigenschaft-type}

Objekttypenangabe des Objekts, **zwingend** fÃ¼r jedes Objekt. Der Wert
ist eine Namespace-URL. FÃ¼r die OParl-Objekttypen sind die folgenden
URLs definiert:

Typ (kurz) Namespace-URL

---

`oparl:AgendaItem` https://schema.oparl.org/1.1/AgendaItem
`oparl:Body` https://schema.oparl.org/1.1/Body
`oparl:Consultation` https://schema.oparl.org/1.1/Consultation
`oparl:File` https://schema.oparl.org/1.1/File
`oparl:LegislativeTerm` https://schema.oparl.org/1.1/LegislativeTerm
`oparl:Location` https://schema.oparl.org/1.1/Location
`oparl:Meeting` https://schema.oparl.org/1.1/Meeting
`oparl:Membership` https://schema.oparl.org/1.1/Membership
`oparl:Organization` https://schema.oparl.org/1.1/Organization
`oparl:Paper` https://schema.oparl.org/1.1/Paper
`oparl:Person` https://schema.oparl.org/1.1/Person
`oparl:System` https://schema.oparl.org/1.1/System

### `name` und `shortName` {#eigenschaft-name-shortname}

Beide Eigenschaften kÃ¶nnen bei vielen Objekttypen genutzt werden um den
Namen des Objekts anzugeben. Ãœblicherweise ist `name` eine
Pflichteigenschaft fÃ¼r den ausgeschriebenen offiziellen Namen, wÃ¤hrend
`shortName` optional angegeben werden kann. Dies ist dann zu empfehlen,
wenn zu einem Namen eine kurze bzw. kompakte und eine lÃ¤ngere, aber
weniger nutzerfreundliche Variante existieren. So ist "Innenministerium"
die Kurzform des offiziellen "Bundesministerium des Inneren".

### `license` {#eigenschaft_license}

Mit `license` wird angegeben, unter welcher Lizenz die Daten des
jeweiligen Objekts stehen. [^23]

Wird `license` im `oparl:System`-Objekt oder am `oparl:Body`-Objekt
verwendet, dann bedeutet das, dass alle Objekte dieses Systems bzw. der
KÃ¶rperschaft unter der angegebenen Lizenz verÃ¶ffentlicht werden, sofern
nicht das einzelne Objekt eine anders lautende Lizenz-URL angibt. Es
wird **empfohlen**, die Lizenzinformation sofern mÃ¶glich global am
`oparl:System` Objekt mitzuteilen und auf redundante Informationen zu
verzichten.

### `created` {#eigenschaft-created}

Datum und Uhrzeit der Erstellung des jeweiligen Objekts.

Diese Eigenschaft **muss** in allen Objekttypen angegeben werden. Neu in
OParl 1.1: Diese Eigenschaft muss auch in Objekten ausgegeben werden,
die intern ausgegeben werden.

### `modified` {#eigenschaft-modified}

Diese Eigenschaft kennzeichnet stets Datum und Uhrzeit der letzten
Ã„nderung des jeweiligen Objekts.

Diese Eigenschaft **muss** - genau wie `created` - in allen Objekttypen
angegeben werden. Neu in OParl 1.1: Diese Eigenschaft muss auch in
Objekten ausgegeben werden, die intern ausgegeben werden.

Es ist **zwingend**, dass bei jeder Ã„nderung eines Objekts der Wert
dieses Attributs auf die zu diesem Zeitpunkt aktuelle Uhrzeit gesetzt
wird, da ein Client in der Regel seinen Datenbestand nur auf Basis
dieses Attributs verlustfrei aktualisieren kann.

### `keyword` {#eigenschaft-keyword}

Die Eigenschaft `keyword` dient der optionalen Kategorisierung eines
Objekts.

### `web` {#eigenschaft-web}

Gibt die URL einer Website an, die das Objekt im Browser darstellt. Das
ist z.B. die HTML-Ansicht eines parlamentarischen Informationssystems.

### `deleted` {#eigenschaft-deleted}

Falls das Objekt gelÃ¶scht wurde, muss dieses gemÃ¤ÃŸ Kapitel 2.8 das
Attribut `deleted: true` bekommen.

## System {#entity-system}

Ein `oparl:System`-Objekt reprÃ¤sentiert eine OParl-Schnittstelle fÃ¼r
eine bestimmte OParl-Version. Es ist auÃŸerdem der Startpunkt fÃ¼r Clients
beim Zugriff auf einen Server.

MÃ¶chte ein Server mehrere zueinander inkompatible OParl-Versionen
unterstÃ¼tzen, dann **muss** der Server fÃ¼r jede Version eine eigenen
OParl-Schnittstelle mit einem eigenen `OparlSystemDto`-Objekt ausgeben.

---

Name Typ Beschreibung

---

`id` url

`type` string

`oparlVersion` string **ZWINGEND** Die URL der
OParl-Spezifikation, die von diesem
Server unterstÃ¼tzt wird. Aktuell
kommt hier nur ein Wert in Frage.
Mit zukÃ¼nftigen OParl-Versionen
kommen weitere mÃ¶gliche URLs hinzu.
Wert:
`https://schema.oparl.org/1.1/`

`otherOparlVersions` array of url Dient der Angabe von System-Objekten
(System) mit anderen OParl-Versionen.

`license` url Lizenz, unter der durch diese API
abrufbaren Daten stehen, sofern
nicht am einzelnen Objekt anders
angegeben. Siehe
[`license`](#eigenschaft_license).

`body` url (externalList) **ZWINGEND** Link zur
[Objektliste](#objektlisten) mit
allen KÃ¶rperschaften, die auf dem
System existieren.

`name` string Nutzerfreundlicher Name fÃ¼r das
System, mit dessen Hilfe Nutzerinnen
und Nutzer das System erkennen und
von anderen unterscheiden kÃ¶nnen.

`contactEmail` string E-Mail-Adresse fÃ¼r Anfragen zur
OParl-API. Die Angabe einer
E-Mail-Adresse dient sowohl
NutzerInnen wie auch Entwicklerinnen
von Clients zur Kontaktaufnahme mit
dem Betreiber.

`contactName` string Name der Ansprechpartnerin bzw. des
Ansprechpartners oder der Abteilung,
die Ã¼ber die in `contactEmail`
angegebene Adresse erreicht werden
kann.

`website` url URL der Website des
parlamentarischen
Informationssystems

`vendor` url URL der Website des
Softwareanbieters, von dem die
OParl-Server-Software stammt.

`product` url URL zu Informationen Ã¼ber die auf
dem System genutzte
OParl-Server-Software

`created` date-time

`modified` date-time

`web` url

`deleted` boolean

---

**Beispiel**

```{.json}
{
    "id": "https://oparl.example.org/",
    "type": "https://schema.oparl.org/1.1/System",
    "oparlVersion": "https://schema.oparl.org/1.1/",
    "body": "https://oparl.example.org/bodies",
    "name": "Beispiel-System",
    "contactEmail": "info@example.org",
    "contactName": "Allgemeiner OParl Kontakt",
    "website": "http://www.example.org/",
    "vendor": "http://example-software.com/",
    "product": "http://example-software.com/oparl-server/",
    "otherOparlVersions": [
        "https://oparl2.example.org/"
    ],
    "created": "2011-11-11T11:11:00+01:00",
    "modified": "2012-11-11T11:11:00+01:00"
}
```

\pagebreak
Body {#entity-body}

---

Der Objekttyp oparl:Body dient dazu, eine KÃ¶rperschaft zu
reprÃ¤sentieren. Eine KÃ¶rperschaft ist in den meisten FÃ¤llen eine
Gemeinde, eine Stadt oder ein Landkreis. In der Regel sind auf einem
OParl-Server Daten von genau einer KÃ¶rperschaft gespeichert und es wird
daher auch nur ein Body-Objekt ausgegeben. Sind auf dem Server jedoch
Daten von mehreren KÃ¶rperschaften gespeichert, **muss** fÃ¼r jede
KÃ¶rperschaft ein eigenes Body-Objekt ausgegeben werden.

---

Name Typ Beschreibung

---

`id` url

`type` string

`system` url (System) System, zu dem dieses Objekt gehÃ¶rt.

`shortName` string Kurzer Name der KÃ¶rperschaft.

`name` string **ZWINGEND** Der offizielle lange
Name der KÃ¶rperschaft.

`website` url Allgemeine Website der KÃ¶rperschaft.

`license` url Lizenz, unter der die Daten dieser
KÃ¶rperschaft stehen, sofern nicht am
einzelnen Objekt anders angegeben.
Siehe
[`license`](#eigenschaft_license).

`licenseValidSince` date-time Zeitpunkt, seit dem die unter
`license` angegebene Lizenz gilt.
_Vorsicht bei Ã„nderungen der Lizenz
die zu restriktiveren Bedingungen
fÃ¼hren!_

`oparlSince` date-time Zeitpunkt, ab dem OParl fÃ¼r dieses
Body bereitgestellt wurde. Dies
hilft, um die DatenqualitÃ¤t
einzuschÃ¤tzen, denn erst ab der
Einrichtung fÃ¼r OParl kann
sichergestellt werden, dass
sÃ¤mtliche Werte korrekt in der
Original-Quelle vorliegen.

`ags` string Der achtstellige Amtliche
GemeindeschlÃ¼ssel[^24].

`rgs` string Der zwÃ¶lfstellige RegionalschlÃ¼ssel.

`equivalent` array of url Dient der Angabe zusÃ¤tzlicher URLs,
die dieselbe KÃ¶rperschaft
reprÃ¤sentieren. Hier kÃ¶nnen
beispielsweise der entsprechende
Eintrag der gemeinsamen Normdatei
der Deutschen
Nationalbibliothek[^25], der
DBPedia[^26] oder der Wikipedia[^27]
angegeben werden. Body- oder
System-Objekte mit anderen
OParl-Versionen **dÃ¼rfen nicht**
Teil der Liste sein.

`contactEmail` string Dient der Angabe einer
Kontakt-E-Mail-Adresse. Die Adresse
soll die Kontaktaufnahme zu einer
fÃ¼r die KÃ¶rperschaft und
idealerweise das parlamentarische
Informationssystem zustÃ¤ndigen
Stelle ermÃ¶glichen.

`contactName` string Name oder Bezeichnung der mit
`contactEmail` erreichbaren Stelle.

`organization` url (externalList) **ZWINGEND** Link zur
[Objektliste](#objektlisten) mit
allen Gruppierungen der
KÃ¶rperschaft.

`person` url (externalList) **ZWINGEND** Link zur
[Objektliste](#objektlisten) mit
allen Personen der KÃ¶rperschaft.

`meeting` url (externalList) **ZWINGEND** Link zur
[Objektliste](#objektlisten) mit
allen Sitzungen der KÃ¶rperschaft.

`paper` url (externalList) **ZWINGEND** Link zur
[Objektliste](#objektlisten) mit
allen Drucksachen der KÃ¶rperschaft.

`legislativeTerm` array of object **ZWINGEND**
(LegislativeTerm) [Objektliste](#objektlisten) mit den
Wahlperioden der KÃ¶rperschaft.

`agendaItem` url (externalList) **ZWINGEND** Link zur
[Objektliste](#objektlisten) mit
allen Tagesordnungspunkten der
KÃ¶rperschaft. Neu in OParl 1.1.

`consultation` url (externalList) **ZWINGEND** Link zur
[Objektliste](#objektlisten) mit
allen Beratungen der KÃ¶rperschaft.
Neu in OParl 1.1.

`file` url (externalList) **ZWINGEND** Link zur
[Objektliste](#objektlisten) mit
allen Dateien der KÃ¶rperschaft. Neu
in OParl 1.1.

`locationList` url (externalList) **ZWINGEND** Link zur
[Objektliste](#objektlisten) mit
allen Ortsangaben der KÃ¶rperschaft.
Neu in OParl 1.1.

`legislativeTermList` url (externalList) **ZWINGEND** Link zur
[Objektliste](#objektlisten) mit
allen Legislaturperioden der
KÃ¶rperschaft. Neu in OParl 1.1. Die
externe Objektliste enthÃ¤lt die
gleichen Objekte wie
`legislativeTerm`

`membership` url (externalList) **ZWINGEND** Link zur
[Objektliste](#objektlisten) mit
allen Mitgliedschaften der
KÃ¶rperschaft. Neu in OParl 1.1.

`classification` string Art der KÃ¶rperschaft.

`location` object (Location) Ort, an dem die KÃ¶rperschaft
beheimatet ist.

`keyword` array of string

`created` date-time

`modified` date-time

`web` url

`deleted` boolean

---

**Beispiel**

```{.json}
{
    "id": "https://oparl.example.org/body/0",
    "type": "https://schema.oparl.org/1.1/Body",
    "system": "https://oparl.example.org/",
    "contactEmail": "ris@beispielstadt.de",
    "contactName": "RIS-Betreuung",
    "ags": "05315000",
    "rgs": "053150000000",
    "equivalent": [
        "http://d-nb.info/gnd/2015732-0",
        "http://dbpedia.org/resource/Cologne"
    ],
    "shortName": "KÃ¶ln",
    "name": "Stadt KÃ¶ln, kreisfreie Stadt",
    "website": "http://www.beispielstadt.de/",
    "license": "http://creativecommons.org/licenses/by/4.0/",
    "licenseValidSince": "2014-01-01T00:00:00+02:00",
    "organization": "https://oparl.example.org/body/0/organizations/",
    "person": "https://oparl.example.org/body/0/persons/",
    "meeting": "https://oparl.example.org/body/0/meetings/",
    "paper": "https://oparl.example.org/body/0/papers/",
    "agendaItem": "https://oparl.example.org/body/0/agendaItems/",
    "consultation": "https://oparl.example.org/body/0/consultations/",
    "file": "https://oparl.example.org/body/0/files/",
    "locationList": "https://oparl.example.org/body/0/locations/",
    "membership": "https://oparl.example.org/body/0/memberships/",
    "legislativeTerm": [
        {
            "id": "https://oparl.example.org/term/21",
            "type": "https://schema.oparl.org/1.1/LegislativeTerm",
            "body": "https://oparl.example.org/body/0",
            "name": "21. Wahlperiode",
            "startDate": "2010-12-03",
            "endDate": "2013-12-03",
            "created": "2014-01-08T14:28:31+01:00",
            "modified": "2014-01-08T14:28:31+01:00"
        }
    ],
    "location": {
        "id": "https://oparl.example.org/location/0",
        "type": "https://schema.oparl.org/1.1/Location",
        "description": "Rathaus der Beispielstadt, Ratshausplatz 1, 12345 Beispielstadt",
        "created": "2014-01-08T14:28:31+01:00",
        "modified": "2014-01-08T14:28:31+01:00",
        "geojson": {
            "type": "Feature",
            "geometry": {
                "type": "Point",
                "coordinates": [
                    50.1234,
                    10.4321
                ]
            },
            "properties": {
                "name": "Rathausplatz"
            }
        }
    },
    "classification": "Kreisfreie Stadt",
    "created": "2014-01-08T14:28:31+01:00",
    "modified": "2014-01-08T14:28:31+01:00"
}
```

\pagebreak
LegislativeTerm {#entity-legislativeterm}

---

Dieser Objekttyp dient der Beschreibung einer Wahlperiode.

---

Name Typ Beschreibung

---

`id` url

`type` string

`body` url (Body) RÃ¼ckreferenz auf die KÃ¶rperschaft,
welche nur dann ausgegeben werden
muss, wenn das
LegislativeTerm-Objekt einzeln
abgerufen wird, d.h. nicht Teil
einer internen Ausgabe ist.

`name` string Nutzerfreundliche Bezeichnung der
Wahlperiode.

`startDate` date Der erste Tag der Wahlperiode.

`endDate` date Der letzte Tag der Wahlperiode.

`license` string

`keyword` array of string

`created` date-time

`modified` date-time

`web` url

`deleted` boolean

---

## Organization {#entity-organization}

Dieser Objekttyp dient dazu, Gruppierungen von Personen abzubilden, die
in der parlamentarischen Arbeit eine Rolle spielen. Dazu zÃ¤hlen in der
Praxis insbesondere Fraktionen und Gremien.

---

Name Typ Beschreibung

---

`id` url

`type` string

`body` url (Body) KÃ¶rperschaft, zu der diese
Gruppierung gehÃ¶rt.

`name` string Offizielle (lange) Form des Namens
der Gruppierung.

`membership` array of url Mitgliedschaften dieser Gruppierung.
(Membership)

`meeting` url (externalList) URL auf eine externe Objektliste mit
den Sitzungen dieser Gruppierung.
Invers zur Eigenschaft
`organization` der Klasse
`oparl:Meeting`

`consultation` url (externalList) URL auf eine externe Objektliste mit
den Beratungen dieser Gruppierung.
Invers zur Eigenschaft
`organization` der Klasse
`oparl:Consultation`

`shortName` string Der Name der Gruppierung als
Kurzform.

`post` array of string Positionen, die fÃ¼r diese
Gruppierung vorgesehen sind.

`subOrganizationOf` url (Organization) URL einer eventuellen Ã¼bergeordneten
Gruppierung.

`organizationType` string Grobe Kategorisierung der
Gruppierung. MÃ¶gliche Werte sind
"Gremium", "Partei", "Fraktion",
"Verwaltungsbereich", "externes
Gremium", "Institution" und
"Sonstiges".

`classification` string Die Art der Gruppierung. In Frage
kommen z.B. "Parlament",
"Ausschuss", "Beirat",
"Projektbeirat", "Kommission", "AG",
"Verwaltungsrat", "Fraktion" oder
"Partei". Die Angabe **sollte**
mÃ¶glichst prÃ¤zise erfolgen. AuÃŸerdem
**sollten** AbkÃ¼rzungen vermieden
werden. FÃ¼r die hÃ¶chste
demokratische Instanz in der Kommune
**sollte** immer der Begriff
"Parlament" verwendet werden, nicht
"Rat" oder "Hauptausschuss".

`startDate` date GrÃ¼ndungsdatum der Gruppierung. Kann
z. B. das Datum der konstituierenden
Sitzung sein.

`endDate` date Datum des letzten Tages der Existenz
der Gruppierung.

`website` url Allgemeine Website der Gruppierung.

`location` object (Location) Ort, an dem die Organisation
beheimatet ist

`externalBody` url (Body) Externer OParl Body, der dieser
Organisation entspricht. Diese
Eigenschaft ist dafÃ¼r gedacht auf
eventuelle konkretere
OParl-Schnittstellen zu verweisen.
Ein Beispiel hierfÃ¼r wÃ¤re eine
Stadt, die sowohl ein Ã¼bergreifendes
parlamentarisches
Informationssystem, als auch
bezirksspezifische Systeme hat.

`license` string

`keyword` array of string

`created` date-time

`modified` date-time

`web` url

`deleted` boolean

---

**Beispiel**

```{.json}
{
    "id": "https://oparl.example.org/organization/34",
    "type": "https://schema.oparl.org/1.1/Organization",
    "body": "https://oparl.example.org/bodies/1",
    "name": "Ausschuss fÃ¼r Haushalt und Finanzen",
    "shortName": "Finanzausschuss",
    "startDate": "2012-07-17",
    "organizationType": "Gremium",
    "location": {
        "id": "https://oparl.example.org/location/0",
        "type": "https://schema.oparl.org/1.1/Location",
        "description": "Rathaus der Beispielstadt, Ratshausplatz 1, 12345 Beispielstadt",
        "created": "2012-01-06T12:01:00+01:00",
        "modified": "2012-01-08T14:05:27+01:00",
        "geojson": {
            "type": "Feature",
            "geometry": {
                "type": "Point",
                "coordinates": [
                    50.1234,
                    10.4321
                ]
            },
            "properties": {
                "name": "Rathausplatz"
            }
        }
    },
    "post": [
        "Vorsitzender",
        "1. Stellvertreter",
        "Mitglied"
    ],
    "meeting": "https://oparl.example.org/organization/34/meetings",
    "membership": [
        "https://oparl.example.org/membership/27",
        "https://oparl.example.org/membership/48",
        "https://oparl.example.org/membership/57"
    ],
    "classification": "Ausschuss",
    "keyword": [
        "finanzen",
        "haushalt"
    ],
    "created": "2012-07-16T00:00:00+02:00",
    "modified": "2012-08-16T12:34:56+02:00"
}
```

\pagebreak
Person {#entity-person}

---

Jede natÃ¼rliche Person, die in der parlamentarischen Arbeit tÃ¤tig und
insbesondere Mitglied in einer Gruppierung
([oparl:Organization](#oparl_organization)) ist, wird mit einem Objekt
vom Typ `oparl:Person` abgebildet.

---

Name Typ Beschreibung

---

`id` url

`type` string

`body` url (Body) KÃ¶rperschaft, zu der die Person gehÃ¶rt.

`name` string Der vollstÃ¤ndige Name der Person mit
akademischem Grad und dem gebrÃ¤uchlichen
Vornamen, wie er zur Anzeige durch den
Client genutzt werden kann.

`familyName` string Familienname bzw. Nachname.

`givenName` string Vorname bzw. Taufname.

`formOfAddress` string Anrede.

`affix` string Namenszusatz (z.B. `jun.` oder `MdL.`)

`title` array of string Akademische Titel

`gender` string Geschlecht. Vorgegebene Werte sind `female`
und `male`, weitere werden durch die
durchgehend klein geschriebene englische
Bezeichnung angegeben. FÃ¼r den Fall, dass
das Geschlecht der Person unbekannt ist,
**sollte** die Eigenschaft nicht ausgegeben
werden.

`phone` array of string Telefonnummern der Person.

`email` array of string E-Mail-Adressen der Person.

`location` url (Location) Referenz der Kontakt-Anschrift der Person.

`locationObject` object (Location) Kontakt-Anschrift der Person. Wenn diese
Eigenschaft ausgegeben wird, dann **muss**
auch die Eigenschaft `location` ausgegeben
werden und auf das gleiche Location-Objekt
verweisen. Dieses Feld sollte die
eigentliche Ausgabeform von `location` in
OParl 1.0 werden. vgl.
https://github.com/OParl/spec/issues/373.
Neu in OParl 1.1

`status` array of string Status, d.h. Rollen in der Kommune.

`membership` array of object Mitgliedschaften der Person in
(Membership) Gruppierungen, z. B. Gremien und
Fraktionen. Es **sollen** sowohl aktuelle
als auch vergangene Mitgliedschaften
angegeben werden

`life` string Kurzer Informationstext zur Person. Eine
LÃ¤nge von weniger als 300 Zeichen ist
**empfohlen**

`lifeSource` string Angabe der Quelle, aus der die
Informationen fÃ¼r `life` stammen. Bei
Angabe von `life` ist diese Eigenschaft
**empfohlen**

`license` string

`keyword` array of string

`created` date-time

`modified` date-time

`web` url

`deleted` boolean

---

**Beispiel**

```{.json}
{
    "id": "https://oparl.example.org/person/29",
    "type": "https://schema.oparl.org/1.1/Person",
    "body": "https://oparl.example.org/body/0",
    "name": "Prof. Dr. Max Mustermann",
    "familyName": "Mustermann",
    "givenName": "Max",
    "title": [
        "Prof.",
        "Dr."
    ],
    "formOfAddress": "Ratsfrau",
    "gender": "male",
    "email": [
        "max@mustermann.de"
    ],
    "phone": [
        "+493012345678"
    ],
    "status": [
        "BezirksbÃ¼rgermeister"
    ],
    "membership": [
        {
            "id": "https://oparl.example.org/memberships/385",
            "type": "https://schema.oparl.org/1.1/Membership",
            "organization": "https://oparl.example.org/organizations/5",
            "role": "Vorsitzende",
            "votingRight": true,
            "startDate": "2013-12-03",
            "created": "2011-11-11T11:11:00+01:00",
            "modified": "2012-08-16T14:05:27+02:00"
        },
        {
            "id": "https://oparl.example.org/memberships/693",
            "type": "https://schema.oparl.org/1.1/Membership",
            "organization": "https://oparl.example.org/organizations/9",
            "role": "Sachkundige BÃ¼rgerin",
            "votingRight": false,
            "startDate": "2013-12-03",
            "endDate": "2014-07-28",
            "created": "2011-11-11T11:11:00+01:00",
            "modified": "2012-08-16T14:05:27+02:00"
        }
    ],
    "created": "2011-11-11T11:11:00+01:00",
    "modified": "2012-08-16T14:05:27+02:00"
}
```

\pagebreak
Membership {#entity-membership}

---

Ãœber Objekte dieses Typs wird die Mitgliedschaft von Personen in
Gruppierungen dargestellt. Diese Mitgliedschaften kÃ¶nnen zeitlich
begrenzt sein. Zudem kann abgebildet werden, dass eine Person eine
bestimmte Rolle bzw. Position innerhalb der Gruppierung innehat,
beispielsweise den Vorsitz einer Fraktion.

---

Name Typ Beschreibung

---

`id` url

`type` string

`person` url (Person) RÃ¼ckreferenz auf Person, welches nur
dann ausgegeben werden muss, wenn
das Membership-Objekt einzeln
abgerufen wird, d.h. nicht Teil
einer internen Ausgabe ist.

`organization` url (Organization) Die Gruppierung, in der die Person
Mitglied ist oder war.

`role` string Rolle der Person fÃ¼r die
Gruppierung. Kann genutzt werden, um
verschiedene Arten von
Mitgliedschaften zum Beispiel in
Gremien zu unterscheiden.

`votingRight` boolean Gibt an, ob die Person in der
Gruppierung stimmberechtigtes
Mitglied ist.

`startDate` date Datum, an dem die Mitgliedschaft
beginnt.

`endDate` date Datum, an dem die Mitgliedschaft
endet.

`onBehalfOf` url (Organization) Die Gruppierung, fÃ¼r die die Person
in der unter `organization`
angegebenen Organisation sitzt.
Beispiel: Mitgliedschaft als
Vertreter einer Ratsfraktion, einer
Gruppierung oder einer externen
Organisation.

`license` string

`keyword` array of string

`created` date-time

`modified` date-time

`web` url

`deleted` boolean

---

## Meeting {#entity-meeting}

Eine Sitzung ist die Versammlung einer oder mehrerer Gruppierungen
(oparl:Organization) zu einem bestimmten Zeitpunkt an einem bestimmten
Ort.

Die geladenen Teilnehmer der Sitzung sind jeweils als Objekte vom Typ
oparl:Person, die in entsprechender Form referenziert werden.
Verschiedene Dateien (Einladung, Ergebnis- und Wortprotokoll, sonstige
Anlagen) kÃ¶nnen referenziert werden. Die Inhalte einer Sitzung werden
durch Tagesordnungspunkte (oparl:AgendaItem) abgebildet.

---

Name Typ Beschreibung

---

`id` url

`type` string

`name` string Name der Sitzung.

`meetingState` string Aktueller Status der Sitzung.
**Empfohlen** ist die Verwendung von
`terminiert` (geplant), `eingeladen`
(vor der Sitzung bis zur Freigabe
des Protokolls) und `durchgefÃ¼hrt`
(nach Freigabe des Protokolls).

`cancelled` boolean Wenn die Sitzung ausfÃ¤llt, wird
cancelled auf true gesetzt.

`start` date-time Datum und Uhrzeit des
Anfangszeitpunkts der Sitzung. Bei
einer zukÃ¼nftigen Sitzung ist dies
der geplante Zeitpunkt, bei einer
stattgefundenen **kann** es der
tatsÃ¤chliche Startzeitpunkt sein.

`end` date-time Endzeitpunkt der Sitzung als
Datum/Uhrzeit. Bei einer zukÃ¼nftigen
Sitzung ist dies der geplante
Zeitpunkt, bei einer stattgefundenen
**kann** es der tatsÃ¤chliche
Endzeitpunkt sein.

`location` object (Location) Sitzungsort.

`organization` array of url Gruppierungen, denen die Sitzung
(Organization) zugeordnet ist. Im Regelfall wird
hier eine Gruppierung verknÃ¼pft
sein, es kann jedoch auch gemeinsame
Sitzungen mehrerer Gruppierungen
geben. Das erste Element **sollte**
dann das federfÃ¼hrende Gremium sein.

`participant` array of url Personen, die an der Sitzung
(Person) teilgenommen haben (d.h. nicht nur
die eingeladenen Personen, sondern
die tatsÃ¤chlich anwesenden). Diese
Eigenschaft kann selbstverstÃ¤ndlich
erst nach dem Stattfinden der
Sitzung vorkommen.

`invitation` object (File) Einladungsdokument zur Sitzung.

`resultsProtocol` object (File) Ergebnisprotokoll zur Sitzung. Diese
Eigenschaft kann selbstverstÃ¤ndlich
erst nachdem Stattfinden der Sitzung
vorkommen.

`verbatimProtocol` object (File) Wortprotokoll zur Sitzung. Diese
Eigenschaft kann selbstverstÃ¤ndlich
erst nach dem Stattfinden der
Sitzung vorkommen.

`auxiliaryFile` array of object Dateianhang zur Sitzung. Hiermit
(File) sind Dateien gemeint, die
Ã¼blicherweise mit der Einladung zu
einer Sitzung verteilt werden, und
die nicht bereits Ã¼ber einzelne
Tagesordnungspunkte referenziert
sind.

`agendaItem` array of object Tagesordnungspunkte der Sitzung. Die
(AgendaItem) Reihenfolge ist relevant. Es kann
Sitzungen ohne TOPs geben.

`license` string

`keyword` array of string

`created` date-time

`modified` date-time

`web` url

`deleted` boolean

---

**Beispiel**

```{.json}
{
    "id": "https://oparl.example.org/meeting/281",
    "type": "https://schema.oparl.org/1.1/Meeting",
    "name": "4. Sitzung des Finanzausschusses",
    "start": "2013-01-04T08:00:00+01:00",
    "end": "2013-01-04T12:00:00+01:00",
    "location": {
        "id": "https://oparl.example.org/location/0",
        "type": "https://schema.oparl.org/1.1/Location",
        "description": "Rathaus der Beispielstadt, Ratshausplatz 1, 12345 Beispielstadt",
        "created": "2012-01-06T12:01:00+01:00",
        "modified": "2012-01-08T14:05:27+01:00",
        "geojson": {
            "type": "Feature",
            "geometry": {
                "type": "Point",
                "coordinates": [
                    50.1234,
                    10.4321
                ]
            },
            "properties": {
                "name": "Rathausplatz"
            }
        }
    },
    "organization": [
        "https://oparl.example.org/organization/34"
    ],
    "invitation": {
        "id": "https://oparl.example.org/files/57739",
        "type": "https://schema.oparl.org/1.1/File",
        "name": "Einladung",
        "fileName": "einladung.pdf",
        "mimeType": "application/pdf",
        "date": "2012-01-08",
        "modified": "2012-01-08T14:05:27+01:00",
        "sha1Checksum": "da39a3ee5e6b4b0d3255bfef95601890afd80709",
        "size": 82930,
        "accessUrl": "https://oparl.example.org/files/57739.pdf",
        "downloadUrl": "https://oparl.example.org/files/download/57739.pdf",
        "created": "2012-01-06T12:01:00+01:00"
    },
    "resultsProtocol": {
        "id": "https://oparl.example.org/files/57739",
        "type": "https://schema.oparl.org/1.1/File",
        "name": "Protokoll",
        "fileName": "protokoll.pdf",
        "mimeType": "application/pdf",
        "date": "2012-01-08",
        "sha1Checksum": "da39a3ee5e6b4b0d3255bfef95601890afd80709",
        "size": 82930,
        "accessUrl": "https://oparl.example.org/files/57739.pdf",
        "downloadUrl": "https://oparl.example.org/files/download/57739.pdf",
        "modified": "2012-01-08T14:05:27+01:00",
        "created": "2012-01-06T12:01:00+01:00"
    },
    "verbatimProtocol": {
        "id": "https://oparl.example.org/files/57739",
        "type": "https://schema.oparl.org/1.1/File",
        "name": "Wortprotokoll",
        "fileName": "wortprotokoll.pdf",
        "mimeType": "application/pdf",
        "date": "2012-01-08",
        "sha1Checksum": "da39a3ee5e6b4b0d3255bfef95601890afd80709",
        "size": 82930,
        "accessUrl": "https://oparl.example.org/files/57739.pdf",
        "downloadUrl": "https://oparl.example.org/files/download/57739.pdf",
        "modified": "2012-01-08T14:05:27+01:00",
        "created": "2012-01-08T14:05:27+01:00"
    },
    "auxiliaryFile": [
        {
            "id": "https://oparl.example.org/files/57739",
            "type": "https://schema.oparl.org/1.1/File",
            "name": "Nachtrags-Tagesordnung",
            "fileName": "nachtrag-TO.pdf",
            "mimeType": "application/pdf",
            "date": "2012-01-08",
            "sha1Checksum": "da39a3ee5e6b4b0d3255bfef95601890afd80709",
            "size": 82930,
            "accessUrl": "https://oparl.example.org/files/57739.pdf",
            "downloadUrl": "https://oparl.example.org/files/download/57739.pdf",
            "modified": "2012-01-08T14:05:27+01:00",
            "created": "2012-01-08T14:05:27+01:00"
        }
    ],
    "agendaItem": [
        {
            "id": "https://oparl.example.org/agendaitem/3271",
            "type": "https://schema.oparl.org/1.1/AgendaItem",
            "meeting": "https://oparl.example.org/meeting/281",
            "number": "10.1",
            "name": "SatzungsÃ¤nderung fÃ¼r Ausschreibungen",
            "public": true,
            "consultation": "https://oparl.example.org/consultation/1034",
            "result": "GeÃ¤ndert beschlossen",
            "resolutionText": "Der Beschluss weicht wie folgt vom Antrag ab: ...",
            "created": "2012-01-06T12:01:00+01:00",
            "modified": "2012-01-08T14:05:27+01:00"
        }
    ],
    "created": "2012-01-06T12:01:00+01:00",
    "modified": "2012-01-08T14:05:27+01:00"
}
```

\pagebreak
AgendaItem {#entity-agendaitem}

---

Tagesordnungspunkte sind die Bestandteile von Sitzungen
(`oparl:Meeting`). Jeder Tagesordnungspunkt widmet sich inhaltlich einem
bestimmten Thema, wozu in der Regel auch die Beratung bestimmter
Drucksachen gehÃ¶rt.

Die Beziehung zwischen einem Tagesordnungspunkt und einer Drucksache
wird Ã¼ber ein Objekt vom Typ `oparl:Consultation` hergestellt, das Ã¼ber
die Eigenschaft `consultation` referenziert werden kann.

---

Name Typ Beschreibung

---

`id` url

`type` string

`meeting` url (Meeting) RÃ¼ckreferenz auf das Meeting,
welches nur dann ausgegeben werden
muss, wenn das agendaItem-Objekt
einzeln abgerufen wird, d.h. nicht
Teil einer internen Ausgabe ist.

`number` string Gliederungs-"Nummer" des
Tagesordnungspunktes. Eine beliebige
Zeichenkette, wie z. B. "10.",
"10.1", "C", "c)" o. Ã¤. Die
Reihenfolge wird nicht dadurch,
sondern durch die Reihenfolge der
TOPs im `agendaItem`-Attribut von
`oparl:Meeting` festgelegt,
**sollte** allerdings zu dieser
identisch sein.

`order` integer **ZWINGEND** Neu in OParl 1.1: Die
Position des Tagesordnungspunkts in
der Sitzung, wenn alle
Tagesordnungspunkte von 0 an
durchgehend numeriert werden. Diese
Nummer entspricht der Position in
`Meeting:agendaItem`

`name` string Das Thema des Tagesordnungspunktes.

`public` boolean Kennzeichnet, ob der
Tagesordnungspunkt zur Behandlung in
Ã¶ffentlicher Sitzung vorgesehen
ist/war. Es wird ein Wahrheitswert
(`true` oder `false`) erwartet.

`consultation` url (Consultation) Beratung, die diesem
Tagesordnungspunkt zugewiesen ist.

`result` string Kategorische Information darÃ¼ber,
welches Ergebnis die Beratung des
Tagesordnungspunktes erbracht hat,
in der Bedeutung etwa "UnverÃ¤ndert
beschlossen" oder "GeÃ¤ndert
beschlossen".

`resolutionText` string Falls in diesem Tagesordnungspunkt
ein Beschluss gefasst wurde, kann
hier ein Text angegeben werden. Das
ist besonders dann in der Praxis
relevant, wenn der gefasste
Beschluss (z. B. durch
Ã„nderungsantrag) von der
Beschlussvorlage abweicht.

`resolutionFile` object (File) Falls in diesem Tagesordnungspunkt
ein Beschluss gefasst wurde, kann
hier eine Datei angegeben werden.
Das ist besonders dann in der Praxis
relevant, wenn der gefasste
Beschluss (z. B. durch
Ã„nderungsantrag) von der
Beschlussvorlage abweicht.

`auxiliaryFile` array of object Weitere DateianhÃ¤nge zum
(File) Tagesordnungspunkt.

`start` date-time Datum und Uhrzeit des
Anfangszeitpunkts des
Tagesordnungspunktes. Bei
zukÃ¼nftigen Tagesordnungspunkten ist
dies der geplante Zeitpunkt, bei
einem stattgefundenen **kann** es
der tatsÃ¤chliche Startzeitpunkt
sein.

`end` date-time Endzeitpunkt des
Tagesordnungspunktes als
Datum/Uhrzeit. Bei zukÃ¼nftigen
Tagesordnungspunkten ist dies der
geplante Zeitpunkt, bei einer
stattgefundenen **kann** es der
tatsÃ¤chliche Endzeitpunkt sein.

`license` string

`keyword` array of string

`created` date-time

`modified` date-time

`web` url

`deleted` boolean

---

## Paper {#entity-paper}

Dieser Objekttyp dient der Abbildung von Drucksachen in der
parlamentarischen Arbeit, wie zum Beispiel Anfragen, AntrÃ¤gen und
Beschlussvorlagen. Drucksachen werden in Form einer Beratung
(oparl:Consultation) im Rahmen eines Tagesordnungspunkts
(oparl:AgendaItem) einer Sitzung (oparl:Meeting) behandelt.

Drucksachen spielen in der schriftlichen wie mÃ¼ndlichen Kommunikation
eine besondere Rolle, da in vielen Texten auf bestimmte Drucksachen
Bezug genommen wird. Hierbei kommen in parlamentarischen
Informationssystemen in der Regel unverÃ¤nderliche Kennungen der
Drucksachen zum Einsatz.

---

Name Typ Beschreibung

---

`id` url

`type` string

`body` url (Body) KÃ¶rperschaft, zu der die Drucksache
gehÃ¶rt.

`name` string Titel der Drucksache.

`reference` string Kennung bzw. Aktenzeichen der
Drucksache, mit der sie in der
parlamentarischen Arbeit eindeutig
referenziert werden kann.

`date` date Datum, welches als Startpunkt fÃ¼r
Fristen u.Ã¤. verwendet ist.

`paperType` string Art der Drucksache, z. B.
Beantwortung einer Anfrage.

`relatedPaper` array of url (Paper) Inhaltlich verwandte Drucksachen.

`superordinatedPaper` array of url (Paper) Ãœbergeordnete Drucksachen.

`subordinatedPaper` array of url (Paper) Untergeordnete Drucksachen.

`mainFile` object (File) Die Hauptdatei zu dieser Drucksache.
Beispiel: Die Drucksache
reprÃ¤sentiert eine Beschlussvorlage
und die Hauptdatei enthÃ¤lt den Text
der Beschlussvorlage. Sollte keine
eindeutige Hauptdatei vorhanden
sein, wird diese Eigenschaft nicht
ausgegeben.

`auxiliaryFile` array of object Alle weiteren Dateien zur Drucksache
(File) ausgenommen der gegebenenfalls in
`mainFile` angegebenen.

`location` array of object Sofern die Drucksache einen
(Location) inhaltlichen Ortsbezug hat,
beschreibt diese Eigenschaft den Ort
in Textform und/oder in Form von
Geodaten.

`originatorPerson` array of url Urheber der Drucksache, falls der
(Person) Urheber eine Person ist. Es kÃ¶nnen
auch mehrere Personen angegeben
werden.

`underDirectionOf` array of url FederfÃ¼hrung. Amt oder Abteilung,
(Organization) fÃ¼r die Inhalte oder Beantwortung
der Drucksache verantwortlich.

`originatorOrganization` array of url Urheber der Drucksache, falls der
(Organization) Urheber eine Gruppierung ist. Es
kÃ¶nnen auch mehrere Gruppierungen
angegeben werden.

`consultation` array of object Beratungen der Drucksache.
(Consultation)

`license` string

`keyword` array of string

`created` date-time

`modified` date-time

`web` url

`deleted` boolean

---

**Beispiel**

```{.json}
{
    "id": "https://oparl.example.org/paper/749",
    "type": "https://schema.oparl.org/1.1/Paper",
    "body": "https://oparl.example.org/bodies/1",
    "name": "Antwort auf Anfrage 1200/2014",
    "reference": "1234/2014",
    "date": "2014-04-04",
    "paperType": "Beantwortung einer Anfrage",
    "relatedPaper": [
        "https://oparl.example.org/paper/699"
    ],
    "mainFile": {
        "id": "https://oparl.example.org/files/57737",
        "type": "https://schema.oparl.org/1.1/File",
        "name": "Anlage 1 zur Anfrage",
        "fileName": "anlage_1_zur_anfrage.pdf",
        "mimeType": "application/pdf",
        "date": "2013-01-04",
        "sha1Checksum": "d749751af44a32c818b9b1e1515251c67734f5d2",
        "size": 82930,
        "accessUrl": "https://oparl.example.org/files/57737.pdf",
        "downloadUrl": "https://oparl.example.org/files/download/57737.pdf",
        "license": "http://www.opendefinition.org/licenses/cc-by",
        "created": "2013-01-04T07:54:13+01:00",
        "modified": "2013-01-04T07:54:13+01:00"
    },
    "auxiliaryFile": [
        {
            "id": "https://oparl.example.org/files/57739",
            "type": "https://schema.oparl.org/1.1/File",
            "name": "Anlage 1 zur Anfrage",
            "fileName": "anlage.pdf",
            "mimeType": "application/pdf",
            "date": "2013-01-04",
            "sha1Checksum": "da39a3ee5e6b4b0d3255bfef95601890afd80709",
            "size": 82930,
            "accessUrl": "https://oparl.example.org/files/57739.pdf",
            "downloadUrl": "https://oparl.example.org/files/download/57739.pdf",
            "text": "Der Ãœbersichtsplan zeigt alle Ebenen des ...",
            "masterFile": "https://oparl.example.org/files/57738",
            "license": "http://www.opendefinition.org/licenses/cc-by",
            "created": "2013-01-04T07:54:13+01:00",
            "modified": "2013-01-04T07:54:13+01:00"
        }
    ],
    "location": [
        {
            "id": "https://oparl.example.org/locations/29856",
            "type": "https://schema.oparl.org/1.1/Location",
            "description": "HonschaftsstraÃŸe 312, KÃ¶ln",
            "created": "2012-01-08T14:05:27+01:00",
            "modified": "2012-01-08T14:05:27+01:00",
            "geojson": {
                "type": "Point",
                "coordinates": [
                    7.03291,
                    50.98249
                ]
            }
        }
    ],
    "originatorPerson": [
        "https://oparl.example.org/person/2000",
        "https://oparl.example.org/person/1000"
    ],
    "originatorOrganization": [
        "https://oparl.example.org/organization/2000",
        "https://oparl.example.org/organization/1000"
    ],
    "consultation": [
        {
            "id": "https://oparl.example.org/consultation/47594",
            "type": "https://schema.oparl.org/1.1/Consultation",
            "agendaItem": "https://oparl.example.org/agendaitem/15569",
            "meeting": "https://oparl.example.org/meeting/243",
            "organization": [
                "https://oparl.example.org/organization/96"
            ],
            "authoritative": false,
            "role": "Beschlussfassung",
            "created": "2012-01-08T14:05:27+01:00",
            "modified": "2012-01-08T14:05:27+01:00"
        }
    ],
    "underDirectionOf": [
        "https://oparl.example.org/organization/2000"
    ],
    "created": "2013-01-08T12:05:27+01:00",
    "modified": "2013-01-08T12:05:27+01:00"
}
```

\pagebreak
Consultation {#entity-consultation}

---

Der Objekttyp `oparl:Consultation` dient dazu, die Beratung einer
Drucksache ([`oparl:Paper`](#oparl_paper)) in einer Sitzung abzubilden.
Dabei ist es nicht entscheidend, ob diese Beratung in der Vergangenheit
stattgefunden hat oder diese fÃ¼r die Zukunft geplant ist. Die Gesamtheit
aller Objekte des Typs `oparl:Consultation` zu einer bestimmten
Drucksache bildet das ab, was in der Praxis als "Beratungsfolge" der
Drucksache bezeichnet wird.

---

Name Typ Beschreibung

---

`id` url

`type` string

`paper` url (Paper) Referenz auf das Paper, welche nur
dann ausgegeben werden muss, wenn
das Consultation-Objekt einzeln
abgerufen wird, d.h. nicht Teil
einer internen Ausgabe ist.

`agendaItem` url (AgendaItem) Referenz auf den Tagesordnungspunkt,
unter dem die Drucksache beraten
wird, welcher nur dann ausgegeben
werden muss, wenn das
Consultation-Objekt einzeln
abgerufen wird, d.h. nicht Teil
einer internen Ausgabe ist.

`meeting` url (Meeting) Referenz auf die Sitzung, in der die
Drucksache beraten wird oder wurde,
welche nur dann ausgegeben werden
muss, wenn das Consultation-Objekt
einzeln abgerufen wird, d.h. nicht
Teil einer internen Ausgabe ist.

`organization` array of url Gremium, in dem die Drucksache
(Organization) beraten wird. Hier kann auch eine
mit Liste von Gremien angegeben
werden (die verschiedenen
`oparl:Body` und `oparl:System`
angehÃ¶ren kÃ¶nnen). Die Liste ist
dann geordnet. Das erste Gremium der
Liste ist federfÃ¼hrend.

`authoritative` boolean DrÃ¼ckt aus, ob bei dieser Beratung
ein Beschluss zu der Drucksache
gefasst wird oder wurde (`true`)
oder nicht (`false`).

`role` string Rolle oder Funktion der Beratung.
Zum Beispiel AnhÃ¶rung, Entscheidung,
Kenntnisnahme, Vorberatung usw.

`license` string

`keyword` array of string

`created` date-time

`modified` date-time

`web` url

`deleted` boolean

---

## File {#entity-file}

Ein Objekt vom Typ `oparl:File` reprÃ¤sentiert eine Datei, beispielsweise
eine PDF-Datei, ein RTF- oder ODF-Dokument, und hÃ¤lt Metadaten zu der
Datei sowie URLs zum Zugriff auf die Datei bereit.

Objekte vom Typ `oparl:File` kÃ¶nnen unter anderem mit Drucksachen
(`oparl:Paper`) oder Sitzungen (`oparl:Meeting`) in Beziehung stehen.
Dies wird durch die Eigenschaft `paper` bzw. `meeting` angezeigt.
Mehrere Objekte vom Typ `oparl:File` kÃ¶nnen mit einander in direkter
Beziehung stehen, z.B. wenn sie den selben Inhalt in unterschiedlichen
technischen Formaten wiedergeben. HierfÃ¼r werden die Eigenschaften
`masterFile` bzw. `derivativeFile` eingesetzt. Das gezeigte
Beispiel-Objekt reprÃ¤sentiert eine PDF-Datei (zu erkennen an der
Eigenschaft `mimeType`) und zeigt auÃŸerdem Ã¼ber die Eigenschaft
`masterFile` an, von welcher anderen Datei es abgeleitet wurde.
Umgekehrt **kann** Ã¼ber die Eigenschaft `derivativeFile` angezeigt
werden, welche Ableitungen einer Datei existieren.

---

Name Typ Beschreibung

---

`id` url

`type` string

`name` string Ein zur Anzeige fÃ¼r Endnutzer
bestimmter Name fÃ¼r dieses Objekt.
Leerzeichen **dÃ¼rfen** enthalten
sein, Datei-Endungen wie ".pdf"
**sollten nicht** enthalten sein.

`fileName` string Dateiname, unter dem die Datei in
einem Dateisystem gespeichert werden
kann. Beispiel: "einedatei.pdf". Da
der Name den kompletten
Unicode-Zeichenumfang nutzen kann,
**sollten** Clients ggfs. selbst
dafÃ¼r sorgen, diesen beim Speichern
in ein Dateisystem den lokalen
Erfordernissen anzupassen.

`mimeType` string MIME-Type der Datei [^28].

`date` date Datum, welches als Startpunkt fÃ¼r
Fristen u.Ã¤. verwendet ist.

`size` integer GrÃ¶ÃŸe der Datei in Bytes.

`sha1Checksum` string \[Veraltet\] SHA1-PrÃ¼fsumme des
Dateiinhalts in
Hexadezimal-Schreibweise. Sollte
nicht mehr verwendet werden, da sha1
als unsicher gilt. Stattdessen
sollte `sha512checksum` verwendet
werden.

`sha512Checksum` string SHA512-PrÃ¼fsumme des Dateiinhalts in
Hexadezimal-Schreibweise.

`text` string Reine Text-Wiedergabe des
Dateiinhalts, sofern dieser in
Textform wiedergegeben werden kann.

`accessUrl` url **ZWINGEND** URL zum allgemeinen
Zugriff auf die Datei. NÃ¤heres unter
[Dateizugriffe](#dateizugriff).

`downloadUrl` url URL zum Download der Datei. NÃ¤heres
unter
[Dateizugriffe](#dateizugriff).

`externalServiceUrl` url Externe URL, welche eine zusÃ¤tzliche
ZugriffsmÃ¶glichkeit bietet.
Beispiel: YouTube-Video.

`masterFile` url (File) Datei, von der das aktuelle Objekt
abgeleitet wurde. Details dazu in
der allgemeinen Beschreibung weiter
oben.

`derivativeFile` array of url (File) Dateien, die von dem aktuellen
Objekt abgeleitet wurden. Details
dazu in der allgemeinen Beschreibung
weiter oben.

`fileLicense` url Lizenz, unter der die Datei
angeboten wird. Wenn diese
Eigenschaft nicht verwendet wird,
ist der Wert von `license`
beziehungsweise die Lizenz eines
Ã¼bergeordneten Objektes maÃŸgeblich.
Siehe
[license](#eigenschaft_license)

`meeting` array of url RÃ¼ckreferenzen auf Meeting-Objekte.
(Meeting) Wird nur dann ausgegeben, wenn das
File-Objekt nicht als eingebettetes
Objekt aufgerufen wird.

`agendaItem` array of url RÃ¼ckreferenzen auf
(AgendaItem) AgendaItem-Objekte. Wird nur dann
ausgegeben, wenn das File-Objekt
nicht als eingebettetes Objekt
aufgerufen wird.

`paper` array of url (Paper) RÃ¼ckreferenzen auf Paper-Objekte.
Wird nur dann ausgegeben, wenn das
File-Objekt nicht als eingebettetes
Objekt aufgerufen wird.

`license` string

`keyword` array of string

`created` date-time

`modified` date-time

`web` url

`deleted` boolean

---

**Beispiel**

```{.json}
{
    "id": "https://oparl.example.org/files/57737",
    "type": "https://schema.oparl.org/1.1/File",
    "name": "Anlage 1 zur Anfrage",
    "fileName": "anlage_1_zur_anfrage.pdf",
    "mimeType": "application/pdf",
    "date": "2013-01-04",
    "size": 82930,
    "sha1Checksum": "d749751af44a32c818b9b1e1515251c67734f5d2",
    "accessUrl": "https://oparl.example.org/files/57737.pdf",
    "downloadUrl": "https://oparl.example.org/files/download/57737.pdf",
    "derivativeFile": [
        "https://oparl.example.org/files/57739"
    ],
    "fileLicense": "http://www.opendefinition.org/licenses/cc-by",
    "created": "2013-01-04T07:54:13+01:00",
    "modified": "2013-01-04T07:54:13+01:00"
}
```

\pagebreak
Location {#entity-location}

---

Dieser Objekttyp dient dazu, einen Ortsbezug formal abzubilden.
Ortsangaben kÃ¶nnen sowohl aus Textinformationen bestehen (beispielsweise
dem Namen einer StraÃŸe/eines Platzes oder eine genaue Adresse) als auch
aus Geodaten. Ortsangaben sind auch nicht auf einzelne Positionen
beschrÃ¤nkt, sondern kÃ¶nnen eine Vielzahl von Positionen, FlÃ¤chen,
Strecken etc. abdecken.

---

Name Typ Beschreibung

---

`id` url

`type` string

`description` string Textuelle Beschreibung eines Orts,
z. B. in Form einer Adresse.

`geojson` object Geodaten-ReprÃ¤sentation des Orts.
Der Wert dieser Eigenschaft **muss**
der Spezifikation von GeoJSON
entsprechen, d.h. es **muss** ein
vollstÃ¤ndiges `Feature`-Objekt
ausgegeben werden.

`streetAddress` string StraÃŸe und Hausnummer der Anschrift.

`room` string Raumangabe der Anschrift

`postalCode` string Postleitzahl der Anschrift.

`subLocality` string Untergeordnete Ortsangabe der
Anschrift, z.B. Stadtbezirk,
Ortsteil oder Dorf.

`locality` string Ortsangabe der Anschrift.

`bodies` array of url (Body) RÃ¼ckreferenzen auf Body-Objekte.
Wird nur dann ausgegeben, wenn das
Location-Objekt nicht als
eingebettetes Objekt aufgerufen
wird.

`organizations` array of url RÃ¼ckreferenzen auf
(Organization) Organization-Objekte. Wird nur dann
ausgegeben, wenn das Location-Objekt
nicht als eingebettetes Objekt
aufgerufen wird.

`persons` array of url RÃ¼ckreferenzen auf Person-Objekte.
(Person) Wird nur dann ausgegeben, wenn das
Location-Objekt nicht als
eingebettetes Objekt aufgerufen
wird.

`meetings` array of url RÃ¼ckreferenzen auf Meeting-Objekte.
(Meeting) Wird nur dann ausgegeben, wenn das
Location-Objekt nicht als
eingebettetes Objekt aufgerufen
wird.

`papers` array of url (Paper) RÃ¼ckreferenzen auf Paper-Objekte.
Wird nur dann ausgegeben, wenn das
Location-Objekt nicht als
eingebettetes Objekt aufgerufen
wird.

`license` string

`keyword` array of string

`created` date-time

`modified` date-time

`web` url

`deleted` boolean

---

**Beispiel**

```{.json}
{
    "id": "https://oparl.example.org/location/0",
    "type": "https://schema.oparl.org/1.1/Location",
    "description": "Rathaus der Beispielstadt, Ratshausplatz 1, 12345 Beispielstadt",
    "created": "2014-01-08T14:28:31+01:00",
    "modified": "2014-01-08T14:28:31+01:00",
    "geojson": {
        "type": "Feature",
        "geometry": {
            "type": "Point",
            "coordinates": [
                50.1234,
                10.4321
            ]
        },
        "properties": {
            "name": "Rathausplatz"
        }
    }
}
```

\pagebreak
Ã„nderungen und Migration
========================

Dieses Kapitel beschreibt die Ã„nderungen zwischen den einzelnen
OParl-Versionen und die zur Migration notwendigen Schritte.

## OParl 1.1 {#oparl-1-1}

In OParl 1.1 setzen wir die seit der VerÃ¶ffentlichung von OParl 1.0
gewonnen Erfahrungen um. OParl 1.1 ist dabei im Sinne von semver
kompatibel zu OParl 1.0. Das bedeutet, dass ein fÃ¼r OParl 1.0
entwickelter Client auch die Ausgabe von OParl 1.1 versteht. Dadurch
wird der Migrationsaufwand von OParl 1.0 zu OParl 1.1 gering gehalten.

OParl 1.0 wurde in der Annahme geschrieben, dass fÃ¼r sechs Objekttypen
(LegislativeTerm, Membership, AgendaItem, Consultation, File, Location)
keine verlÃ¤sslichen Werte fÃ¼r `created` und `modified` existieren. Aus
diesem Grund hatten wir uns fÃ¼r das Design mit eingebetteten Objekten
entschieden. Da sich nun jedoch herausgestellt hat, dass `created` und
`modified` bei allen Objekten existieren, kÃ¶nnen auch fÃ¼r alle Objekte
Listen angeboten werden. Das bringt bei groÃŸe Vereinfachungen fÃ¼r
Clients bei der Synchronisation.

Konkret sind `created` und `modified` in OParl 1.1 fÃ¼r alle Objekte
zwingend und es gibt sechs neue externe Objektlisten in Body:
AgendaItem, Consultation, File, LegislativeTerm, Location und
Membership. Das Attribut fÃ¼r die Location-Liste in Body heiÃŸt dabei
`locationList`, um eine Kollision mit dem bereits existierenden
`location` zu vermeiden. Das gleiche gilt auch fÃ¼r
`legislativeTermList`.

Es entsteht dabei Redundanz zwischen den bereits existierenden
Objektlisten mit eingebetteten Objekten (Body, Paper, Meeting, Person,
Organization) und den neuen externen Listen, die die bisher
eingebetteten Objekte extern ausgeben. Diese Redundanz lÃ¤sst sich auf
Grund der Semver-Regeln in Version 1.1 nicht vermeiden und kann erst in
einer Version 2 beseitigt werden. Um diese Redundanz zumindest bei der
Aktualisierung eines lokalen Datenbestands vermeiden zu kÃ¶nnen wurde die
URL-Parameter `omit_internal` eingefÃ¼hrt.

### Weitere Ã„nderungen

- Namespace-URLs werden durchgÃ¤ngig im Camel Case geschrieben
- Externe Objektlisten kÃ¶nnen ein `web` Attribut angeben
- Jedes Objekt des Schemas hat seine eigene Datei bekommen
- Definition eines Fehlerobjektes fÃ¼r die Ausnahmebehandlung
- sha1 veraltet und sha512 als Ersatz hinzugefÃ¼gt. (s.
  https://shattered.io)
- Die RÃ¼ckreferenz von Location auf Person wird zusÃ¤tzlich auch noch
  eingebettet ausgeben (s. https://github.com/OParl/spec/issues/373)

[^1]:
    In Deutschland hat sich auf kommunaler Ebene der Begriff
    "Ratsinformationssystem" etabliert. OParl ist jedoch nicht auf
    GemeinderÃ¤te beschrÃ¤nkt und verwendet daher den Begriff
    "parlamentarisches Informationssystem".

[^2]: Frankfurt Gestalten: <http://www.frankfurt-gestalten.de/>

[^3]: Politik Bei Uns: <https://politik-bei-uns.de>

[^4]:
    Eine weltweite Ãœbersicht zu Open-Data-Projekten bietet z. B. der
    Open-Data-Showroom <http://opendata-showroom.org/de/>

[^5]: vgl. <https://de.wikipedia.org/wiki/Open_data>

[^6]:
    Ten Principles for Opening Up Open Government Information,
    <https://sunlightfoundation.com/policy/documents/ten-open-data-principles>

[^7]:
    Barrierefreie Informationstechnik-Verordnung 2.0
    <http://www.gesetze-im-internet.de/bitv_2_0/>

[^8]:
    Weitere generelle Informationen zur Bereitstellung offener
    Verwaltungsdaten bieten bspw.

    - Praktische Informationen: Open-Data-Handbook der Open Knowledge
      Foundation
      <http://opendatahandbook.org/de/how-to-open-up-data/index.html>
    - GrundsÃ¤tzliche Informationen: Die vom Bundesministerium des
      Innern beauftragte Studie "Open Government Data Deutschland"
      <http://www.bmi.bund.de/SharedDocs/Downloads/DE/Themen/OED_Verwaltung/ModerneVerwaltung/opengovernment.pdf>

[^9]: RFC2119 <http://tools.ietf.org/html/rfc2119>

[^10]:
    Das Ratsinformationssystem BoRis, eine Eigenentwicklung der Stadt
    Bonn <http://www2.bonn.de/bo_ris/ris_sql/agm_index.asp>

[^11]: bÃ¼rgerbautstadt, <http://www.buergerbautstadt.de>

[^12]: <http://patterns.dataincubator.org/book/follow-your-nose.html>

[^13]: RFC 3986: <http://tools.ietf.org/html/rfc3986>

[^14]:
    Berners-Lee, Tim: Cool URIs don't change.
    <http://www.w3.org/Provider/Style/URI.html>

[^15]:
    Study on persistent URIs, with identification of best practices
    and recommendations on the topic for the MSs and the EC. (PDF)
    <https://joinup.ec.europa.eu/sites/default/files/D7.1.3%20-%20Study%20on%20persistent%20URIs.pdf>

[^16]: RFC 7159: <https://tools.ietf.org/html/rfc7159>

[^17]:
    [RFC 7159 Section
    8.1](https://tools.ietf.org/html/rfc7159#section-8.1)

[^18]:
    [RFC 7159 Section
    7](https://tools.ietf.org/html/rfc7159#section-7)

[^19]:
    Zu semantisch identischen Formaten zÃ¤hlen u.a.: YAML,
    MessagePack, etc.

[^20]:
    vgl. Wikipedia: Same-Origin-Policy
    <https://de.wikipedia.org/wiki/Same-Origin-Policy>

[^21]:
    Cross Origin Resource Sharing - W3C Recommendation 16. Januar
    2014: <http://www.w3.org/TR/cors/>

[^22]: vgl. RFC2138<http://www.ietf.org/rfc/rfc2183>

[^23]:
    Verzeichnisse fÃ¼r Lizenz-URLs sind unter anderem unter
    <http://licenses.opendefinition.org/> und
    <https://github.com/fraunhoferfokus/ogd-metadata/blob/master/lizenzen/deutschland.json>
    zu finden. Allgemeine Informationen zur Lizensierung von Open Data
    finden sich auch im Open Data Handbook der Open Knowledge Foundation
    unter
    <http://opendatahandbook.org/de/how-to-open-up-data/apply-an-open-license.html>.

[^24]:
    Amtliche GemeindeschlÃ¼ssel kÃ¶nnen im [Gemeindeverzeichnis
    (GV-ISys) des Statistischen
    Bundesamtes](https://www.destatis.de/DE/ZahlenFakten/LaenderRegionen/Regionales/Gemeindeverzeichnis/Gemeindeverzeichnis.html)
    eingesehen werden

[^25]: Gemeinsame Normdatei <http://www.dnb.de/gnd>

[^26]: DBPedia <http://www.dbpedia.org/>

[^27]: Wikipedia <http://de.wikipedia.org/>

[^28]: vgl. RFC2046: <http://tools.ietf.org/html/rfc2046>
