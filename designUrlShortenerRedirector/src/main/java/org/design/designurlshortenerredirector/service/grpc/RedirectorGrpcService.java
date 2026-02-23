package org.design.designurlshortenerredirector.service.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import redirector.RedirectorServiceGrpc;
import redirector.Redirector.ResolveResponse;

@GrpcService
public class RedirectorGrpcService extends RedirectorServiceGrpc.RedirectorServiceImplBase {

    @Override
    public void resolve(redirector.Redirector.ResolveRequest request, StreamObserver<ResolveResponse> responseObserver) {
        String code = request.getShortCode();

        // TODO: lookup database/cache
        String url = "https://example.com/" + code;

        ResolveResponse response = ResolveResponse.newBuilder()
                .setOriginalUrl(url)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
