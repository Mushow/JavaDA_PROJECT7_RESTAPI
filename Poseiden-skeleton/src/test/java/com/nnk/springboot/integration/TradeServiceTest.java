package com.nnk.springboot.integration;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.services.TradeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters=false)
@ActiveProfiles("test")
@Sql("../../../../scripts/trade.sql")
public class TradeServiceTest {
    @Autowired
    private TradeService tradeService;

    @Test
    public void tradeIT() {
        List<Trade> trades = tradeService.findAll();

        Trade trade1 = trades.get(0);
        Trade trade2 = trades.get(1);

        Assertions.assertEquals("Account #1", trade1.getAccount());
        Assertions.assertEquals("Type #1", trade1.getType());
        Assertions.assertEquals(1, trade1.getBuyQuantity());
        Assertions.assertEquals("Account #2", trade2.getAccount());
        Assertions.assertEquals("Type #2", trade2.getType());
        Assertions.assertEquals(2, trade2.getBuyQuantity());

        Assertions.assertEquals(2, trades.size());

        Trade trade = new Trade();
        trade.setBuyQuantity(10.0);
        trade.setAccount("Account #3");
        trade.setType("Type #3");

        Trade tradeResponse = tradeService.save(trade);
        Assertions.assertNotNull(tradeResponse.getTradeId());
        Assertions.assertEquals(trade.getAccount(), tradeResponse.getAccount());
        Assertions.assertEquals(trade.getType(), tradeResponse.getType());

        Optional<Trade> optionalTrade = tradeService.findById(3);

        Assertions.assertTrue(optionalTrade.isPresent());
        Assertions.assertEquals("Account #3", optionalTrade.get().getAccount());
        Assertions.assertEquals(10.0, optionalTrade.get().getBuyQuantity());

        tradeService.deleteById(3);

        optionalTrade = tradeService.findById(3);

        Assertions.assertTrue(optionalTrade.isEmpty());
    }

}