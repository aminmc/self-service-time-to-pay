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
