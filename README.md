# Good Feel Tour 여행사 업무 관리 시스템

완전한 시스템 명세에 따라 구현된 Good Feel Tour 여행사 업무 관리 시스템의 서비스 레이어입니다.

## 🎯 시스템 개요

### 주요 기능
- **File 검색**: 파일 레코드 검색 및 조회
- **신규등록**: REF No 자동 생성 및 파일 레코드 관리
- **AGT 관리**: 에이전트 정보 및 업무 관리
- **고객명단**: 고객 정보 관리 (일반/VIP/기업 분류)
- **단체행동지침서**: 가이드 지침서 작성 및 관리
- **정산보고서**: 재무 계산 및 정산 관리

### 문서 워크플로우
```
견적서 → 수배서 → 청구서 → 관광확인 → 가이드지침서
```

## 🏗 시스템 구조

### 엔티티 구조
- **User**: 시스템 사용자 관리
- **Customer**: 고객 정보 관리 (VIP/기업 고객 지원)
- **Tour**: 여행 상품 관리
- **Booking**: 예약 정보 관리
- **FileRecord**: 신규등록 파일 관리 (REF No 자동 생성)
- **Document**: 5개 문서 타입 관리 (워크플로우 검증)
- **AGT**: 에이전트 정보 관리
- **FinancialRecord**: 재무 기록 관리 (자동 계산)
- **GuideInstruction**: 가이드 지침서 관리

### 서비스 레이어

#### 📁 FileRecordService
- REF No 자동 생성 (GF-YYYYMMDD-XXX 형식)
- 중복 검사 및 파일 관리
- 검색 및 필터링 기능

```kotlin
val refNo = fileRecordService.generateRefNo() // "GF-20240101-001"
```

#### 📄 DocumentService
- 5개 문서 타입별 CRUD
- 워크플로우 순서 검증
- 템플릿 기반 문서 생성

```kotlin
// 견적서 → 수배서 → 청구서 → 관광확인 → 가이드지침서 순서 검증
val document = documentService.createDocument(fileRecord, DocumentType.QUOTATION, ...)
```

#### 👥 AGTService  
- 에이전트 정보 관리
- 업무 통계 및 성과 관리
- 파일 배정 관리

#### 💰 FinancialService
- 재무 계산 자동화
- Sub Total = 수취송금 - 판매송금 - 운용비용
- 수수료 계산 및 미지급액 관리

```kotlin
val subTotal = receivedAmount - salesAmount - operatingCost
val unpaidAmount = subTotal - commission
```

#### 🧑‍🤝‍🧑 CustomerService
- 고객 정보 관리 (일반/VIP/기업)
- VIP 승급 기능
- 생일 알림 기능

#### 🗺 GuideService
- 가이드 지침서 템플릿 생성
- 안전 수칙 및 비상 연락처 관리
- 지침서 승인 및 배포

## 🔧 기술 스택

- **Spring Boot 3.1.5** + **Kotlin 1.8.22**
- **JPA/Hibernate** (엔티티 관계 매핑)
- **Spring Security** (보안)
- **MySQL/H2** (데이터베이스)
- **Bean Validation** (데이터 검증)
- **@Transactional** (트랜잭션 관리)

## 📊 데이터 무결성 및 비즈니스 로직

### 검증 규칙
- REF No 중복 방지
- 문서 워크플로우 순서 검증
- 재무 계산 정확성 보장
- 상태 관리 (신규 → 진행중 → 완료)

### 권한 관리
- 사용자별 접근 권한
- 문서 수정 권한 제어
- 재무 정보 접근 제한

## 🚀 실행 방법

### 개발 환경 실행
```bash
# H2 데이터베이스 사용 (메모리 DB)
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### API 엔드포인트
```
GET    /api/dashboard/statistics           - 대시보드 통계
GET    /api/file-records/generate-ref-no   - REF No 생성
POST   /api/file-records                   - 파일 레코드 생성
GET    /api/file-records/search           - 파일 검색
POST   /api/documents/from-template       - 템플릿 문서 생성
PUT    /api/documents/{id}/status         - 문서 상태 변경
```

### H2 Console 접속
- URL: http://localhost:8080/gftour/h2-console
- JDBC URL: `jdbc:h2:mem:gftour`
- Username: `sa`
- Password: (공백)

## 📋 시스템 특징

### 자동화된 기능
- **REF No 자동 생성**: 날짜 기반 일련번호 자동 생성
- **재무 계산**: Sub Total 및 미지급액 자동 계산  
- **문서 템플릿**: 고객/투어 정보 기반 자동 생성
- **상태 관리**: 워크플로우 기반 자동 상태 전환

### 비즈니스 로직
- **문서 순서 검증**: 이전 문서 승인 완료 후 다음 문서 작성 가능
- **VIP 고객 관리**: 자동 승급 및 특별 관리
- **AGT 업무 통계**: 배정 파일 수, 완료율 등 자동 집계
- **가이드 지침서**: 표준 템플릿 기반 안전 수칙 자동 생성

## 🧪 테스트

기본적인 서비스 로직 테스트가 포함되어 있습니다:
- `FileRecordServiceTest`: REF No 생성 및 파일 관리 테스트  
- `DocumentServiceTest`: 문서 생성 및 워크플로우 테스트

## 🔍 코드 품질

- **한국어 주석**: 모든 클래스와 메서드에 한국어 설명
- **로깅**: SLF4J를 통한 체계적 로깅
- **예외 처리**: 커스텀 예외와 적절한 에러 메시지
- **트랜잭션**: @Transactional을 통한 데이터 일관성 보장

---

> 이 시스템은 완전한 시스템 명세에 따라 구현된 Good Feel Tour 여행사 업무 관리 시스템으로, 
> 실제 여행사 업무 프로세스를 반영하여 설계되었습니다.