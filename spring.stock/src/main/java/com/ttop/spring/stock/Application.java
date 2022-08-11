package com.ttop.spring.stock;

import java.time.LocalDate;

import com.ttop.spring.stock.domain.Comment;
import com.ttop.spring.stock.domain.Member;
import com.ttop.spring.stock.dto.CommentCreateDto;
import com.ttop.spring.stock.dto.MemberSignUpDto;
import com.ttop.spring.stock.dto.StockDetailDto;
import com.ttop.spring.stock.dto.StockGraphSingleDto;
import com.ttop.spring.stock.service.CommentService;
import com.ttop.spring.stock.service.MemberService;
import com.ttop.spring.stock.service.StockGraphService;
import com.ttop.spring.stock.service.StockDetailService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.extern.slf4j.Slf4j;

@EnableJpaAuditing
@EnableCaching
@SpringBootApplication
@Slf4j
public class Application {

	// 127.0.0.1:8080/
	// sudo kill -9 `sudo lsof -t -i:8080`  spring port
	// sudo kill -9 `sudo lsof -t -i:6977`   emedded redis

	public static void main(String[] args) {
		log.debug("start========================");
		
		SpringApplication.run(Application.class, args);

	}

	
	

	





	// run after spring start for my own test
	@Profile("commandRunner")
	@Bean
	CommandLineRunner run(Environment env, MemberService memberService, StockDetailService stockService, StockGraphService stockGraphService, CommentService commentService){
		return args ->{
			Member member1=memberService.registerMember(new MemberSignUpDto("id", "pw","name4"));
			Member member2=memberService.registerMember(new MemberSignUpDto("ttop324@gmail.com", "123456789","name5"));
			
			stockService.saveStock(new StockDetailDto("tickerlg","namelg"));
			stockService.saveStock(new StockDetailDto("tickersam","namesam"));
			stockService.saveStock(new StockDetailDto("tickers33","namesa33"));
			
			stockGraphService.addStockGraphSingle(StockGraphSingleDto.builder().tickerId("tickerlg").axesType("high").axesValue(100).date(LocalDate.of(1982, 02, 19)).build());
			stockGraphService.addStockGraphSingle(StockGraphSingleDto.builder().tickerId("tickerlg").axesType("high").axesValue(200).date(LocalDate.of(1982, 02, 23)).build());
			stockGraphService.addStockGraphSingle(StockGraphSingleDto.builder().tickerId("tickerlg").axesType("high").axesValue(300).date(LocalDate.of(1982, 02, 21)).build());
			stockGraphService.addStockGraphSingle(StockGraphSingleDto.builder().tickerId("tickerlg").axesType("high").axesValue(400).date(LocalDate.of(1982, 02, 24)).build());
			stockGraphService.addStockGraphSingle(StockGraphSingleDto.builder().tickerId("tickerlg").axesType("high").axesValue(800).date(LocalDate.of(2022, 02, 24)).build());
			stockGraphService.addStockGraphSingle(StockGraphSingleDto.builder().tickerId("tickerlg").axesType("low").axesValue(900).date(LocalDate.of(2022, 02, 24)).build());
			stockGraphService.addStockGraphSingle(StockGraphSingleDto.builder().tickerId("tickerlg").axesType("low").axesValue(1000).date(LocalDate.of(2022, 02, 25)).build());
			stockGraphService.addStockGraphSingle(StockGraphSingleDto.builder().tickerId("tickersam").axesType("high").axesValue(300).date(LocalDate.of(1982, 02, 21)).build());
			stockGraphService.addStockGraphSingle(StockGraphSingleDto.builder().tickerId("tickersam").axesType("high").axesValue(400).date(LocalDate.of(1982, 02, 24)).build());
		
			
			Comment comment1=commentService.insertComment(CommentCreateDto.builder().content("content1").tickerId("tickerlg").build(),member2);
			Comment comment2=commentService.insertComment(CommentCreateDto.builder().content("content2").tickerId("tickerlg").build(),member2);
			Comment comment3=commentService.insertComment(CommentCreateDto.builder().content("content3").tickerId("tickerlg").replyParentId(comment2.getId()).build(),member2);
			Comment comment4=commentService.insertComment(CommentCreateDto.builder().content("content4").tickerId("tickerlg").build(),member1);
				
		};
	}
}