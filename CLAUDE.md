# CLAUDE.md — RicoFilm2

Système Java de gestion distribuée de médiathèque : inventaire des films sur NAS, enrichissement métadonnées TheMovieDB, stockage MongoDB, transfert SFTP vers serveur central.

---

## Stack

| Couche | Technologie | Version |
|--------|-------------|---------|
| Language | Java | 8 (compilé en 11 via Docker) |
| Build | Maven | 3.8 |
| Base de données | MongoDB driver sync | 4.11.1 |
| HTTP Client | Apache HTTPClient | 4.5.12 |
| SSH/SFTP | JSch | 0.2.16 |
| Crypto SSH | BouncyCastle | 1.70 (Ed25519) |
| Media | jaffree (FFprobe) | 2022.06.03 |
| JSON | json-simple + gson | 1.1.1 / 2.10 |
| Logging | Logback | 1.2.3 |

---

## Commandes

```bash
# Build
mvn clean package

# Run en développement
mvn exec:java -Dexec.mainClass="ricohoho.themoviedb.RicoFilm" -Dexec.args="ACTION"

# Run JAR (production)
java -jar target/RicoFilm2-0.0.1-SNAPSHOT-jar-with-dependencies.jar ACTION [PATH]

# Docker
docker build -t ricofilm2 .
docker run -e MONGODB_HOST=mongo -e SERVEUR_NAME=NOS-RICO ricofilm2 AJOUT_FILM

# Déploiement NAS
./scp-nos-rico.sh
```

---

## Actions disponibles

| Action | Description | Images | DB |
|--------|-------------|--------|-----|
| `DOWNLOAD_IMAGE` | Télécharge uniquement les posters | Oui | Non |
| `AJOUT_FILM` | Ajoute films + images | Oui | Oui |
| `AJOUT_FILM_SANS_IMAGE` | Ajoute films sans images | Non | Oui |
| `MAJ_FILM` | Mise à jour (ajout + suppression) | Oui | Oui |
| `MAJ_FILM_SANS_IMAGE` | Mise à jour sans images | Non | Oui |
| `SUPPRIME_FILM` | Supprime films absents du répertoire | - | Oui |
| `REQUEST_FILM` | Traite les demandes de transfert SFTP | - | - |

---

## Architecture

```
/src/ricohoho/
  themoviedb/     # Logique métier principale
    RicoFilm.java         # Main — point d'entrée, parsing args
    TheMovieDb.java       # Orchestrateur principal (~600 lignes)
    FileMAnager.java      # Scan récursif .avi/.mkv/.mp4
    UrlManager.java       # Client HTTP REST (TheMovieDB + backend)
    RequestManager.java   # Traitement demandes de transfert
    FilmRestManager.java  # Client REST films
    Film.java             # POJO — métadonnées TheMovieDB
    Fichier.java          # POJO — fichier local
    FilmFichier.java      # POJO — nom de fichier parsé
    Request.java          # POJO — demande de film
    User.java             # POJO — utilisateur (GSON)
    LogText.java          # Logger fichier texte

  mongo/          # Couche base de données
    MongoManager.java     # CRUD + opérations tableaux MongoDB (~620 lignes)

  ffmpeg/         # Analyse média
    StreamFilm.java       # FFprobe : codec, audio, sous-titres

  tools/          # Utilitaires
    FileTools.java        # Transfert SFTP/SSH (JSch)
    ReadPropertiesFile.java # Chargement config (fichier + env vars)
```

---

## Flux de traitement principal (`TheMovieDb.traiteDossierFilm`)

```
1. Scanner le répertoire  →  FileMAnager.getPAthFile()
2. Parser le nom de fichier  →  extractNomFilm() (regex année/titre)
3. Rechercher sur TheMovieDB  →  getFilmTheMovieDb()
4. Enrichir les métadonnées  →  getFilmTheMovieDbDetail()
5. Analyser les streams  →  StreamFilm.getInformationsFile() (FFprobe)
6. Télécharger le poster  →  downloadImage()
7. Persister en MongoDB  →  MongoManager.insertJSON() / updateDB()
```

## Flux REQUEST_FILM

```
1. GET /request/list?status=AFAIRE&serveur_name=NOS-RICO
2. Pour chaque demande → FileTools.sftpAvecConservationDate()
3. POST /request/edit  →  status = "FAIT"
```

---

## Schéma MongoDB (`films`)

```json
{
  "id": 603,
  "title": "The Matrix",
  "release_date": "1999-03-30",
  "vote_average": 7.9,
  "poster_path": "/hEpWvX6Bp79eLxY1kX5ZJcme5U.jpg",
  "RICO_FICHIER": [
    {
      "serveur_name": "NOS-RICO",
      "path": "C:\\films\\",
      "file": "The.Matrix.1999.mkv",
      "fileDate": "2024-03-15T00:00:00Z",
      "taille": 713905290
    }
  ],
  "RICO_STREAMS": [
    { "CodecType": "VIDEO", "CodecName": "h264", "AvgFrameRate": "24000/1001" },
    { "CodecType": "AUDIO", "CodecName": "aac", "Channels": 2, "tag-language": "fr" }
  ]
}
```

`RICO_FICHIER` est un tableau : un film peut exister sur plusieurs serveurs.

---

## Configuration (`init.properties`)

```properties
# Action à exécuter
ACTION1=AJOUT_FILM

# Répertoire des films
SERVEUR_NAME=NOS-RICO
PATH_FILM=dir-films/
PATH_FILM_NIV_SSDOSSIER=1          # 0=racine, 1=sous-dossiers
PATH_FILM_NON_TRAITE=hoho,hihi     # Dossiers à ignorer

# MongoDB
MONGODB_HOST=localhost
MONGODB_PORT=27017
MONGODB_NAME=ricofilm
MONGODB_USER=ricohoho
MONGODB_PWD=rico$2025

# SFTP Serveur central
CENTRAL_SFTP_HOST=davic.mkdh.fr
CENTRAL_SFTP_PORT=4322
CENTRAL_SFTP_USER=ricohoho
CENTRAL_SFTP_SFTP_PASS=serveur$linux
CENTRAL_SFTP_WORKINGDIR=/home/ricohoho/test
CENTRAL_SFTP_CERTIF=N              # O=certificat, N=password

# HTTP Backend
REQUEST_HTTP_SCHEME=http
REQUEST_HTTP_HOST=localhost
REQUEST_HTTP_PORT=3000
REQUEST_STATUS_AFAIRE=AFAIRE
REQUEST_STATUS_FAIT=FAIT
```

Toutes les propriétés peuvent être surchargées par des **variables d'environnement** (support Docker/CI).

---

## Topologie de déploiement

```
NAS Local (NOS-RICO)                Serveur Central (davic.mkdh.fr)
┌─────────────────────┐             ┌──────────────────────────┐
│ RicoFilm.jar        │             │ HTTP :3000 (backend Node) │
│ init.properties     │ ←─ REST ──→ │ MongoDB :27017            │
│ dir-films/          │ ──SFTP:4322→│ /home/ricohoho/test/      │
└─────────────────────┘             └──────────────────────────┘
```

---

## Conventions de code

- **Packages** : `ricohoho.themoviedb`, `ricohoho.mongo`, `ricohoho.tools`, `ricohoho.ffmpeg`
- **Langue** : mélange français/anglais (variables françaises, API anglaise)
- **Nommage** : camelCase classes/méthodes, UPPER_SNAKE_CASE champs MongoDB
- **Préfixes** : `i_` = entier compteur, `b` = booléen (legacy)
- **Style** : Java 8, synchrone/bloquant, try-catch explicites, pas d'async
- **Logs** : SLF4J + Logback ; `ricohoho.themoviedb` → FILE, `ricohoho.mongo` → CONSOLE

---

## Logging

| Appender | Destination | Niveau |
|----------|-------------|--------|
| CONSOLE | stdout | DEBUG (mongo, tools) |
| FILE | logs/ricofilm.log | DEBUG (themoviedb) |
| FILE-ROLLING | logs/archived/app.date.i.log | INFO (root) |

Rotation : 1 MB par fichier, 60 jours de rétention, 20 GB max.

---

## Points d'attention

- **Pas de pool MongoDB** : nouveau `MongoClient` à chaque opération — à optimiser pour les gros catalogues
- **Synchrone** : tout est bloquant, pas de parallélisme
- **Parsing nom de fichier** : regex sur l'année (`(19|20)\d{2}`) — peut échouer sur noms atypiques
- **Syntax spéciale** : `[[TMDBID]]` dans le nom de fichier force l'ID TheMovieDB
- **Idempotent** : une même action peut être relancée sans créer de doublons (vérification par `id` TMDB)
- **Tests** : les classes `*Test.java` sont des tests manuels/intégration, pas des tests unitaires automatisés
