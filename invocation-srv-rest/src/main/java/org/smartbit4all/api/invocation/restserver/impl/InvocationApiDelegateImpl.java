package org.smartbit4all.api.invocation.restserver.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.restserver.InvocationApiDelegate;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InvocationApiDelegateImpl implements InvocationApiDelegate {

  @Autowired
  private InvocationApi invocationApi;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ObjectApi objectApi;

  @Override
  public ResponseEntity<InvocationParameter> invokeApi(
      InvocationRequest body) throws InvocationRestException {

    InvocationParameter result;
    try {
      result = invokeBase(body);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      throw new InvocationRestException(body, e);
    }

  }

  private InvocationParameter invokeBase(InvocationRequest body) throws ApiNotFoundException {
    for (InvocationParameter param : body.getParameters()) {
      Invocations.resolveParam(objectMapper, param);
    }

    InvocationParameter result = invocationApi.invoke(body);
    return result;
  }

  @Override
  public ResponseEntity<Resource> invokeDownload(InvocationRequest body)
      throws Exception {
    InvocationParameter result = invokeBase(body);
    if (result.getValue() instanceof BinaryData) {
      return ResponseEntity
          .ok(new InputStreamResource(((BinaryData) result.getValue()).inputStream()));
    }
    return ResponseEntity.ok().body(null);
  }

  @Override
  public ResponseEntity<InvocationParameter> invokeUploadMultiple(UUID uuid,
      String invocationRequest, List<MultipartFile> contents) throws Exception {
    InvocationRequest request = resolveMultipartRequest(invocationRequest, contents);

    InvocationParameter result = invocationApi.invoke(request);
    return ResponseEntity.ok(result);
  }

  @Override
  public ResponseEntity<Resource> invokeUploadDownloadMultiple(UUID uuid, String invocationRequest,
      List<MultipartFile> contents) throws Exception {
    InvocationRequest request = resolveMultipartRequest(invocationRequest, contents);

    InvocationParameter result = invocationApi.invoke(request);

    if (result.getValue() instanceof BinaryData) {
      return ResponseEntity
          .ok(new InputStreamResource(((BinaryData) result.getValue()).inputStream()));
    }
    return ResponseEntity.ok().body(null);
  }

  private final InvocationRequest resolveMultipartRequest(String invocationRequest,
      List<MultipartFile> contents) throws IOException {
    InvocationRequest request = objectApi.asType(InvocationRequest.class, invocationRequest);
    List<BinaryData> binaryDataContents;
    if (contents != null) {
      binaryDataContents = new ArrayList<>();
      for (MultipartFile multipartFile : contents) {
        binaryDataContents.add(BinaryData.of(multipartFile.getInputStream()));
      }
    } else {
      binaryDataContents = Collections.emptyList();
    }
    for (InvocationParameter param : request.getParameters()) {
      resolveParam(param, binaryDataContents);
    }
    return request;
  }

  private final void resolveParam(InvocationParameter param, List<BinaryData> binaryDataContents)
      throws IOException {
    if (BinaryData.class.getName().equals(param.getTypeClass())) {
      param.value(binaryDataContents.get(objectApi.asType(Integer.class, param.getValue())));
    } else {
      Invocations.resolveParam(objectMapper, param);
    }
  }

}
