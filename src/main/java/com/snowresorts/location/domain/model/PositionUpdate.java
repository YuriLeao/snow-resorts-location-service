package com.snowresorts.location.domain.model;

import java.util.UUID;

/** Inbound STOMP payload published by the mobile client to report its current position. */
public record PositionUpdate(double lat, double lng, UUID trailId, double speedKmh) {
}
