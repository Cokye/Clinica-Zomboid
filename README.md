# 🏥 Clínica Zomboid

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![ArangoDB](https://img.shields.io/badge/ArangoDB-DDE072?style=for-the-badge&logo=arangodb&logoColor=black)](https://www.arangodb.com/)
[![Angular](https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white)](https://angular.dev/)
[![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)](https://react.dev/)

Sistema integral de gestión clínica compuesto por un backend robusto en **Spring Boot**, persistencia NoSQL/Multimodelo en **ArangoDB**, y dos clientes frontend independientes diseñados para flujos de usuario diferenciados: adquisición de convenios y agendamiento médico.

---

## 📌 Arquitectura del Sistema

El proyecto está dividido en tres módulos principales comunicados mediante APIs REST:

1. **Backend (`backend-clinica`):** API REST desarrollada con Spring Boot encargada de la lógica de negocio, autenticación, gestión de citas y aplicación de descuentos por convenios sobre ArangoDB.
2. **Portal de Convenios (`frontend-angular`):** Aplicación en Angular con módulo de login y autenticación. Permite a los usuarios consultar y adquirir convenios médicos que otorgan rebajas tarifarias.
3. **Portal de Agendamiento (`frontend-react`):** Aplicación en React enfocada en el flujo directo y ágil de búsqueda de especialistas, selección de horarios y reserva de horas médicas aplicando los convenios adquiridos.

              +--------------------------------+
              |           ArangoDB             |
              +--------------------------------+
                              ▲
                              │ (Spring Data / Driver)
              +--------------------------------+
              |     Backend (Spring Boot)      |
              +--------------------------------+
                            ▲        ▲
                 (REST/JSON)│        │(REST/JSON)
        +-------------------+        +-------------------+
        │                                                │
+-----------------------+                        +-----------------------+
|  Portal Convenios     |                        |  Portal Agendamiento  |
|      (Angular)        |                        |       (React)         |
| Login / Auth          |                        | Toma de horas         |
| Venta de convenios    |                        | Descuentos médicos    |
+-----------------------+                        +-----------------------+


## 🛠️ Tecnologías y Dependencias

### ⚙️ Backend (Spring Boot)
* **Lenguaje:** Java 17+
* **Framework:** Spring Boot 3.x
* **Dependencias principales:**
  * `spring-boot-starter-web`: Creación de endpoints RESTful.
  * `arangodb-spring-data` (o `arangodb-java-driver`): Conectividad y mapeo con la base de datos ArangoDB.
  * `spring-boot-starter-validation`: Validación de payloads en peticiones (Bean Validation).
  * `spring-boot-starter-security`: Manejo de autenticación y autorización (Login/JWT).
  * `lombok`: Reducción de código repetitivo (Getters, Setters, Builders).

### 🅰️ Frontend Angular (Gestión de Convenios y Login)
* **Framework:** Angular 17+ / 18+
* **Dependencias principales:**
  * `@angular/common/http`: Consumo de servicios REST con `HttpClient`.
  * `@angular/forms`: Manejo de formularios reactivos (`ReactiveFormsModule`) para el Login y compra.
  * `@angular/router`: Enrutamiento y protección de rutas mediante Guards.
  * `rxjs`: Programación reactiva y manejo de flujos asíncronos.

### ⚛️ Frontend React (Toma de Horas Médicas)
* **Librería base:** React 18+
* **Herramienta de compilación:** Vite / Create React App
* **Dependencias principales:**
  * `react-router-dom`: Navegación interna entre vistas del agendamiento.
  * `axios` (o Fetch API nativa): Peticiones HTTP al backend de citas.
  * `lucide-react` / `react-icons`: Iconografía de interfaz.

---

## 📋 Requisitos Previos

Asegúrate de contar con el siguiente entorno configurado en tu equipo:

* **Java JDK:** Versión 17 o superior.
* **Node.js:** Versión 18.x o 20.x (LTS recomendada) y `npm`.
* **Angular CLI:** `npm install -g @angular/cli`
* **ArangoDB:** Instancia local en ejecución (puerto por defecto `8529`) o contenedor Docker.

---

## 🚀 Instalación y Puesta en Marcha

### 1. Clonar el repositorio
```bash
git clone [https://github.com/Cokye/Clinica-Zomboid.git](https://github.com/Cokye/Clinica-Zomboid.git)
cd Clinica-Zomboid

### 2. Base de Datos (ArangoDB)

Asegúrate de tener ArangoDB levantado. Si usas Docker:

```bash
docker run -e ARANGO_ROOT_PASSWORD=rootpassword -p 8529:8529 -d --name arangodb-clinica arangodb:latest
```

Configura las credenciales en `backend-clinica/src/main/resources/application.properties`.

---

### 3. Backend (Spring Boot)

```bash
cd backend-clinica
./mvnw clean spring-boot:run
```

Disponible en: `http://localhost:8080`

---

### 4. Portal de Convenios (Angular)

```bash
cd ../frontend-angular
npm install
ng serve
```

Disponible en: `http://localhost:4200`

---

### 5. Portal de Agendamiento (React)

```bash
cd ../frontend-react
npm install
npm run dev
```

Disponible en: `http://localhost:5173`

---

## 🗄️ Modelo de Datos (ArangoDB)

Colecciones documentales utilizadas:

* **usuarios:** Pacientes registrados y credenciales.
* **convenios:** Tipos de coberturas, porcentajes de descuento y plazos de vigencia.
* **convenios_usuarios:** Registro de convenios contratados por paciente.
* **medicos:** Especialistas y bloques horarios disponibles.
* **citas:** Horas médicas agendadas con cálculo del descuento aplicado.

## 👤 Autor

* Desarrollado por **Felipe** ([@Cokye](https://github.com/Cokye))

