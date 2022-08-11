package com.ttop.spring.stock.error;


import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "INVALID001","리프레시 토큰이 유효하지 않습니다"),
    MISMATCH_REFRESH_TOKEN(HttpStatus.BAD_REQUEST,"INVALID002", "리프레시 토큰의 유저 정보가 일치하지 않습니다"),
    CANNOT_FOLLOW_MYSELF(HttpStatus.BAD_REQUEST,"INVALID003", "자기 자신은 팔로우 할 수 없습니다"),

    INVALID_EMAIL_TOKEN(HttpStatus.BAD_REQUEST,"INVALID004", "이메일 토큰 유효시간이 지났습니다"),
    INVALID_BODY_PRAM_INPUT(HttpStatus.BAD_REQUEST,"INVALID005","유효하지 않은 입력입니다"),

    INVALID_JSON(HttpStatus.BAD_REQUEST,"INVALID006","잘못된 JSON 형식입니다."),
    INVALID_PREV_PW(HttpStatus.BAD_REQUEST,"INVALID007","잘못된 이전 패스워드 입력 입니다."),


    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST,"INVALID008", "Invalid Type Value"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST,"INVALID009", "Invalid Input Value"),
    // Member
    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST,"INVALID010", "Email is Duplicated"),
    LOGIN_INPUT_INVALID(HttpStatus.BAD_REQUEST,"INVALID011", "Login input is invalid"),
    DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST,"INVALID012", "JPA Data integrity violation"),

    NO_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST,"INVALID012", "No request parameter"),




    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_AUTH_TOKEN(HttpStatus.UNAUTHORIZED,"AUTH001", "권한 정보가 없는 토큰입니다"),
    UNAUTHORIZED_MEMBER(HttpStatus.UNAUTHORIZED,"AUTH002", "현재 내 계정 정보가 존재하지 않습니다"),
    INVALID_AUTHORIZATION(HttpStatus.UNAUTHORIZED,"AUTH003", "잘못된 로그인 정보입니다. ID 혹은 pw가 틀립니다."),
    EMAIL_NOT_AUTHORIZATION(HttpStatus.UNAUTHORIZED,"AUTH004", "Email is not verified"),


    // 403 권한 없음
    INVALID_USER_PERMISSION(HttpStatus.FORBIDDEN,"ROLE001","해당 사용자는 권한이 없습니다"),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN,"ROLE002", "Access is Denied"),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "NOT001", "해당 유저 정보를 찾을 수 없습니다"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND,"NOT002", "로그아웃 된 사용자입니다"),
    NOT_FOLLOW(HttpStatus.NOT_FOUND,"NOT003", "팔로우 중이지 않습니다"),
    STOCK_NOT_FOUND(HttpStatus.NOT_FOUND,"NOT004","해당 주식 정보를 찾을 수 없습니다"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND,"NOT005","해당 댓글 정보를 찾을 수 없습니다"),
    EMAIL_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND,"NOT006","이메일 토큰을 찾을 수 없습니다"),

    // 405 error url get post를 대응하는게 없을떄
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD001","Method not allowed"),


    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT,"CONF001", "데이터가 이미 존재합니다"),


    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"INTERNAL001","Internal server error"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String detail;
}