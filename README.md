[README.md](https://github.com/user-attachments/files/26195002/README.md)
# SIGEU Backend — Spring Boot

Sistema Inteligente de Gestión de Emergencias Urbanas  
Backend REST API — Módulo Final

---

## Estructura del proyecto

```
sigeu-backend/
├── src/main/java/com/sigeu/
│   ├── SigeuApplication.java          ← Punto de entrada
│   ├── controller/
│   │   ├── EmergencyController.java   ← POST /emergency, GET /emergency/health
│   │   └── GlobalExceptionHandler.java← Manejo de errores
│   ├── model/
│   │   ├── EmergencyRequest.java      ← Datos que recibe el backend
│   │   └── EmergencyResponse.java     ← Datos que devuelve el backend
│   └── service/
│       └── EmergencyService.java      ← Lógica de clasificación
├── src/main/resources/
│   └── application.properties
├── Dockerfile                         ← Para despliegue en Render
├── pom.xml
└── api.js                             ← Copiar al frontend
```

---

## Endpoint

### POST /emergency

**Request:**
```json
{
  "type": "fire",
  "answers": ["hay personas atrapadas", "el fuego se expande"],
  "location": { "lat": 4.710989, "lng": -74.072092 },
  "description": "Incendio en edificio residencial"
}
```

**Response:**
```json
{
  "entity": "Bomberos",
  "priority": "Alta",
  "message": "Los bomberos han sido alertados. Prioridad ALTA — unidades en camino de inmediato.",
  "type": "fire",
  "reportId": "EM-A1B2C3D4"
}
```

### GET /emergency/health
```json
{ "status": "UP", "service": "SIGEU Backend", "version": "1.0.0" }
```

---

## Cómo desplegar en Render (paso a paso)

### 1. Subir el backend a GitHub

```bash
# Desde la carpeta sigeu-backend/
git init
git add .
git commit -m "feat: SIGEU Spring Boot backend"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/sigeu-backend.git
git push -u origin main
```

### 2. Crear el servicio en Render

1. Entra a **https://render.com** y crea una cuenta gratuita
2. Haz clic en **"New +"** → **"Web Service"**
3. Conecta tu cuenta de GitHub y selecciona el repositorio `sigeu-backend`
4. Configura el servicio:

| Campo | Valor |
|---|---|
| **Name** | sigeu-backend |
| **Region** | Oregon (US West) |
| **Branch** | main |
| **Runtime** | Docker |
| **Instance Type** | Free |

5. Haz clic en **"Create Web Service"**
6. Render construirá el Docker automáticamente (tarda ~5 minutos la primera vez)

### 3. Obtener la URL pública

Cuando el deploy termine verás:
```
https://sigeu-backend.onrender.com
```
Esa es tu URL. Pruébala en el navegador:
```
https://sigeu-backend.onrender.com/emergency/health
```
Debe responder: `{ "status": "UP" }`

### 4. Conectar con el frontend

1. Copia `api.js` a la carpeta de tu frontend (donde están los .html)
2. Abre `api.js` y cambia la URL:
```javascript
const BACKEND_URL = 'https://sigeu-backend.onrender.com'; // tu URL real
```
3. En `citizen.html`, agrega este script ANTES de `citizen.js`:
```html
<script src="api.js"></script>
```
4. En `citizen.js`, dentro del submit del formulario, después de guardar localmente agrega:
```javascript
// After addEmergency(...) call:
const backendResult = await sendToBackend({
  type:        selectedType,
  description: descIn.value.trim(),
  location:    locationText,
  coords:      gpsCoords,
  answers:     [],
});

if (backendResult) {
  showAlert(
    `✅ Reporte enviado. ${backendResult.entity} asignado — Prioridad ${backendResult.priority}`,
    'success'
  );
}
```

---

## Nota sobre Render Free tier

El plan gratuito de Render **hiberna el servicio** después de 15 minutos sin uso.  
La primera petición después de la hibernación tarda ~30 segundos (cold start).  
Para una presentación académica, abre la URL de health check en el navegador  
5 minutos antes para que el servidor esté activo:
```
https://sigeu-backend.onrender.com/emergency/health
```

---

## Probar localmente antes de subir

```bash
# Requiere Java 17 y Maven instalados
cd sigeu-backend
mvn spring-boot:run
```

Prueba con curl:
```bash
curl -X POST http://localhost:8080/emergency \
  -H "Content-Type: application/json" \
  -d '{"type":"fire","answers":["hay heridos"],"location":{"lat":4.71,"lng":-74.07}}'
```
