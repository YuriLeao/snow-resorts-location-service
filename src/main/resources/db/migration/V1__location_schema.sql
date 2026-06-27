CREATE TABLE groups (
    id          UUID PRIMARY KEY,
    resort_id   UUID,
    name        VARCHAR(120) NOT NULL,
    invite_code VARCHAR(12)  NOT NULL UNIQUE,
    created_by  UUID NOT NULL,
    expires_at  TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE group_members (
    group_id  UUID NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    user_id   UUID NOT NULL,
    joined_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (group_id, user_id)
);
CREATE TABLE location_snapshots (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL,
    resort_id   UUID,
    lat         DOUBLE PRECISION NOT NULL,
    lng         DOUBLE PRECISION NOT NULL,
    trail_id    UUID,
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_snapshots_user ON location_snapshots (user_id);
CREATE INDEX idx_snapshots_recorded ON location_snapshots (recorded_at);
