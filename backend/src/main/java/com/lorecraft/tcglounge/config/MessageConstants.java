package com.lorecraft.tcglounge.config;

public final class MessageConstants {
    
    private MessageConstants() {
        // Private constructor to prevent instantiation
    }
    
    // Success Messages
    public static final class Success {
        public static final String LOGIN = "로그인 성공";
        public static final String SIGNUP = "회원가입이 완료되었습니다.";
        public static final String LOGOUT = "로그아웃되었습니다.";
        public static final String USER_INFO_RETRIEVED = "사용자 정보 조회 성공";
        public static final String USER_ID_AVAILABLE = "사용 가능한 아이디입니다.";
        public static final String USER_ID_UNAVAILABLE = "이미 사용 중인 아이디입니다.";
        public static final String EMAIL_AVAILABLE = "사용 가능한 이메일입니다.";
        public static final String EMAIL_UNAVAILABLE = "이미 사용 중인 이메일입니다.";
        public static final String NICKNAME_AVAILABLE = "사용 가능한 닉네임입니다.";
        public static final String NICKNAME_UNAVAILABLE = "이미 사용 중인 닉네임입니다.";
        public static final String PASSWORD_CHANGED = "비밀번호가 변경되었습니다.";
        public static final String PROFILE_UPDATED = "프로필이 업데이트되었습니다.";
        public static final String DATA_RETRIEVED = "데이터 조회 성공";
        public static final String DATA_CREATED = "데이터 생성 성공";
        public static final String DATA_UPDATED = "데이터 수정 성공";
        public static final String DATA_DELETED = "데이터 삭제 성공";
    }
    
    // Error Messages
    public static final class Error {
        public static final String LOGIN_FAILED = "로그인에 실패했습니다: ";
        public static final String SIGNUP_FAILED = "회원가입에 실패했습니다: ";
        public static final String INVALID_USER_TYPE = "유효하지 않은 사용자 유형입니다.";
        public static final String USER_NOT_FOUND = "사용자를 찾을 수 없습니다.";
        public static final String INVALID_CREDENTIALS = "아이디 또는 비밀번호가 올바르지 않습니다.";
        public static final String TOKEN_EXPIRED = "인증 토큰이 만료되었습니다.";
        public static final String TOKEN_INVALID = "유효하지 않은 인증 토큰입니다.";
        public static final String ACCESS_DENIED = "접근 권한이 없습니다.";
        public static final String INTERNAL_ERROR = "서버 내부 오류가 발생했습니다.";
        public static final String VALIDATION_FAILED = "입력값 검증에 실패했습니다.";
        public static final String DUPLICATE_USER_ID = "이미 사용 중인 아이디입니다.";
        public static final String DUPLICATE_EMAIL = "이미 사용 중인 이메일입니다.";
        public static final String DUPLICATE_NICKNAME = "이미 사용 중인 닉네임입니다.";
        public static final String WEAK_PASSWORD = "비밀번호가 보안 요구사항을 충족하지 않습니다.";
        public static final String INVALID_EMAIL_FORMAT = "유효하지 않은 이메일 형식입니다.";
        public static final String INVALID_PHONE_FORMAT = "유효하지 않은 전화번호 형식입니다.";
        public static final String REQUIRED_FIELD_MISSING = "필수 입력 항목이 누락되었습니다.";
    }
    
    // Field Names
    public static final class Field {
        public static final String USER_ID = "사용자 ID";
        public static final String PASSWORD = "비밀번호";
        public static final String EMAIL = "이메일";
        public static final String PHONE_NUMBER = "전화번호";
        public static final String NICKNAME = "닉네임";
        public static final String USER_TYPE = "사용자 유형";
        public static final String STORE_NAME = "매장명";
        public static final String STORE_LOCATION = "매장 위치";
        public static final String STORE_ZIPCODE = "우편번호";
        public static final String BUSINESS_LICENSE = "사업자 등록번호";
        public static final String CONTACT_NUMBER = "연락처";
        public static final String EMPLOYEE_ID = "사원번호";
        public static final String DEPARTMENT = "부서";
    }
    
    // User Types
    public static final class UserType {
        public static final String GAMER = "GAMER";
        public static final String STORE_OWNER = "STORE_OWNER";
        public static final String ADMIN = "ADMIN";
    }
    
    // Validation Patterns
    public static final class Pattern {
        public static final String EMAIL = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        public static final String PHONE = "^\\d{3}-\\d{3,4}-\\d{4}$";
        public static final String USER_ID = "^[a-zA-Z0-9_]{4,20}$";
        public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$";
        public static final String NICKNAME = "^[a-zA-Z0-9가-힣]{2,20}$";
        public static final String BUSINESS_LICENSE = "^\\d{3}-\\d{2}-\\d{5}$";
    }
    
    // Validation Messages
    public static final class Validation {
        public static final String USER_ID_PATTERN = "아이디는 4~20자의 영문, 숫자, 언더스코어만 사용 가능합니다.";
        public static final String PASSWORD_PATTERN = "비밀번호는 8자 이상, 영문과 숫자를 포함해야 합니다.";
        public static final String EMAIL_PATTERN = "올바른 이메일 형식이 아닙니다.";
        public static final String PHONE_PATTERN = "전화번호는 000-0000-0000 형식이어야 합니다.";
        public static final String NICKNAME_PATTERN = "닉네임은 2~20자의 한글, 영문, 숫자만 사용 가능합니다.";
        public static final String BUSINESS_LICENSE_PATTERN = "사업자 등록번호는 000-00-00000 형식이어야 합니다.";
    }
    
    // Competition Messages
    public static final class Competition {
        public static final String CREATED = "대회가 생성되었습니다.";
        public static final String UPDATED = "대회 정보가 수정되었습니다.";
        public static final String DELETED = "대회가 삭제되었습니다.";
        public static final String ENROLLED = "대회 참가 신청이 완료되었습니다.";
        public static final String ENROLLMENT_CANCELLED = "대회 참가가 취소되었습니다.";
        public static final String STARTED = "대회가 시작되었습니다.";
        public static final String ENDED = "대회가 종료되었습니다.";
        public static final String NOT_FOUND = "대회를 찾을 수 없습니다.";
        public static final String ALREADY_ENROLLED = "이미 참가 신청한 대회입니다.";
        public static final String ENROLLMENT_FULL = "대회 참가 인원이 가득 찼습니다.";
    }
    
    // Card Messages
    public static final class Card {
        public static final String CREATED = "카드가 생성되었습니다.";
        public static final String UPDATED = "카드 정보가 수정되었습니다.";
        public static final String DELETED = "카드가 삭제되었습니다.";
        public static final String NOT_FOUND = "카드를 찾을 수 없습니다.";
        public static final String DECK_CREATED = "덱이 생성되었습니다.";
        public static final String DECK_UPDATED = "덱이 수정되었습니다.";
        public static final String DECK_DELETED = "덱이 삭제되었습니다.";
        public static final String DECK_NOT_FOUND = "덱을 찾을 수 없습니다.";
        public static final String DECK_FULL = "덱이 가득 찼습니다.";
        public static final String CARD_ADDED_TO_DECK = "카드가 덱에 추가되었습니다.";
        public static final String CARD_REMOVED_FROM_DECK = "카드가 덱에서 제거되었습니다.";
    }
    
    // Store Messages
    public static final class Store {
        public static final String CREATED = "매장이 등록되었습니다.";
        public static final String UPDATED = "매장 정보가 수정되었습니다.";
        public static final String DELETED = "매장이 삭제되었습니다.";
        public static final String NOT_FOUND = "매장을 찾을 수 없습니다.";
        public static final String ITEM_CREATED = "상품이 등록되었습니다.";
        public static final String ITEM_UPDATED = "상품 정보가 수정되었습니다.";
        public static final String ITEM_DELETED = "상품이 삭제되었습니다.";
        public static final String ITEM_NOT_FOUND = "상품을 찾을 수 없습니다.";
        public static final String ITEM_OUT_OF_STOCK = "재고가 부족합니다.";
    }
    
    // Order Messages
    public static final class Order {
        public static final String CREATED = "주문이 생성되었습니다.";
        public static final String UPDATED = "주문이 수정되었습니다.";
        public static final String CANCELLED = "주문이 취소되었습니다.";
        public static final String COMPLETED = "주문이 완료되었습니다.";
        public static final String NOT_FOUND = "주문을 찾을 수 없습니다.";
        public static final String CANNOT_CANCEL = "취소할 수 없는 주문입니다.";
        public static final String ALREADY_COMPLETED = "이미 완료된 주문입니다.";
        public static final String PAYMENT_SUCCESS = "결제가 완료되었습니다.";
        public static final String PAYMENT_FAILED = "결제에 실패했습니다.";
    }
}