package io.zenwave360.example.events.support.model;

import io.zenwave360.example.events.oneMessage.model.CustomerEventPayload;

import java.util.Map;

public class Envelope {
    public Map metadata;
    public CustomerEventPayload payload;

    public Map getMetadata() {
        return metadata;
    }

    public void setMetadata(Map metadata) {
        this.metadata = metadata;
    }

    public CustomerEventPayload getPayload() {
        return payload;
    }

    public void setPayload(CustomerEventPayload payload) {
        this.payload = payload;
    }
}
