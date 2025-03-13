<img src = "https://github.com/user-attachments/assets/7dcc66df-daab-4901-902a-1a63582ab4ef" width = "100%"/></a>

# 공모주 알리미 Consumer 클론 코딩

<br>

<div align="center">
<img src="https://img.shields.io/badge/Java 17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Boot 3.3.5-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/></a>
<img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"/></a>
</div>

<br>
Java, 비동기 멀티 스레딩 공부를 하면서 인턴 시절 Kotlin 으로 개발했던 알림 발송 시스템의 Consumer 부분을 Java로 클론 코딩해 보았습니다. 구현의 편의를 위해 일부 요소는 생략, 변형하였습니다. 


## 개발 기간
2024.11.20 (1일)

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
    - 이 프로젝트에서는 구현을 간단히 하기위해 kafka를 사용하지 않고, 메세지 플랫폼에서 비동기로 응답해 준다고 가정하겠음.
2. 구현의 편의를 위해 kafka는 사용하지 않음
   - consumer가 1분 18000개의 메세지를 커밋한다고 가정한다.
   - consumer의 partition size는 3이라고 가정한다.
     
## 참고한 아키텍처
실제 서비스에서 설계했던 아키텍처 입니다. Consumer 모듈을 담당하여 구현했습니다. 

### 전체 아키텍처 
<details>
<summary>더보기</summary>
    
- ![공모주 알리미 아키텍처(간략) (2)](https://github.com/user-attachments/assets/0333c750-837b-4b90-9c85-487372976815)

</details>

### Consumer 서버 아키텍처 
<details>
<summary>더보기</summary>

- ![공모주 알리미 컨슈머 아키텍처 (실제)](https://github.com/user-attachments/assets/d955f723-1f3e-490a-94fa-abfed36adbd9)
</details>

## 프로젝트 아키텍처 V1
![공모주 알리미 컨슈머 아키텍처 (개인플젝1)](https://github.com/user-attachments/assets/84abcd51-73b4-4e12-a089-e6d12cb40c3c)



## V1 구현 과정 설명 
| 비동기 멀티 스레딩 중심으로 설명

1. 카프카 컨슈머를 대체하기 위한 `KafkaSimulator` 구현
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


