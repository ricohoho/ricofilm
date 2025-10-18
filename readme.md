"#RicoFilm Java" 
Réparation du Git

# Configuration 
Installer OpenJDK 8

`sudo apt install openjdk-8-jdk`

Clone du code : 

`git clone https://github.com/ricohoho/ricofilm.git`

Installation de Mvn 

` sudo apt install maven`


# Compile : création du jar ou execution 
**creation du package :**
RicoFilm2-0.0.1-SNAPSHOT-jar-with-dependencies.jar
` mvn package`

**execution je l'application : (en maven sans jar) **
` mvn exec:java -Dexec.mainClass="ricohoho.themoviedb.RicoFilm"`

**execution je l'application : (avec le jar jar) **
` java -jar Ricofilm action path`
**LEs paramètres :** 
patch : /home/efassel/ricofilm/ricofilm/dir-films/
action : 
Les parametres d'action possibles sont :
**DOWNLOAD_IMAGE**  => provoque le teechargment de l'image des fichier film dans le dossier :/home/efassel/ricofilm/ricofilm/dir-films/

**AJOUT_FILM**                    => provoque l'ajout dans la base Ricofilms des fichiers du dossier :/home/efassel/ricofilm/ricofilm/dir-films/

**AJOUT_FILM_SANS_IMAGE**    => provoque l'ajout dans la base Ricofilms, sans l'image affiche,  des fichiers du dossier :/home/efassel/ricofilm/ricofilm/dir-films/

**SUPPRIME_FILM**                   => provoque la suppression dans le base des Ricofilms n'etant plus dans le dossier :/home/efassel/ricofilm/ricofilm/dir-films/

**MAJ_FILM**                        => provoque la MAJ des films : ajout + supprime dans le base dans le dossier :/home/efassel/ricofilm/ricofilm/dir-films/

**MAJ_FILM_SANS_IMAGE**      => provoque la MAJ des films : ajout + supprime dans le base dans le dossier :/home/efassel/ricofilm/ricofilm/dir-films/

**REQUEST_FILM**                    => interoge la base ricofilms afin de savoir si un fichier de votre serveur a ete demande et si oui l'envoi !