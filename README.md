# [PigeonStargram](https://pigeonstargram.com)  Backend

## 📖 프로젝트 소개(https://pigeonstargram.com)
PigeonStargram은 인스타그램의 기능들을 구현하는 것을 목표로 한 소셜 미디어 클론 프로젝트입니다. [V1(Version1)](#-주요-기능-v1)에서는 기능 구현에만 초점을 맞춰 다양한 기능을 구현하였으며, [V2(Version2)](#-성능-개선-v2)에서는 실제 테스트를 통해 V1과 동일한 기능을 유지하면서도 최적화에 중점을 두어 개발되었습니다.

## 🛠 기술 스택
- **Server**: Java, Spring Boot, Spring Data JPA, WebSocket, OAuth 2.0, Spring Security, AWS S3, Gmail SMTP
- **Redis**: 캐시, 세션 관리, Pub/Sub, 메시지 큐, 자료구조 활용(Set, Sorted Set, Hash...)
- **Database**: MariaDB, MongoDB
- **Deployment**: Nginx, Travis CI, CodeDeploy
- **Testing**: JUnit, JMeter

## 📌 주요 기능 (V1)

### 유저

- **회원가입/로그인/로그아웃/계정 탈퇴**: 구글을 통한 회원가입이 가능하며, 소셜 로그인 또는 직접 입력을 통한 로그인 기능을 제공합니다. 로그아웃 및 계정 탈퇴 기능도 포함되어 있습니다.
  
    <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    #### 회원가입
    <p align="center">
      <img src="https://github.com/user-attachments/assets/6be7b7f3-94f4-412a-95af-0b20a01669ed" alt="회원가입 이미지 1" width="45%" />
      <img src="https://github.com/user-attachments/assets/c230035e-1420-4032-a628-1c4543565c74" alt="회원가입 이미지 2" width="45%" />
    </p>

    #### 로그인
    <p align="center">
      <img src="https://github.com/user-attachments/assets/1c7cce6f-e6d3-42c0-abaa-07abbd4405a5" alt="로그인 이미지 1" width="45%" />
      <img src="https://github.com/user-attachments/assets/b282403d-2f64-4d8e-bfde-a28c3e698fa7" alt="로그인 이미지 2" width="45%" />
    </p>
    
    #### 로그아웃
   <p align="center">
    <img src="https://github.com/user-attachments/assets/a88cd423-5fd6-4b30-911f-44918d8f594f" alt="로그아웃 이미지" width="45%" />
   </p>
    
    #### 계정 탈퇴
    //TODO
    
  </details>

- **비밀번호 찾기**: 비밀번호 재설정 링크가 포함된 이메일을 수신하고, 해당 링크로 이동하여 비밀번호를 재설정할 수 있습니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>

    <p align="center">
      <img src="https://github.com/user-attachments/assets/5f33ef4e-4233-4d40-8485-764ae608308f" alt="비밀번호 찾기 이미지 1" width="45%" />
      <img src="https://github.com/user-attachments/assets/4d410414-6b59-4f8f-9eb9-cd95bdbee628" alt="비밀번호 찾기 이미지 2" width="45%" />
    </p>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/214a7de6-4886-4d84-8284-581b71436124" alt="비밀번호 찾기 이미지 3" width="45%" />
      <img src="https://github.com/user-attachments/assets/2146bcc8-4f6e-43ae-86b7-e7edbc4930f2" alt="비밀번호 찾기 이미지 4" width="45%" />
    </p>

  </details>


- **팔로우/팔로잉**: 유저는 다른 사용자를 팔로우 할 수 있습니다. 또한 팔로워/팔로잉 숫자와 누가 팔로우했는지도 확인할 수 있습니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>

    <p align="center">
      <img src="https://github.com/user-attachments/assets/b9995170-4654-40f3-8943-5cb143fa6f87" alt="팔로우 이미지 1" width="45%" />
      <img src="https://github.com/user-attachments/assets/cf239920-8409-448f-bd21-bb9333312011" alt="팔로우 이미지 2" width="45%" />
    </p>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/18f7ca47-6eb8-4a9a-bf00-46146fb366e9" alt="팔로우 이미지 3" width="45%" />
    </p>

  </details>


- **친구 추천**: 내가 팔로우하지 않은 사용자 중, 나와 가까운 사람을 추천해주는 친구 추천 기능을 제공합니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>

    <p align="center">
      <img src="https://github.com/user-attachments/assets/3f10eb43-7666-414a-86be-3881d062452e" alt="팔로우 이미지 3" width="45%" />
    </p>
    
  </details>

### 스토리

- **스토리 업로드 및 삭제**: 사용자는 이미지와 글을 합쳐서 스토리를 여러 개 업로드할 수 있으며, 업로드한 스토리는 삭제가 가능합니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/9e806949-7df5-4f7d-9952-8f7a80aa4267" alt="스토리 업로드 이미지 1" width="45%" />
      <img src="https://github.com/user-attachments/assets/a55921ba-d699-46b0-8f73-8ad9103d59ca" alt="스토리 업로드 이미지 2" width="45%" />
    </p>

  </details>

- **스토리 목록 보기**: 사용자는 팔로우한 유저들의 스토리를 확인할 수 있으며, 스토리의 읽음 여부에 따라 표시 방식이 다르게 적용됩니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/bb5f0c35-6981-46a8-a023-35342136b1a4" alt="스토리 목록 보기 이미지" width="45%" />
    </p>

  </details>

- **스토리 조회자 확인**: 사용자는 자신의 스토리를 누가 조회했는지 확인할 수 있습니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/c5b092c8-7989-4f2d-b717-afd1e05d8d3f" alt="스토리 조회자 확인 이미지" width="45%" />
    </p>

  </details>

- **스토리 답장**: 사용자는 다른 사람의 스토리에 답장을 보낼 수 있는 기능을 제공합니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/90fb379a-db1e-43d5-8499-51b8a39b4af6" alt="스토리 답장 이미지 1" width="45%" />
      <img src="https://github.com/user-attachments/assets/363adce5-3ea8-4f03-9714-34a39ac631ac" alt="스토리 답장 이미지 2" width="45%" />
    </p>

  </details>


### 게시글
- **게시글 작성/수정/삭제**: 사용자는 이미지나 글을 포함한 게시글을 작성할 수 있으며, 게시글을 수정하거나 삭제할 수 있습니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/309c8ad3-8b40-418d-8e23-11785a8a1ef2" alt="게시글 작성 이미지 1" width="45%" />
      <img src="https://github.com/user-attachments/assets/dfd6fd24-f1ce-4805-a89f-1f57af97fb23" alt="게시글 작성 이미지 2" width="45%" />
    </p>

  </details>

- **댓글 및 답글 작성/수정/삭제**: 사용자는 게시글에 댓글과 답글을 작성할 수 있으며, 이를 수정하거나 삭제할 수 있습니다. 또한, 이전 댓글을 불러와서 확인할 수 있습니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/870d94d5-1539-4ab6-9e76-4eaeb469511c" alt="댓글 이미지 1" width="45%" />
      <img src="https://github.com/user-attachments/assets/58dec37a-c433-40d0-b77b-559490dee427" alt="댓글 이미지 2" width="45%" />
    </p>

  </details>

- **유저 태그(@)**: 게시글, 댓글, 답글에서 유저를 @를 사용해 태그할 수 있습니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/595c53f3-47ea-440f-9072-810d6ef4bf7d" alt="유저 태그 이미지 1" width="45%" />
      <img src="https://github.com/user-attachments/assets/3874130f-0fb0-42b1-b129-160a3279ce85" alt="유저 태그 이미지 2" width="45%" />
    </p>

  </details>

- **좋아요/좋아요 취소**: 사용자는 게시글, 댓글, 답글에 좋아요를 누르거나 좋아요를 취소할 수 있습니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/b7aa0e41-22b8-44aa-839d-3617538666f7" alt="좋아요 이미지 1" width="45%" />
      <img src="https://github.com/user-attachments/assets/7b91a23d-3680-44bc-906b-715fce23cede" alt="좋아요 이미지 2" width="45%" />
    </p>

  </details>

- **프로필 및 타임라인**: 다른 유저의 프로필에서 그 유저의 게시글을 확인할 수 있으며, 타임라인(홈)에서는 팔로우한 유저들의 최신 게시물을 볼 수 있습니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/a593c1f0-e298-48da-afc5-681b249672a1" alt="프로필 이미지" width="45%" />
      <img src="https://github.com/user-attachments/assets/b315aa21-97c0-47b4-9a9a-f63c241bce29" alt="타임라인 이미지" width="45%" />
    </p>

  </details>

  
### 채팅

- **채팅 상대 선택**: 팔로우 및 팔로잉 목록을 기준으로 채팅 상대를 선택하고 대화를 시작할 수 있습니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/becc2bc8-c301-4544-a8d9-70bcd0b37f16" alt="채팅 상대 선택 이미지" width="45%" />
    </p>

  </details>

- **실시간 채팅**: 채팅에서 이미지나 글을 실시간으로 주고받을 수 있으며, 메시지 확인이 가능합니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/f5e1c4a5-0df5-49e5-885e-f1c17b931a16" alt="실시간 채팅 이미지" width="45%" />
    </p>

  </details>

- **읽지 않은 메시지 수**: 상대가 채팅방에 없을 때 메시지가 오면, 읽지 않은 메시지 수가 실시간으로 쌓이며 확인할 수 있습니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/17fe0e9c-8d19-4528-adb8-bc032cc4b0b3" alt="읽지 않은 메시지 수 이미지" width="45%" />
    </p>

  </details>

- **실시간 온라인 상태**: 유저의 온라인 상태를 설정하고 실시간으로 확인할 수 있습니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/f2bafa1b-6c1e-4b51-8f3e-7ea82eef723b" alt="실시간 온라인 상태 이미지 1" width="45%" />
      <img src="https://github.com/user-attachments/assets/074813b4-7d7b-4e1b-81fc-97a3c59ebc5e" alt="실시간 온라인 상태 이미지 2" width="45%" />
    </p>

  </details>

- **이전 채팅 불러오기**: 채팅 기록을 10개씩 불러와서 이전 대화를 확인할 수 있습니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/f5625120-86c6-41c7-9f1f-f670ffe13d6f" alt="이전 채팅 불러오기 이미지 1" width="45%" />
      <img src="https://github.com/user-attachments/assets/8b1193ec-5cc0-4935-b025-cf0a70bd1bb3" alt="이전 채팅 불러오기 이미지 2" width="45%" />
    </p>

  </details>

### 알림

- **실시간 알림**: 다양한 이벤트(댓글, 답글, 좋아요, 알림 설정한 상대의 게시글 작성, 팔로우 등)에 대해 실시간으로 알림을 받을 수 있습니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/5000e639-3889-479f-a8e9-2a95ac53fd2f" alt="실시간 알림 이미지" width="45%" />
    </p>

  </details>

- **알림 관리**: 알림을 개별적으로 삭제하거나, 모두 삭제할 수 있으며, 알림을 클릭하면 해당 이벤트가 발생한 위치로 이동이 가능합니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/eefdab74-a1bd-4ce5-aba2-5d36fa3e9916" alt="알림 관리 이미지 1" width="45%" />
      <img src="https://github.com/user-attachments/assets/7494275e-f147-4606-941f-c12de8df65f0" alt="알림 관리 이미지 2" width="45%" />
    </p>

  </details>


### 검색

- **유저 검색 및 자동완성**: 사용자는 검색 기능을 통해 입력한 키워드에 해당하는 유저를 찾을 수 있으며, 검색 중에 자동완성(검색어 추천) 기능이 제공됩니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/d972533d-dd85-49f2-81f8-34aeed955273" alt="유저 검색 이미지 1" width="45%" />
      <img src="https://github.com/user-attachments/assets/79605c15-2956-4355-a939-efc584b124e1" alt="유저 검색 이미지 2" width="45%" />
    </p>

  </details>

- **검색 기록**: 검색한 키워드는 검색 기록으로 저장되며, 개별 삭제 및 모두 삭제가 가능합니다.
  
  <details>
    <summary>📷 이미지와 함께 설명 보기</summary>
    
    <p align="center">
      <img src="https://github.com/user-attachments/assets/74ef87c3-a84f-49b8-bed1-528675fcf3bf" alt="검색 기록 이미지 1" width="45%" />
      <img src="https://github.com/user-attachments/assets/79bdb321-b43e-414b-a741-4facb412250e" alt="검색 기록 이미지 2" width="45%" />
    </p>

  </details>

## ⚡ 성능 개선 (V2)

### 공통1 (캐시)

#### V1 문제점:
- 모든 읽기와 쓰기 작업이 데이터베이스에서 이루어져 성능 저하와 지연이 발생했습니다.

#### V2 해결점:
- **Redis 캐시**를 도입하여 **look-aside** 방식으로 읽기 작업의 속도를 개선하고, **write-back** 및 **write-through** 방식으로 쓰기 작업의 성능을 최적화했습니다.
- **복잡하지 않은 캐시**는 애노테이션 기반으로 관리하였고, **복잡한 자료구조 캐시**는 수동으로 구현하여 최적화하였습니다.
- **Write-back 캐시 관리**: LRU 전략에 따라 일정 주기로 캐시에서 오래된 데이터를 데이터베이스에 플러시했습니다. 자주 조회되는 데이터는 캐시에서 계속 사용해 **starvation**이 발생할 수 있으므로, 체크포인트 시점에 모든 데이터를 데이터베이스에 플러시하여 데이터베이스와의 일관성을 유지합니다.

<details>
  <summary>🔧 성능 개선 보기</summary>

  TODO

</details>

### 공통2 (자료구조 활용)

#### V1 문제점:
- DB 엔티티를 사용하여 데이터 처리 및 관리를 하였으나, 성능 최적화에 한계가 있었습니다.

#### V2 해결점:
- Redis의 자료구조를 적극 활용하여 성능을 최적화했습니다.
  - **Sorted Set**을 사용하여 검색 기록을 효율적으로 관리했습니다.
  - **Set**을 사용하여 팔로우/팔로잉 유저 목록을 관리하여 빠른 접근이 가능하도록 했습니다.
  - 그 외...
<details>
  <summary>🔧 성능 개선 보기</summary>

 TODO

</details>

### 타임라인 (Timeline)

#### V1 문제점:
- 팔로워의 최신 게시글을 데이터베이스에서 검색하여 타임라인을 생성하는 방식으로 성능 저하가 발생했습니다.

#### V2 해결점:
- **타임라인 캐시 활용**: 팔로워의 최신 게시글을 데이터베이스에서 가져오는 대신 타임라인 캐시에서 가져오도록 변경했습니다. 특정 유저가 게시글을 작성하면, 그 유저를 팔로우하는 모든 팔로워의 타임라인에 해당 게시글의 ID를 저장합니다. 이렇게 함으로써, 타임라인 조회 시 데이터베이스에 접근하는 대신 캐시에서 게시글 ID를 종합하여 가져오게 됩니다.
- **유명인 처리**: 유명인이 게시글을 작성할 때는 타임라인 캐시에 게시글을 추가하는 작업이 부하를 증가시킬 수 있어, 유명인 게시글은 데이터베이스에서 직접 가져오는 방식을 유지합니다.
- **타임라인 병합**: 유저의 타임라인은 타임라인 캐시와 유명인 게시글을 직접 가져온 결과를 병합하여 제공, 이를 통해 조회 성능을 개선하고 데이터베이스 부하를 줄입니다.

<details>
  <summary>🔧 성능 개선 보기</summary>
  
  TODO
  
</details>

### 채팅 (Chat)

#### V1 문제점:
- V1에서는 단일 서버와 RDBMS를 사용하여 모든 채팅 기록을 불러올 때 모든 내용을 한 번에 불러왔습니다. 이로 인해 데이터베이스의 부하가 증가하고, 서버와 클라이언트 간의 전송 데이터 양이 많아졌습니다.
- 단일 서버에서는 웹소켓을 통해 실시간 채팅이 가능했지만, 분산 서버 환경에서는 클라이언트가 연결된 서버가 다를 수 있어 웹소켓 연결 문제가 발생했습니다.

#### V2 해결점:
- **데이터베이스 최적화**: NoSQL 데이터베이스를 사용하여 채팅 기록의 조회 속도를 개선하였습니다. 모든 채팅 기록을 불러오는 대신, ID 기반으로 페이징 처리하여 10개씩 불러오도록 하여 서버와 클라이언트 간의 전송 데이터 양을 줄였습니다.
- **웹소켓과 분산 서버**: 단일 서버에서는 웹소켓을 직접 사용하여 실시간 채팅을 처리하였지만, 분산 서버 환경에서는 Redis Pub/Sub를 사용하여 서로 다른 서버에 연결된 클라이언트 간의 메시지 전달 문제를 해결하였습니다. 

<details>
  <summary>🔧 성능 개선 보기</summary>
  
  TODO
  
</details>


### 알림 (Notification)

#### V1 문제점:
- V1에서는 알림 작업을 처리하기 위해 별도의 비동기 처리 없이 직접 데이터베이스에 접근하였습니다. 이로 인해 자원을 많이 소모하고, 팔로워가 많은 유명인의 게시글 작성 시 동일한 알림 데이터가 대량으로 생성되어 데이터베이스에 과도한 부하가 발생했습니다.

#### V2 해결점:
- **비동기 및 병렬 처리**: 알림 전용 워커 스레드를 생성하고, `BRPOP`을 활용하여 메시지 큐에서 블로킹 방식으로 비동기적으로 알림 작업을 처리합니다. 이로 인해 자원을 최소화하면서 여러 서버에서 분산하여 알림 작업을 처리할 수 있게 됩니다.
- **캐싱 및 데이터베이스 접근 최적화**: 알림 데이터를 `content`와 매핑 정보(수신인, 발신인)로 분리하여, 공통 정보인 `content`는 캐싱하고 매핑 정보만 데이터베이스에 저장하여 동일한 알림 내용이 중복 저장되지 않도록 합니다.
- **Batch 처리**: 알람을 매번 단일로 저장하면 DB접근 횟수가 많아지므로 알림 생성 시 `batch size`를 설정하여 일정 크기로 나눈 후, 한 번에 `batch size` 만큼의 알림만 데이터베이스에 저장하였습니다. 너무 큰 `batch size`는 데이터베이스 연결 시간을 길어지게 하므로, 적절한 `threshold`를 설정하여 최적화하였습니다.
- **분산 서버 처리**: 쪼개진 알림은 알림 워커를 통해 Redis Pub/Sub를 사용하여 분산 서버에 웹소켓을 통해 전달하였습니다. 이를 통해 실시간으로 알림을 여러 서버에 분산하여 전달할 수 있습니다.

<details>
  <summary>🔧 성능 개선 보기</summary>
  
  TODO
  
</details>

### 게시글 작성 (Create Post)

#### V1 문제점:
- 게시글 작성 시 이미지가 여러 개인 경우, 클라이언트는 서버에 이미지를 업로드하고, **서버는 이미지 파일들을 S3에 저장하며**, 포스트 정보를 데이터베이스에 매핑하여 결과를 받아오는 데 시간이 걸렸습니다. 이로 인해 전체 게시글 작성 시간이 길어져 사용자 경험이 저하되었습니다.

#### V2 해결점:
- 게시글 작성 시 이미지를 서버에 업로드할 때, **이미지 업로드는 비동기 및 병렬적으로 처리**하여 클라이언트에게는 **즉시 포스트 ID만 반환**하도록 하였습니다.
- 게시글 작성자는 이미지 업로드 중에도 게시글이 작성된 것처럼 빠르게 느낄 수 있으며, ID를 알고 있기 때문에 댓글, 좋아요 등의 작업도 문제 없이 즉시 수행할 수 있습니다.
- 다른 사용자는 게시글이 완전히 업로드된 후에만 볼 수 있도록 하여 기존처럼 문제가 없으며, 작성자는 기존보다 빠르게 게시글을 작성하는 것처럼 느끼게 됩니다.

<details>
  <summary>🔧 성능 개선 보기</summary>
  
  TODO
  
</details>

### 비밀번호 찾기 (Password Recovery)

#### V1 문제점:
- 비밀번호를 찾기 위해 SMTP를 통해 이메일을 직접 보내고, 이메일 전송 완료까지 기다린 후 메일 확인 창으로 이동하는 방식이었습니다. 이로 인해 사용자 경험이 저하되었고, 사용자에게는 이메일이 도착할 때까지 기다려야 하는 불편함이 있었습니다.

#### V2 해결점:
- 이메일 전송 작업을 비동기적으로 처리하기 위해, 메일 전용 워커 스레드를 몇 개를 파서 Redis의 `BRPOP`을 사용하여 작업 큐에서 대기하게 하였습니다. `BRPOP`은 블로킹되기 때문에 자원을 거의 소모하지 않으면서도 이메일 보내는 작업이 즉시 처리될 수 있게 합니다.
- 클라이언트가 비밀번호 찾기 요청을 하면 즉시 확인 창을 응답받을 수 있으며, 이메일 전송 작업은 백그라운드 워커가 비동기적으로 처리하여 사용자에게 더 나은 경험을 제공합니다.

<details>
  <summary>🔧 성능 개선 보기</summary>
  
  TODO
  
</details>





