package com.labourlink.app.models;

public class LiveLocationDto {

    private Long requestId;
    private Long workerId;

    private Double workerLatitude;
    private Double workerLongitude;

    private Double workLatitude;
    private Double workLongitude;

    private String status;
    private String lastUpdated;

    public LiveLocationDto() {
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public Double getWorkerLatitude() {
        return workerLatitude;
    }

    public void setWorkerLatitude(Double workerLatitude) {
        this.workerLatitude = workerLatitude;
    }

    public Double getWorkerLongitude() {
        return workerLongitude;
    }

    public void setWorkerLongitude(Double workerLongitude) {
        this.workerLongitude = workerLongitude;
    }

    public Double getWorkLatitude() {
        return workLatitude;
    }

    public void setWorkLatitude(Double workLatitude) {
        this.workLatitude = workLatitude;
    }

    public Double getWorkLongitude() {
        return workLongitude;
    }

    public void setWorkLongitude(Double workLongitude) {
        this.workLongitude = workLongitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}