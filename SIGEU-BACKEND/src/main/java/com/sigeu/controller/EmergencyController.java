package com.sigeu.controller;

import com.sigeu.model.EmergencyRequest;
import com.sigeu.model.EmergencyResponse;
import com.sigeu.service.EmergencyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for the emergency endpoint.
 * Exposes POST /emergency to the frontend.
 */
@RestController
@RequestMapping("/emergency")
@CrossOrigin(origins = "*")   // Allow requests from GitHub Pages or any frontend
public class EmergencyController {

    private final EmergencyService emergencyService;

    public EmergencyController(EmergencyService emergencyService) {
        this.emergencyService = emergencyService;
    }

    /**
     * POST /emergency
     *
     * Receives emergency data from the citizen frontend,
     * classifies it, and returns the assigned entity + priority.
     *
     * Request body example:
     * {
     *   "type": "fire",
     *   "answers": ["hay humo denso", "hay personas atrapadas"],
     *   "location": { "lat": 4.710989, "lng": -74.072092 },
     *   "description": "Incendio en edificio de apartamentos"
     * }
     *
     * Response example:
     * {
     *   "entity": "Bomberos",
     *   "priority": "Alta",
     *   "message": "Los bomberos han sido alertados. Prioridad ALTA — unidades en camino.",
     *   "type": "fire",
     *   "reportId": "EM-A1B2C3D4"
     * }
     */
    @PostMapping
    public ResponseEntity<EmergencyResponse> receiveEmergency(
            @Valid @RequestBody EmergencyRequest request) {

        EmergencyResponse response = emergencyService.classify(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /emergency/health
     * Simple health check so Render knows the service is alive.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status",  "UP",
            "service", "SIGEU Backend",
            "version", "1.0.0"
        ));
    }
}
