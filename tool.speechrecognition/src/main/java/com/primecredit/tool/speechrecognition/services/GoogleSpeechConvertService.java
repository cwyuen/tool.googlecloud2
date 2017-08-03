package com.primecredit.tool.speechrecognition.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.gax.grpc.OperationFuture;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;
import com.primecredit.tool.common.util.WavFileHandler;

@Service
public class GoogleSpeechConvertService {
	
	private static Logger logger = LoggerFactory.getLogger(GoogleSpeechConvertService.class);
	
	@Value("${temp.path}")
	private String tempPath;
	
	public List<String> convert(String sourceFile) {

		WavFileHandler wavFileHandler = WavFileHandler.getInstance();
		
		Map<Integer, String> resultMap = initResultMap();

		try {
			List<String> tmpFiles = wavFileHandler.splitWavFile(sourceFile,tempPath);
			// List<String> tmpFiles = null;
			logger.debug("Google Speech API {} - Start ....",sourceFile);
			Iterator<String> fileIter = tmpFiles.iterator();
			while (fileIter.hasNext()) {
				String tmpFileName = fileIter.next();
				Map<Integer, String> tmpMap = asyncRecognizeFile(tmpFileName);
				appendMapResult(resultMap, tmpMap);
				File tmpFile = new File(tmpFileName);
				tmpFile.delete();
				
			}

			logger.debug("\nGoogle Speech API End ....");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<String> results = new ArrayList<String>();
		results.addAll(resultMap.values());
		return results;
	}

	private void appendMapResult(Map<Integer, String> targetMap, Map<Integer, String> updateMap) {
		Iterator<Integer> keyIter = updateMap.keySet().iterator();
		while (keyIter.hasNext()) {
			int key = keyIter.next();
			if (targetMap.containsKey(key)) {
				String value = targetMap.get(key);
				String append = updateMap.get(key);
				StringBuilder sb = new StringBuilder();
				sb.append(value);
				sb.append(append);
				targetMap.put(key, sb.toString());
			} else {
				String append = updateMap.get(key);
				targetMap.put(key, append);
			}
		}
	}

	private Map<Integer, String> initResultMap() {
		Map<Integer, String> resultMap = new LinkedHashMap<Integer, String>();
		resultMap.put(1, "");
		resultMap.put(2, "");
		resultMap.put(3, "");
		resultMap.put(4, "");
		resultMap.put(5, "");

		return resultMap;
	}

	private Map<Integer, String> asyncRecognizeFile(String fileName) throws Exception, IOException {
		Map<Integer, String> resultMap = new LinkedHashMap<Integer, String>();
		StringBuilder sbTranscript1 = new StringBuilder();
		StringBuilder sbTranscript2 = new StringBuilder();
		StringBuilder sbTranscript3 = new StringBuilder();
		StringBuilder sbTranscript4 = new StringBuilder();
		StringBuilder sbTranscript5 = new StringBuilder();

		// Instantiates a client with GOOGLE_APPLICATION_CREDENTIALS
		SpeechClient speech = SpeechClient.create();

		Path path = Paths.get(fileName);
		byte[] data = Files.readAllBytes(path);
		ByteString audioBytes = ByteString.copyFrom(data);

		// Configure request with local raw PCM audio
		RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.LINEAR16)
				.setLanguageCode("yue-Hant-HK").setSampleRateHertz(8000).setProfanityFilter(false).setMaxAlternatives(5)
				.build();

		RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

		// Use non-blocking call for getting file transcription

		OperationFuture<LongRunningRecognizeResponse> response = speech.longRunningRecognizeAsync(config, audio);
		while (!response.isDone()) {
			// System.out.println("Waiting for response...");
			Thread.sleep(5000);
		}
		List<SpeechRecognitionResult> results = response.get().getResultsList();
		// System.out.println(response.get);

		// RecognizeResponse response = speech.recognize(config, audio);
		// List<SpeechRecognitionResult> results = response.getResultsList();

		for (SpeechRecognitionResult result : results) {
			List<SpeechRecognitionAlternative> alternatives = result.getAlternativesList();

			int i = 1;
			for (SpeechRecognitionAlternative alternative : alternatives) {
				// System.out.println(alternative.getConfidence());
				// System.out.println("[" + i + "]\t" + alternative.getTranscript());
				if (i == 1)
					sbTranscript1.append(alternative.getTranscript());
				if (i == 2)
					sbTranscript2.append(alternative.getTranscript());
				if (i == 3)
					sbTranscript3.append(alternative.getTranscript());
				if (i == 4)
					sbTranscript4.append(alternative.getTranscript());
				if (i == 5)
					sbTranscript5.append(alternative.getTranscript());
				i++;
			}
		}
		speech.close();
		resultMap.put(1, sbTranscript1.toString());
		resultMap.put(2, sbTranscript2.toString());
		resultMap.put(3, sbTranscript3.toString());
		resultMap.put(4, sbTranscript4.toString());
		resultMap.put(5, sbTranscript5.toString());
		return resultMap;
	}
	
}