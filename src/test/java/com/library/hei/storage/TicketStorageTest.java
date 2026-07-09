package com.library.hei.storage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.library.hei.conf.BucketConf;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@ExtendWith(MockitoExtension.class)
class TicketStorageTest {

  @Mock private S3Client s3Client;
  @Mock private S3Presigner s3Presigner;
  @Mock private BucketConf bucketConf;
  @Mock private PresignedGetObjectRequest presignedGetObjectRequest;

  private TicketStorage ticketStorage;

  @BeforeEach
  void setUp() {
    ticketStorage = new TicketStorage(s3Client, s3Presigner, bucketConf);
  }

  @Test
  void upload_putsObjectAndReturnsPresignedUrl() throws MalformedURLException {
    when(bucketConf.getBucketName()).thenReturn("library-hei-tickets");
    URL presignedUrl =
        new URL("https://library-hei-tickets.s3.amazonaws.com/tickets/sale-1.pdf?signed=1");
    when(presignedGetObjectRequest.url()).thenReturn(presignedUrl);
    when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
        .thenReturn(presignedGetObjectRequest);

    byte[] content = "%PDF-fake-content".getBytes();
    String result = ticketStorage.upload("tickets/sale-1.pdf", content);

    assertEquals(presignedUrl.toString(), result);

    ArgumentCaptor<PutObjectRequest> putRequestCaptor =
        ArgumentCaptor.forClass(PutObjectRequest.class);
    verify(s3Client)
        .putObject(
            putRequestCaptor.capture(), any(software.amazon.awssdk.core.sync.RequestBody.class));

    PutObjectRequest putRequest = putRequestCaptor.getValue();
    assertEquals("library-hei-tickets", putRequest.bucket());
    assertEquals("tickets/sale-1.pdf", putRequest.key());
    assertEquals("application/pdf", putRequest.contentType());

    verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
  }
}
