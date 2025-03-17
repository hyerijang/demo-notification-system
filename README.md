<img src = "https://github.com/user-attachments/assets/7dcc66df-daab-4901-902a-1a63582ab4ef" width = "100%"/></a>

# 공모주 알리미 클론 코딩

<br>

<div align="center">
<img src="https://img.shields.io/badge/Java 17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Boot 3.3.5-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/></a>
</div>
<div align="center">
<img src="https://img.shields.io/badge/MySQL 8-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"/></a>
<img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"/></a>
<img src="https://img.shields.io/badge/docker-257bd6?style=for-the-badge&logo=docker&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white"/></a>
</div>

<br>

Java, 비동기 멀티 스레딩 공부를 하면서 인턴 시절 Kotlin 으로 개발했던 알림 발송 시스템을 Java로 클론 코딩해 보았습니다. 

구현의 편의를 위해 일부 요소는 생략, 변형하였습니다.

<br>


- [[알림 서비스 클론 코딩] : V1 기본 기능 구현](https://dev-jhl.tistory.com/118)
- [[알림 서비스 클론 코딩] : V2 설계 변경 및 Kafka 추가](https://dev-jhl.tistory.com/120)
- [[알림 서비스 클론 코딩] : V3 멀티 모듈 전환기](https://dev-jhl.tistory.com/122)

## 개발 기간
- V1 / 2024.11.20 / 간략화한 설계,  비동기 멀티스레딩으로 간단한 알림 전송 구현
- V2 / 2025.03.13 ~ 14 / 책임을 명확히 하기 위해 메세지 스레드 풀과 메세지 결과 스레드 풀을 분리, Kafka 도입
- V3 / 2023.03.15 : / 단일 모듈을 멀티 모듈로 세분화
## 요구사항 분석
- 공모주 알리미 서비스 구독자 (120만명) 대상으로 매일 2회, 공모주 청약 알림 메세지(카카오톡 or 앱푸시)을 발송하는 서비스의 Consumer 부분을 구현한다.
### 메세지 발송 속도
- 120만개의 메세지를 2시간 내에 발송해야 함.
- 1분에 약 18000~ 20000개 메세지를 발송해야하는 것을 목표로 한다.
- 너무 빠르게 발송한 경우, 메세지 플랫폼 쪽에 부하가 심해질 수 있으므로, 분당 20000개를 초과하지 않도록 한다. (Thread sleep 활용)
### 중복 전송 방지 필요
- 장애로 인해 consumer에서 데이터를 중복으로 읽을 수 있으므로, 중복 전송 방지를 위한 로직이 필요하다.
- 분산 락의 사용은 권장되지 않음 : 시스템 복잡도 증가, 성능 저하
### 구현을 단순히 하기 위한 가정
1. 메세지 플랫폼
    - 사내 공통 서비스인 '메세지 플랫폼'에서 전사 톡메세지/앱푸시 발송을 담당한다고 가정한다.
    - (1) 지정된 형식에 맞게 메세지를 생성하여 메세지 플랫폼에 request를 보내면
    - (2) 메세지 플랫폼에서 메세지를 발송하고 결과를 `공모주알리미 메세지 결과 topic`에 발행한다
    -  메세지 플랫폼에서 비동기로 응답해준다고 가정하겠음.
2. Kafka의 처리 성능 가정
   - consumer가 1분 18000개의 메세지를 커밋한다고 가정한다.
   - consumer의 partition size는 3이라고 가정한다.

## 전체 아키텍처

![공모주 알리미 아키텍처(간략) (2)](https://github.com/user-attachments/assets/0333c750-837b-4b90-9c85-487372976815)


## 프로젝트 아키텍처 

### V1 간략화한 설계,  비동기 멀티스레딩으로 알림 전송 간단히 구현
![공모주 알리미 클론 V1](https://github.com/user-attachments/assets/ec3b21a3-ac4f-4312-b6c5-e3595ab35d2f)

### V2 메세지 스레드 풀과 메세지 결과 스레드 풀을 분리, Kafka 도입
![공모주 알리미 v2](https://github.com/user-attachments/assets/a324b814-e5f4-4736-8f9a-486745cd2495)

### V3 단일 모듈을 멀티 모듈로 세분화
![공모주 클론 v3](https://github.com/user-attachments/assets/b230ae1b-6f69-4fbf-8c0b-55611589c260)


## 구현 과정 설명
## V1  간략화한 설계,  비동기 멀티스레딩으로 알림 전송 간단히 구현

- [[알림 서비스 클론 코딩] : V1 기본 기능 구현](https://dev-jhl.tistory.com/118)

| 비동기 멀티 스레딩 중심으로 설명

1. 메세지 전송용 카프카를 대체하기 위해 `Producer, Consumer` 구현
   - LinkedBlockingQueue 활용
   - consume시 네트워크 통신에 20ms 정도 걸린다고 가정함
2. `@Async("consumerThreadPoolTaskExecutor")` : 카프카 컨슈머 스레드를 대체하기 비동기 멀티 스레드 구현
   - 스레드 수는 consumer 파티션 수와 동일한 3으로 설졍
3. `@Async("messageSenderThreadPoolTaskExecutor")`: 메세지 플랫폼 발송 및 결과 수신을 위한 비동기 멀티 스레드 구현
   - 스레드 수는 기존 서비스와 동일하게 50개로 설정
     4. ThreadPoolTaskExecutor 관련 설정
        - CPU, 메모리 리소스 사용량을 안정적으로 유지하기 위해 고정 풀 전략 사용
           - 고정 풀 전략 : CorePoolSize와 MaxPoolSize를 동일하게 설정하고, 큐 사이즈는 무제한으로 한다.
        - 알림 발송 누락 방지를 위해 `RejectedExecution`의 정책은 `CallerRunsPolicy`로 설정한다.
           - CallerRunsPolicy : 처리되지 못한 요청은 무시하지 않고, ThreadPool을 호출한 스레드에서 처리
           - 생산자가 일하므로 생산 자체가 느려짐 -> 작업 속도 자동 조절

        <details>
        <summary> AsyncConfig </summary>
   
        ```java
        @Slf4j
        @Configuration
        public class AsyncConfig {
   
            final int MAX_THREAD_SIZE = 50;
   
            @Bean
            public ThreadPoolTaskExecutor messageSenderThreadPoolTaskExecutor() {
                ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
                executor.setCorePoolSize(MAX_THREAD_SIZE); // 코어 스레드 풀 크기 설정
                executor.setMaxPoolSize(MAX_THREAD_SIZE); // 최대 스레드 풀 크기 설정
                executor.setQueueCapacity(Integer.MAX_VALUE); // 작업 큐 용량 설정
                executor.setThreadNamePrefix("message-sender-child-executor"); // 스레드 이름 접두사 설정
                executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy() {
                                                         @Override
                                                         public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                                                             log.warn("Message Sender Task Queue is Full");
                                                             super.rejectedExecution(r, e);
                                                         }
                                                     }
                );
                executor.initialize();
                return executor;
            }
   
   
            @Bean
            public ThreadPoolTaskExecutor consumerThreadPoolTaskExecutor() {
                ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
                executor.setCorePoolSize(3); // 코어 스레드 풀 크기 설정
                executor.setMaxPoolSize(3); // 최대 스레드 풀 크기 설정
                executor.setQueueCapacity(Integer.MAX_VALUE); // 작업 큐 용량 설정
                executor.setThreadNamePrefix("consumer-child-executor"); // 스레드 이름 접두사 설정
                executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy() {
                                                         @Override
                                                         public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                                                             log.warn("Consumer Task Queue is Full");
                                                             super.rejectedExecution(r, e);
                                                         }
                                                     }
                );
                executor.initialize();
                return executor;
            }
        }
   
   
        ```
        </details>


###  V2 메세지 스레드 풀과 메세지 결과 스레드 풀을 분리, Kafka 도입

- [[알림 서비스 클론 코딩] : V2 설계 변경 및 Kafka 추가](https://dev-jhl.tistory.com/120)

1. 책임을 명확히 하기 위해 메세지 스레드 풀과 메세지 결과 스레드 풀을 분리
   - 기존 구조에선 메세지 전송 + 전송 결과 수신을 하나의 스레드에서 처리하고 있어서 자원 낭비가 있었고, 책임이 명확하지 않았다.
   - 메세지 전송 스레드 풀과 메세지 결과 수신 스레드 풀을 분리하여 책임을 명확히 하였음
2. Kafka 도입
   - 멀티 브로커(3개)로 구성
   - topic 2개 생성
      - dev.ipo-message-request.v1 (메세지 전송 요청)
      - dev.ipo-message-platform-result.v1 (메세지 플랫폼 전송 결과 수신)

###  V3 구현 과정 설명
- [[알림 서비스 클론 코딩] : V3 멀티 모듈 전환기](https://dev-jhl.tistory.com/122)
- 단일 모듈을 멀티 모듈로 세분화, 각 모듈의 책임 분리하고 가독성 개선
   - ipo-reminder-batch, ipo-reminer, message-platform
   - ![image](https://github.com/user-attachments/assets/65f8ec3f-b2e7-48ca-9f85-28e1236accb6)