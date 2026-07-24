# URL-Struktur (SD.NET RIM, OParl 1.1)

Die folgenden Pfade sind relativ zur Basis-URL des OParl-Systems:

```text
{base-url}/webservice/oparl/v1.1
```

Platzhalter in geschweiften Klammern stehen für den jeweiligen serverseitigen
Bezeichner.

## Baumstruktur

```text
/system
└── /body
    └── /body/{body}
        ├── /organization
        │   └── /organization/{organization}
        │       └── /organization/{organization}/meeting
        ├── /person
        │   └── /person/{person}
        ├── /meeting
        │   └── /meeting/{meeting}
        ├── /paper
        │   └── /paper/{paper}
        ├── /agendaitem
        │   └── /agendaitem/{agenda-item}
        ├── /consultation
        │   └── /consultation/{consultation}
        ├── /file
        │   └── /file/{file}
        ├── /location
        │   └── /location/{location}
        ├── /legislativeterm
        │   └── /legislativeterm/{legislative-term}
        └── /membership
            └── /membership/{membership}
```

## Verweisstruktur zwischen Ressourcen

```text
Meeting
├── AgendaItem
│   ├── Consultation
│   │   └── Paper
│   └── File
├── Organization
└── File

Membership ── Person
           ├─ Organization
           └─ LegislativeTerm

Person ────── Location
Organization ─ Location
```

Die Verbindungslinien beschreiben OParl-Referenzen innerhalb der JSON-Objekte;
sie sind keine verschachtelten URL-Pfade. Alle Ressourcen bleiben unter dem
Body-Prefix direkt adressierbar.

## Pfad-Schema

| Ressource | Collection | Einzelressource |
| --- | --- | --- |
| System | `/system` | `/system` |
| Body | `/body` | `/body/{body}` |
| Organization | `/body/{body}/organization` | `/body/{body}/organization/{organization}` |
| Organization-Meetings | `/body/{body}/organization/{organization}/meeting` | – |
| Person | `/body/{body}/person` | `/body/{body}/person/{person}` |
| Meeting | `/body/{body}/meeting` | `/body/{body}/meeting/{meeting}` |
| Paper | `/body/{body}/paper` | `/body/{body}/paper/{paper}` |
| AgendaItem | `/body/{body}/agendaitem` | `/body/{body}/agendaitem/{agenda-item}` |
| Consultation | `/body/{body}/consultation` | `/body/{body}/consultation/{consultation}` |
| File | `/body/{body}/file` | `/body/{body}/file/{file}` |
| Location | `/body/{body}/location` | `/body/{body}/location/{location}` |
| LegislativeTerm | `/body/{body}/legislativeterm` | `/body/{body}/legislativeterm/{legislative-term}` |
| Membership | `/body/{body}/membership` | `/body/{body}/membership/{membership}` |
