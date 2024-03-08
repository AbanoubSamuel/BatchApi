package org.abg.batchapi.batch;

import org.abg.batchapi.entity.Visitor;
import org.abg.batchapi.utls.JsonResponse;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class VisitorItemProcessor implements ItemProcessor<Visitor, Visitor> {

    @Autowired
    private RestTemplate restTemplate;


    public VisitorItemProcessor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Visitor process(Visitor item) {
        String uri = "https://mocki.io/v1/dbc5d55d-1470-438a-a7ee-b4df8bef404a";
        JsonResponse jsonResponse = restTemplate.getForEntity(uri, JsonResponse.class).getBody();
        assert jsonResponse != null;
        assert jsonResponse.getData() != null;
        item.setAddress(jsonResponse.getData().toString());
        return item;
    }

}
