# E-Commerce — Application Full Stack

Application e-commerce complète avec un frontend **Angular** et un backend **Spring Boot** déployé via Docker.

---

## Architecture

```
Angular Frontend (:4200)
        │
        │ HTTP → localhost:8090
        ▼
┌─────────────────────────────────────────┐
│           e-commerce-backend            │
│                                         │
│  auth-service       :8090  ← point      │
│  user-service       :8081    d'entrée   │
│  product-add        :8082    unique     │
│  account-creation   :8083    du         │
│  invoice-service    :8084    frontend   │
│  image-service      :8085               │
│  product-display    :8086               │
│                                         │
│  PostgreSQL (plusieurs bases)           │
└─────────────────────────────────────────┘
```

---

## Prérequis

| Outil | Version minimale | Vérification |
|---|---|---|
| [Docker Desktop](https://www.docker.com/products/docker-desktop/) | 20+ | `docker --version` |
| [Node.js](https://nodejs.org/) | 16 ou 18 (LTS) | `node --version` |
| npm | 8+ (inclus avec Node) | `npm --version` |
| Git | toute version | `git --version` |

> Java et Maven **ne sont pas nécessaires** : les JARs sont déjà compilés dans le dépôt.

---

## Lancer le projet

### 1. Cloner le dépôt

```bash
git clone <url-du-repo>
cd projet-intergiciel
```

### 2. Démarrer le backend (Docker)

Le fichier `docker-compose.yml` se trouve dans `e-commerce-backend`.

```bash
cd e-commerce-backend
docker compose up -d
```

> Sur les anciennes versions de Docker, utiliser `docker-compose up -d` (avec le tiret).

Attendre ~1-2 minutes, puis vérifier que tous les containers sont `Up` :

```bash
docker compose ps
```

Les services doivent tous apparaître avec le statut `Up`. Le point d'entrée principal du frontend est **[http://localhost:8090](http://localhost:8090)**.

### 3. Démarrer le frontend Angular

Ouvrir un **nouveau terminal** et se placer dans le dossier frontend :

```bash
cd e-commerce-frontend
```

Installer les dépendances (à faire une seule fois) :

```bash
npm install --legacy-peer-deps
```

Lancer le serveur de développement :

```bash
npm start
```

> **Important** : utiliser `npm start` et **non** `ng serve`. Le script `npm start` inclut l'option `--openssl-legacy-provider` requise par cette version d'Angular avec Node 17+. Sans cela, la compilation échoue.

L'application est disponible sur **[http://localhost:4200](http://localhost:4200)**.

---

## Problèmes fréquents

### Erreur OpenSSL au démarrage du frontend

```
Error: error:0308010C:digital envelope routines::unsupported
```

**Cause** : Node.js 17+ a désactivé des algorithmes OpenSSL legacy utilisés par l'ancienne version de webpack d'Angular.

**Solution** : toujours lancer le frontend avec `npm start` (pas `ng serve`). Si on veut utiliser `ng serve` directement, définir la variable d'environnement avant :

```bash
# Windows PowerShell
$env:NODE_OPTIONS = "--openssl-legacy-provider"
ng serve

# Windows CMD
set NODE_OPTIONS=--openssl-legacy-provider
ng serve

# macOS / Linux
export NODE_OPTIONS=--openssl-legacy-provider
ng serve
```

### Le frontend affiche "0 articles disponibles"

Le backend met quelques secondes à être prêt après `docker compose up`. Attendre 1-2 minutes et rafraîchir la page.

### Un container Docker est en erreur (`Exit` ou `Restarting`)

Consulter les logs du service concerné :

```bash
docker compose logs auth-service
```

Remplacer `auth-service` par le nom du service en erreur (`user-service`, `product-add-service`, etc.).

### Conflit de port (port déjà utilisé)

Si le port 8090 est déjà occupé par un autre processus :

```bash
# Windows PowerShell
netstat -ano | findstr :8090

# macOS / Linux
lsof -i :8090
```

---

## Comptes utilisateurs

L'inscription se fait directement depuis l'interface sur [http://localhost:4200/register](http://localhost:4200/register).

Par défaut, tout nouveau compte a le rôle **User**. Pour obtenir les droits **Admin** :

1. S'inscrire via l'interface
2. Aller dans la table `roles` et `user_role` de la base `auth_db` et affecter le rôle Admin au compte

---

## Fonctionnalités

| Fonctionnalité | User | Admin |
|---|:---:|:---:|
| Parcourir le catalogue | ✓ | ✓ |
| Rechercher un produit | ✓ | ✓ |
| Ajouter au panier | ✓ | ✓ |
| Passer une commande | ✓ | ✓ |
| Consulter ses commandes | ✓ | ✓ |
| Ajouter / modifier un produit | — | ✓ |
| Gérer toutes les commandes | — | ✓ |
| Marquer une commande comme livrée | — | ✓ |

---

## Structure du projet

```
projet-intergiciel/
│
├── e-commerce-frontend/          # Application Angular 21
│   ├── src/app/
│   │   ├── _model/               # Interfaces TypeScript (Product, User...)
│   │   ├── _services/            # Services HTTP Angular
│   │   ├── home/                 # Page d'accueil — grille produits
│   │   ├── login/                # Connexion
│   │   ├── register/             # Inscription
│   │   ├── product-view-details/ # Détail produit
│   │   ├── cart/                 # Panier
│   │   ├── buy-product/          # Tunnel de commande
│   │   ├── my-orders/            # Historique commandes (User)
│   │   ├── order-details/        # Gestion commandes (Admin)
│   │   ├── add-new-product/      # Ajout / modification produit (Admin)
│   │   └── show-product-details/ # Liste produits (Admin)
│   └── package.json
│
├── e-commerce-backend/           # Backend Spring Boot (JARs pré-compilés)
│   ├── Docker-compose.yml        # Orchestration des services + PostgreSQL
│   ├── auth-service.jar          # Authentification JWT — port 8090
│   ├── user-service.jar          # Gestion utilisateurs — port 8081
│   ├── product-add-service.jar   # Ajout produits — port 8082
│   ├── account-creation-service.jar  # Création de comptes — port 8083
│   ├── invoice-service.jar       # Facturation — port 8084
│   ├── image-service.jar         # Gestion images — port 8085
│   └── product-display-service.jar   # Affichage produits — port 8086
│
└── ecommerce-microservices/      # Version microservices avancée (Spring Cloud)
    ├── docker-compose.yml
    ├── api-gateway/              # Spring Cloud Gateway
    ├── eureka-server/            # Service discovery
    ├── auth-service/
    ├── product-service/
    ├── order-service/
    ├── payment-service/
    └── report-service/
```

---

## Arrêter le projet

```bash
# Arrêter les containers sans supprimer les données
cd e-commerce-backend
docker compose down

# Arrêter ET supprimer les volumes (repart de zéro)
docker compose down -v
```

---

## Technologies utilisées

| Couche | Technologie |
|---|---|
| Frontend | Angular 21, Angular Material, Bootstrap 5 |
| Backend | Spring Boot 2.4, Spring Security, JWT |
| Base de données | PostgreSQL 15 (via Docker) |
| Conteneurisation | Docker, Docker Compose |
| Build backend | Maven (JARs pré-compilés inclus) |
