# 스프링 DB 1편 - 데이터 접근 핵심 원리

## JDBC

---

- 애플리케이션 - DB
  - 커넥션 연결
  - SQL 전달
  - 결과 응답
- 각 DB 사용법이 다름 
  - 다른 DB로 변경 시, 애플리케이션 코드도 DB 인터페이스에 맞게 변경
  - 각 DB에 대해 새로 학습해야 함
- 이런 문제를 해결하기 위해 JDBC라는 자바 표준이 등장

### JDBC 표준 인터페이스

- Java Database Connectivity
- 자바에서 데이터베이스에 접속할 수 있도록 하는 자바 API
- JDBC 인터페이스
  - 커넥션 연결 : `java.sql.Connection`
  - SQL 전달 : `java.sql.Statement`
  - 결과 응답 : `java.sql.ResultSet`

- JDBC 드라이버
  - 각 DB벤더에서 JDBC 인터페이스를 구현한 라이브러리를 제공
  - 이를 JDBC 드라이버라고 함
  - 애플리케이션이 다른 DB를 이용하고 싶다면 JDBC 드라이버를 변경하면 됨

### JDBC 사용법

- 옛날에는 JDBC를 직접 사용
- 최근에는 SQL Mapper, ORM 기술 등이 등장
- SQL Mapper
  - JdbcTemplate, MyBatis
  - 개발자가 SQL을 직접 작성해야 함
- ORM
  - JPA
  - 객체를 기반으로 SQL을 동적으로 만들어서 실행
- 두 기술 모두 내부적으로은 JDBC 사용하기 때문에 JDBC 기본 원리는 알아둬야 함

- DriverManager는 현재 애플리케이션에 import 되어있는 JDBC 드라이버들을 인식하고, 적합한 드라이버를 찾아 커넥션을 요청함