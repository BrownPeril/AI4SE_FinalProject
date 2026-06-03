# 奶茶记录 实现计划

> **给智能体工作者：** 必需技能：使用 superpowers:subagent-driven-development（推荐）或 superpowers:executing-plans 逐任务执行本计划。步骤使用复选框（`- [ ]`）语法追踪进度。

**目标：** 构建一个个人奶茶消费追踪工具，Vue3+Vite 前端 + SpringBoot 后端，Docker 容器化部署。

**架构：** 经典三层前后端分离。后端：SpringBoot + JPA + H2 提供 RESTful JSON API。前端：Vue3 + Naive UI + ECharts 消费 API。Docker Compose 编排 Nginx（静态前端）和 SpringBoot JAR。

**技术栈：** Java 17, Spring Boot 3, Spring Data JPA, H2, Maven（后端）；Vue 3, Vite, Naive UI, Pinia, Vue Router 4, Axios, ECharts（前端）；Docker, docker-compose, GitHub Actions（基础设施）

---

## 依赖关系图

```
任务 1（后端脚手架）
  → 任务 2（模型+枚举）
    → 任务 3（仓储层+测试）
      → 任务 4（DTO）
        → 任务 5（MilkTeaRecordService+测试）
          → 任务 6（StatsService+测试）
            → 任务 7（MilkTeaRecordController+测试）
              → 任务 8（StatsController+测试）
                → 任务 9（CORS配置+全局异常处理+样例数据）

任务 10（前端脚手架）  ← 与任务 1-9 可并行
  → 任务 11（主题常量）
    → 任务 12（API模块）
      → 任务 13（Pinia状态管理+路由）
        → 任务 14（记录列表页）
          → 任务 15（记录表单页）
            → 任务 16（统计页面）

任务 9 + 任务 16 → 任务 17（后端 Dockerfile）
  → 任务 18（前端 Dockerfile + nginx）
    → 任务 19（docker-compose）
      → 任务 20（CI）
        → 任务 21（README + 根目录文档）
```

**可并行部分：** 任务 1-9（后端）和 任务 10-16（前端）相互独立，可在不同 worktree 中同时进行。

---

## 任务 1：后端项目脚手架

**涉及文件：**
- 创建：`backend/pom.xml`
- 创建：`backend/src/main/java/com/milktea/MilkTeaApplication.java`
- 创建：`backend/src/main/resources/application.yml`
- 创建：`backend/src/test/java/com/milktea/MilkTeaApplicationTest.java`

- [ ] **步骤 1：创建 pom.xml，包含所有依赖**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>
    <groupId>com.milktea</groupId>
    <artifactId>milk-tea-record</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>milk-tea-record</name>
    <description>Milk Tea Record Tracker</description>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **步骤 2：创建 MilkTeaApplication.java**

```java
package com.milktea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MilkTeaApplication {
    public static void main(String[] args) {
        SpringApplication.run(MilkTeaApplication.class, args);
    }
}
```

- [ ] **步骤 3：创建 application.yml**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:file:./data/milktea
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: Asia/Shanghai

management:
  endpoints:
    web:
      exposure:
        include: health,info
```

- [ ] **步骤 4：编写失败测试 — 验证 Spring 上下文加载**

```java
package com.milktea;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MilkTeaApplicationTest {
    @Test
    void contextLoads() {
    }
}
```

- [ ] **步骤 5：运行测试，确认通过**

运行：`cd backend && mvn test -pl . -Dtest=MilkTeaApplicationTest`
预期：通过 — Spring 上下文成功加载

- [ ] **步骤 6：提交**

```bash
cd backend && git add -A && git commit -m "feat(backend): scaffold SpringBoot project with H2, JPA, validation, actuator"
```

---

## 任务 2：模型 + 枚举

**涉及文件：**
- 创建：`backend/src/main/java/com/milktea/model/enums/SugarLevel.java`
- 创建：`backend/src/main/java/com/milktea/model/enums/IceLevel.java`
- 创建：`backend/src/main/java/com/milktea/model/enums/CupSize.java`
- 创建：`backend/src/main/java/com/milktea/model/MilkTeaRecord.java`
- 创建：`backend/src/test/java/com/milktea/model/enums/SugarLevelTest.java`
- 创建：`backend/src/test/java/com/milktea/model/enums/IceLevelTest.java`
- 创建：`backend/src/test/java/com/milktea/model/enums/CupSizeTest.java`

- [ ] **步骤 1：编写枚举的失败测试**

```java
package com.milktea.model.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SugarLevelTest {
    @Test
    void shouldContainAllValues() {
        assertEquals(5, SugarLevel.values().length);
        assertNotNull(SugarLevel.valueOf("无糖"));
        assertNotNull(SugarLevel.valueOf("微糖"));
        assertNotNull(SugarLevel.valueOf("半糖"));
        assertNotNull(SugarLevel.valueOf("少糖"));
        assertNotNull(SugarLevel.valueOf("全糖"));
    }
}
```

```java
package com.milktea.model.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IceLevelTest {
    @Test
    void shouldContainAllValues() {
        assertEquals(6, IceLevel.values().length);
        assertNotNull(IceLevel.valueOf("去冰"));
        assertNotNull(IceLevel.valueOf("少冰"));
        assertNotNull(IceLevel.valueOf("正常冰"));
        assertNotNull(IceLevel.valueOf("多冰"));
        assertNotNull(IceLevel.valueOf("温"));
        assertNotNull(IceLevel.valueOf("热"));
    }
}
```

```java
package com.milktea.model.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CupSizeTest {
    @Test
    void shouldContainAllValues() {
        assertEquals(4, CupSize.values().length);
        assertNotNull(CupSize.valueOf("小杯"));
        assertNotNull(CupSize.valueOf("中杯"));
        assertNotNull(CupSize.valueOf("大杯"));
        assertNotNull(CupSize.valueOf("超大杯"));
    }
}
```

- [ ] **步骤 2：运行测试，确认失败**

运行：`cd backend && mvn test -Dtest="SugarLevelTest,IceLevelTest,CupSizeTest"`
预期：失败 — 类未找到

- [ ] **步骤 3：实现枚举**

```java
package com.milktea.model.enums;

public enum SugarLevel {
    无糖, 微糖, 半糖, 少糖, 全糖
}
```

```java
package com.milktea.model.enums;

public enum IceLevel {
    去冰, 少冰, 正常冰, 多冰, 温, 热
}
```

```java
package com.milktea.model.enums;

public enum CupSize {
    小杯, 中杯, 大杯, 超大杯
}
```

- [ ] **步骤 4：运行测试，确认通过**

运行：`cd backend && mvn test -Dtest="SugarLevelTest,IceLevelTest,CupSizeTest"`
预期：通过

- [ ] **步骤 5：编写 MilkTeaRecord 实体的失败测试**

```java
package com.milktea.model;

import com.milktea.model.enums.CupSize;
import com.milktea.model.enums.IceLevel;
import com.milktea.model.enums.SugarLevel;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class MilkTeaRecordTest {
    @Test
    void shouldCreateRecordWithAllFields() {
        MilkTeaRecord record = new MilkTeaRecord();
        record.setBrand("喜茶");
        record.setDrinkName("多肉葡萄");
        record.setPrice(new BigDecimal("25.00"));
        record.setConsumeDate(LocalDateTime.of(2024, 6, 1, 14, 30));
        record.setSugarLevel(SugarLevel.半糖);
        record.setIceLevel(IceLevel.少冰);
        record.setCupSize(CupSize.大杯);
        record.setTeaBase("绿茶");
        record.setRating(4);
        record.setComment("好喝");
        record.setWouldRecommend(true);

        assertEquals("喜茶", record.getBrand());
        assertEquals("多肉葡萄", record.getDrinkName());
        assertEquals(new BigDecimal("25.00"), record.getPrice());
        assertEquals(SugarLevel.半糖, record.getSugarLevel());
        assertEquals(IceLevel.少冰, record.getIceLevel());
        assertEquals(CupSize.大杯, record.getCupSize());
        assertEquals(4, record.getRating());
        assertTrue(record.getWouldRecommend());
    }
}
```

- [ ] **步骤 6：运行测试，确认失败**

运行：`cd backend && mvn test -Dtest=MilkTeaRecordTest`
预期：失败 — MilkTeaRecord 类未找到

- [ ] **步骤 7：实现 MilkTeaRecord 实体**

```java
package com.milktea.model;

import com.milktea.model.enums.CupSize;
import com.milktea.model.enums.IceLevel;
import com.milktea.model.enums.SugarLevel;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "milk_tea_records")
public class MilkTeaRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String drinkName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime consumeDate;

    @Enumerated(EnumType.STRING)
    private SugarLevel sugarLevel;

    @Enumerated(EnumType.STRING)
    private IceLevel iceLevel;

    @Enumerated(EnumType.STRING)
    private CupSize cupSize;

    private String teaBase;

    private Integer rating;

    private String comment;

    private Boolean wouldRecommend;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

- [ ] **步骤 8：运行所有模型测试，确认通过**

运行：`cd backend && mvn test -Dtest="SugarLevelTest,IceLevelTest,CupSizeTest,MilkTeaRecordTest"`
预期：通过

- [ ] **步骤 9：提交**

```bash
cd backend && git add -A && git commit -m "feat(backend): add MilkTeaRecord entity and enums (SugarLevel, IceLevel, CupSize)"
```

---

## 任务 3：仓储层 + 测试

**涉及文件：**
- 创建：`backend/src/main/java/com/milktea/repository/MilkTeaRecordRepository.java`
- 创建：`backend/src/test/java/com/milktea/repository/MilkTeaRecordRepositoryTest.java`

- [ ] **步骤 1：编写仓储层的失败测试**

```java
package com.milktea.repository;

import com.milktea.model.MilkTeaRecord;
import com.milktea.model.enums.CupSize;
import com.milktea.model.enums.IceLevel;
import com.milktea.model.enums.SugarLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MilkTeaRecordRepositoryTest {
    @Autowired
    private MilkTeaRecordRepository repository;

    private MilkTeaRecord sampleRecord;

    @BeforeEach
    void setUp() {
        sampleRecord = new MilkTeaRecord();
        sampleRecord.setBrand("喜茶");
        sampleRecord.setDrinkName("多肉葡萄");
        sampleRecord.setPrice(new BigDecimal("25.00"));
        sampleRecord.setConsumeDate(LocalDateTime.of(2024, 6, 1, 14, 30));
        sampleRecord.setSugarLevel(SugarLevel.半糖);
        sampleRecord.setIceLevel(IceLevel.少冰);
        sampleRecord.setCupSize(CupSize.大杯);
        sampleRecord.setRating(4);
        sampleRecord.setWouldRecommend(true);
    }

    @Test
    void shouldSaveAndFindById() {
        MilkTeaRecord saved = repository.save(sampleRecord);
        Optional<MilkTeaRecord> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("喜茶", found.get().getBrand());
    }

    @Test
    void shouldFindAllWithPagination() {
        repository.save(sampleRecord);
        Page<MilkTeaRecord> page = repository.findAll(PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
        assertEquals(1, page.getContent().size());
    }

    @Test
    void shouldDeleteById() {
        MilkTeaRecord saved = repository.save(sampleRecord);
        repository.deleteById(saved.getId());
        assertFalse(repository.findById(saved.getId()).isPresent());
    }

    @Test
    void shouldFindByBrand() {
        repository.save(sampleRecord);
        List<MilkTeaRecord> results = repository.findByBrand("喜茶");
        assertEquals(1, results.size());

        List<MilkTeaRecord> empty = repository.findByBrand("蜜雪冰城");
        assertTrue(empty.isEmpty());
    }

    @Test
    void shouldFindByBrandContainingOrDrinkNameContaining() {
        repository.save(sampleRecord);
        List<MilkTeaRecord> byBrand = repository.findByBrandContainingOrDrinkNameContaining("喜", "不存在的饮品");
        assertEquals(1, byBrand.size());

        List<MilkTeaRecord> byDrink = repository.findByBrandContainingOrDrinkNameContaining("不存在的品牌", "葡萄");
        assertEquals(1, byDrink.size());
    }

    @Test
    void shouldFindByPriceBetween() {
        repository.save(sampleRecord);
        List<MilkTeaRecord> results = repository.findByPriceBetween(
                new BigDecimal("20.00"), new BigDecimal("30.00"));
        assertEquals(1, results.size());

        List<MilkTeaRecord> empty = repository.findByPriceBetween(
                new BigDecimal("50.00"), new BigDecimal("100.00"));
        assertTrue(empty.isEmpty());
    }

    @Test
    void shouldFindBySugarLevel() {
        repository.save(sampleRecord);
        List<MilkTeaRecord> results = repository.findBySugarLevel(SugarLevel.半糖);
        assertEquals(1, results.size());
    }
}
```

- [ ] **步骤 2：运行测试，确认失败**

运行：`cd backend && mvn test -Dtest=MilkTeaRecordRepositoryTest`
预期：失败 — MilkTeaRecordRepository 未找到

- [ ] **步骤 3：实现仓储接口**

```java
package com.milktea.repository;

import com.milktea.model.MilkTeaRecord;
import com.milktea.model.enums.SugarLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MilkTeaRecordRepository extends JpaRepository<MilkTeaRecord, Long> {
    List<MilkTeaRecord> findByBrand(String brand);

    List<MilkTeaRecord> findByBrandContainingOrDrinkNameContaining(String brandKeyword, String drinkKeyword);

    List<MilkTeaRecord> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<MilkTeaRecord> findBySugarLevel(SugarLevel sugarLevel);

    Page<MilkTeaRecord> findByBrandContainingOrDrinkNameContaining(
            String brandKeyword, String drinkKeyword, Pageable pageable);

    @Query("SELECT m FROM MilkTeaRecord m WHERE " +
            "(:brand IS NULL OR m.brand = :brand) AND " +
            "(:sugarLevel IS NULL OR m.sugarLevel = :sugarLevel) AND " +
            "(:minPrice IS NULL OR m.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR m.price <= :maxPrice) AND " +
            "(:keyword IS NULL OR m.brand LIKE %:keyword% OR m.drinkName LIKE %:keyword%)")
    Page<MilkTeaRecord> findWithFilters(
            @Param("brand") String brand,
            @Param("sugarLevel") SugarLevel sugarLevel,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT SUM(m.price) FROM MilkTeaRecord m")
    BigDecimal sumTotalPrice();

    @Query("SELECT COUNT(m) FROM MilkTeaRecord m")
    Long countTotalRecords();

    @Query("SELECT m.brand, COUNT(m) as cnt FROM MilkTeaRecord m GROUP BY m.brand ORDER BY cnt DESC LIMIT 1")
    List<Object[]> findTopBrand();

    @Query("SELECT CAST(m.consumeDate AS date) as day, SUM(m.price) as total " +
            "FROM MilkTeaRecord m " +
            "WHERE m.consumeDate >= :startDate " +
            "GROUP BY CAST(m.consumeDate AS date) " +
            "ORDER BY CAST(m.consumeDate AS date)")
    List<Object[]> findDailyTrend(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT m.brand, COUNT(m) as cnt, SUM(m.price) as total " +
            "FROM MilkTeaRecord m " +
            "GROUP BY m.brand")
    List<Object[]> findBrandDistribution();

    @Query("SELECT YEARWEEK(m.consumeDate) as week, SUM(m.price) as total, COUNT(m) as cnt " +
            "FROM MilkTeaRecord m " +
            "WHERE m.consumeDate >= :startDate " +
            "GROUP BY YEARWEEK(m.consumeDate) " +
            "ORDER BY YEARWEEK(m.consumeDate)")
    List<Object[]> findWeeklySummary(@Param("startDate") LocalDateTime startDate);
}
```

- [ ] **步骤 4：运行测试，确认通过**

运行：`cd backend && mvn test -Dtest=MilkTeaRecordRepositoryTest`
预期：通过

- [ ] **步骤 5：提交**

```bash
cd backend && git add -A && git commit -m "feat(backend): add MilkTeaRecordRepository with filter and stats queries"
```

---

## 任务 4：数据传输对象（DTO）

**涉及文件：**
- 创建：`backend/src/main/java/com/milktea/dto/ApiResponse.java`
- 创建：`backend/src/main/java/com/milktea/dto/MilkTeaRecordRequest.java`
- 创建：`backend/src/main/java/com/milktea/dto/MilkTeaRecordResponse.java`
- 创建：`backend/src/main/java/com/milktea/dto/StatsOverviewResponse.java`
- 创建：`backend/src/main/java/com/milktea/dto/DailyTrendResponse.java`
- 创建：`backend/src/main/java/com/milktea/dto/BrandDistributionResponse.java`
- 创建：`backend/src/main/java/com/milktea/dto/WeeklySummaryResponse.java`
- 创建：`backend/src/test/java/com/milktea/dto/MilkTeaRecordRequestTest.java`

- [ ] **步骤 1：编写请求校验的失败测试**

```java
package com.milktea.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MilkTeaRecordRequestTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationWithRequiredFields() {
        MilkTeaRecordRequest request = new MilkTeaRecordRequest();
        request.setBrand("喜茶");
        request.setDrinkName("多肉葡萄");
        request.setPrice(new BigDecimal("25.00"));
        request.setConsumeDate(LocalDateTime.of(2024, 6, 1, 14, 30));
        assertEquals(0, validator.validate(request).size());
    }

    @Test
    void shouldFailWhenBrandIsNull() {
        MilkTeaRecordRequest request = new MilkTeaRecordRequest();
        request.setBrand(null);
        request.setDrinkName("多肉葡萄");
        request.setPrice(new BigDecimal("25.00"));
        request.setConsumeDate(LocalDateTime.now());
        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldFailWhenPriceIsNegative() {
        MilkTeaRecordRequest request = new MilkTeaRecordRequest();
        request.setBrand("喜茶");
        request.setDrinkName("多肉葡萄");
        request.setPrice(new BigDecimal("-1.00"));
        request.setConsumeDate(LocalDateTime.now());
        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldFailWhenRatingOutOfRange() {
        MilkTeaRecordRequest request = new MilkTeaRecordRequest();
        request.setBrand("喜茶");
        request.setDrinkName("多肉葡萄");
        request.setPrice(new BigDecimal("25.00"));
        request.setConsumeDate(LocalDateTime.now());
        request.setRating(6);
        assertFalse(validator.validate(request).isEmpty());
    }
}
```

- [ ] **步骤 2：运行测试，确认失败**

运行：`cd backend && mvn test -Dtest=MilkTeaRecordRequestTest`
预期：失败 — 类未找到

- [ ] **步骤 3：实现所有 DTO**

```java
package com.milktea.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
```

```java
package com.milktea.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MilkTeaRecordRequest {
    @NotBlank(message = "品牌不能为空")
    private String brand;

    @NotBlank(message = "饮品名不能为空")
    private String drinkName;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.00", message = "价格不能为负数")
    private BigDecimal price;

    @NotNull(message = "消费时间不能为空")
    private LocalDateTime consumeDate;

    private String sugarLevel;

    private String iceLevel;

    private String cupSize;

    private String teaBase;

    @Min(value = 1, message = "评分最低1星")
    @Max(value = 5, message = "评分最高5星")
    private Integer rating;

    private String comment;

    private Boolean wouldRecommend;
}
```

```java
package com.milktea.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MilkTeaRecordResponse {
    private Long id;
    private String brand;
    private String drinkName;
    private BigDecimal price;
    private LocalDateTime consumeDate;
    private String sugarLevel;
    private String iceLevel;
    private String cupSize;
    private String teaBase;
    private Integer rating;
    private String comment;
    private Boolean wouldRecommend;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

```java
package com.milktea.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsOverviewResponse {
    private BigDecimal totalSpent;
    private Long totalCount;
    private BigDecimal avgPrice;
    private String topBrand;
}
```

```java
package com.milktea.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyTrendResponse {
    private List<String> dates;
    private List<java.math.BigDecimal> amounts;
}
```

```java
package com.milktea.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandDistributionResponse {
    private List<String> brands;
    private List<Long> counts;
    private List<BigDecimal> amounts;
}
```

```java
package com.milktea.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklySummaryResponse {
    private List<String> weeks;
    private List<BigDecimal> amounts;
    private List<Long> counts;
}
```

- [ ] **步骤 4：运行测试，确认通过**

运行：`cd backend && mvn test -Dtest=MilkTeaRecordRequestTest`
预期：通过

- [ ] **步骤 5：提交**

```bash
cd backend && git add -A && git commit -m "feat(backend): add DTOs (ApiResponse, Request/Response, Stats DTOs)"
```

---

## 任务 5：MilkTeaRecordService + 测试

**涉及文件：**
- 创建：`backend/src/main/java/com/milktea/service/MilkTeaRecordService.java`
- 创建：`backend/src/test/java/com/milktea/service/MilkTeaRecordServiceTest.java`

- [ ] **步骤 1：编写服务层的失败测试**

```java
package com.milktea.service;

import com.milktea.dto.MilkTeaRecordRequest;
import com.milktea.dto.MilkTeaRecordResponse;
import com.milktea.model.MilkTeaRecord;
import com.milktea.model.enums.CupSize;
import com.milktea.model.enums.IceLevel;
import com.milktea.model.enums.SugarLevel;
import com.milktea.repository.MilkTeaRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MilkTeaRecordServiceTest {
    @Mock
    private MilkTeaRecordRepository repository;

    @InjectMocks
    private MilkTeaRecordService service;

    private MilkTeaRecord sampleRecord;
    private MilkTeaRecordRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleRecord = new MilkTeaRecord();
        sampleRecord.setId(1L);
        sampleRecord.setBrand("喜茶");
        sampleRecord.setDrinkName("多肉葡萄");
        sampleRecord.setPrice(new BigDecimal("25.00"));
        sampleRecord.setConsumeDate(LocalDateTime.of(2024, 6, 1, 14, 30));
        sampleRecord.setSugarLevel(SugarLevel.半糖);
        sampleRecord.setIceLevel(IceLevel.少冰);
        sampleRecord.setCupSize(CupSize.大杯);
        sampleRecord.setRating(4);
        sampleRecord.setWouldRecommend(true);

        sampleRequest = new MilkTeaRecordRequest();
        sampleRequest.setBrand("喜茶");
        sampleRequest.setDrinkName("多肉葡萄");
        sampleRequest.setPrice(new BigDecimal("25.00"));
        sampleRequest.setConsumeDate(LocalDateTime.of(2024, 6, 1, 14, 30));
        sampleRequest.setSugarLevel("半糖");
        sampleRequest.setIceLevel("少冰");
        sampleRequest.setCupSize("大杯");
        sampleRequest.setRating(4);
        sampleRequest.setWouldRecommend(true);
    }

    @Test
    void shouldCreateRecord() {
        when(repository.save(any(MilkTeaRecord.class))).thenReturn(sampleRecord);
        MilkTeaRecordResponse response = service.createRecord(sampleRequest);
        assertEquals("喜茶", response.getBrand());
        assertEquals("多肉葡萄", response.getDrinkName());
        verify(repository).save(any(MilkTeaRecord.class));
    }

    @Test
    void shouldGetRecordById() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleRecord));
        MilkTeaRecordResponse response = service.getRecordById(1L);
        assertEquals("喜茶", response.getBrand());
    }

    @Test
    void shouldThrowWhenRecordNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getRecordById(999L));
    }

    @Test
    void shouldUpdateRecord() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleRecord));
        when(repository.save(any(MilkTeaRecord.class))).thenReturn(sampleRecord);

        MilkTeaRecordRequest updateRequest = new MilkTeaRecordRequest();
        updateRequest.setBrand("茶百道");
        updateRequest.setDrinkName("豆乳玉麒麟");
        updateRequest.setPrice(new BigDecimal("18.00"));
        updateRequest.setConsumeDate(LocalDateTime.now());

        MilkTeaRecordResponse response = service.updateRecord(1L, updateRequest);
        verify(repository).save(argThat(record ->
                "茶百道".equals(record.getBrand()) &&
                "豆乳玉麒麟".equals(record.getDrinkName())
        ));
    }

    @Test
    void shouldDeleteRecord() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);
        service.deleteRecord(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeleteNonExistentRecord() {
        when(repository.existsById(999L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.deleteRecord(999L));
    }

    @Test
    void shouldListRecordsWithPagination() {
        Page<MilkTeaRecord> page = new PageImpl<>(List.of(sampleRecord));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        Page<MilkTeaRecordResponse> result = service.listRecords(null, null, null, null, null, 0, 10);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldListRecordsWithFilters() {
        Page<MilkTeaRecord> page = new PageImpl<>(List.of(sampleRecord));
        when(repository.findWithFilters(any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);
        Page<MilkTeaRecordResponse> result = service.listRecords("喜茶", SugarLevel.半糖, new BigDecimal("10"), new BigDecimal("30"), "葡萄", 0, 10);
        assertEquals(1, result.getTotalElements());
    }
}
```

- [ ] **步骤 2：运行测试，确认失败**

运行：`cd backend && mvn test -Dtest=MilkTeaRecordServiceTest`
预期：失败 — MilkTeaRecordService 未找到

- [ ] **步骤 3：实现 MilkTeaRecordService**

```java
package com.milktea.service;

import com.milktea.dto.MilkTeaRecordRequest;
import com.milktea.dto.MilkTeaRecordResponse;
import com.milktea.model.MilkTeaRecord;
import com.milktea.model.enums.CupSize;
import com.milktea.model.enums.IceLevel;
import com.milktea.model.enums.SugarLevel;
import com.milktea.repository.MilkTeaRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class MilkTeaRecordService {
    private final MilkTeaRecordRepository repository;

    @Transactional
    public MilkTeaRecordResponse createRecord(MilkTeaRecordRequest request) {
        MilkTeaRecord record = toEntity(request);
        MilkTeaRecord saved = repository.save(record);
        return toResponse(saved);
    }

    public MilkTeaRecordResponse getRecordById(Long id) {
        MilkTeaRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("记录不存在"));
        return toResponse(record);
    }

    @Transactional
    public MilkTeaRecordResponse updateRecord(Long id, MilkTeaRecordRequest request) {
        MilkTeaRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("记录不存在"));
        updateEntity(record, request);
        MilkTeaRecord saved = repository.save(record);
        return toResponse(saved);
    }

    @Transactional
    public void deleteRecord(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("记录不存在");
        }
        repository.deleteById(id);
    }

    public Page<MilkTeaRecordResponse> listRecords(String brand, SugarLevel sugarLevel,
                                                     BigDecimal minPrice, BigDecimal maxPrice,
                                                     String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "consumeDate"));

        boolean hasFilters = brand != null || sugarLevel != null ||
                minPrice != null || maxPrice != null || keyword != null;

        Page<MilkTeaRecord> recordPage;
        if (hasFilters) {
            recordPage = repository.findWithFilters(brand, sugarLevel, minPrice, maxPrice, keyword, pageable);
        } else {
            recordPage = repository.findAll(pageable);
        }

        return recordPage.map(this::toResponse);
    }

    private MilkTeaRecord toEntity(MilkTeaRecordRequest request) {
        MilkTeaRecord record = new MilkTeaRecord();
        updateEntity(record, request);
        return record;
    }

    private void updateEntity(MilkTeaRecord record, MilkTeaRecordRequest request) {
        record.setBrand(request.getBrand());
        record.setDrinkName(request.getDrinkName());
        record.setPrice(request.getPrice());
        record.setConsumeDate(request.getConsumeDate());
        if (request.getSugarLevel() != null) {
            record.setSugarLevel(SugarLevel.valueOf(request.getSugarLevel()));
        }
        if (request.getIceLevel() != null) {
            record.setIceLevel(IceLevel.valueOf(request.getIceLevel()));
        }
        if (request.getCupSize() != null) {
            record.setCupSize(CupSize.valueOf(request.getCupSize()));
        }
        record.setTeaBase(request.getTeaBase());
        record.setRating(request.getRating());
        record.setComment(request.getComment());
        record.setWouldRecommend(request.getWouldRecommend());
    }

    private MilkTeaRecordResponse toResponse(MilkTeaRecord record) {
        MilkTeaRecordResponse response = new MilkTeaRecordResponse();
        response.setId(record.getId());
        response.setBrand(record.getBrand());
        response.setDrinkName(record.getDrinkName());
        response.setPrice(record.getPrice());
        response.setConsumeDate(record.getConsumeDate());
        response.setSugarLevel(record.getSugarLevel() != null ? record.getSugarLevel().name() : null);
        response.setIceLevel(record.getIceLevel() != null ? record.getIceLevel().name() : null);
        response.setCupSize(record.getCupSize() != null ? record.getCupSize().name() : null);
        response.setTeaBase(record.getTeaBase());
        response.setRating(record.getRating());
        response.setComment(record.getComment());
        response.setWouldRecommend(record.getWouldRecommend());
        response.setCreatedAt(record.getCreatedAt());
        response.setUpdatedAt(record.getUpdatedAt());
        return response;
    }
}
```

- [ ] **步骤 4：运行测试，确认通过**

运行：`cd backend && mvn test -Dtest=MilkTeaRecordServiceTest`
预期：通过

- [ ] **步骤 5：提交**

```bash
cd backend && git add -A && git commit -m "feat(backend): add MilkTeaRecordService with CRUD and filter logic"
```

---

## 任务 6：StatsService + 测试

**涉及文件：**
- 创建：`backend/src/main/java/com/milktea/service/StatsService.java`
- 创建：`backend/src/test/java/com/milktea/service/StatsServiceTest.java`

- [ ] **步骤 1：编写统计服务的失败测试**

```java
package com.milktea.service;

import com.milktea.dto.BrandDistributionResponse;
import com.milktea.dto.DailyTrendResponse;
import com.milktea.dto.StatsOverviewResponse;
import com.milktea.dto.WeeklySummaryResponse;
import com.milktea.repository.MilkTeaRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {
    @Mock
    private MilkTeaRecordRepository repository;

    @InjectMocks
    private StatsService service;

    @Test
    void shouldReturnOverviewWithData() {
        when(repository.sumTotalPrice()).thenReturn(new BigDecimal("1280.00"));
        when(repository.countTotalRecords()).thenReturn(42L);
        when(repository.findTopBrand()).thenReturn(List.of(new Object[]{"喜茶", 15L}));

        StatsOverviewResponse overview = service.getOverview();

        assertEquals(new BigDecimal("1280.00"), overview.getTotalSpent());
        assertEquals(42L, overview.getTotalCount());
        assertEquals(new BigDecimal("30.48"), overview.getAvgPrice());
        assertEquals("喜茶", overview.getTopBrand());
    }

    @Test
    void shouldReturnOverviewWithZeroWhenNoData() {
        when(repository.sumTotalPrice()).thenReturn(null);
        when(repository.countTotalRecords()).thenReturn(0L);
        when(repository.findTopBrand()).thenReturn(Collections.emptyList());

        StatsOverviewResponse overview = service.getOverview();

        assertEquals(BigDecimal.ZERO, overview.getTotalSpent());
        assertEquals(0L, overview.getTotalCount());
        assertEquals(BigDecimal.ZERO, overview.getAvgPrice());
        assertNull(overview.getTopBrand());
    }

    @Test
    void shouldReturnDailyTrend() {
        List<Object[]> mockData = List.of(
                new Object[]{java.sql.Date.valueOf("2024-06-01"), new BigDecimal("50.00")},
                new Object[]{java.sql.Date.valueOf("2024-06-02"), new BigDecimal("75.00")}
        );
        when(repository.findDailyTrend(any(LocalDateTime.class))).thenReturn(mockData);

        DailyTrendResponse trend = service.getDailyTrend(30);

        assertEquals(2, trend.getDates().size());
        assertEquals(2, trend.getAmounts().size());
        assertEquals(new BigDecimal("50.00"), trend.getAmounts().get(0));
    }

    @Test
    void shouldReturnBrandDistribution() {
        List<Object[]> mockData = List.of(
                new Object[]{"喜茶", 15L, new BigDecimal("375.00")},
                new Object[]{"茶百道", 10L, new BigDecimal("180.00")}
        );
        when(repository.findBrandDistribution()).thenReturn(mockData);

        BrandDistributionResponse dist = service.getBrandDistribution();

        assertEquals(2, dist.getBrands().size());
        assertEquals("喜茶", dist.getBrands().get(0));
        assertEquals(15L, dist.getCounts().get(0));
    }

    @Test
    void shouldReturnWeeklySummary() {
        List<Object[]> mockData = List.of(
                new Object[]{202422, new BigDecimal("250.00"), 8L}
        );
        when(repository.findWeeklySummary(any(LocalDateTime.class))).thenReturn(mockData);

        WeeklySummaryResponse summary = service.getWeeklySummary(4);

        assertEquals(1, summary.getWeeks().size());
        assertEquals(new BigDecimal("250.00"), summary.getAmounts().get(0));
        assertEquals(8L, summary.getCounts().get(0));
    }
}
```

- [ ] **步骤 2：运行测试，确认失败**

运行：`cd backend && mvn test -Dtest=StatsServiceTest`
预期：失败 — StatsService 未找到

- [ ] **步骤 3：实现 StatsService**

```java
package com.milktea.service;

import com.milktea.dto.BrandDistributionResponse;
import com.milktea.dto.DailyTrendResponse;
import com.milktea.dto.StatsOverviewResponse;
import com.milktea.dto.WeeklySummaryResponse;
import com.milktea.repository.MilkTeaRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final MilkTeaRecordRepository repository;

    public StatsOverviewResponse getOverview() {
        BigDecimal totalSpent = repository.sumTotalPrice();
        Long totalCount = repository.countTotalRecords();
        List<Object[]> topBrandResult = repository.findTopBrand();

        if (totalSpent == null) {
            totalSpent = BigDecimal.ZERO;
        }
        if (totalCount == null) {
            totalCount = 0L;
        }

        BigDecimal avgPrice = totalCount > 0
                ? totalSpent.divide(new BigDecimal(totalCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        String topBrand = null;
        if (!topBrandResult.isEmpty()) {
            topBrand = (String) topBrandResult.get(0)[0];
        }

        return new StatsOverviewResponse(totalSpent, totalCount, avgPrice, topBrand);
    }

    public DailyTrendResponse getDailyTrend(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Object[]> results = repository.findDailyTrend(startDate);

        List<String> dates = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();

        for (Object[] row : results) {
            dates.add(row[0].toString());
            amounts.add((BigDecimal) row[1]);
        }

        return new DailyTrendResponse(dates, amounts);
    }

    public BrandDistributionResponse getBrandDistribution() {
        List<Object[]> results = repository.findBrandDistribution();

        List<String> brands = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();

        for (Object[] row : results) {
            brands.add((String) row[0]);
            counts.add((Long) row[1]);
            amounts.add((BigDecimal) row[2]);
        }

        return new BrandDistributionResponse(brands, counts, amounts);
    }

    public WeeklySummaryResponse getWeeklySummary(int weeks) {
        LocalDateTime startDate = LocalDateTime.now().minusWeeks(weeks);
        List<Object[]> results = repository.findWeeklySummary(startDate);

        List<String> weeksList = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        for (Object[] row : results) {
            weeksList.add(String.valueOf(row[0]));
            amounts.add((BigDecimal) row[1]);
            counts.add((Long) row[2]);
        }

        return new WeeklySummaryResponse(weeksList, amounts, counts);
    }
}
```

- [ ] **步骤 4：运行测试，确认通过**

运行：`cd backend && mvn test -Dtest=StatsServiceTest`
预期：通过

- [ ] **步骤 5：提交**

```bash
cd backend && git add -A && git commit -m "feat(backend): add StatsService with overview, daily-trend, brand-distribution, weekly-summary"
```

---

## 任务 7：MilkTeaRecordController + 测试

**涉及文件：**
- 创建：`backend/src/main/java/com/milktea/controller/MilkTeaRecordController.java`
- 创建：`backend/src/test/java/com/milktea/controller/MilkTeaRecordControllerTest.java`

- [ ] **步骤 1：编写控制器的失败测试**

```java
package com.milktea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milktea.dto.MilkTeaRecordRequest;
import com.milktea.dto.MilkTeaRecordResponse;
import com.milktea.service.MilkTeaRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MilkTeaRecordController.class)
class MilkTeaRecordControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MilkTeaRecordService service;

    private MilkTeaRecordResponse sampleResponse() {
        MilkTeaRecordResponse response = new MilkTeaRecordResponse();
        response.setId(1L);
        response.setBrand("喜茶");
        response.setDrinkName("多肉葡萄");
        response.setPrice(new BigDecimal("25.00"));
        response.setConsumeDate(LocalDateTime.of(2024, 6, 1, 14, 30));
        response.setSugarLevel("半糖");
        response.setIceLevel("少冰");
        response.setCupSize("大杯");
        response.setRating(4);
        response.setWouldRecommend(true);
        return response;
    }

    @Test
    void shouldCreateRecord() throws Exception {
        MilkTeaRecordRequest request = new MilkTeaRecordRequest();
        request.setBrand("喜茶");
        request.setDrinkName("多肉葡萄");
        request.setPrice(new BigDecimal("25.00"));
        request.setConsumeDate(LocalDateTime.of(2024, 6, 1, 14, 30));

        when(service.createRecord(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/milk-tea-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.brand").value("喜茶"));
    }

    @Test
    void shouldGetRecordById() throws Exception {
        when(service.getRecordById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/milk-tea-records/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.drinkName").value("多肉葡萄"));
    }

    @Test
    void shouldReturn404WhenNotFound() throws Exception {
        when(service.getRecordById(999L)).thenThrow(new RuntimeException("记录不存在"));

        mockMvc.perform(get("/api/milk-tea-records/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void shouldListRecords() throws Exception {
        Page<MilkTeaRecordResponse> page = new PageImpl<>(List.of(sampleResponse()));
        when(service.listRecords(isNull(), isNull(), isNull(), isNull(), isNull(), eq(0), eq(10)))
                .thenReturn(page);

        mockMvc.perform(get("/api/milk-tea-records?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].brand").value("喜茶"));
    }

    @Test
    void shouldUpdateRecord() throws Exception {
        MilkTeaRecordRequest request = new MilkTeaRecordRequest();
        request.setBrand("茶百道");
        request.setDrinkName("豆乳玉麒麟");
        request.setPrice(new BigDecimal("18.00"));
        request.setConsumeDate(LocalDateTime.now());

        when(service.updateRecord(eq(1L), any())).thenReturn(sampleResponse());

        mockMvc.perform(put("/api/milk-tea-records/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldDeleteRecord() throws Exception {
        doNothing().when(service).deleteRecord(1L);

        mockMvc.perform(delete("/api/milk-tea-records/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
```

- [ ] **步骤 2：运行测试，确认失败**

运行：`cd backend && mvn test -Dtest=MilkTeaRecordControllerTest`
预期：失败 — MilkTeaRecordController 未找到

- [ ] **步骤 3：实现 MilkTeaRecordController**

```java
package com.milktea.controller;

import com.milktea.dto.ApiResponse;
import com.milktea.dto.MilkTeaRecordRequest;
import com.milktea.dto.MilkTeaRecordResponse;
import com.milktea.model.enums.SugarLevel;
import com.milktea.service.MilkTeaRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/milk-tea-records")
@RequiredArgsConstructor
public class MilkTeaRecordController {
    private final MilkTeaRecordService service;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MilkTeaRecordResponse>>> listRecords(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String sugarLevel,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        SugarLevel sugar = sugarLevel != null ? SugarLevel.valueOf(sugarLevel) : null;
        Page<MilkTeaRecordResponse> result = service.listRecords(brand, sugar, minPrice, maxPrice, keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MilkTeaRecordResponse>> getRecord(@PathVariable Long id) {
        try {
            MilkTeaRecordResponse response = service.getRecordById(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(404, e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MilkTeaRecordResponse>> createRecord(
            @Valid @RequestBody MilkTeaRecordRequest request) {
        MilkTeaRecordResponse response = service.createRecord(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MilkTeaRecordResponse>> updateRecord(
            @PathVariable Long id, @Valid @RequestBody MilkTeaRecordRequest request) {
        try {
            MilkTeaRecordResponse response = service.updateRecord(id, request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(404, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable Long id) {
        try {
            service.deleteRecord(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(404, e.getMessage()));
        }
    }
}
```

- [ ] **步骤 4：运行测试，确认通过**

运行：`cd backend && mvn test -Dtest=MilkTeaRecordControllerTest`
预期：通过

- [ ] **步骤 5：提交**

```bash
cd backend && git add -A && git commit -m "feat(backend): add MilkTeaRecordController with CRUD endpoints"
```

---

## 任务 8：StatsController + 测试

**涉及文件：**
- 创建：`backend/src/main/java/com/milktea/controller/StatsController.java`
- 创建：`backend/src/test/java/com/milktea/controller/StatsControllerTest.java`

- [ ] **步骤 1：编写统计控制器的失败测试**

```java
package com.milktea.controller;

import com.milktea.dto.BrandDistributionResponse;
import com.milktea.dto.DailyTrendResponse;
import com.milktea.dto.StatsOverviewResponse;
import com.milktea.dto.WeeklySummaryResponse;
import com.milktea.service.StatsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatsController.class)
class StatsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatsService service;

    @Test
    void shouldGetOverview() throws Exception {
        when(service.getOverview()).thenReturn(
                new StatsOverviewResponse(new BigDecimal("1280.00"), 42L, new BigDecimal("30.48"), "喜茶"));

        mockMvc.perform(get("/api/stats/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalSpent").value(1280.00))
                .andExpect(jsonPath("$.data.totalCount").value(42))
                .andExpect(jsonPath("$.data.topBrand").value("喜茶"));
    }

    @Test
    void shouldGetDailyTrend() throws Exception {
        when(service.getDailyTrend(30)).thenReturn(
                new DailyTrendResponse(List.of("2024-06-01"), List.of(new BigDecimal("50.00"))));

        mockMvc.perform(get("/api/stats/daily-trend?days=30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dates[0]").value("2024-06-01"));
    }

    @Test
    void shouldGetBrandDistribution() throws Exception {
        when(service.getBrandDistribution()).thenReturn(
                new BrandDistributionResponse(List.of("喜茶"), List.of(15L), List.of(new BigDecimal("375.00"))));

        mockMvc.perform(get("/api/stats/brand-distribution"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.brands[0]").value("喜茶"));
    }

    @Test
    void shouldGetWeeklySummary() throws Exception {
        when(service.getWeeklySummary(4)).thenReturn(
                new WeeklySummaryResponse(List.of("202422"), List.of(new BigDecimal("250.00")), List.of(8L)));

        mockMvc.perform(get("/api/stats/weekly-summary?weeks=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.weeks[0]").value("202422"));
    }
}
```

- [ ] **步骤 2：运行测试，确认失败**

运行：`cd backend && mvn test -Dtest=StatsControllerTest`
预期：失败 — StatsController 未找到

- [ ] **步骤 3：实现 StatsController**

```java
package com.milktea.controller;

import com.milktea.dto.ApiResponse;
import com.milktea.dto.BrandDistributionResponse;
import com.milktea.dto.DailyTrendResponse;
import com.milktea.dto.StatsOverviewResponse;
import com.milktea.dto.WeeklySummaryResponse;
import com.milktea.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService service;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<StatsOverviewResponse>> getOverview() {
        return ResponseEntity.ok(ApiResponse.success(service.getOverview()));
    }

    @GetMapping("/daily-trend")
    public ResponseEntity<ApiResponse<DailyTrendResponse>> getDailyTrend(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.success(service.getDailyTrend(days)));
    }

    @GetMapping("/brand-distribution")
    public ResponseEntity<ApiResponse<BrandDistributionResponse>> getBrandDistribution() {
        return ResponseEntity.ok(ApiResponse.success(service.getBrandDistribution()));
    }

    @GetMapping("/weekly-summary")
    public ResponseEntity<ApiResponse<WeeklySummaryResponse>> getWeeklySummary(
            @RequestParam(defaultValue = "4") int weeks) {
        return ResponseEntity.ok(ApiResponse.success(service.getWeeklySummary(weeks)));
    }
}
```

- [ ] **步骤 4：运行测试，确认通过**

运行：`cd backend && mvn test -Dtest=StatsControllerTest`
预期：通过

- [ ] **步骤 5：提交**

```bash
cd backend && git add -A && git commit -m "feat(backend): add StatsController with overview, daily-trend, brand-distribution, weekly-summary"
```

---

## 任务 9：CORS 配置 + 全局异常处理 + 样例数据

**涉及文件：**
- 创建：`backend/src/main/java/com/milktea/config/CorsConfig.java`
- 创建：`backend/src/main/java/com/milktea/config/WebConfig.java`
- 创建：`backend/src/main/java/com/milktea/exception/GlobalExceptionHandler.java`
- 修改：`backend/src/main/resources/application.yml`
- 创建：`backend/src/main/resources/data.sql`

- [ ] **步骤 1：实现 CorsConfig**

```java
package com.milktea.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

- [ ] **步骤 2：实现 WebConfig**

```java
package com.milktea.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
}
```

- [ ] **步骤 3：实现 GlobalExceptionHandler**

```java
package com.milktea.exception;

import com.milktea.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.ok(ApiResponse.error(500, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("参数校验失败");
        return ResponseEntity.ok(ApiResponse.error(400, message));
    }
}
```

- [ ] **步骤 4：更新 application.yml — 添加 SQL 初始化配置**

在 `application.yml` 末尾添加：

```yaml
spring:
  sql:
    init:
      mode: always
      data-locations: classpath:data.sql
```

- [ ] **步骤 5：创建 data.sql 样例数据**

```sql
INSERT INTO milk_tea_records (brand, drink_name, price, consume_date, sugar_level, ice_level, cup_size, tea_base, rating, comment, would_recommend, created_at, updated_at) VALUES
('喜茶', '多肉葡萄', 25.00, '2024-06-01 14:30:00', '半糖', '少冰', '大杯', '绿茶', 4, '果味浓郁，非常清爽', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('茶百道', '豆乳玉麒麟', 18.00, '2024-06-02 10:15:00', '微糖', '去冰', '中杯', '乌龙茶', 3, '豆乳味不错但偏甜', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('蜜雪冰城', '冰鲜柠檬水', 4.00, '2024-06-03 16:00:00', '全糖', '多冰', '大杯', NULL, 5, '性价比之王', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('喜茶', '烤黑糖波波牛乳', 22.00, '2024-06-04 09:30:00', '少糖', '温', '中杯', '红茶', 4, '冬天喝超暖', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('茶颜悦色', '幽兰拿铁', 16.00, '2024-06-05 15:00:00', '半糖', '少冰', '大杯', '红茶', 5, '经典必点', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CoCo都可', '奶茶三兄弟', 12.00, '2024-06-06 11:00:00', '半糖', '正常冰', '大杯', '红茶', 3, '中规中矩', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('喜茶', '芝芝莓莓', 28.00, '2024-06-07 14:00:00', '微糖', '少冰', '大杯', '绿茶', 4, '草莓很新鲜', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('茶百道', '杨枝甘露', 16.00, '2024-06-08 17:30:00', '半糖', '少冰', '中杯', NULL, 4, '芒果味浓郁', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('蜜雪冰城', '摇摇奶昔', 6.00, '2024-06-09 20:00:00', '全糖', '多冰', '中杯', NULL, 3, '便宜好喝', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('茶颜悦色', '声声乌龙', 15.00, '2024-06-10 13:00:00', '无糖', '去冰', '大杯', '乌龙茶', 5, '清香不腻', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

- [ ] **步骤 6：运行所有后端测试**

运行：`cd backend && mvn test`
预期：全部通过

- [ ] **步骤 7：验证应用启动**

运行：`cd backend && mvn spring-boot:run`（看到 "Started MilkTeaApplication" 后 Ctrl+C）
预期：应用在 8080 端口正常启动，无报错

- [ ] **步骤 8：提交**

```bash
cd backend && git add -A && git commit -m "feat(backend): add CORS config, global exception handler, and sample data.sql"
```

---

## 任务 10：前端项目脚手架

**涉及文件：**
- 创建：`frontend/package.json`
- 创建：`frontend/vite.config.js`
- 创建：`frontend/index.html`
- 创建：`frontend/src/main.js`
- 创建：`frontend/src/App.vue`

*本任务使用 `npm create vite@latest` 初始化，然后自定义。*

- [ ] **步骤 1：初始化 Vue3 + Vite 项目**

```bash
cd e:/Grade3/2-AI4SE/AI4SE_FinalProject
npm create vite@latest frontend -- --template vue
```

- [ ] **步骤 2：安装依赖**

```bash
cd frontend
npm install
npm install naive-ui @vicons/ionicons5 pinia vue-router@4 axios echarts vue-echarts
npm install -D @vitejs/plugin-vue
```

- [ ] **步骤 3：更新 vite.config.js，配置开发代理**

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **步骤 4：编写 App.vue 骨架**

```vue
<template>
  <n-config-provider :theme-overrides="themeOverrides">
    <n-message-provider>
      <n-layout style="min-height: 100vh">
        <n-layout-header bordered style="padding: 12px 24px; display: flex; align-items: center; justify-content: space-between;">
          <div style="display: flex; align-items: center; gap: 12px;">
            <span style="font-size: 24px;">🧋</span>
            <span style="font-size: 20px; font-weight: bold; color: #5D4037;">奶茶记录</span>
            <n-menu mode="horizontal" :value="activeKey" :options="menuOptions" @update:value="handleMenuUpdate" />
          </div>
          <n-button type="primary" @click="router.push('/records/new')">
            + 记一杯奶茶
          </n-button>
        </n-layout-header>
        <n-layout-content style="padding: 24px;">
          <router-view />
        </n-layout-content>
      </n-layout>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NConfigProvider, NMessageProvider, NLayout, NLayoutHeader, NLayoutContent, NMenu, NButton } from 'naive-ui'

const router = useRouter()
const route = useRoute()

const activeKey = computed(() => {
  if (route.path.startsWith('/statistics')) return 'statistics'
  return 'records'
})

const menuOptions = [
  { label: '记录列表', key: 'records' },
  { label: '消费统计', key: 'statistics' }
]

const themeOverrides = {
  common: {
    primaryColor: '#FF8F00',
    primaryColorHover: '#FFA726',
    primaryColorPressed: '#E65100',
    primaryColorSuppl: '#FF6D00'
  }
}

function handleMenuUpdate(key) {
  if (key === 'records') router.push('/records')
  else if (key === 'statistics') router.push('/statistics')
}
</script>

<style>
body {
  margin: 0;
  background-color: #FFF8E1;
}
</style>
```

- [ ] **步骤 5：更新 main.js**

```javascript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')
```

- [ ] **步骤 6：验证开发服务器启动**

运行：`cd frontend && npm run dev`
预期：Vite 开发服务器在 http://localhost:5173 启动（然后 Ctrl+C）

- [ ] **步骤 7：提交**

```bash
cd frontend && git add -A && git commit -m "feat(frontend): scaffold Vue3+Vite project with NaiveUI, Pinia, Router, Axios"
```

---

## 任务 11：主题常量

**涉及文件：**
- 创建：`frontend/src/assets/styles/theme.js`

- [ ] **步骤 1：创建主题常量文件**

```javascript
// 奶茶色主题常量
export const theme = {
  colors: {
    primary: '#FF8F00',
    primaryLight: '#FFA726',
    primaryDark: '#E65100',
    background: '#FFF8E1',
    cardBg: '#FFFFFF',
    textPrimary: '#3E2723',
    textSecondary: '#795548',
    textMuted: '#A1887F',
    border: '#FFE0B2',
    success: '#2E7D32',
    danger: '#C62828'
  },
  sugarLevelOptions: [
    { label: '无糖', value: '无糖' },
    { label: '微糖', value: '微糖' },
    { label: '半糖', value: '半糖' },
    { label: '少糖', value: '少糖' },
    { label: '全糖', value: '全糖' }
  ],
  iceLevelOptions: [
    { label: '去冰', value: '去冰' },
    { label: '少冰', value: '少冰' },
    { label: '正常冰', value: '正常冰' },
    { label: '多冰', value: '多冰' },
    { label: '温', value: '温' },
    { label: '热', value: '热' }
  ],
  cupSizeOptions: [
    { label: '小杯', value: '小杯' },
    { label: '中杯', value: '中杯' },
    { label: '大杯', value: '大杯' },
    { label: '超大杯', value: '超大杯' }
  ]
}
```

- [ ] **步骤 2：提交**

```bash
cd frontend && git add -A && git commit -m "feat(frontend): add theme constants (milk tea color palette, enum options)"
```

---

## 任务 12：API 模块

**涉及文件：**
- 创建：`frontend/src/api/milkTea.js`

- [ ] **步骤 1：创建基于 axios 的 API 模块**

```javascript
import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

// 响应拦截器：解包 ApiResponse
api.interceptors.response.use(
  (response) => {
    const data = response.data
    if (data.code !== 200) {
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return data.data
  },
  (error) => {
    return Promise.reject(error)
  }
)

// ---- 奶茶记录 CRUD ----
export function getRecords(params) {
  return api.get('/milk-tea-records', { params })
}

export function getRecordById(id) {
  return api.get(`/milk-tea-records/${id}`)
}

export function createRecord(data) {
  return api.post('/milk-tea-records', data)
}

export function updateRecord(id, data) {
  return api.put(`/milk-tea-records/${id}`, data)
}

export function deleteRecord(id) {
  return api.delete(`/milk-tea-records/${id}`)
}

// ---- 统计接口 ----
export function getStatsOverview() {
  return api.get('/stats/overview')
}

export function getDailyTrend(days = 30) {
  return api.get('/stats/daily-trend', { params: { days } })
}

export function getBrandDistribution() {
  return api.get('/stats/brand-distribution')
}

export function getWeeklySummary(weeks = 4) {
  return api.get('/stats/weekly-summary', { params: { weeks } })
}
```

- [ ] **步骤 2：提交**

```bash
cd frontend && git add -A && git commit -m "feat(frontend): add API module with axios interceptors for all endpoints"
```

---

## 任务 13：Pinia 状态管理 + 路由

**涉及文件：**
- 创建：`frontend/src/stores/milkTea.js`
- 创建：`frontend/src/router/index.js`

- [ ] **步骤 1：创建 Pinia store**

```javascript
import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getRecords, getRecordById, createRecord, updateRecord, deleteRecord,
  getStatsOverview, getDailyTrend, getBrandDistribution, getWeeklySummary
} from '../api/milkTea'

export const useMilkTeaStore = defineStore('milkTea', () => {
  // 记录列表状态
  const records = ref([])
  const totalElements = ref(0)
  const totalPages = ref(0)
  const currentPage = ref(0)
  const pageSize = ref(10)
  const loading = ref(false)

  // 当前记录（详情/编辑）
  const currentRecord = ref(null)

  // 统计状态
  const overview = ref({ totalSpent: 0, totalCount: 0, avgPrice: 0, topBrand: null })
  const dailyTrend = ref({ dates: [], amounts: [] })
  const brandDistribution = ref({ brands: [], counts: [], amounts: [] })
  const weeklySummary = ref({ weeks: [], amounts: [], counts: [] })

  // 获取记录列表（支持筛选）
  async function fetchRecords({ brand, sugarLevel, minPrice, maxPrice, keyword, page = 0, size = 10 } = {}) {
    loading.value = true
    try {
      const result = await getRecords({ brand, sugarLevel, minPrice, maxPrice, keyword, page, size })
      records.value = result.content
      totalElements.value = result.totalElements
      totalPages.value = result.totalPages
      currentPage.value = result.number
      pageSize.value = result.size
    } finally {
      loading.value = false
    }
  }

  async function fetchRecordById(id) {
    currentRecord.value = await getRecordById(id)
  }

  async function addRecord(data) {
    await createRecord(data)
  }

  async function editRecord(id, data) {
    await updateRecord(id, data)
  }

  async function removeRecord(id) {
    await deleteRecord(id)
  }

  // 统计操作
  async function fetchOverview() {
    overview.value = await getStatsOverview()
  }

  async function fetchDailyTrend(days = 30) {
    dailyTrend.value = await getDailyTrend(days)
  }

  async function fetchBrandDistribution() {
    brandDistribution.value = await getBrandDistribution()
  }

  async function fetchWeeklySummary(weeks = 4) {
    weeklySummary.value = await getWeeklySummary(weeks)
  }

  async function fetchAllStats() {
    await Promise.all([
      fetchOverview(),
      fetchDailyTrend(),
      fetchBrandDistribution(),
      fetchWeeklySummary()
    ])
  }

  return {
    records, totalElements, totalPages, currentPage, pageSize, loading, currentRecord,
    overview, dailyTrend, brandDistribution, weeklySummary,
    fetchRecords, fetchRecordById, addRecord, editRecord, removeRecord,
    fetchOverview, fetchDailyTrend, fetchBrandDistribution, fetchWeeklySummary, fetchAllStats
  }
})
```

- [ ] **步骤 2：创建路由**

```javascript
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/records'
  },
  {
    path: '/records',
    name: 'RecordList',
    component: () => import('../views/RecordListView.vue')
  },
  {
    path: '/records/new',
    name: 'NewRecord',
    component: () => import('../views/RecordFormView.vue')
  },
  {
    path: '/records/:id/edit',
    name: 'EditRecord',
    component: () => import('../views/RecordFormView.vue')
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: () => import('../views/StatisticsView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
```

- [ ] **步骤 3：提交**

```bash
cd frontend && git add -A && git commit -m "feat(frontend): add Pinia store and Vue Router with lazy-loaded routes"
```

---

## 任务 14：记录列表页（FilterBar + RecordCard）

**涉及文件：**
- 创建：`frontend/src/components/FilterBar.vue`
- 创建：`frontend/src/components/RecordCard.vue`
- 创建：`frontend/src/views/RecordListView.vue`

- [ ] **步骤 1：创建 FilterBar 组件**

```vue
<template>
  <n-card title="筛选" size="small" style="margin-bottom: 16px;">
    <n-space vertical>
      <n-select v-model:value="filters.brand" :options="brandOptions" placeholder="品牌" clearable />
      <n-select v-model:value="filters.sugarLevel" :options="sugarLevelOptions" placeholder="糖度" clearable />
      <n-input-group>
        <n-input-number v-model:value="filters.minPrice" placeholder="最低价" :min="0" style="width: 50%;" />
        <n-input-number v-model:value="filters.maxPrice" placeholder="最高价" :min="0" style="width: 50%;" />
      </n-input-group>
      <n-input v-model:value="filters.keyword" placeholder="搜索品牌或饮品名" clearable />
      <n-button type="primary" block @click="$emit('filter', { ...filters })">筛选</n-button>
      <n-button block @click="resetFilters">重置</n-button>
    </n-space>
  </n-card>
</template>

<script setup>
import { reactive } from 'vue'
import { NCard, NSpace, NSelect, NInputGroup, NInputNumber, NInput, NButton } from 'naive-ui'
import { theme } from '../assets/styles/theme'

const emit = defineEmits(['filter'])

const filters = reactive({
  brand: null,
  sugarLevel: null,
  minPrice: null,
  maxPrice: null,
  keyword: null
})

const sugarLevelOptions = theme.sugarLevelOptions

const brandOptions = [
  { label: '喜茶', value: '喜茶' },
  { label: '茶百道', value: '茶百道' },
  { label: '蜜雪冰城', value: '蜜雪冰城' },
  { label: '茶颜悦色', value: '茶颜悦色' },
  { label: 'CoCo都可', value: 'CoCo都可' },
  { label: '1点点', value: '1点点' },
  { label: '古茗', value: '古茗' },
  { label: '书亦烧仙草', value: '书亦烧仙草' }
]

function resetFilters() {
  filters.brand = null
  filters.sugarLevel = null
  filters.minPrice = null
  filters.maxPrice = null
  filters.keyword = null
  emit('filter', { ...filters })
}
</script>
```

- [ ] **步骤 2：创建 RecordCard 组件**

```vue
<template>
  <n-card hoverable style="margin-bottom: 12px;">
    <template #header>
      <span style="font-weight: bold;">{{ record.brand }} · {{ record.drinkName }}</span>
    </template>
    <template #header-extra>
      <n-rate :value="record.rating || 0" readonly size="small" />
    </template>
    <n-space justify="space-between" align="center">
      <n-space>
        <n-tag v-if="record.sugarLevel" size="small" type="info">{{ record.sugarLevel }}</n-tag>
        <n-tag v-if="record.iceLevel" size="small" type="info">{{ record.iceLevel }}</n-tag>
        <n-tag v-if="record.cupSize" size="small">{{ record.cupSize }}</n-tag>
        <n-tag v-if="record.teaBase" size="small" type="warning">{{ record.teaBase }}</n-tag>
      </n-space>
      <span style="font-size: 18px; font-weight: bold; color: #FF8F00;">¥{{ record.price }}</span>
    </n-space>
    <div v-if="record.comment" style="margin-top: 8px; color: #795548; font-size: 13px;">{{ record.comment }}</div>
    <template #footer>
      <n-space justify="space-between" align="center">
        <span style="font-size: 12px; color: #A1887F;">{{ formatDate(record.consumeDate) }}</span>
        <n-space>
          <n-button text type="primary" @click="$emit('edit', record)">编辑</n-button>
          <n-button text type="error" @click="$emit('delete', record.id)">删除</n-button>
        </n-space>
      </n-space>
    </template>
  </n-card>
</template>

<script setup>
import { NCard, NRate, NTag, NSpace, NButton } from 'naive-ui'

defineProps({
  record: { type: Object, required: true }
})

defineEmits(['edit', 'delete'])

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}
</script>
```

- [ ] **步骤 3：创建 RecordListView**

```vue
<template>
  <div style="display: flex; gap: 20px;">
    <div style="width: 240px; flex-shrink: 0;">
      <FilterBar @filter="handleFilter" />
    </div>
    <div style="flex: 1;">
      <div style="margin-bottom: 12px; color: #795548;">
        共 {{ store.totalElements }} 款饮品记录
      </div>
      <n-spin :show="store.loading">
        <RecordCard
          v-for="record in store.records"
          :key="record.id"
          :record="record"
          @edit="handleEdit"
          @delete="handleDelete"
        />
        <n-empty v-if="!store.loading && store.records.length === 0" description="暂无记录，快去记一杯奶茶吧！" />
      </n-spin>
      <n-pagination
        v-if="store.totalPages > 1"
        :page="store.currentPage + 1"
        :page-count="store.totalPages"
        @update:page="handlePageChange"
        style="margin-top: 16px; justify-content: center;"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage, useDialog, NSpin, NEmpty, NPagination } from 'naive-ui'
import { useMilkTeaStore } from '../stores/milkTea'
import FilterBar from '../components/FilterBar.vue'
import RecordCard from '../components/RecordCard.vue'

const router = useRouter()
const store = useMilkTeaStore()
const message = useMessage()
const dialog = useDialog()

let currentFilters = {}

onMounted(() => {
  store.fetchRecords()
})

function handleFilter(filters) {
  currentFilters = filters
  store.fetchRecords({ ...filters, page: 0 })
}

function handlePageChange(page) {
  store.fetchRecords({ ...currentFilters, page: page - 1 })
}

function handleEdit(record) {
  router.push(`/records/${record.id}/edit`)
}

function handleDelete(id) {
  dialog.warning({
    title: '确认删除',
    content: '确定要删除这条奶茶记录吗？',
    positiveText: '删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      await store.removeRecord(id)
      message.success('删除成功')
      store.fetchRecords({ ...currentFilters, page: store.currentPage })
    }
  })
}
</script>
```

- [ ] **步骤 4：验证页面渲染**

运行：`cd frontend && npm run dev`
预期：记录列表页在 http://localhost:5173/records 渲染（空状态）

- [ ] **步骤 5：提交**

```bash
cd frontend && git add -A && git commit -m "feat(frontend): add RecordList page with FilterBar and RecordCard components"
```

---

## 任务 15：记录表单页

**涉及文件：**
- 创建：`frontend/src/components/RecordForm.vue`
- 创建：`frontend/src/views/RecordFormView.vue`

- [ ] **步骤 1：创建 RecordForm 组件**

```vue
<template>
  <n-form ref="formRef" :model="form" :rules="rules" label-placement="top">
    <n-grid :cols="3" :x-gap="24">
      <!-- 第一栏：基本信息 -->
      <n-gi>
        <n-h4 style="color: #795548;">基本信息</n-h4>
        <n-form-item label="品牌" path="brand">
          <n-input v-model:value="form.brand" placeholder="如：喜茶" />
        </n-form-item>
        <n-form-item label="饮品名" path="drinkName">
          <n-input v-model:value="form.drinkName" placeholder="如：多肉葡萄" />
        </n-form-item>
        <n-form-item label="价格" path="price">
          <n-input-number v-model:value="form.price" :min="0" :precision="2" style="width: 100%;">
            <template #prefix>¥</template>
          </n-input-number>
        </n-form-item>
        <n-form-item label="消费时间" path="consumeDate">
          <n-date-picker v-model:formatted-value="form.consumeDate" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" style="width: 100%;" />
        </n-form-item>
      </n-gi>

      <!-- 第二栏：饮品详情 -->
      <n-gi>
        <n-h4 style="color: #795548;">饮品详情</n-h4>
        <n-form-item label="糖度" path="sugarLevel">
          <n-select v-model:value="form.sugarLevel" :options="sugarLevelOptions" placeholder="选择糖度" clearable />
        </n-form-item>
        <n-form-item label="冰度" path="iceLevel">
          <n-select v-model:value="form.iceLevel" :options="iceLevelOptions" placeholder="选择冰度" clearable />
        </n-form-item>
        <n-form-item label="杯型" path="cupSize">
          <n-select v-model:value="form.cupSize" :options="cupSizeOptions" placeholder="选择杯型" clearable />
        </n-form-item>
        <n-form-item label="茶底" path="teaBase">
          <n-input v-model:value="form.teaBase" placeholder="如：绿茶、红茶、乌龙茶" />
        </n-form-item>
      </n-gi>

      <!-- 第三栏：主观评价 -->
      <n-gi>
        <n-h4 style="color: #795548;">主观评价</n-h4>
        <n-form-item label="评分" path="rating">
          <n-rate v-model:value="form.rating" />
        </n-form-item>
        <n-form-item label="点评" path="comment">
          <n-input v-model:value="form.comment" type="textarea" :rows="3" placeholder="这杯奶茶怎么样？" />
        </n-form-item>
        <n-form-item label="推荐" path="wouldRecommend">
          <n-switch v-model:value="form.wouldRecommend" />
        </n-form-item>
      </n-gi>
    </n-grid>

    <n-space justify="center" style="margin-top: 24px;">
      <n-button type="primary" @click="handleSubmit" :loading="submitting">
        {{ isEdit ? '更新' : '保存' }}
      </n-button>
      <n-button @click="router.back()">取消</n-button>
    </n-space>
  </n-form>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage, NForm, NFormItem, NInput, NInputNumber, NSelect, NDatePicker, NRate, NSwitch, NButton, NSpace, NGrid, NGi, NH4 } from 'naive-ui'
import { useMilkTeaStore } from '../stores/milkTea'
import { theme } from '../assets/styles/theme'

const props = defineProps({
  recordId: { type: [Number, String], default: null }
})

const router = useRouter()
const store = useMilkTeaStore()
const message = useMessage()
const formRef = ref(null)
const submitting = ref(false)

const isEdit = computed(() => !!props.recordId)

const sugarLevelOptions = theme.sugarLevelOptions
const iceLevelOptions = theme.iceLevelOptions
const cupSizeOptions = theme.cupSizeOptions

const form = ref({
  brand: null,
  drinkName: null,
  price: null,
  consumeDate: null,
  sugarLevel: null,
  iceLevel: null,
  cupSize: null,
  teaBase: null,
  rating: null,
  comment: null,
  wouldRecommend: null
})

const rules = {
  brand: { required: true, message: '请输入品牌', trigger: 'blur' },
  drinkName: { required: true, message: '请输入饮品名', trigger: 'blur' },
  price: { required: true, type: 'number', message: '请输入价格', trigger: 'blur' },
  consumeDate: { required: true, message: '请选择消费时间', trigger: 'change' }
}

onMounted(async () => {
  if (isEdit.value) {
    await store.fetchRecordById(props.recordId)
    const r = store.currentRecord
    if (r) {
      form.value = {
        brand: r.brand,
        drinkName: r.drinkName,
        price: r.price,
        consumeDate: r.consumeDate,
        sugarLevel: r.sugarLevel,
        iceLevel: r.iceLevel,
        cupSize: r.cupSize,
        teaBase: r.teaBase,
        rating: r.rating,
        comment: r.comment,
        wouldRecommend: r.wouldRecommend
      }
    }
  }
})

async function handleSubmit() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    const data = { ...form.value }
    if (isEdit.value) {
      await store.editRecord(props.recordId, data)
      message.success('更新成功')
    } else {
      await store.addRecord(data)
      message.success('记录成功')
    }
    router.push('/records')
  } catch (e) {
    message.error(e.message || '操作失败')
  } finally {
    submitting.value = false
  }
}
</script>
```

- [ ] **步骤 2：创建 RecordFormView**

```vue
<template>
  <n-card :title="isEdit ? '编辑奶茶记录' : '记一杯奶茶'" style="max-width: 960px; margin: 0 auto;">
    <RecordForm :record-id="recordId" />
  </n-card>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { NCard } from 'naive-ui'
import RecordForm from '../components/RecordForm.vue'

const route = useRoute()
const recordId = computed(() => route.params.id || null)
const isEdit = computed(() => !!route.params.id)
</script>
```

- [ ] **步骤 3：验证表单渲染**

运行：`cd frontend && npm run dev`，访问 http://localhost:5173/records/new
预期：三栏表单正常渲染，所有字段可见

- [ ] **步骤 4：提交**

```bash
cd frontend && git add -A && git commit -m "feat(frontend): add RecordForm page with create/edit modes"
```

---

## 任务 16：统计页面（OverviewCards + ChartPanel）

**涉及文件：**
- 创建：`frontend/src/components/OverviewCards.vue`
- 创建：`frontend/src/components/ChartPanel.vue`
- 创建：`frontend/src/views/StatisticsView.vue`

- [ ] **步骤 1：创建 OverviewCards 组件**

```vue
<template>
  <n-grid :cols="4" :x-gap="16" style="margin-bottom: 24px;">
    <n-gi>
      <n-card>
        <n-statistic label="总消费" prefix="¥">
          <n-number-animation :from="0" :to="Number(overview.totalSpent) || 0" :precision="2" />
        </n-statistic>
      </n-card>
    </n-gi>
    <n-gi>
      <n-card>
        <n-statistic label="总杯数">
          <n-number-animation :from="0" :to="overview.totalCount || 0" />
        </n-statistic>
      </n-card>
    </n-gi>
    <n-gi>
      <n-card>
        <n-statistic label="均价" prefix="¥">
          <n-number-animation :from="0" :to="Number(overview.avgPrice) || 0" :precision="2" />
        </n-statistic>
      </n-card>
    </n-gi>
    <n-gi>
      <n-card>
        <n-statistic label="最常喝">
          <template #default>
            <span style="font-size: 24px; font-weight: bold; color: #7B1FA2;">
              {{ overview.topBrand || '暂无' }}
            </span>
          </template>
        </n-statistic>
      </n-card>
    </n-gi>
  </n-grid>
</template>

<script setup>
import { NGrid, NGi, NCard, NStatistic, NNumberAnimation } from 'naive-ui'

defineProps({
  overview: { type: Object, required: true }
})
</script>
```

- [ ] **步骤 2：创建 ChartPanel 组件**

```vue
<template>
  <n-card :title="title" style="margin-bottom: 16px;">
    <v-chart :option="chartOption" :style="{ height: height }" autoresize />
  </n-card>
</template>

<script setup>
import { computed } from 'vue'
import { NCard } from 'naive-ui'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart, BarChart } from 'echarts/charts'
import {
  TitleComponent, TooltipComponent, LegendComponent, GridComponent
} from 'echarts/components'

use([CanvasRenderer, LineChart, PieChart, BarChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const props = defineProps({
  title: { type: String, default: '' },
  type: { type: String, required: true }, // 'line' | 'pie' | 'bar'
  data: { type: Object, required: true },
  height: { type: String, default: '320px' }
})

const themeColors = ['#FF8F00', '#FFB74D', '#FF6D00', '#E65100', '#FFE0B2', '#795548', '#A1887F', '#BCAAA4']

const chartOption = computed(() => {
  if (props.type === 'line') {
    return {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: props.data.dates || [] },
      yAxis: { type: 'value' },
      series: [{
        data: props.data.amounts || [],
        type: 'line',
        smooth: true,
        areaStyle: { color: '#FFE0B2' },
        lineStyle: { color: '#FF8F00', width: 2 },
        itemStyle: { color: '#FF8F00' }
      }]
    }
  }
  if (props.type === 'pie') {
    return {
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: (props.data.brands || []).map((brand, i) => ({
          name: brand,
          value: props.data.amounts?.[i] || 0,
          itemStyle: { color: themeColors[i % themeColors.length] }
        })),
        label: { formatter: '{b}: ¥{c}' }
      }]
    }
  }
  if (props.type === 'bar') {
    return {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: props.data.weeks || [] },
      yAxis: { type: 'value' },
      series: [{
        data: props.data.amounts || [],
        type: 'bar',
        itemStyle: { color: '#FF8F00', borderRadius: [4, 4, 0, 0] }
      }]
    }
  }
  return {}
})
</script>
```

- [ ] **步骤 3：创建 StatisticsView**

```vue
<template>
  <div>
    <OverviewCards :overview="store.overview" />
    <n-grid :cols="2" :x-gap="16" style="margin-bottom: 16px;">
      <n-gi>
        <ChartPanel title="每日消费趋势（近30天）" type="line" :data="store.dailyTrend" />
      </n-gi>
      <n-gi>
        <ChartPanel title="品牌消费分布" type="pie" :data="store.brandDistribution" />
      </n-gi>
    </n-grid>
    <ChartPanel title="每周消费汇总" type="bar" :data="store.weeklySummary" height="280px" />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { NGrid, NGi } from 'naive-ui'
import { useMilkTeaStore } from '../stores/milkTea'
import OverviewCards from '../components/OverviewCards.vue'
import ChartPanel from '../components/ChartPanel.vue'

const store = useMilkTeaStore()

onMounted(() => {
  store.fetchAllStats()
})
</script>
```

- [ ] **步骤 4：验证统计页面渲染**

运行：`cd frontend && npm run dev`，访问 http://localhost:5173/statistics
预期：概览卡片和空图表区域正常渲染

- [ ] **步骤 5：提交**

```bash
cd frontend && git add -A && git commit -m "feat(frontend): add Statistics page with OverviewCards and ChartPanel (ECharts)"
```

---

## 任务 17：后端 Dockerfile

**涉及文件：**
- 创建：`backend/Dockerfile`

**依赖：** 任务 9

- [ ] **步骤 1：创建后端 Dockerfile（多阶段构建）**

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/milk-tea-record-0.0.1-SNAPSHOT.jar app.jar

RUN mkdir -p /app/data
VOLUME /app/data

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

- [ ] **步骤 2：验证 Docker 构建**

运行：`cd backend && docker build -t milk-tea-backend .`
预期：镜像构建成功

- [ ] **步骤 3：提交**

```bash
cd backend && git add Dockerfile && git commit -m "infra(backend): add multi-stage Dockerfile"
```

---

## 任务 18：前端 Dockerfile + nginx.conf

**涉及文件：**
- 创建：`frontend/Dockerfile`
- 创建：`frontend/nginx.conf`

**依赖：** 任务 16

- [ ] **步骤 1：创建 nginx.conf**

```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    location /api/ {
        proxy_pass http://backend:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

- [ ] **步骤 2：创建前端 Dockerfile（多阶段构建）**

```dockerfile
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

- [ ] **步骤 3：验证 Docker 构建**

运行：`cd frontend && docker build -t milk-tea-frontend .`
预期：镜像构建成功

- [ ] **步骤 4：提交**

```bash
cd frontend && git add Dockerfile nginx.conf && git commit -m "infra(frontend): add Dockerfile with Nginx and SPA routing config"
```

---

## 任务 19：docker-compose.yml

**涉及文件：**
- 创建：`docker-compose.yml`

**依赖：** 任务 17, 任务 18

- [ ] **步骤 1：创建 docker-compose.yml**

```yaml
version: '3.8'

services:
  backend:
    build:
      context: ./backend
    container_name: milk-tea-backend
    ports:
      - "8080:8080"
    volumes:
      - milk-tea-data:/app/data
    restart: unless-stopped

  frontend:
    build:
      context: ./frontend
    container_name: milk-tea-frontend
    ports:
      - "80:80"
    depends_on:
      - backend
    restart: unless-stopped

volumes:
  milk-tea-data:
```

- [ ] **步骤 2：验证编排构建**

运行：`docker-compose build`
预期：两个镜像均构建成功，无报错

- [ ] **步骤 3：提交**

```bash
git add docker-compose.yml && git commit -m "infra: add docker-compose.yml with backend + frontend services"
```

---

## 任务 20：GitHub Actions CI

**涉及文件：**
- 创建：`.github/workflows/ci.yml`

**依赖：** 任务 19

- [ ] **步骤 1：创建 CI 工作流**

```yaml
name: CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  backend-test:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: backend
    steps:
      - uses: actions/checkout@v4
      - name: 设置 JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: 运行测试
        run: mvn test

  frontend-test:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: frontend
    steps:
      - uses: actions/checkout@v4
      - name: 设置 Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
          cache-dependency-path: frontend/package-lock.json
      - name: 安装依赖
        run: npm ci
      - name: 构建
        run: npm run build

  docker-build:
    runs-on: ubuntu-latest
    needs: [backend-test, frontend-test]
    steps:
      - uses: actions/checkout@v4
      - name: 构建后端镜像
        run: docker build -t milk-tea-backend ./backend
      - name: 构建前端镜像
        run: docker build -t milk-tea-frontend ./frontend
```

- [ ] **步骤 2：提交**

```bash
git add .github/workflows/ci.yml && git commit -m "infra: add GitHub Actions CI (test + Docker build)"
```

---

## 任务 21：README + 根目录文档

**涉及文件：**
- 创建：`README.md`

**依赖：** 任务 20

- [ ] **步骤 1：创建 README.md**

```markdown
# 🧋 奶茶记录 (MilkTea Record)

> 个人奶茶消费追踪工具 — 记录每一杯，了解你的奶茶消费模式

## 功能

- 📝 记录奶茶：品牌、饮品名、价格、糖度、冰度、杯型、茶底、评分、点评
- 🔍 筛选搜索：按品牌、糖度、价格区间、关键词组合筛选
- 📊 消费统计：总览卡片、每日趋势折线图、品牌分布饼图、每周汇总柱状图

## 技术栈

- **前端**: Vue 3 + Vite + Naive UI + ECharts + Pinia + Axios
- **后端**: Spring Boot 3 + Java 17 + Spring Data JPA + H2
- **容器化**: Docker + docker-compose + Nginx

## 快速开始

### 开发模式

```bash
# 启动后端
cd backend && mvn spring-boot:run

# 启动前端（新终端）
cd frontend && npm install && npm run dev
```

- 前端: http://localhost:5173
- 后端 API: http://localhost:8080/api
- H2 控制台: http://localhost:8080/h2-console

### Docker 部署

```bash
docker-compose up --build
```

- 前端: http://localhost
- 后端 API: http://localhost/api

## 目录结构

```
├── backend/          Spring Boot 后端
├── frontend/         Vue3 前端
├── docker-compose.yml
└── .github/workflows/ci.yml
```

## 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| SERVER_PORT | 8080 | 后端服务端口 |

## License

MIT
```

- [ ] **步骤 2：提交**

```bash
git add README.md && git commit -m "docs: add README with quickstart, tech stack, and directory structure"
```

---

## 自检清单

- [x] **规约覆盖**：US-1→任务5+7+15, US-2→任务5+7+14, US-3→任务5+7+14, US-4→任务6+8+16, US-5→任务6+8+16, US-6→任务6+8+16。6 个用户故事全覆盖。
- [x] **占位符扫描**：无 TBD/TODO/待实现。所有代码块包含实际代码。
- [x] **类型一致性**：SugarLevel/IceLevel/CupSize 枚举值在 model、DTO、service、前端 theme.js 中一致。ApiResponse 格式在所有 controller 中一致。字段名（brand, drinkName, price, consumeDate 等）全文一致。
- [x] **容器化**（§4.10）：任务 17-19 覆盖 Dockerfile + docker-compose。
- [x] **CI**（§4.8）：任务 20 覆盖 GitHub Actions。
- [x] **测试覆盖**（§11）：任务 3-8 包含 Repository、Service、Controller 测试。
