package com.member.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration // Configuration이 지정되어 있는 클래스를 자바 기반의 설정파일로 인식.
@PropertySource("classpath:/application.properties") // 해당 클래스에서 참조할 프로퍼티 파일의 위치를 지정
public class DatabaseConfig {

	@Autowired // Bean으로 등록된 인스턴스(객체)를 클래스에 주입하는데 사용.
	private ApplicationContext context;
	// 스프링 컨테이너중 하나로, Bean의 생성과 사용, 관계설정,생명 주기 등을 관리

	@Bean // Configuration의 메서드에만 지정이 가능,
	@ConfigurationProperties(prefix = "spring.datasource.hikari") // (application.properties)에서 읽어올 설정 정보의 prefix 지정
	public HikariConfig hikariConfig() {

		return new HikariConfig(); // 커넥션 풀 라이브러리인 HikariCP 객체를 생성해서 반환.

	}

	@Bean
	public DataSource dataSource() {
		// DataSource = DB와 관계된 커넥션 정보를 담고 있으며, 빈으로 등록하여 인자로 넘겨준다.
		return new HikariDataSource(hikariConfig());
		// 순수 JDBC는 SQL을 실행할 때 마다 커넥션을 맺고 끚는 I/O작업을 진행하는데,
		// 이러한 작업은 상당한 리소스를 잡아 먹게된다.
		// 이러한 문제의 해결 방법으로 커넥션 풀이 등장. 커넥션 풀은 커넥션 객체를 생성해두고,
		// DB에 접근하는 사용자에게 미리 생성해 둔 커넥션을 제공 했다가 다시 돌려받는 방법이다.
		// DataSource는 커넥션 풀을 지원하기 위한 인터페이스다.
	}

	// 사실상 DB와의 연결의 직접적인 메소드
	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		// SqlSessionFactory : 데이터베이스와의 연결과 SQL의 실행에 대한 모든 것을 가진 가장 중요한 객체,
		// 이 객체가 DataSource를 참조해 MyBatis와 Mysql서버를 연동 시켜준다.
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		// SqlSessionFactory를 사용하기 위해서는 SqlSessionFactoryBean객체를 선언해 줘야한다.
		// SqlSessionFactoryBean은 MyBatis와 스프링의 연동 모듈로 사용되는데,
		// 마이바티스 Mapper XML, 기타 설정 파일의 위치등을 지정하고,
		// SqlSessionFactoryBean자체가 아닌 , getObject메소드가 리턴하는 SqlSessionFactory를 생성
		factoryBean.setDataSource(dataSource());
		// 위의 메소드에서 받아온 DataSource를 Bean의 source값에 주입
		factoryBean.setMapperLocations(context.getResources("classpath:/mapper/**/*Mapper.xml"));
		// DB와 연결시킬 Mapper.XML파일의 위치를 설정해 스프링이 Mapper를 인식할 수 있게함.
		// context는 ApplicationContext로 IoC(컨테이너)를 담당, Bean의 생성 ,관계 설정을 한다
		factoryBean.setConfiguration(mybatisConfig());
		// myBatisConfig를 통해 읽어온 application프로퍼티의 설정을 Bean에 설정
		return factoryBean.getObject();
	}

	@Bean
	public SqlSessionTemplate sqlSession() throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory());
		// SqlSessionFactory를 통해서 생성되고, DB의 커밋,롤백등
		// SQL의 실행에 필요한 모든 메서드를 가지고 있는 객체라고 볼 수 있다.
	}

	@Bean
	@ConfigurationProperties(prefix = "mybatis.configuration")
	public org.apache.ibatis.session.Configuration mybatisConfig() {
		return new org.apache.ibatis.session.Configuration();
		// 프로퍼티에서 mybatis.configuration으로 시작하는 모든 설정 값을 읽어온다
		// DB의 처리방식인 스네이크 케이스를 > 자바방식인 카멜 케이스로 변경하는 설정을 가져오게됨.
	}

}