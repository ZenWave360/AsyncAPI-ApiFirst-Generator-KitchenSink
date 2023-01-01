package io.zenwave360.example.events.support.model;


import io.zenwave360.example.adapters.events.avro.Envelope;
import io.zenwave360.example.events.oneMessage.imperative.avro.dtos.envelope.streambridge.client.OnCustomerEventAvroConsumer;
import io.zenwave360.example.events.oneMessage.imperative.avro.dtos.envelope.streambridge.provider.CustomerEventsProducer;

public class AvroEnvelopeWrapperUnWrapper implements CustomerEventsProducer.EnvelopeWrapper, OnCustomerEventAvroConsumer.EnvelopeUnWrapper {
    @Override
    public Object wrap(Object payload) {
        var envelope = new Envelope();
        envelope.setPayload(payload);
        return envelope;
    }

    @Override
    public Object unwrap(Object envelope) {
        return ((Envelope) envelope).getPayload();
    }
}
