package com.lorecraft.tcglounge.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // Common Errors (1000~1999)
    SUCCESS(1000, HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),
    INVALID_INPUT(1001, HttpStatus.BAD_REQUEST, "유효하지 않은 입력값입니다."),
    INTERNAL_SERVER_ERROR(1002, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    UNAUTHORIZED(1003, HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(1004, HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_FOUND(1005, HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(1006, HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 HTTP 메소드입니다."),
    CONFLICT(1007, HttpStatus.CONFLICT, "리소스 충돌이 발생했습니다."),
    BAD_REQUEST(1008, HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    
    // Authentication & Authorization Errors (2000~2999)
    AUTH_LOGIN_FAILED(2001, HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다."),
    AUTH_INVALID_CREDENTIALS(2002, HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
    AUTH_TOKEN_EXPIRED(2003, HttpStatus.UNAUTHORIZED, "인증 토큰이 만료되었습니다."),
    AUTH_TOKEN_INVALID(2004, HttpStatus.UNAUTHORIZED, "유효하지 않은 인증 토큰입니다."),
    AUTH_USER_NOT_FOUND(2005, HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    AUTH_ACCESS_DENIED(2006, HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),
    AUTH_ACCOUNT_DISABLED(2007, HttpStatus.FORBIDDEN, "비활성화된 계정입니다."),
    AUTH_ACCOUNT_LOCKED(2008, HttpStatus.FORBIDDEN, "잠긴 계정입니다."),
    
    // User Registration Errors (2100~2199)
    USER_ALREADY_EXISTS(2101, HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
    USER_ID_DUPLICATE(2102, HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
    USER_EMAIL_DUPLICATE(2103, HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    USER_NICKNAME_DUPLICATE(2104, HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    USER_INVALID_TYPE(2105, HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 유형입니다."),
    USER_REGISTRATION_FAILED(2106, HttpStatus.INTERNAL_SERVER_ERROR, "회원가입 중 오류가 발생했습니다."),
    USER_PHONE_INVALID(2107, HttpStatus.BAD_REQUEST, "유효하지 않은 전화번호 형식입니다."),
    USER_EMAIL_INVALID(2108, HttpStatus.BAD_REQUEST, "유효하지 않은 이메일 형식입니다."),
    USER_PASSWORD_WEAK(2109, HttpStatus.BAD_REQUEST, "비밀번호가 보안 요구사항을 충족하지 않습니다."),
    
    // Store Owner Specific Errors (2200~2299)
    STORE_NAME_REQUIRED(2201, HttpStatus.BAD_REQUEST, "매장명은 필수 입력 항목입니다."),
    STORE_LOCATION_REQUIRED(2202, HttpStatus.BAD_REQUEST, "매장 위치는 필수 입력 항목입니다."),
    STORE_LICENSE_REQUIRED(2203, HttpStatus.BAD_REQUEST, "사업자 등록번호는 필수 입력 항목입니다."),
    STORE_LICENSE_INVALID(2204, HttpStatus.BAD_REQUEST, "유효하지 않은 사업자 등록번호입니다."),
    STORE_ALREADY_EXISTS(2205, HttpStatus.CONFLICT, "이미 등록된 매장입니다."),
    
    // Admin Specific Errors (2300~2399)
    ADMIN_EMPLOYEE_ID_REQUIRED(2301, HttpStatus.BAD_REQUEST, "사원번호는 필수 입력 항목입니다."),
    ADMIN_DEPARTMENT_REQUIRED(2302, HttpStatus.BAD_REQUEST, "부서명은 필수 입력 항목입니다."),
    ADMIN_EMPLOYEE_ID_DUPLICATE(2303, HttpStatus.CONFLICT, "이미 등록된 사원번호입니다."),
    
    // Card Related Errors (3000~3999)
    CARD_NOT_FOUND(3001, HttpStatus.NOT_FOUND, "카드를 찾을 수 없습니다."),
    CARD_ALREADY_EXISTS(3002, HttpStatus.CONFLICT, "이미 존재하는 카드입니다."),
    CARD_INVALID_RARITY(3003, HttpStatus.BAD_REQUEST, "유효하지 않은 카드 등급입니다."),
    CARD_PACK_NOT_FOUND(3004, HttpStatus.NOT_FOUND, "카드팩을 찾을 수 없습니다."),
    CARD_DECK_NOT_FOUND(3005, HttpStatus.NOT_FOUND, "덱을 찾을 수 없습니다."),
    CARD_DECK_FULL(3006, HttpStatus.BAD_REQUEST, "덱이 가득 찼습니다."),
    CARD_DECK_INVALID_SIZE(3007, HttpStatus.BAD_REQUEST, "유효하지 않은 덱 크기입니다."),
    
    // Competition Related Errors (4000~4999)
    COMPETITION_NOT_FOUND(4001, HttpStatus.NOT_FOUND, "대회를 찾을 수 없습니다."),
    COMPETITION_ALREADY_STARTED(4002, HttpStatus.BAD_REQUEST, "이미 시작된 대회입니다."),
    COMPETITION_ENDED(4003, HttpStatus.BAD_REQUEST, "종료된 대회입니다."),
    COMPETITION_FULL(4004, HttpStatus.BAD_REQUEST, "대회 참가 인원이 가득 찼습니다."),
    COMPETITION_NOT_STARTED(4005, HttpStatus.BAD_REQUEST, "아직 시작되지 않은 대회입니다."),
    ENROLLMENT_ALREADY_EXISTS(4006, HttpStatus.CONFLICT, "이미 대회에 등록되어 있습니다."),
    ENROLLMENT_NOT_FOUND(4007, HttpStatus.NOT_FOUND, "등록 정보를 찾을 수 없습니다."),
    MATCH_NOT_FOUND(4008, HttpStatus.NOT_FOUND, "경기를 찾을 수 없습니다."),
    MATCH_ALREADY_COMPLETED(4009, HttpStatus.BAD_REQUEST, "이미 완료된 경기입니다."),
    
    // Store & Order Related Errors (5000~5999)
    STORE_NOT_FOUND(5001, HttpStatus.NOT_FOUND, "매장을 찾을 수 없습니다."),
    STORE_ITEM_NOT_FOUND(5002, HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    STORE_ITEM_OUT_OF_STOCK(5003, HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
    ORDER_NOT_FOUND(5004, HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    ORDER_CANNOT_CANCEL(5005, HttpStatus.BAD_REQUEST, "취소할 수 없는 주문입니다."),
    ORDER_ALREADY_COMPLETED(5006, HttpStatus.BAD_REQUEST, "이미 완료된 주문입니다."),
    
    // Content Related Errors (6000~6999)
    CONTENT_NOT_FOUND(6001, HttpStatus.NOT_FOUND, "콘텐츠를 찾을 수 없습니다."),
    CONTENT_ACCESS_DENIED(6002, HttpStatus.FORBIDDEN, "콘텐츠 접근 권한이 없습니다."),
    EVENT_NOT_FOUND(6003, HttpStatus.NOT_FOUND, "이벤트를 찾을 수 없습니다."),
    EVENT_EXPIRED(6004, HttpStatus.BAD_REQUEST, "만료된 이벤트입니다."),
    
    // Validation Errors (7000~7999)
    VALIDATION_FAILED(7001, HttpStatus.BAD_REQUEST, "입력값 검증에 실패했습니다."),
    REQUIRED_FIELD_MISSING(7002, HttpStatus.BAD_REQUEST, "필수 입력 항목이 누락되었습니다."),
    INVALID_FORMAT(7003, HttpStatus.BAD_REQUEST, "잘못된 형식입니다."),
    VALUE_OUT_OF_RANGE(7004, HttpStatus.BAD_REQUEST, "값이 허용 범위를 벗어났습니다."),
    
    // Database Errors (8000~8999)
    DATABASE_ERROR(8001, HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다."),
    TRANSACTION_FAILED(8002, HttpStatus.INTERNAL_SERVER_ERROR, "트랜잭션 처리에 실패했습니다."),
    DATA_INTEGRITY_VIOLATION(8003, HttpStatus.CONFLICT, "데이터 무결성 제약 조건을 위반했습니다."),
    
    // External Service Errors (9000~9999)
    EXTERNAL_SERVICE_ERROR(9001, HttpStatus.SERVICE_UNAVAILABLE, "외부 서비스 오류가 발생했습니다."),
    PAYMENT_FAILED(9002, HttpStatus.BAD_REQUEST, "결제 처리에 실패했습니다."),
    EMAIL_SEND_FAILED(9003, HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다.");
    
    private final int code;
    private final HttpStatus httpStatus;
    private final String message;
    
    public String getCodeString() {
        return String.format("E%04d", code);
    }
}