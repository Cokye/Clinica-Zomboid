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
