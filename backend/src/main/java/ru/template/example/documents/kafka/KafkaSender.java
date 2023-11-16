package ru.template.example.documents.kafka;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;

@Component
@AllArgsConstructor
public class KafkaSender {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String document,String key, String topicName) {
        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topicName, key, document);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("failure");
            }
            @Override
            public void onSuccess(SendResult<String, String> result) {
                System.out.println("success");
            }
        });
        try {

            //Синхронный процесс ожидания получения от брокера ответа
            SendResult<String, String> sendResult = future.get();
            System.out.println(sendResult.getProducerRecord());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
