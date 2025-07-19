package com.conduct.interview.practise.soap;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;

@Endpoint
public class HelloEndpoint {

    private static final String NAMESPACE = "http://conduct.com/interview/practise/soap";

    @PayloadRoot(namespace = NAMESPACE, localPart = "HelloRequest")
    @ResponsePayload
    public Element handleHello(@RequestPayload Element request) throws Exception {
        String name = request.getElementsByTagName("name").item(0).getTextContent();

        String responseXml = "<ns2:HelloResponse xmlns:ns2=\"" + NAMESPACE + "\">" +
                "<message>Hello, " + name + "!</message>" +
                "</ns2:HelloResponse>";

        return DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new java.io.ByteArrayInputStream(responseXml.getBytes()))
                .getDocumentElement();
    }
}
