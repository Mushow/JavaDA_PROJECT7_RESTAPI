package com.nnk.springboot.integration;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.services.CurvePointService;
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
@Sql("../../../../scripts/curvepoint.sql")
public class CurvePointServiceTest {

    @Autowired
    private CurvePointService curvePointService;

    @Test
    public void curvePointIT() {
        List<CurvePoint> curvePoints = curvePointService.findAll();

        CurvePoint curvePoint1 = curvePoints.get(0);
        CurvePoint curvePoint2 = curvePoints.get(1);

        Assertions.assertEquals(1.0, curvePoint1.getTerm());
        Assertions.assertEquals(2.0, curvePoint1.getValue());
        Assertions.assertEquals(3.0, curvePoint2.getTerm());
        Assertions.assertEquals(4.0, curvePoint2.getValue());

        Assertions.assertEquals(2, curvePoints.size());

        CurvePoint curvePoint = new CurvePoint();
        curvePoint.setTerm(5.0);
        curvePoint.setValue(6.0);

        CurvePoint curvePointResponse = curvePointService.save(curvePoint);
        Assertions.assertNotNull(curvePointResponse.getId());
        Assertions.assertEquals(curvePoint.getTerm(), curvePointResponse.getTerm());

        Optional<CurvePoint> optionalCurvePoint = curvePointService.findById(3);

        Assertions.assertTrue(optionalCurvePoint.isPresent());
        Assertions.assertEquals(5.0, optionalCurvePoint.get().getTerm());
        Assertions.assertEquals(6.0, optionalCurvePoint.get().getValue());

        curvePointService.deleteById(3);

        optionalCurvePoint = curvePointService.findById(3);

        Assertions.assertTrue(optionalCurvePoint.isEmpty());
    }

}
