package org.abg.batchapi.batch;

import org.abg.batchapi.entities.Visitor;
import org.abg.batchapi.repositories.VisitorRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;

@Component
public class VisitorItemProcessor implements ItemProcessor<Visitor, Visitor> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private VisitorRepository visitorRepository;

//    public VisitorItemProcessor(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//
//    }

    @Override
    public Visitor process(Visitor item) {
        item.setAddress("ABG Egypt");
        return item;
    }

}
