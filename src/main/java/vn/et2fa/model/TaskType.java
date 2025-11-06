package vn.et2fa.model;

/**
 * Enum biểu diễn loại tác vụ (task type) trong T2FA theo paper ET2FA:
 * - TYPE0: Tasks that are alone in their topological level
 * - TYPE1: Parent nodes in MOSI (multiple output single input) structure
 * - TYPE2: Child nodes in MOSI structure
 * - TYPE3: Parent nodes in SOMI (single output multiple input) structure
 * - TYPE4: Child nodes in SOMI structure
 * - GENERAL: General tasks that don't fit the above categories
 */
public enum TaskType {
    TYPE0,
    TYPE1,
    TYPE2,
    TYPE3,
    TYPE4,
    GENERAL
}
