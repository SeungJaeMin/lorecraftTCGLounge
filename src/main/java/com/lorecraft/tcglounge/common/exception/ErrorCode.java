package com.lorecraft.tcglounge.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "Invalid input"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C002", "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "C003", "Forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "C004", "Resource not found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C005", "Internal server error"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "User not found"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U002", "Email already exists"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U003", "Invalid password"),

    // Card
    CARD_NOT_FOUND(HttpStatus.NOT_FOUND, "CD001", "Card not found"),
    DECK_NOT_FOUND(HttpStatus.NOT_FOUND, "CD002", "Deck not found"),
    INVALID_DECK_SIZE(HttpStatus.BAD_REQUEST, "CD003", "Invalid deck size"),

    // Competition
    COMPETITION_NOT_FOUND(HttpStatus.NOT_FOUND, "CP001", "Competition not found"),
    COMPETITION_ALREADY_STARTED(HttpStatus.BAD_REQUEST, "CP002", "Competition already started"),
    ENROLLMENT_CLOSED(HttpStatus.BAD_REQUEST, "CP003", "Enrollment period closed"),
    ALREADY_ENROLLED(HttpStatus.CONFLICT, "CP004", "Already enrolled in this competition"),
    MATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "CP005", "Match not found"),

    // Store
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "ST001", "Store not found"),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ST002", "Order not found"),

    // Content
    CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CT001", "Content not found");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}