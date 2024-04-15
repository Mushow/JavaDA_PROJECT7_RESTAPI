package com.nnk.springboot.integration;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.services.BidListService;
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
@Sql("../../../../scripts/bidlist.sql")
public class BidListServiceIT {

    @Autowired
    private BidListService bidListService;

    @Test
    public void bidListIT() {
        List<BidList> bidLists = bidListService.findAll();

        BidList bidList1 = bidLists.get(0);
        BidList bidList2 = bidLists.get(1);

        Assertions.assertEquals("Account #1", bidList1.getAccount());
        Assertions.assertEquals("Type #1", bidList1.getType());
        Assertions.assertEquals(2.0, bidList1.getBidQuantity());
        Assertions.assertEquals("Account #2", bidList2.getAccount());
        Assertions.assertEquals("Type #2", bidList2.getType());
        Assertions.assertEquals(9.0, bidList2.getBidQuantity());

        Assertions.assertEquals(2, bidLists.size());

        BidList bidList = new BidList();
        bidList.setBidQuantity(5.0);
        bidList.setAccount("Account #3");
        bidList.setType("Type #3");

        BidList bidListResponse = bidListService.save(bidList);
        Assertions.assertNotNull(bidListResponse.getBidListId());
        Assertions.assertEquals(bidList.getAccount(), bidListResponse.getAccount());

        Optional<BidList> optionalBidList = bidListService.findById(3);

        Assertions.assertTrue(optionalBidList.isPresent());
        Assertions.assertEquals(5.0, optionalBidList.get().getBidQuantity());
        Assertions.assertEquals("Account #3", optionalBidList.get().getAccount());

        bidListService.deleteById(3);

        optionalBidList = bidListService.findById(3);

        Assertions.assertTrue(optionalBidList.isEmpty());
    }

}