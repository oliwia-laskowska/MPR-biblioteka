# Library App — Spring Boot REST API (PostgreSQL + JPA + Swagger + JaCoCo)

Projekt zaliczeniowy: aplikacja REST w temacie **Biblioteka**.  
Aplikacja udostępnia CRUD dla encji bibliotecznych oraz obsługę relacji między encjami.
Komunikacja odbywa się przez REST API w formacie JSON, a dokumentacja endpointów jest udostępniona w Swagger UI.

---

## 1. Technologie

- Java 17+
- Spring Boot 3.x
- Maven
- Spring Web (REST)
- Spring Data JPA (ORM / Hibernate)
- PostgreSQL
- Swagger / OpenAPI (springdoc-openapi)
- JUnit 5 + Mockito (testy jednostkowe)
- JaCoCo (weryfikacja minimalnego pokrycia testami)

---

## 2. Uruchomienie aplikacji

### 2.1. Wymagania lokalne
- Java 17+ (np. Temurin / Corretto)
- Maven 3+
- PostgreSQL (lokalnie lub Docker)

### 2.2. Utworzenie bazy danych
Zaloguj się do PostgreSQL i wykonaj:

```sql
CREATE DATABASE library;
````

### 2.3. Konfiguracja połączenia z bazą

Plik: `src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/library
    username: postgres
    password: postgres
```

* docker exec -it postgres-library psql -U postgres -d library


### 2.4. Build + testy + JaCoCo check

```bash
mvn clean verify
```

> Komenda `verify` uruchamia testy + generuje raport JaCoCo + sprawdza minimalny próg pokrycia.

### 2.5. Start aplikacji

```bash
mvn spring-boot:run
```

Aplikacja startuje na:

* `http://localhost:8080`

---

## 3. Swagger (dokumentacja API)

Swagger UI dostępny jest pod:

* `http://localhost:8080/swagger-ui.html`

W Swaggerze można:

* zobaczyć wszystkie endpointy
* wysyłać requesty (POST/GET/PUT/DELETE)
* testować aplikację bez Postmana

---

## 4. Encje (min. 5 encji)

Aplikacja posiada 5 encji:

1. **User** – użytkownik biblioteki
2. **UserProfile** – profil użytkownika (adres, telefon)
3. **Book** – książka
4. **Author** – autor
5. **Loan** – wypożyczenie książki

---

## 5. Relacje pomiędzy encjami (1:1, 1:N, N:M)

### 5.1. Relacja 1:1 (jeden do jeden)

* `User ↔ UserProfile`
  Jeden użytkownik posiada maksymalnie jeden profil.

### 5.2. Relacja 1:N (jeden do wiele)

* `User → Loan`
  Jeden użytkownik może mieć wiele wypożyczeń.
* `Book → Loan`
  Jedna książka może być wypożyczana wielokrotnie (historia wypożyczeń).

### 5.3. Relacja N:M (wiele do wiele)

* `Book ↔ Author`
  Książka może mieć wielu autorów, a autor może napisać wiele książek.

---

## 6. Architektura aplikacji (flow)

Aplikacja jest zbudowana zgodnie z warstwami:

**RESTController → Service → Repository**

* **Controller** przyjmuje żądania REST
* **Service** zawiera logikę biznesową, walidacje i obsługę wyjątków
* **Repository** odpowiada za zapis/odczyt danych (Spring Data JPA)

Dane przesyłane są w JSON, a obiekty wejściowe/wyjściowe są mapowane przez DTO oraz klasy mapperów.

---

## 7. Walidacja i wyjątki (custom checked exceptions)

Walidacja logiki biznesowej realizowana jest poprzez własne **checked exceptions**:

* `ValidationException extends Exception`
* `BusinessRuleException extends Exception`
* `NotFoundException extends Exception`

Przykłady walidacji:

* nie można utworzyć profilu, jeśli user już go posiada
* nie można wypożyczyć książki, jeśli brak dostępnych egzemplarzy
* nie można oddać wypożyczenia, które już zostało oddane
* nie można tworzyć użytkownika z istniejącym adresem email

Błędy są zwracane w JSON przez `ApiExceptionHandler`.

---

## 8. CRUD — funkcjonalności aplikacji

Aplikacja dostarcza CRUD dla encji w kontekście bibliotecznym:

* **Users** (CRUD)
* **Profiles** (CRUD)
* **Authors** (CRUD)
* **Books** (CRUD)
* **Loans** (CRUD + dodatkowo oddanie książki)

---

## 9. Brak SQL Query

Aplikacja **nie zawiera żadnych zapytań SQL**:

* brak `@Query`
* brak native SQL
* brak JDBC Template

Zapis i odczyt danych odbywa się wyłącznie poprzez Spring Data JPA oraz ORM (Hibernate).

---

## 10. Testy jednostkowe i JaCoCo (min. 50%)

Projekt posiada testy jednostkowe napisane w JUnit 5 + Mockito.
Minimalny próg pokrycia kodu ustawiony w JaCoCo to **50%** i jest sprawdzany w trakcie budowania aplikacji.

### 10.1. Uruchomienie testów i sprawdzenie progu

```bash
mvn clean verify
```

### 10.2. Raport JaCoCo (HTML)

Po buildzie raport dostępny jest w:

* `target/site/jacoco/index.html`

Na macOS:

```bash
open target/site/jacoco/index.html
```

---

## 11. Lista endpointów (skrót)

### Users

* `POST /api/users`
* `GET /api/users`
* `GET /api/users/{id}`
* `PUT /api/users/{id}`
* `DELETE /api/users/{id}`

### Profiles

* `POST /api/users/{userId}/profile`
* `GET /api/profiles/{profileId}`
* `PUT /api/profiles/{profileId}`
* `DELETE /api/profiles/{profileId}`

### Authors

* `POST /api/authors`
* `GET /api/authors`
* `GET /api/authors/{id}`
* `PUT /api/authors/{id}`
* `DELETE /api/authors/{id}`

### Books

* `POST /api/books`
* `GET /api/books`
* `GET /api/books/{id}`
* `PUT /api/books/{id}`
* `DELETE /api/books/{id}`
* `POST /api/books/{bookId}/authors/{authorId}` (przypisanie autora)
* `DELETE /api/books/{bookId}/authors/{authorId}` (usunięcie autora)

### Loans

* `POST /api/loans` (wypożycz książkę)
* `GET /api/loans`
* `GET /api/loans/{id}`
* `PUT /api/loans/{id}/return` (oddaj książkę)
* `DELETE /api/loans/{id}`

---

## 12. Przykładowy scenariusz testowania (demo)

1. Dodaj użytkownika: `POST /api/users`
2. Dodaj profil: `POST /api/users/{userId}/profile`
3. Dodaj autora: `POST /api/authors`
4. Dodaj książkę: `POST /api/books`
5. Przypisz autora do książki: `POST /api/books/{bookId}/authors/{authorId}`
6. Wypożycz książkę: `POST /api/loans`
7. Oddaj książkę: `PUT /api/loans/{id}/return`
8. Sprawdź, że `availableCopies` wróciło do poprawnej wartości: `GET /api/books/{id}`

---

## Autor

Oliwia Laskowska

