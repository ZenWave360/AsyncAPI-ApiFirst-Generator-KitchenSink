package io.zenwave360.example.events.support.model;


import io.zenwave360.example.events.oneMessage.imperative.json.dtos.envelope.streambridge.client.OnCustomerEventConsumer;
import io.zenwave360.example.events.oneMessage.imperative.json.dtos.envelope.streambridge.provider.CustomerEventsProducer;
import io.zenwave360.example.events.oneMessage.model.CustomerEventPayload;

public class EnvelopeWrapperUnWrapper implements CustomerEventsProducer.EnvelopeWrapper, OnCustomerEventConsumer.EnvelopeUnWrapper {
    @Override
    public Object wrap(Object payload) {
        var envelope = new Envelope();
        envelope.setPayload((CustomerEventPayload) payload);
        return envelope;
    }

    @Override
    public Object unwrap(Object envelope) {
        return ((Envelope) envelope).getPayload();
    }
}
