package com.gftour.exception

/**
 * 기본 비즈니스 예외 클래스
 */
abstract class BusinessException(
    message: String,
    val errorCode: String
) : RuntimeException(message)

/**
 * 사용자 관련 예외
 */
class UserException(message: String, errorCode: String = "USER_ERROR") : BusinessException(message, errorCode)

/**
 * 투어 관련 예외
 */
class TourException(message: String, errorCode: String = "TOUR_ERROR") : BusinessException(message, errorCode)

/**
 * 예약 관련 예외
 */
class BookingException(message: String, errorCode: String = "BOOKING_ERROR") : BusinessException(message, errorCode)

/**
 * 사용자 관련 구체적 예외들
 */
class UserNotFoundException(message: String = "사용자를 찾을 수 없습니다") : 
    UserException(message, "USER_NOT_FOUND")

class DuplicateEmailException(message: String = "이미 등록된 이메일입니다") : 
    UserException(message, "DUPLICATE_EMAIL")

class InvalidPasswordException(message: String = "잘못된 비밀번호입니다") : 
    UserException(message, "INVALID_PASSWORD")

class UserNotActiveException(message: String = "비활성화된 사용자입니다") : 
    UserException(message, "USER_NOT_ACTIVE")

class UnauthorizedException(message: String = "권한이 없습니다") : 
    UserException(message, "UNAUTHORIZED")

/**
 * 투어 관련 구체적 예외들
 */
class TourNotFoundException(message: String = "투어를 찾을 수 없습니다") : 
    TourException(message, "TOUR_NOT_FOUND")

class TourNotAvailableException(message: String = "예약할 수 없는 투어입니다") : 
    TourException(message, "TOUR_NOT_AVAILABLE")

class TourCapacityExceededException(message: String = "투어 정원을 초과했습니다") : 
    TourException(message, "TOUR_CAPACITY_EXCEEDED")

class TourAlreadyStartedException(message: String = "이미 시작된 투어입니다") : 
    TourException(message, "TOUR_ALREADY_STARTED")

/**
 * 예약 관련 구체적 예외들
 */
class BookingNotFoundException(message: String = "예약을 찾을 수 없습니다") : 
    BookingException(message, "BOOKING_NOT_FOUND")

class DuplicateBookingException(message: String = "이미 예약된 투어입니다") : 
    BookingException(message, "DUPLICATE_BOOKING")

class BookingNotCancellableException(message: String = "취소할 수 없는 예약입니다") : 
    BookingException(message, "BOOKING_NOT_CANCELLABLE")

class InvalidParticipantCountException(message: String = "잘못된 참가자 수입니다") : 
    BookingException(message, "INVALID_PARTICIPANT_COUNT")

class InsufficientCapacityException(message: String = "투어 정원이 부족합니다") : 
    BookingException(message, "INSUFFICIENT_CAPACITY")