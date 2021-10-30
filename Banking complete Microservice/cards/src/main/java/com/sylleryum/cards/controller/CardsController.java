package com.sylleryum.cards.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sylleryum.cards.config.CardsServiceConfig;
import com.sylleryum.cards.model.Cards;
import com.sylleryum.cards.model.Customer;
import com.sylleryum.cards.model.Properties;
import com.sylleryum.cards.repository.CardsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CardsController {

    private static final Logger logger = LoggerFactory.getLogger(CardsController.class);
    final private CardsRepository cardsRepository;
    private final CardsServiceConfig cardsConfig;

    public CardsController(CardsRepository cardsRepository, CardsServiceConfig cardsConfig) {
        this.cardsRepository = cardsRepository;
        this.cardsConfig = cardsConfig;
    }

    @PostMapping("/myCards")
    public List<Cards> getCardDetails(@RequestHeader("trace-id")String traceId, @RequestBody Customer customer) {
        logger.debug("trace inside cards: {}",traceId);
        List<Cards> cards = cardsRepository.findByCustomerId(customer.getCustomerId());
        if (cards != null) {
            return cards;
        } else {
            return null;
        }

    }

    @GetMapping("/cards/properties")
    public String getPropertyDetails() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = new Properties(cardsConfig.getMsg(), cardsConfig.getBuildVersion(),
                cardsConfig.getMailDetails(), cardsConfig.getActiveBranches());
        String jsonStr = ow.writeValueAsString(properties);
        return jsonStr;
    }

}