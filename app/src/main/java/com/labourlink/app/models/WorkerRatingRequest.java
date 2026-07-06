package com.labourlink.app.models;

public class WorkerRatingRequest {

    private Long requestId;
    private Integer stars;

    public WorkerRatingRequest(Long requestId, Integer stars) {
        this.requestId = requestId;
        this.stars = stars;
    }

    public Long getRequestId() {
        return requestId;
    }

    public Integer getStars() {
        return stars;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }
}