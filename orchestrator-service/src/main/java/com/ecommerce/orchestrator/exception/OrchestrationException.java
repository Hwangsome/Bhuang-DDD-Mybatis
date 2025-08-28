package com.ecommerce.orchestrator.exception;

/**
 * 编排服务异常类
 * 职责：定义编排服务中的业务异常
 */
public class OrchestrationException extends RuntimeException {
    
    private String errorCode;
    private String errorMessage;
    
    public OrchestrationException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
    
    public OrchestrationException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    public OrchestrationException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorMessage = errorMessage;
    }
    
    public OrchestrationException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}