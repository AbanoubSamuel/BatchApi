package org.abg.batchapi.batch;

import org.abg.batchapi.dto.VisitorDto;
import org.abg.batchapi.entities.Visitor;
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

    public VisitorItemProcessor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

    }

    @Override
    public Visitor process(Visitor item) throws Exception {
        item.setVisitDate(dateFormat.parse(item.getStrVisitDate()));
        String uri = "https://localhost.com/api/getData";
        VisitorDto result = restTemplate.postForEntity(uri, item, VisitorDto.class).getBody();
        assert result != null;
        item.setStrVisitDate(result.getVisitDate().toString());
        return item;
    }

}
