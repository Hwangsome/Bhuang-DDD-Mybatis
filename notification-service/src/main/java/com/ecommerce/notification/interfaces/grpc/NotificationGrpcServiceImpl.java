package com.ecommerce.notification.interfaces.grpc;

import com.ecommerce.notification.proto.NotificationServiceProto.*;
import com.ecommerce.notification.proto.NotificationServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import com.ecommerce.notification.interfaces.converter.NotificationProtoConverter;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class NotificationGrpcServiceImpl extends NotificationServiceGrpc.NotificationServiceImplBase {
    
    @Autowired
    private NotificationProtoConverter converter;

    @Override
    public void sendNotification(SendNotificationRequest request, StreamObserver<SendNotificationResponse> responseObserver) {
        try {
            SendNotificationResponse response = SendNotificationResponse.newBuilder()
                    .setNotificationId("NOTIF_" + System.currentTimeMillis())
                    .setStatus(NotificationStatus.NOTIFICATION_SENT)
                    .setResponseStatus(converter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void sendEmail(SendEmailRequest request, StreamObserver<SendEmailResponse> responseObserver) {
        try {
            SendEmailResponse response = SendEmailResponse.newBuilder()
                    .setNotificationId("EMAIL_" + System.currentTimeMillis())
                    .setMessageId("MSG_" + System.currentTimeMillis())
                    .setStatus(converter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void sendSMS(SendSMSRequest request, StreamObserver<SendSMSResponse> responseObserver) {
        try {
            SendSMSResponse response = SendSMSResponse.newBuilder()
                    .setNotificationId("SMS_" + System.currentTimeMillis())
                    .setMessageId("SMS_MSG_" + System.currentTimeMillis())
                    .setStatus(converter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void sendPushNotification(SendPushNotificationRequest request, StreamObserver<SendPushNotificationResponse> responseObserver) {
        try {
            SendPushNotificationResponse response = SendPushNotificationResponse.newBuilder()
                    .setNotificationId("PUSH_" + System.currentTimeMillis())
                    .setMessageId("PUSH_MSG_" + System.currentTimeMillis())
                    .setStatus(converter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void batchSendNotification(BatchSendNotificationRequest request, StreamObserver<BatchSendNotificationResponse> responseObserver) {
        try {
            BatchSendNotificationResponse response = BatchSendNotificationResponse.newBuilder()
                    .setSuccessCount(request.getUserIdsCount())
                    .setFailedCount(0)
                    .setStatus(converter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getNotificationHistory(GetNotificationHistoryRequest request, StreamObserver<GetNotificationHistoryResponse> responseObserver) {
        try {
            GetNotificationHistoryResponse response = GetNotificationHistoryResponse.newBuilder()
                    .setStatus(converter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getNotificationTemplate(GetNotificationTemplateRequest request, StreamObserver<GetNotificationTemplateResponse> responseObserver) {
        try {
            GetNotificationTemplateResponse response = GetNotificationTemplateResponse.newBuilder()
                    .setTemplate(NotificationTemplate.newBuilder()
                            .setTemplateId(request.getTemplateId())
                            .setTemplateName("Default Template")
                            .setType(NotificationType.USER_REGISTRATION)
                            .setChannel(NotificationChannel.EMAIL)
                            .setTitleTemplate("Welcome")
                            .setContentTemplate("Welcome to our platform")
                            .setActive(true)
                            .build())
                    .setStatus(converter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void createNotificationTemplate(CreateNotificationTemplateRequest request, StreamObserver<NotificationTemplateResponse> responseObserver) {
        try {
            NotificationTemplateResponse response = NotificationTemplateResponse.newBuilder()
                    .setTemplate(NotificationTemplate.newBuilder()
                            .setTemplateId("TPL_" + System.currentTimeMillis())
                            .setTemplateName(request.getTemplateName())
                            .setType(request.getType())
                            .setChannel(request.getChannel())
                            .setTitleTemplate(request.getTitleTemplate())
                            .setContentTemplate(request.getContentTemplate())
                            .setActive(true)
                            .build())
                    .setStatus(converter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getUserNotificationSettings(GetUserNotificationSettingsRequest request, StreamObserver<UserNotificationSettingsResponse> responseObserver) {
        try {
            UserNotificationSettingsResponse response = UserNotificationSettingsResponse.newBuilder()
                    .setSettings(UserNotificationSettings.newBuilder()
                            .setUserId(request.getUserId())
                            .setGlobalEnabled(true)
                            .setTimezone("UTC+8")
                            .setQuietStartHour(22)
                            .setQuietEndHour(8)
                            .build())
                    .setStatus(converter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void updateUserNotificationSettings(UpdateUserNotificationSettingsRequest request, StreamObserver<UserNotificationSettingsResponse> responseObserver) {
        try {
            UserNotificationSettingsResponse response = UserNotificationSettingsResponse.newBuilder()
                    .setSettings(UserNotificationSettings.newBuilder()
                            .setUserId(request.getUserId())
                            .setGlobalEnabled(request.hasGlobalEnabled() ? request.getGlobalEnabled() : true)
                            .setTimezone(request.hasTimezone() ? request.getTimezone() : "UTC+8")
                            .setQuietStartHour(request.hasQuietStartHour() ? request.getQuietStartHour() : 22)
                            .setQuietEndHour(request.hasQuietEndHour() ? request.getQuietEndHour() : 8)
                            .build())
                    .setStatus(converter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}