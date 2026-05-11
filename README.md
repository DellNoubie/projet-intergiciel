# E-Commerce Microservices

Application e-commerce complète basée sur une architecture microservices, avec un frontend Angular et un backend Spring Boot déployé via Docker.

---

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                  Angular Frontend (:4200)                │
└──────────────────────────┬──────────────────────────────┘
                           │ HTTP → localhost:8090
┌──────────────────────────▼──────────────────────────────┐
│              API Gateway (:8080 → exposé :8090)          │
│         JWT validation · routing · CORS                  │
└──┬──────────┬──────────┬──────────┬──────────┬──────────┘
   │          │          │          │          │
   ▼          ▼          ▼          ▼          ▼
 Auth      Product    Order     Payment    Report
(:8081)   (:8082)   (:8083)   (:8085)   (:8086)
   │          │          │          │          │
   └──────────┴──────────┴──────────┘          │
                    Eureka Server (:8761)       │
                                               │
                              JasperReports (PDF)
```

### Microservices

| Service | Port interne | Rôle |
|---|---|---|
| `api-gateway` | 8080 (→ 8090) | Routage, validation JWT, CORS |
| `eureka-server` | 8761 | Service discovery |
| `auth-service` | 8081 | Authentification, génération JWT |
| `product-service` | 8082 | Catalogue produits, images |
| `order-service` | 8083 | Commandes, panier |
| `payment-service` | 8085 | Paiement Razorpay |
| `report-service` | 8086 | Rapports PDF (JasperReports) |

---

## Prérequis

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (avec Docker Compose)
- [Node.js 18+](https://nodejs.org/) et npm
- [Java 17+](https://adoptium.net/) et Maven 3.8+ (pour rebuilder les services)
- Git

---

## Démarrage rapide

### 1. Cloner le projet

```bash
git clone <url-du-repo>
cd projet-intergiciel
```

### 2. Démarrer les microservices (Docker)

```bash
cd ecommerce-microservices
docker-compose up -d
```

Attendre ~2 minutes que tous les services démarrent. Vérifier l'état :

```bash
docker-compose ps
```

Tous les services doivent être `Up`. L'interface Eureka est accessible sur [http://localhost:8761](http://localhost:8761).

### 3. Démarrer le frontend Angular

```bash
cd ../e-commerce-frontend
npm install --legacy-peer-deps
ng serve
```

L'application est disponible sur [http://localhost:4200](http://localhost:4200).

---

## Comptes de test

| Rôle | Identifiant | Mot de passe |
|---|---|---|
| Admin | *(compte créé à l'inscription avec rôle Admin)* | — |
| Utilisateur | *(compte créé à l'inscription)* | — |

> Pour créer un compte admin, s'inscrire via l'interface puis modifier le rôle directement en base de données, ou utiliser l'endpoint `/registerNewUser` avec le champ `role: "Admin"`.

---

## Fonctionnalités implémentées

### Authentification
- Inscription et connexion avec JWT
- Rôles : `User` et `Admin`
- Protection des routes Angular via guards
- Propagation des headers d'auth (`X-Auth-User`, `X-Auth-Role`) entre microservices via Feign interceptor

### Catalogue produits
- Affichage paginé des produits (12 par page)
- Recherche par nom en temps réel
- Vue détail produit avec images
- Ajout/suppression de produits (Admin)
- Grille responsive (4 → 3 → 2 → 1 colonne)

### Panier
- Ajout de produits au panier
- Suppression d'articles
- Checkout depuis le panier

### Commandes
- Passage de commande (achat direct ou depuis le panier)
- Saisie de l'adresse de livraison
- Historique des commandes (utilisateur)
- Gestion des commandes avec filtre par statut : All / Placed / Delivered (Admin)
- Marquage comme livré (Admin)

### Paiement (Razorpay)
- Création d'un ordre de paiement côté backend
- Popup de paiement Razorpay (carte, UPI, netbanking)
- Vérification de signature HMAC-SHA256 après paiement
- Mise à jour du statut de commande après paiement confirmé

### Rapports PDF
- Rapport du catalogue produits (JasperReports)
- Rapport des commandes (JasperReports)
- Téléchargement direct depuis l'interface admin

---

## Configuration Razorpay (paiement de test)

Les clés de test sont configurées dans `payment-service/src/main/resources/application.yml` :

```yaml
razorpay:
  key:
    id: rzp_test_AXBzvN2fkD4ESK
    secret: bsZmiVD7p1GMo6hAWiy4SHSH
```

Pour tester un paiement, utiliser **UPI** avec l'identifiant `success@razorpay` dans la popup Razorpay.

---

## Rebuilder un microservice après modification

```bash
# Depuis le dossier du service (ex: payment-service)
cd ecommerce-microservices/payment-service
mvn clean package -DskipTests

# Depuis le dossier ecommerce-microservices
cd ..
docker-compose build payment-service
docker-compose up -d payment-service
```

---

## Structure du projet

```
projet-intergiciel/
├── e-commerce-frontend/          # Application Angular
│   └── src/app/
│       ├── _model/               # Interfaces TypeScript
│       ├── _services/            # Services HTTP Angular
│       ├── home/                 # Page d'accueil (grille produits)
│       ├── cart/                 # Panier
│       ├── buy-product/          # Tunnel de commande + paiement
│       ├── order-details/        # Gestion commandes (Admin)
│       └── ...
│
└── ecommerce-microservices/
    ├── docker-compose.yml        # Orchestration de tous les services
    ├── api-gateway/              # Spring Cloud Gateway
    ├── eureka-server/            # Netflix Eureka
    ├── auth-service/             # JWT + Spring Security
    ├── product-service/          # Catalogue + images
    ├── order-service/            # Commandes + panier (H2 en mémoire)
    ├── payment-service/          # Razorpay (H2 en mémoire)
    └── report-service/           # JasperReports
```

---

## Notes importantes

- Les bases de données `order-service` et `payment-service` utilisent **H2 en mémoire** (`create-drop`). Les données sont perdues à chaque redémarrage du container.
- Le `product-service` et `auth-service` utilisent une base persistante (selon la config Docker Compose).
- Le démarrage complet de tous les services peut prendre **2 à 5 minutes** selon la machine.
