/* ============================================================
   SIGEU — api.js
   Connection between the existing frontend and the Spring Boot backend.
   Add this file to your project and include it in citizen.html
   BEFORE citizen.js:  <script src="api.js"></script>
   ============================================================ */

'use strict';

/* ── Backend URL — change this after deploying to Render ── */
const BACKEND_URL = 'https://sigeu-backend.onrender.com'; // <- replace with your Render URL

/**
 * Sends an emergency report to the Spring Boot backend.
 * Calls POST /emergency and returns the classification result.
 *
 * @param {Object} data - Emergency data
 * @param {string} data.type        - Emergency type (accident, fire, robbery, medical, other)
 * @param {string} data.description - Optional description
 * @param {string} data.location    - Location text
 * @param {Object} data.coords      - { lat, lng } from GPS or null
 * @param {Array}  data.answers     - Optional answers for priority classification
 *
 * @returns {Promise<{entity, priority, message, reportId} | null>}
 *          Returns null if the backend is unreachable (graceful fallback)
 */
async function sendToBackend(data) {

  /* Build the request body matching EmergencyRequest.java */
  const body = {
    type:        data.type,
    description: data.description || '',
    answers:     data.answers     || [],
    location: data.coords
      ? { lat: parseFloat(data.coords.lat), lng: parseFloat(data.coords.lng) }
      : { lat: 4.710989, lng: -74.072092 }, // default: Bogotá center
  };

  try {
    const response = await fetch(`${BACKEND_URL}/emergency`, {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify(body),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      console.warn('SIGEU backend error:', errorData);
      return null; // Graceful fallback — report still saves locally
    }

    const result = await response.json();
    console.log('SIGEU backend response:', result);
    return result;

  } catch (networkError) {
    /* Backend unreachable (offline, cold start on Render, etc.) */
    console.warn('SIGEU: Backend no disponible, guardando solo localmente.', networkError);
    return null;
  }
}

/**
 * Checks if the backend is alive.
 * Useful to show a status indicator in the UI.
 * @returns {Promise<boolean>}
 */
async function checkBackendHealth() {
  try {
    const res = await fetch(`${BACKEND_URL}/emergency/health`, { method: 'GET' });
    return res.ok;
  } catch {
    return false;
  }
}
