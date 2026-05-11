# 🔄 Migration H2 → PostgreSQL

## Résumé

Migration complète de la base de données mémoire **H2** vers **PostgreSQL** pour les microservices d'e-commerce.

### ✅ Services Migrés

- ✅ **auth-service** (port 8081) → `postgres-auth:5432`
- ✅ **product-service** (port 8083) → `postgres-product:5432`
- ✅ **order-service** (port 8084) → `postgres-order:5432`
- ✅ **payment-service** (port 8085) → `postgres-payment:5432`

---

## 📋 Changements Effectués

### 1️⃣ **Dépendances Maven (pom.xml)**

**Avant (H2):**

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Après (PostgreSQL):**

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Services modifiés:**

- `ecommerce-microservices/auth-service/pom.xml`
- `ecommerce-microservices/product-service/pom.xml`
- `ecommerce-microservices/order-service/pom.xml`
- `ecommerce-microservices/payment-service/pom.xml`

---

### 2️⃣ **Configuration Spring Boot (application.yml)**

**Avant (H2 en mémoire):**

```yaml
datasource:
  url: jdbc:h2:mem:auth_db
  driver-class-name: org.h2.Driver
  username: sa
  password:

h2:
  console:
    enabled: true
    path: /h2-console

jpa:
  database-platform: org.hibernate.dialect.H2Dialect
  hibernate:
    ddl-auto: create-drop
```

**Après (PostgreSQL):**

```yaml
datasource:
  url: jdbc:postgresql://postgres-auth:5432/auth_db
  driver-class-name: org.postgresql.Driver
  username: admin
  password: admin123
  hikari:
    maximum-pool-size: 10
    minimum-idle: 2

jpa:
  database-platform: org.hibernate.dialect.PostgreSQLDialect
  hibernate:
    ddl-auto: update
  show-sql: false
  properties:
    hibernate:
      format_sql: true
```

**Services modifiés:**

- `auth-service/src/main/resources/application.yml`
- `product-service/src/main/resources/application.yml`
- `order-service/src/main/resources/application.yml`
- `payment-service/src/main/resources/application.yml`

**Différences clés:**

| Paramètre | H2 | PostgreSQL |
|-----------|----|-----------|
| **URL** | `jdbc:h2:mem:*_db` | `jdbc:postgresql://host:5432/*_db` |
| **Driver** | H2Driver | PostgreSQL Driver |
| **Utilisateur** | `sa` | `admin` |
| **Mot de passe** | vide | `admin123` |
| **DDL Auto** | `create-drop` | `update` |
| **Console H2** | ✅ Enabled | ❌ Supprimée |
| **Connection Pooling** | Aucun | Hikari (10 connexions) |

---

### 3️⃣ **Docker Compose (docker-compose.yml)**

#### 🆕 Ajout des services PostgreSQL

```yaml
# ─── PostgreSQL Databases ────────────────────────────────────────
postgres-auth:
  image: postgres:15-alpine
  container_name: postgres-auth
  environment:
    POSTGRES_DB: auth_db
    POSTGRES_USER: admin
    POSTGRES_PASSWORD: admin123
  ports:
    - "5432:5432"
  volumes:
    - postgres-auth-data:/var/lib/postgresql/data
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U admin -d auth_db"]
    interval: 10s
    timeout: 5s
    retries: 5
  networks:
    - microservices-network

# Similaire pour:
# - postgres-product (port 5433)
# - postgres-order (port 5434)
# - postgres-payment (port 5435)
```

#### 🔗 Dépendances des Services

**Avant:**

```yaml
depends_on:
  eureka-server:
    condition: service_healthy
```

**Après:**

```yaml
depends_on:
  eureka-server:
    condition: service_healthy
  postgres-auth:           # ← NOUVEAU
    condition: service_healthy
```

#### 🔐 Variables d'Environnement

Ajoutées pour chaque service:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres-auth:5432/auth_db
  SPRING_DATASOURCE_USERNAME: admin
  SPRING_DATASOURCE_PASSWORD: admin123
```

#### 💾 Volumes Persistants

```yaml
volumes:
  postgres-auth-data:
    driver: local
  postgres-product-data:
    driver: local
  postgres-order-data:
    driver: local
  postgres-payment-data:
    driver: local
```

---

## 🚀 Comment Utiliser

### Démarrage avec Docker Compose

```bash
cd ecommerce-microservices
docker-compose up -d
```

Les 4 bases PostgreSQL et tous les microservices démarreront automatiquement.

### ✅ Vérification du Statut

```bash
# Lister tous les conteneurs
docker-compose ps

# Vérifier les logs d'un service
docker-compose logs auth-service
docker-compose logs postgres-auth
```

### 🔍 Se Connecter à PostgreSQL

```bash
# Depuis le terminal du host
psql -h localhost -p 5432 -U admin -d auth_db
# Mot de passe: admin123

# Pour les autres BD:
psql -h localhost -p 5433 -U admin -d product_db
psql -h localhost -p 5434 -U admin -d order_db
psql -h localhost -p 5435 -U admin -d payment_db
```

### 📋 Ports PostgreSQL Exposés

| Service | Container | Port Local |
|---------|-----------|-----------|
| postgres-auth | 5432 | 5432 |
| postgres-product | 5432 | 5433 |
| postgres-order | 5432 | 5434 |
| postgres-payment | 5432 | 5435 |

---

## ⚙️ Configuration de Hiberntate

### DDL Auto Modes

- **H2** utilisait: `create-drop` → Créé/supprimé à chaque redémarrage ❌
- **PostgreSQL** utilise: `update` → Schéma persist et s'adapte ✅

Cela signifie que vos données **survivront** au redémarrage des services! 🎉

### Connection Pooling

PostgreSQL utilise **HikariCP** avec:

- **Max Pool Size**: 10 connexions
- **Min Idle**: 2 connexions
- Améliore les performances et la stabilité

---

## 🔒 Sécurité en Production

⚠️ **NE JAMAIS utiliser ces identifiants en production!**

**À faire:**

1. Changer le mot de passe: `admin123` → mot de passe fort
2. Utiliser des variables d'environnement:

   ```bash
   POSTGRES_PASSWORD=${DB_PASSWORD}
   SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
   ```

3. Configurer SSL/TLS pour les connexions PostgreSQL
4. Restreindre les ports (5432-5435) au réseau interne uniquement

---

## 📊 Données Historiques

Les données créées dans H2 **ne seront pas migrées**.

- H2 était une BD mémoire (données perdues à l'arrêt)
- PostgreSQL est une BD persistante (données sauvegardées)

✅ C'est un point de départ frais et idéal!

---

## 🆘 Dépannage

### Erreur: "postgres-auth container failed to start"

```bash
docker-compose logs postgres-auth
docker-compose up -d postgres-auth  # Redémarrer
```

### Erreur: "Connection refused"

```bash
# Attendre que PostgreSQL soit prêt (~10s)
docker-compose down
docker-compose up -d
sleep 15
```

### Erreur: "Port already in use"

```bash
# Arrêter les services actifs
docker-compose down
# Puis redémarrer
docker-compose up -d
```

---

## ✨ Avantages de PostgreSQL

| Caractéristique | H2 | PostgreSQL |
|-----------------|----|-----------|
| **Type** | In-Memory | Persistante |
| **Production Ready** | ❌ Non | ✅ Oui |
| **Performance** | Rapide (mémoire) | Très rapide (optimisée) |
| **Scalabilité** | Limitée | Illimitée |
| **Données Persistantes** | ❌ Non | ✅ Oui |
| **Transactions** | ✅ Basiques | ✅ Avancées |
| **Backup/Recovery** | ❌ Impossible | ✅ Facile |
| **Monitoring** | ❌ Limité | ✅ Complet |

---

## 📝 Notes

- Tous les services utilisent **PostgreSQL 15 Alpine** (image légère ~80MB)
- Les données sont stockées en volumes Docker (`postgres-*-data`)
- Les données persistent même si les conteneurs sont supprimés
- Les migrations Hibernate se font automatiquement (`ddl-auto: update`)

---
