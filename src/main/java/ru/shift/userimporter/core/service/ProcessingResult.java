package ru.shift.userimporter.core.service;

public record ProcessingResult(int total, int valid, int invalid, int inserted, int updated) {
}
