package com.isaiiapp.backend.order.v1.status.mapper;

import com.isaiiapp.backend.order.v1.status.dto.request.CreateStatusRequest;
import com.isaiiapp.backend.order.v1.status.dto.request.UpdateStatusRequest;
import com.isaiiapp.backend.order.v1.status.dto.response.StatusResponse;
import com.isaiiapp.backend.order.v1.status.model.Status;
import org.springframework.stereotype.Component;

@Component
public class StatusMapper {

    /**
     * Convertir CreateStatusRequest a Status entity
     */
    public Status toEntity(CreateStatusRequest request) {
        Status status = new Status();
        status.setName(request.getName());
        status.setDescription(request.getDescription());
        return status;
    }

    /**
     * Convertir Status entity a StatusResponse
     */
    public StatusResponse toResponse(Status status) {
        StatusResponse response = new StatusResponse();
        response.setId(status.getId());
        response.setName(status.getName());
        response.setDescription(status.getDescription());
        return response;
    }
}
