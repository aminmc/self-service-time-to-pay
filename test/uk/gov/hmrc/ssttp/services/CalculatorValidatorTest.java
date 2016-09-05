/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ssttp.services;

import org.junit.Before;
import org.junit.Test;
import play.i18n.Messages;
import uk.gov.hmrc.ssttp.models.Calculation;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CalculatorValidatorTest {

    private CalculationValidator calculationValidator;

    @Before
    public void init() {
        calculationValidator = new CalculationValidator();
    }

    @Test
    public void canValidateIncorrectPaymentFrequency() {
        //given:
        Calculation calculation = new Calculation();
        calculation.setPaymentFrequency("1X");

        //when:
        Map<String,List<String> > messages = calculationValidator.validate(calculation);

        //then:
        assertThat(messages.size(), is(1));
        assertThat(messages.get("paymentFrequency").get(0), is(Messages.get("payment.frequency.format")));
    }
}
