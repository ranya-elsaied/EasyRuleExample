package com.uefa;


import com.uefa.dto.RuleDto;
import org.jeasy.rules.api.Fact;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.mvel.MVELRule;
import org.springframework.stereotype.Service;
import org.jeasy.rules.core.*;

import java.util.Iterator;

@Service
public class RuleService {

    public static void runRules() {

        //https://github.com/imona/tutorial/wiki/MVEL-Guide#foreach

        Facts values = new Facts();
        values.put("abc", new RuleDto("name1", "10 >5"));
        values.put("abd", new RuleDto("name1", "(7 >5) &&(9<8)"));


        Rule rule1 = new RuleBuilder()
                .name("rule1")
                .description("desc1")
                .when(facts -> ((RuleDto) facts.get("abc")).getName().equals("name2"))
                .then(facts -> System.out.println("valid rule 1" + facts.get("abc").toString()))
                .build();

        MVELRule rule2 = new MVELRule()
                .name("rule2")
                .description("desc2")
                .when(((RuleDto) values.getFact("abd").getValue()).getCondition())
                .then("System.out.println(\"valid rule 2\");");

        Facts data = new Facts();
        data.put("x", 10);

        MVELRule rule3 = new MVELRule()
                .name("rule3")
                .description("desc3")
                .when("x > 6")
                .then("System.out.println(\"valid rule 3\");");


        Rules rules = new Rules();
        rules.register(rule1);
        rules.register(rule2);

        RulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.fire(rules, values);

        rules = new Rules();
        rules.register(rule3);

        rulesEngine = new DefaultRulesEngine();
        rulesEngine.fire(rules, data);


        useMoreThanOneFact(rulesEngine);

    }

    private static void useMoreThanOneFact(RulesEngine rulesEngine) {
        Rules rules;
        Facts otherValues = new Facts();
        otherValues.put("value1", "2");
        otherValues.put("value2", "10");
        RuleDto dto = new RuleDto("ab", "6 >");

        rules = new Rules();
        Iterator<Fact<?>> it = otherValues.iterator();
        while (it.hasNext()) {
            Fact fact = it.next();
            MVELRule otherRule = new MVELRule()
                    .name("other rule" + fact.getName())
                    .description("if true then print")
                    .when(dto.getCondition() + fact.getName())
                    .then("System.out.println(\"valid rule 4 " + fact.getName() + "\");");
            rules.register(otherRule);
        }

        rulesEngine.fire(rules, otherValues);
    }

}
