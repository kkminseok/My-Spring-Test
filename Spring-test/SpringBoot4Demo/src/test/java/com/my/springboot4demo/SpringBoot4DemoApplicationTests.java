package com.my.springboot4demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SpringBoot4DemoApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void downloadFileFromKakaoTalk() throws IOException, InterruptedException {
		// Given
		String fileUrl = "https://talk.kakaocdn.net/dna/bD1RJP/o2EekZp2Jd/av5nKHnghjEjW7BTikMhCI/i_394cb2b4dbcf.jpg?credential=zf3biCPbmWRjbqf40YGePFLewdou7TIK&expires=1769570223&signature=atisb26jWvVFLVpM5htRqoh8ajM%3D";

		Path downloadPath = Paths.get(System.getProperty("user.home"), "Downloads", "kakao_downloaded_image.jpg");

		// HTTP/2를 명시적으로 사용하는 JDK HttpClient
		HttpClient httpClient = HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_2)
				.followRedirects(HttpClient.Redirect.NORMAL)
				.build();

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(fileUrl))
//				.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36")
//				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
//				.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
//				.header("Sec-Fetch-Dest", "document")
//				.header("Sec-Fetch-Mode", "navigate")
//				.header("Sec-Fetch-Site", "none")
//				.header("Sec-Fetch-User", "?1")
//				.header("sec-ch-ua", "\"Google Chrome\";v=\"143\", \"Chromium\";v=\"143\", \"Not A(Brand\";v=\"24\"")
//				.header("sec-ch-ua-mobile", "?0")
//				.header("sec-ch-ua-platform", "\"macOS\"")
				.GET()
				.build();

		// When
		HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

		System.out.println("HTTP 버전: " + response.version());
		System.out.println("응답 코드: " + response.statusCode());

		// Then
		assertThat(response.statusCode()).isEqualTo(200);

		byte[] fileContent = response.body();
		assertThat(fileContent).isNotNull();
		assertThat(fileContent.length).isGreaterThan(0);

		// 파일로 저장
		Files.write(downloadPath, fileContent);

		System.out.println("파일 다운로드 완료!");
		System.out.println("파일 크기: " + fileContent.length + " bytes");
		System.out.println("저장 경로: " + downloadPath.toAbsolutePath());

		// 파일이 실제로 생성되었는지 확인
		assertThat(Files.exists(downloadPath)).isTrue();
	}
}
