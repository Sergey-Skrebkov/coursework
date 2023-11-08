package ru.template.example.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.template.example.documents.controller.dto.DocumentDto;

import java.util.concurrent.ExecutionException;

@Component
public class KafkaSender {
    @Autowired
    private KafkaTemplate<String, DocumentDto> kafkaTemplate;

    public void sendMessage(DocumentDto document, String topicName) {
        ListenableFuture<SendResult<String, DocumentDto>> future =
                kafkaTemplate.send("documents", document);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("failure");
            }
            @Override
            public void onSuccess(SendResult<String, DocumentDto> result) {
                System.out.println("success");
            }
        });
        try {

            //Синхронный процесс ожидания получения от брокера ответа
            SendResult<String, DocumentDto> sendResult = future.get();
            System.out.println(sendResult.getProducerRecord());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
