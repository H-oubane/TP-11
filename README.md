# TP 11 — Localisation d’un smartphone et envoi des coordonnées vers un serveur distant

Application Android qui récupère la position GPS et l'envoie à un serveur PHP pour stockage dans une base MySQL.

## Objectif
- Récupérer la latitude et la longitude du smartphone
- Envoyer les coordonnées à un serveur PHP via HTTP POST
- Stocker les données dans une base MySQL
- Comprendre les permissions de localisation et réseau

## Architecture
Smartphone (Android)

        │
        │ GPS
        ▼

Position détectée

        │
        │ HTTP POST
        ▼

Serveur PHP (XAMPP)

        │
        │ INSERT
        ▼

Base MySQL (localisation.position)



## Structure du projet
LocalisationSmartphone/

└── app/src/main/java/com/example/localisation/

     ├── MainActivity.java

     └── res/layout/activity_main.xml
     

└── php/ → Code PHP (backend)

├── classe/

    └── Position.php

├── connexion/

    └── Connexion.php

├── dao/

    └── IDao.php

├── service/

    └── PositionService.php

└── createPosition.php



## Captures d'écran

### 1. Application avec position GPS
<img width="374" height="720" alt="image" src="https://github.com/user-attachments/assets/c7e54445-969e-4af1-84b2-ba27c821309f" />

### 2. Requête Postman (200 OK)
<img width="1270" height="718" alt="image" src="https://github.com/user-attachments/assets/26eeb148-0484-4ff1-a023-a481f3302e1a" />

### 3. Table MySQL (phpMyAdmin)
<img width="1069" height="378" alt="image" src="https://github.com/user-attachments/assets/bb1efef4-6977-4eb9-bb76-e70812406244" />


## Ce qui fonctionne 

| Fonctionnalité | Statut |
|----------------|--------|
| Récupération GPS | Lat/Lon affichées |
| Affichage des coordonnées à l'écran |  TextView mis à jour |
| Code Java (Volley, LocationManager) |  Correct |
| Code PHP |  Testé avec Postman |
| Base MySQL |  Insertion fonctionnelle (Postman) |


## Technologies utilisées

### Côté mobile
- Android Studio
- Java
- LocationManager (GPS)
- Volley (HTTP)
- API minimum : 24 (Android 7.0)

### Côté serveur
- XAMPP (Apache + MySQL)
- PHP 8
- PDO
- MySQL



## Auteur
**H-oubane**
