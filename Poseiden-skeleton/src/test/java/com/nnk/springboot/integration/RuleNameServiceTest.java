package com.nnk.springboot.integration;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.services.RuleNameService;
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
@Sql("../../../../scripts/rulename.sql")
public class RuleNameServiceTest {

    @Autowired
    private RuleNameService ruleNameService;

    @Test
    public void ruleNameIT() {
        List<RuleName> ruleNames = ruleNameService.findAll();

        RuleName ruleName1 = ruleNames.get(0);
        RuleName ruleName2 = ruleNames.get(1);

        Assertions.assertEquals("Name #1", ruleName1.getName());
        Assertions.assertEquals("Description #1", ruleName1.getDescription());
        Assertions.assertEquals("Json #1", ruleName1.getJson());
        Assertions.assertEquals("Template #1", ruleName1.getTemplate());
        Assertions.assertEquals("Sql Str #1", ruleName1.getSqlStr());
        Assertions.assertEquals("Sql Part #1", ruleName1.getSqlPart());
        Assertions.assertEquals("Name #2", ruleName2.getName());
        Assertions.assertEquals("Description #2", ruleName2.getDescription());
        Assertions.assertEquals("Json #2", ruleName2.getJson());
        Assertions.assertEquals("Template #2", ruleName2.getTemplate());
        Assertions.assertEquals("Sql Str #2", ruleName2.getSqlStr());
        Assertions.assertEquals("Sql Part #2", ruleName2.getSqlPart());

        Assertions.assertEquals(2, ruleNames.size());

        RuleName ruleName = new RuleName();
        ruleName.setName("Name #3");
        ruleName.setDescription("Description #3");
        ruleName.setJson("Json #3");
        ruleName.setTemplate("Template #3");
        ruleName.setSqlStr("Sql Str #3");
        ruleName.setSqlPart("Sql Part #3");

        RuleName ruleNameResponse = ruleNameService.save(ruleName);
        Assertions.assertNotNull(ruleNameResponse.getId());
        Assertions.assertEquals(ruleName.getName(), ruleNameResponse.getName());
        Assertions.assertEquals(ruleName.getJson(), ruleNameResponse.getJson());
        Assertions.assertEquals(ruleName.getSqlPart(), ruleNameResponse.getSqlPart());

        Optional<RuleName> optionalRuleName = ruleNameService.findById(3);

        Assertions.assertTrue(optionalRuleName.isPresent());
        Assertions.assertEquals("Sql Str #3", optionalRuleName.get().getSqlStr());
        Assertions.assertEquals("Template #3", optionalRuleName.get().getTemplate());

        ruleNameService.deleteById(3);

        optionalRuleName = ruleNameService.findById(3);

        Assertions.assertTrue(optionalRuleName.isEmpty());
    }

}