package com.reservAR.backreservar.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ProblemDetail problem(
            HttpStatus status,
            String title,
            String detail,
            String type,
            String instancePath,
            String code
    ) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(title);
        pd.setDetail(detail);
        pd.setType(URI.create(type));
        pd.setInstance(URI.create(instancePath));
        pd.setProperty("code", code);
        return pd;
    }

    @ExceptionHandler(AvailabilityException.class)
    public ProblemDetail handleAvailabilityException(HttpServletRequest rq, AvailabilityException e) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Availability exception",
                e.getMessage(),
                "urn:problem:AvailabilityException",
                rq.getRequestURI(),
                e.getMessage()
        );
    }

    @ExceptionHandler(AvailabilityNotFoundException.class)
    public ProblemDetail handleAvailabilityNotFoundException(HttpServletRequest rq, AvailabilityNotFoundException e) {
        return problem(
                HttpStatus.NOT_FOUND,
                "Availability not found exception",
                e.getMessage(),
                "urn:problem:AvailabilityNotFoundException",
                rq.getRequestURI(),
                e.getMessage()
        );
    }

    @ExceptionHandler(DuplicateRestaurantException.class)
    public ProblemDetail handleDuplicateRestaurantException(HttpServletRequest rq, DuplicateRestaurantException e) {
        return problem(
                HttpStatus.CONFLICT,
                "Restaurant duplicate exception",
                e.getMessage(),
                "urn:problem:RestaurantDuplicateException",
                rq.getRequestURI(),
                e.getMessage()
        );
    }

    @ExceptionHandler(InventoryRuleException.class)
    public ProblemDetail handleInventoryRuleException(HttpServletRequest rq, InventoryRuleException e) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Inventory exception",
                e.getMessage(),
                "urn:problem:InventoryException",
                rq.getRequestURI(),
                e.getMessage()
        );
    }

    @ExceptionHandler(InventoryRuleNotFoundException.class)
    public ProblemDetail handleInventoryRuleNotFoundException(HttpServletRequest rq, InventoryRuleNotFoundException e) {
        return problem(
                HttpStatus.NOT_FOUND,
                "Inventory not found exception",
                e.getMessage(),
                "urn:problem:InventoryNotFoundException",
                rq.getRequestURI(),
                e.getMessage()
        );
    }

    @ExceptionHandler(JwtException.class)
    public ProblemDetail handleJwtException(HttpServletRequest rq, JwtException e) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Jwt exception",
                e.getMessage(),
                "urn:problem:JwtException",
                rq.getRequestURI(),
                e.getMessage()
        );
    }

    @ExceptionHandler(RestaurantException.class)
    public ProblemDetail handleRestaurantException(HttpServletRequest rq, RestaurantException e) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Restaurant exception",
                e.getMessage(),
                "urn:problem:RestaurantException",
                rq.getRequestURI(),
                e.getMessage()
        );
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ProblemDetail handleRestaurantNotFoundException(HttpServletRequest rq, RestaurantNotFoundException e) {
        return problem(
                HttpStatus.NOT_FOUND,
                "Restaurant not found exception",
                e.getMessage(),
                "urn:problem:RestaurantNotFoundException",
                rq.getRequestURI(),
                e.getMessage()
        );
    }
    @ExceptionHandler(TableException.class)
    public ProblemDetail handleTableException(HttpServletRequest rq, TableException e) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Table exception",
                e.getMessage(),
                "urn:problem:TableException",
                rq.getRequestURI(),
                e.getMessage()
        );
    }

    @ExceptionHandler(TableNotFoundException.class)
    public ProblemDetail handleTableNotFoundException(HttpServletRequest rq, TableNotFoundException e) {
        return problem(
                HttpStatus.NOT_FOUND,
                "Table not found exception",
                e.getMessage(),
                "urn:problem:TableNotFoundException",
                rq.getRequestURI(),
                e.getMessage()
        );
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFoundException(HttpServletRequest rq, UserNotFoundException e) {
        return problem(
                HttpStatus.NOT_FOUND,
                "User not found exception",
                e.getMessage(),
                "urn:problem:UserNotFoundException",
                rq.getRequestURI(),
                e.getMessage()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException(HttpServletRequest rq, BadCredentialsException e) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Bad credentials exception",
                e.getMessage(),
                "urn:problem:BadCredentialsException",
                rq.getRequestURI(),
                e.getMessage()
        );
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ProblemDetail handleReservationNotFoundException(HttpServletRequest rq, ReservationNotFoundException e) {
        return problem(
                HttpStatus.NOT_FOUND,
                "Reservation not found",
                e.getMessage(),
                "urn:problem:ReservationNotFoundException",
                rq.getRequestURI(),
                e.getMessage()
        );
    }

    @ExceptionHandler(ReservationStatusException.class)
    public ProblemDetail handleReservationStatusException(HttpServletRequest rq, ReservationStatusException e) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Reservation Problem",
                e.getMessage(),
                "urn:problem:ReservationProblem",
                rq.getRequestURI(),
                e.getMessage()
        );
    }


    @ExceptionHandler(PaymentNotException.class)
    public ProblemDetail handlePaymentNotFoundException(HttpServletRequest rq, ReservationNotFoundException e) {
        return problem(
                HttpStatus.NOT_FOUND,
                "Payment not found",
                e.getMessage(),
                "urn:problem:PaymentNotFoundException",
                rq.getRequestURI(),
                e.getMessage()
        );
    }

    @ExceptionHandler(PaymentException.class)
    public ProblemDetail handlePaymentException(HttpServletRequest rq, ReservationStatusException e) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Payment Problem",
                e.getMessage(),
                "urn:problem:PaymentProblem",
                rq.getRequestURI(),
                e.getMessage()
        );
    }




}
