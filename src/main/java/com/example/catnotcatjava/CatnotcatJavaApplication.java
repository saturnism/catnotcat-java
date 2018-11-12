package com.example.catnotcatjava;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.vision.v1.*;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.Base64;
import java.util.function.Function;

@SpringBootApplication
public class CatnotcatJavaApplication {
	@Bean
	ImageAnnotatorClient imageAnnotatorClient(CredentialsProvider credentialsProvider) throws IOException {
		return ImageAnnotatorClient.create(
				ImageAnnotatorSettings.newBuilder()
						.setCredentialsProvider(credentialsProvider)
						.build());
	}

	@Bean Function<String, String> catnotcat(ImageAnnotatorClient client) {
		return (image) -> {
			BatchAnnotateImagesResponse batchAnnotateImagesResponse = client.batchAnnotateImages(Lists.newArrayList(
					AnnotateImageRequest.newBuilder()
							.setImage(Image.newBuilder()
									.setContent(ByteString.copyFrom(Base64.getDecoder().decode(image)))
									.build())
							.build()
			));

			boolean cat = batchAnnotateImagesResponse.getResponses(0)
					.getLabelAnnotationsList()
					.stream()
					.anyMatch(label -> label.getDescription().equals("cat") && label.getScore() > 0.90);

			if (cat) {
				return "cat";
			} else {
				return "not cat";
			}
		};

	}
	public static void main(String[] args) {
		SpringApplication.run(CatnotcatJavaApplication.class, args);
	}
}
