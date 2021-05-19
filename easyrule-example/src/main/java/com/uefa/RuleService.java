package com.uefa;


import com.uefa.dto.RuleDto;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.mvel.MVELRule;
import org.springframework.stereotype.Service;
import org.jeasy.rules.core.*;

@Service
public class RuleService {

    public static void runRules() {

        //https://github.com/imona/tutorial/wiki/MVEL-Guide#foreach

        Facts values = new Facts();
        values.put("abc", new RuleDto("name1", "10 >5"));
        values.put("abd", new RuleDto("name1", "5 >5"));

        Rule rule = new RuleBuilder()
                .name("myRule")
                .description("myRuleDescription")
                .description("if it rains then take an umbrella")
                .when(facts -> ((RuleDto) facts.get("abc")).getName().equals("name1"))
                .then(facts -> System.out.println("valid rule 1"))
                .build();

        MVELRule weatherRule = new MVELRule()
                .name("weather rule")
                .description("if it rains then take an umbrella")
                .when(((RuleDto) values.getFact("abd").getValue()).getCondition())
                .then("System.out.println(\"valid rule 2\");");

        Rules rules = new Rules();
        rules.register(weatherRule);
        rules.register(rule);
        RulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.fire(rules, values);

    }

}
