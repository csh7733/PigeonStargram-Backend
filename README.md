# PigeonStargram Backend

## 📖 프로젝트 소개
PigeonStargram은 인스타그램의 기능들을 구현하는 것을 목표로 한 소셜 미디어 클론 프로젝트입니다. V1(Version1)에서는 기능 구현에만 초점을 맞춰 다양한 기능을 구현하였으며, V2(Version2)에서는 실제 테스트를 통해 V1과 동일한 기능을 유지하면서도 최적화에 중점을 두어 개발되었습니다.

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

- **DB 캐시 사용**: 데이터베이스 접근 시간을 줄이기 위해 Redis를 캐시로 활용하여 속도를 향상시켰습니다. 또한, Redis의 다양한 자료구조를 활용해 데이터를 효율적으로 관리하였습니다. 

- **분산 서버 환경**: 단일 서버 대신 분산 서버가 되면서 분산 환경에서 발생하는 문제, 특히 채팅 기능의 경우 Redis의 Pub/Sub를 활용해 해결하였습니다. 

- **비동기 및 병렬 처리**: 오래 걸리거나 자주 발생하는 작업들은 메시지 큐 또는 블로킹 팝을 통해 비동기적으로 병렬 처리하여 전체 처리 속도를 개선하였습니다. 

이 외에도 다양한 최적화 작업을 통해 성능을 향상시켰습니다.



