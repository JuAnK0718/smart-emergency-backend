package com.sigeu.service;

import com.sigeu.model.EmergencyRequest;
import com.sigeu.model.EmergencyResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Core business logic for emergency classification.
 *
 * Rules:
 *   accident → Hospital
 *   fire     → Bomberos
 *   robbery  → Policía
 *   medical  → Hospital
 *   other    → Policía (default)
 *
 * Priority is determined by keywords in the answers list:
 *   Alta  — injuries, fire spreading, weapon present, unconscious
 *   Media — property damage, smoke, threat
 *   Baja  — minor incident, no injuries reported
 */
@Service
public class EmergencyService {

    // ── High-priority keywords (Spanish) ──
    private static final List<String> HIGH_PRIORITY_KEYWORDS = List.of(
        "herido", "heridos", "muerto", "muertos", "inconsciente",
        "sangre", "arma", "disparo", "explosión", "atrapado",
        "grave", "critico", "critica", "no respira", "infarto"
    );

    // ── Medium-priority keywords (Spanish) ──
    private static final List<String> MEDIUM_PRIORITY_KEYWORDS = List.of(
        "daño", "daños", "humo", "amenaza", "robo", "forcejeo",
        "sospechoso", "pelea", "choque", "colisión", "lesion"
    );

    /**
     * Processes an emergency request and returns the classified response.
     *
     * @param request The incoming emergency data from the frontend
     * @return EmergencyResponse with entity, priority, and message
     */
    public EmergencyResponse classify(EmergencyRequest request) {

        String type     = request.getType().toLowerCase().trim();
        String entity   = resolveEntity(type);
        String priority = resolvePriority(request.getAnswers(), type);
        String message  = buildMessage(entity, priority, type);
        String reportId = "EM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return new EmergencyResponse(entity, priority, message, type, reportId);
    }

    /**
     * Maps emergency type to the responsible entity.
     */
    private String resolveEntity(String type) {
        return switch (type) {
            case "accident" -> "Hospital";
            case "fire"     -> "Bomberos";
            case "robbery"  -> "Policía";
            case "medical"  -> "Hospital";
            default         -> "Policía";
        };
    }

    /**
     * Determines priority based on keywords found in citizen answers.
     * Falls back to type-based default if no keywords match.
     */
    private String resolvePriority(List<String> answers, String type) {

        if (answers == null || answers.isEmpty()) {
            return defaultPriorityByType(type);
        }

        // Join all answers into one lowercase string for keyword search
        String combined = String.join(" ", answers).toLowerCase();

        // Check high-priority keywords first
        for (String keyword : HIGH_PRIORITY_KEYWORDS) {
            if (combined.contains(keyword)) return "Alta";
        }

        // Then medium-priority keywords
        for (String keyword : MEDIUM_PRIORITY_KEYWORDS) {
            if (combined.contains(keyword)) return "Media";
        }

        return defaultPriorityByType(type);
    }

    /**
     * Default priority per emergency type when no keywords are found.
     */
    private String defaultPriorityByType(String type) {
        return switch (type) {
            case "fire"    -> "Alta";
            case "medical" -> "Alta";
            case "accident"-> "Media";
            case "robbery" -> "Media";
            default        -> "Baja";
        };
    }

    /**
     * Builds a human-readable Spanish message for the citizen.
     */
    private String buildMessage(String entity, String priority, String type) {

        String entityMsg = switch (entity) {
            case "Hospital"  -> "Una unidad médica ha sido notificada";
            case "Bomberos"  -> "Los bomberos han sido alertados";
            case "Policía"   -> "La policía ha sido notificada";
            default          -> "Las autoridades han sido notificadas";
        };

        String priorityMsg = switch (priority) {
            case "Alta"  -> "Prioridad ALTA — unidades en camino de inmediato.";
            case "Media" -> "Prioridad MEDIA — unidades asignadas.";
            default      -> "Prioridad BAJA — reporte registrado.";
        };

        return entityMsg + ". " + priorityMsg;
    }
}
