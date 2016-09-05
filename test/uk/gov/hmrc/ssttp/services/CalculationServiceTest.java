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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.hmrc.ssttp.models.Calculation;
import uk.gov.hmrc.ssttp.models.PaymentSchedule;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CalculationServiceTest {

    @Test
    public void successfulInterestToPayCalculation() throws Exception {
        ObjectMapper objectMapper = ObjectMapperFactory.mapper();
        CalculationService calculationService = new StubbedCalculationService();

        //Given:
        PaymentSchedule paymentSchedule;
        Calculation calculation = objectMapper.readValue(this.getClass().getResourceAsStream("/calculation.json"), Calculation.class);

        //when:
        paymentSchedule = calculationService.generate(calculation);

        //then:
        assertThat(paymentSchedule.getTotalInterestCharged().getAmount(), is(new BigDecimal(35.81).setScale(2, BigDecimal.ROUND_FLOOR)));

    }
}
