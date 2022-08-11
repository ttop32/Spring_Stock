package com.ttop.spring.stock.repo;




import static org.assertj.core.api.Assertions.assertThat;

import com.ttop.spring.stock.domain.EmailToken;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional
public class EmailTokenRepoTest {

    @Autowired
    EmailTokenRepo emailTokenRepo;

    @Test
    public void testFindByTickerId() {
        // given
        EmailToken emailToken = EmailToken.builder().userId(1L).build();
        emailTokenRepo.save(emailToken);
        
        // when
        EmailToken emailTokenResult = emailTokenRepo.findByUserId(emailToken.getUserId()).get();

        // then
        assertThat(emailTokenResult).isNotNull();
        assertThat(emailTokenResult.getUserId()).isEqualTo(emailToken.getUserId());
    }

}
