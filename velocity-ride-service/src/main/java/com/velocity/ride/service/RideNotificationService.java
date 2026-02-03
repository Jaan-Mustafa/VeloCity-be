package com.velocity.ride.service;

import com.velocity.core.dto.ApiResponse;
import com.velocity.core.enums.NotificationType;
import com.velocity.core.enums.RideStatus;
import com.velocity.ride.model.entity.Ride;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RideNotificationService {

    private final RestTemplate restTemplate;

    @Value("${notification.service.url:http://localhost:8085}")
    private String notificationServiceUrl;

    /**
     * Send notification when ride status changes
     */
    public void notifyRideStatusChange(Ride ride, RideStatus newStatus) {
        try {
            String message = buildStatusMessage(ride, newStatus);
            String title = buildStatusTitle(newStatus);

            sendNotification(ride.getUserId(), title, message, NotificationType.RIDE_UPDATE);
            
            log.info("Notification sent for ride {} status change to {}", ride.getId(), newStatus);
        } catch (Exception e) {
            log.error("Failed to send notification for ride {}: {}", ride.getId(), e.getMessage());
            // Don't throw exception - notification failure shouldn't break ride flow
        }
    }

    /**
     * Notify driver about new ride request
     */
    public void notifyDriverAboutRideRequest(Long driverId, Ride ride) {
        try {
            String message = String.format(
                "New ride request from %s to %s. Fare: ₹%.2f",
                ride.getPickupAddress() != null ? ride.getPickupAddress() : "Pickup location",
                ride.getDropoffAddress() != null ? ride.getDropoffAddress() : "Dropoff location",
                ride.getFare()
            );

            sendNotification(driverId, "New Ride Request", message, NotificationType.RIDE_REQUEST);
            
            log.info("Ride request notification sent to driver {}", driverId);
        } catch (Exception e) {
            log.error("Failed to notify driver {}: {}", driverId, e.getMessage());
        }
    }

    /**
     * Notify user about driver acceptance
     */
    public void notifyUserDriverAccepted(Ride ride, String driverName, String vehicleNumber) {
        try {
            String message = String.format(
                "%s has accepted your ride request. Vehicle: %s",
                driverName,
                vehicleNumber
            );

            sendNotification(ride.getUserId(), "Driver Accepted", message, NotificationType.RIDE_UPDATE);
            
            log.info("Driver acceptance notification sent to user {}", ride.getUserId());
        } catch (Exception e) {
            log.error("Failed to notify user {}: {}", ride.getUserId(), e.getMessage());
        }
    }

    /**
     * Send generic notification via notification service
     */
    private void sendNotification(Long userId, String title, String message, NotificationType type) {
        try {
            String url = notificationServiceUrl + "/api/notifications/send";

            Map<String, Object> request = new HashMap<>();
            request.put("userId", userId);
            request.put("type", type.name());
            request.put("title", title);
            request.put("message", message);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            restTemplate.postForEntity(url, entity, ApiResponse.class);
            
        } catch (Exception e) {
            log.warn("Notification service call failed: {}", e.getMessage());
            // Silently fail - notification is not critical
        }
    }

    /**
     * Build user-friendly status message
     */
    private String buildStatusMessage(Ride ride, RideStatus status) {
        return switch(status) {
            case REQUESTED -> "Your ride request has been received. Finding nearby drivers...";
            case ACCEPTED -> "Driver has accepted your ride request!";
            case ARRIVED -> "Driver has arrived at your pickup location";
            case IN_PROGRESS -> "Your ride has started. Have a safe journey!";
            case COMPLETED -> String.format("Ride completed. Fare: ₹%.2f. Thank you for using VeloCity!", ride.getFare());
            case CANCELLED -> "Your ride has been cancelled";
            default -> "Ride status updated";
        };
    }

    /**
     * Build notification title based on status
     */
    private String buildStatusTitle(RideStatus status) {
        return switch(status) {
            case REQUESTED -> "Ride Requested";
            case ACCEPTED -> "Driver Accepted";
            case ARRIVED -> "Driver Arrived";
            case IN_PROGRESS -> "Ride Started";
            case COMPLETED -> "Ride Completed";
            case CANCELLED -> "Ride Cancelled";
            default -> "Ride Update";
        };
    }
}
