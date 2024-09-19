# [PigeonStargram](https://pigeonstargram.com)  Backend

## 📖 프로젝트 소개(https://pigeonstargram.com)
PigeonStargram은 인스타그램의 기능들을 구현하는 것을 목표로 한 소셜 미디어 클론 프로젝트입니다. [V1(Version1)](#-주요-기능-v1)에서는 기능 구현에만 초점을 맞춰 다양한 기능을 구현하였으며, [V2(Version2)](#-성능-개선-v2)에서는 실제 테스트를 통해 V1과 동일한 기능을 유지하면서도 최적화에 중점을 두어 개발되었습니다.

<details>
<summary>👤 테스트 용 계정</summary>
  <br></br>
  아래 계정을 사용하면 회원가입 없이 사용할 수 있습니다.

  - **ID**: test1@gmil.com  
  - **P.W**: test  
  - **Username**: PigeonKing  
  ---------------------------
  - **ID**: test2@gmil.com  
  - **P.W**: test  
  - **Username**: ChickMaster  
---------------------------
  - **ID**: test3@gmil.com  
  - **P.W**: test  
  - **Username**: WingedHero  
---------------------------
  - **ID**: test4@gmil.com  
  - **P.W**: test  
  - **Username**: RoosterLeader  
---------------------------
  - **ID**: test5@gmil.com  
  - **P.W**: test  
  - **Username**: FeatherQueen  
---------------------------
  - **ID**: test6@gmil.com  
  - **P.W**: test  
  - **Username**: EggGuardian  
---------------------------
  - **ID**: test7@gmil.com  
  - **P.W**: test  
  - **Username**: SkyWatcher  
---------------------------
  - **ID**: test8@gmil.com  
  - **P.W**: test  
  - **Username**: NestProtector  
---------------------------
  - **ID**: test9@gmil.com  
  - **P.W**: test  
  - **Username**: CluckCommander  
---------------------------
  - **ID**: test10@gmil.com  
  - **P.W**: test  
  - **Username**: DoveChampion  
---------------------------
  - **ID**: test11@gmil.com  
  - **P.W**: test  
  - **Username**: HatchMaster  
---------------------------
  - **ID**: test12@gmil.com  
  - **P.W**: test  
  - **Username**: FlightLord  
---------------------------
  - **ID**: test13@gmil.com  
  - **P.W**: test  
  - **Username**: ChickKing  
---------------------------
  - **ID**: test14@gmil.com  
  - **P.W**: test  
  - **Username**: FalconRider  
---------------------------
  - **ID**: test15@gmil.com  
  - **P.W**: test  
  - **Username**: HenQueen  

</details>

## 🛠 기술 스택
- **Server**: Java, Spring Boot, Spring Data JPA, WebSocket, OAuth 2.0, Spring Security, AWS S3, Gmail SMTP
- **Redis**: 캐시, 세션 관리, Pub/Sub, 메시지 큐, 자료구조 활용(Set, Sorted Set, Hash...)
- **Database**: MariaDB, MongoDB
- **Deployment**: Nginx, Travis CI, CodeDeploy
- **Testing**: JUnit, JMeter

## 📌 주요 기능 (V1)

### 유저

<details>
  <summary>유저 기능 (회원가입/로그인/로그아웃/계정 탈퇴, 비밀번호 찾기, 팔로우/팔로잉, 친구 추천)</summary>
  
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
        <img src="https://github.com/user-attachments/assets/3f10eb43-7666-414a-86be-3881d062452e" alt="친구 추천 이미지" width="45%" />
      </p>
      
    </details>

</details>


### 스토리

<details>
  <summary>스토리 기능 (업로드, 삭제, 목록 보기, 조회자 확인, 답장)</summary>
  
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

</details>


### 게시글

<details>
  <summary>게시글 기능 (작성/수정/삭제, 댓글 및 답글 작성/수정/삭제, 유저 태그(@), 좋아요/좋아요 취소, 프로필 및 타임라인)</summary>
  
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

</details>

  
### 채팅

<details>
  <summary>채팅 기능 (상대 선택, 실시간 채팅, 읽지 않은 메시지 수, 실시간 온라인 상태, 이전 채팅 불러오기)</summary>
  
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
  
  - **읽지 않은 메시지 수**: 내가 채팅방에 없을 때 상대방에게 메시지가 오면, 읽지 않은 메시지 수가 실시간으로 쌓이며 확인할 수 있습니다.
    
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

</details>

### 알림

<details>
  <summary>알림 기능 (실시간 알림, 알림 관리)</summary>
  
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

</details>


### 검색

<details>
  <summary>검색 기능 (유저 검색 및 자동완성, 검색 기록)</summary>
  
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

</details>


## ⚡ 성능 개선 (V2)

### 공통1 (캐시)

- **캐시** 도입으로 읽기 및 쓰기 작업 성능을 크게 개선했습니다.

  <details>
    <summary>🔧 문제점 및 성능 개선 보기</summary>
  
    #### V1 문제점:
    - 모든 읽기와 쓰기 작업이 데이터베이자스에서 이루어져 성능 저하와 지연이 발생했습니다.
  
    #### V2 해결점:
  
    - **단순 엔티티 캐시**(예: **Post 엔티티**와 같은 경우)는 **애노테이션 기반**으로 처리하여 캐싱 했고, **Set, Hash**와 같은 **복잡한 자료구조 캐시**는 수동으로 구현하여 성능을 최적화했습니다.
      <details>
      <summary>자세히 보기</summary>
      
        **단순 엔티티 캐시**(예: **Post 엔티티**)

        단순 엔티티 캐시는 애노테이션 기반으로 처리하여 캐싱합니다. 예를 들어, `Post` 엔티티의 경우 다음과 같이 `@Cacheable` 애노테이션을 사용하여 캐싱을 구현할 수 있습니다:
      
        ```java
        @Cacheable(value = POST,
                   key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).POST_ID + '_' + #postId")
        @Transactional(readOnly = true)
        public Post findById(Long postId) {
            return repository.findById(postId)
                     .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND_ID));
        }
        ```
        ### 복잡한 자료구조 캐시 (예: postId Set)

        복잡한 자료구조 캐시는 수동으로 구현하여 성능을 최적화합니다. 예를 들어, `postId Set`을 캐시할 때는 다음과 같이 구현할 수 있습니다:
        
        ```java
        @Transactional(readOnly = true)
        @Override
        public List<Long> findPostIdByUserId(Long userId) {
            // 사용자 ID를 기반으로 캐시 키를 생성합니다
            String cacheKey = cacheKeyGenerator(ALL_POST_IDS, USER_ID, userId.toString());
        
            // 캐시에서 게시물 ID 목록을 찾습니다.
            if (redisService.hasKey(cacheKey)) {
                return redisService.getSetAsLongListExcludeDummy(cacheKey);
            }
        
            // 캐시에 게시물 ID 목록이 없으면 데이터베이스에서 조회합니다.
            List<Long> postIds = getPostIdFromRepository(userId);
        
            // 조회된 게시물 ID 목록을 캐시에 저장하고 반환합니다.
            return redisService.cacheListToSetWithDummy(postIds, cacheKey, ONE_DAY_TTL);
        }
        ```
      </details>
  
    - **Redis 캐시**를 도입하여 **look-aside** 방식으로 읽기 작업의 속도를 개선하고, **write-back** 및 **write-through** 방식으로 쓰기 작업의 성능을 최적화했습니다.
    
      <details>
        <summary>자세히 보기</summary>
    
        자주 수정되지 않는 데이터는 쓰기 전략으로 **write-through** 방식을 사용하여 캐싱하였습니다. 예를 들어, `Post` 저장 시 다음과 같이 구현합니다:
    
        ```java
        @CachePut(value = POST,
                  key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).POST_ID + '_' + #post.id")
        @Transactional
        public Post save(Post post) {
            // 게시물을 데이터베이스에 저장합니다.
            Post savedPost = repository.save(post);
    
            Long postUserId = post.getUser().getId();
    
            // 사용자의 모든 게시물 ID 캐시에 반영합니다.
            String allPostIdsKey = cacheKeyGenerator(ALL_POST_IDS, USER_ID, postUserId.toString());
            if (redisService.hasKey(allPostIdsKey)) {
                redisService.addToSet(allPostIdsKey, post.getId(), ONE_DAY_TTL);
            }
            //... 추가로직
    
            return savedPost;
        }
        ```
    
        자주 수정되는 데이터는 쓰기 전략으로 **write-back** 방식을 사용하여 캐싱하였습니다. 예를 들어, 읽지 않은 채팅 수를 증가시킬 때는 다음과 같이 구현합니다:
    
        ```java
        @Transactional
        public Integer increaseUnReadChatCount(Long userId, Long toUserId) {
            // 캐시 키 생성
            String cacheKey = cacheKeyGenerator(UNREAD_CHAT_COUNT, USER_ID, userId.toString());
            String fieldKey = toUserId.toString();
    
            // 나중에 비동기적으로 DB에 flush 하도록 Write-back 작업을 위한 Sorted Set에 추가
            String dirtyKey = combineHashKeyAndFieldKey(cacheKey, fieldKey);
            redisService.pushToWriteBackSortedSet(dirtyKey);
    
            // 캐시에서 값 조회 및 증가
            if (redisService.hasFieldInHash(cacheKey, fieldKey)) {
                return incrementUnreadCountInCache(cacheKey, fieldKey, userId, toUserId);
            }
    
            // 캐시 미스 시 DB에서 값 조회 및 캐시에 저장
            return incrementUnreadCountInDbAndSaveInCache(userId, toUserId, cacheKey, fieldKey);
        }
        ```
    
      </details>

    
  - **Write-back 캐시 관리**: LRU 전략에 따라 일정 주기로 캐시에서 오래된 데이터를 데이터베이스에 플러시했습니다. 자주 조회되는 데이터는 캐시에서 계속 사용해 DB에 Flush되지 않고 캐시에만 있는 **starvation**이 발생할 수 있으므로, 체크포인트 시점에는 모든 데이터를 데이터베이스에 플러시하여 데이터베이스와의 일관성을 유지하게 했습니다.
  
    <details>
      <summary>자세히 보기</summary>
  
      일정 간격으로 캐시 데이터를 DB에 동기화하는 스케줄러 메서드는 다음과 같이 구현되어 있습니다. 각 주기마다 Redis Sorted Set에서 가장 오래된 데이터를 가져와 DB에 기록합니다. LRU를 구현하기 위해 날짜를 score로 사용한 Sorted Set을 활용했습니다.
  
      ```java
      @Scheduled(fixedRate = 10000)
      public void syncCacheToDB() {
          // 한 번에 가져올 Write-Back 작업의 개수 설정
          Integer writeBackBatchSize = (Integer) redisService.getValue(WRITE_BACK_BATCH_SIZE);
          if (writeBackBatchSize == null) {
              writeBackBatchSize = WRITE_BACK_BATCH_SIZE_INIT;
          }
  
          // Redis Sorted Set에서 하위 N개의 값을 가져옴
          List<String> sortedSetList =
                  redisService.getAndRemoveBottomNFromSortedSet(WRITE_BACK, writeBackBatchSize, String.class);
  
          if (sortedSetList.size() < writeBackBatchSize) {
              redisService.setValue(WRITE_BACK_BATCH_SIZE, WRITE_BACK_BATCH_SIZE_INIT);
          }
  
          // 각 가져온 키에 대해 DB 기록 처리
          for (String writeBackKey : sortedSetList) {
              writeBack(writeBackKey); // 각 키에 대해 writeBack 처리
          }
      }
      ```
  
      이때 해당 스케줄러는 여러 서버에서 동시에 발생하므로 `getAndRemoveBottomNFromSortedSet` 메서드는 atomic하게 동작해야 합니다. 이를 위해 Lua 스크립트를 사용하여 atomic하게 처리하였습니다.
  
      자주 수정되는 데이터는 LRU 전략에 따라 DB로의 flush가 일어나지 않을 수 있으므로, 체크포인트 시점을 더 길게 설정하여 주기적으로 부스팅을 수행합니다.
  
      ```java
      @Profile("write-back-boost")
      @Scheduled(fixedRate = 200000)
      public void syncAllCache() {
          // 부스팅 시점에는 DB로 flush하는 갯수를 증가시켜 일정 시간 내에 캐시에 dirty key들에 대해 모두 flush가 반드시 일어나게 합니다.
      }
      ```
  
    </details>


</details>

### 공통2 (자료구조 활용)

- **Redis 자료구조**를 활용하여 데이터 처리 속도를 개선했습니다.

  <details>
    <summary>🔧 문제점 및 성능 개선 보기</summary>
  
    #### V1 문제점:
    - DB 엔티티를 사용하여 데이터 처리 및 관리를 하였으나, 성능 최적화에 한계가 있었습니다.
  
    #### V2 해결점:
    - **Sorted Set**을 사용하여 검색 기록, 자동 완성, 읽지 않은 채팅 수 등을 효율적으로 관리했습니다.
    
      <details>
        <summary>자세히 보기</summary>
    
        **검색 기록 관리 예시**: 검색 기록을 Date를 score로 사용하여 Sorted Set을 이용하여 관리하는 방법은 다음과 같습니다.
    
        ```java
        @Transactional(readOnly = true)
        public List<ResponseSearchHistoryDto> getTopSearchHistory(Long userId) {
            String cacheKey = cacheKeyGenerator(SEARCH_HISTORY, USER_ID, userId.toString());
    
            // 캐시에서 검색 기록을 조회
            List<ResponseSearchHistoryDto> cachedHistory = getCachedSearchHistory(cacheKey);
            if (!cachedHistory.isEmpty()) {
                return cachedHistory;  // 캐시에 검색 기록이 있으면 반환 (캐시 히트)
            }
    
            // 캐시에 데이터가 없으면 DB에서 검색 기록 조회 (캐시 미스)
            User user = userService.getUserById(userId);
            List<SearchHistory> searchHistories = searchHistoryRepository.findTop5ByUserOrderByScoreDesc(user);
    
            // 검색 기록을 캐시에 저장
            saveSearchHistoryToCache(cacheKey, searchHistories);
    
            // DB에서 가져온 검색 기록을 DTO로 변환하여 반환
            return searchHistories.stream()
                    .map(SearchDtoConvertor::toResponseSearchHistoryDto)
                    .collect(Collectors.toList());
        }
        ```
  
      </details>

    
    - **Set**을 사용하여 팔로우/팔로잉 유저 Id 목록, 스토리 조회 유저 Id 목록 등을 빠른 접근이 가능하도록 했습니다.
    
      <details>
        <summary>자세히 보기</summary>
    
        **팔로워 유저 ID 목록 관리 예시**: `Set`을 활용하여 팔로워 유저 ID 목록을 캐싱하는 방법은 다음과 같습니다.
    
        ```java
        @Transactional(readOnly = true)
        public List<Long> findFollowerIds(Long userId) {
            String cacheKey = cacheKeyGenerator(FOLLOWER_IDS, USER_ID, userId.toString());
    
            // 캐시에서 가져오기
            if (redisService.hasKey(cacheKey)) {
                return redisService.getSetAsLongListExcludeDummy(cacheKey);
            }
    
            // 캐시 미스 : DB에서 가져오기
            return getFollowerIdsFromDB(userId, cacheKey);
        }
        ```
    
      </details>

    
    - **기타 자료구조**: 추가적인 Redis 자료구조(Hash 등)를 사용하여 특정 작업을 최적화했습니다.
    
      <details>
        <summary>자세히 보기</summary>
    
        **채팅방에서 유저 관리 예시**: 채팅방에서 사용자가 어느 채팅방에 있는지를 관리하기 위해 Redis Hash를 사용하는 방법은 다음과 같습니다.
    
        ```java
        @EventListener
        public void handleSessionConnected(SessionConnectEvent event) {
            StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
            Long userId = stringToLong(sha.getFirstNativeHeader("user-id"));
            Long partnerUserId = stringToLong(sha.getFirstNativeHeader("partner-user-id"));
    
            if (userId != null && partnerUserId != null) {
                // Redis Hash에 세션 ID와 사용자 ID를 매핑하여 저장
                // key: sessionId, value: 사용자 ID
                redisService.putValueInHash(SESSION_USER_MAP_KEY, sha.getSessionId(), userId);
    
                // 두 사용자가 같은 채팅방에서 대화를 나누기 위한 설정
                // WebSocket 연결 시, Redis Hash에 자신의 사용자 ID와 상대방의 사용자 ID를 매핑하여 저장
                // HashKey: 나의 userId, fieldKey: 상대방의 userId, value: 접속 상태(true)
                String activeUsersKey = ACTIVE_USERS_KEY_PREFIX + userId;
                redisService.putValueInHash(activeUsersKey, String.valueOf(partnerUserId), true);
    
                // 사용자 연결 이벤트 발생
                eventPublisher.publishEvent(new UserConnectEvent(this, userId, partnerUserId));
            }
        }
        ```
    
        이 코드는 WebSocket 연결 이벤트를 처리하여 사용자의 세션 ID와 사용자 ID를 Redis Hash에 저장하고, 두 사용자가 같은 채팅방에서 대화하고 있는지를 추적하기 위해 사용자 ID와 상대방의 사용자 ID를 Redis Hash에 매핑합니다.
    
      </details>


  </details>


### 타임라인

- **타임라인 캐시**를 도입하여 팔로워 게시글 조회 성능을 크게 개선했습니다.

  <details>
    <summary>🔧 문제점 및 성능 개선 보기</summary>
  
    #### V1 문제점:
    - 팔로워의 최신 게시글을 데이터베이스에서 검색하여 타임라인을 생성하는 방식으로 성능 저하가 발생했습니다.
  
    #### V2 해결점:
    - **타임라인 캐시 활용**: 팔로워의 최신 게시글을 데이터베이스에서 가져오는 대신 타임라인 캐시에서 가져오도록 변경했습니다. 특정 유저가 게시글을 작성하면, 그 유저를 팔로우하는 모든 팔로워의 타임라인에 해당 게시글의 ID를 저장합니다. 이렇게 함으로써, 타임라인 조회 시 데이터베이스에 접근하는 대신 캐시에서 게시글 ID를 종합하여 가져오게 됩니다.
      <details>
        <summary>자세히 보기</summary>
        
        게시글을 생성할 때 팔로워의 타임라인(sortedSet)에 작성한 게시글 ID를 추가합니다:
        ```java
        @Transactional
        public Long createPost(CreatePostDto dto) {
            User loginUser = userService.getUserById(dto.getLoginUserId());
            Post post = PostFactory.createPost(dto, loginUser);
            // 중간 로직 생략...
    
            // 타임라인 처리
            addPostToFollowersTimeline(loginUser, savedPost);
            return savedPost.getId();
        }
    
        private void addPostToFollowersTimeline(User loginUser, Post savedPost) {
            // 팔로워 수가 많지 않은 경우 팔로워 타임라인에 게시물 추가
            // 유명인일 경우에는 해당 로직을 실행하지 않음
            if (!followService.isFamousUser(loginUser.getId())) {
                // 팔로워 목록 가져오기
                List<Long> followerIds = followService.getFollowerIds(loginUser.getId());
    
                // 현재 시간을 타임스탬프로 사용
                Double currentTime = getCurrentTimeMillis();
    
                // 각 팔로워의 타임라인에 게시물 추가
                followerIds.forEach(followerId -> {
                    String timelineKey = cacheKeyGenerator(TIMELINE, USER_ID, followerId.toString());
                    redisService.addToSortedSet(timelineKey, currentTime, savedPost.getId());
                });
            }
        }
        ```
    
        이제 타임라인을 가져올 때는 타임라인 캐시에서 24시간 이내의 게시글만 가져옵니다:
        ```java
        private List<ResponsePostDto> getUnFamousFollowingsRecentPosts(Long userId) {
            // Redis에서 해당 사용자의 타임라인 키 생성
            String timelineKey = cacheKeyGenerator(TIMELINE, USER_ID, userId.toString());
    
            // 24시간 이전의 시간
            Double expirationTimeMillis = getTimeMillis(getExpirationTime());
    
            // Redis에서 타임라인에 저장된 모든 게시물 ID와 스코어(타임스탬프)를 가져옴
            List<Long> validPostIds = redisService.getAllFromSortedSetWithScores(timelineKey, Long.class).stream()
                    .filter(scoreWithValue -> isValidPost(scoreWithValue, timelineKey, expirationTimeMillis)) // 24시간 이내
                    .map(ScoreWithValue::getValue)
                    .filter(postId -> !redisService.isMemberOfSet(UPLOADING_POSTS_SET, postId))
                    .collect(Collectors.toList());
    
            // 필터링된 게시물 ID들로 실제 게시물 데이터 가져오기
            return validPostIds.stream().map(postService::getCombinedPost).collect(Collectors.toList());
        }
        ```
    
      </details>

  
    - **유명인 처리**: 유명인이 게시글을 작성할 때는 타임라인 캐시에 게시글을 추가하는 작업이 부하를 증가시킬 수 있어, 유명인 게시글은 유명인의 게시물Id 목록에서 직접 최근 게시물을 조회한 후 가져옵니다
      <details>
        <summary>자세히 보기</summary>
        
        유명인의 최신 게시글을 가져오는 방법은 다음과 같습니다:
        ```java
        private List<ResponsePostDto> getFamousFollowingsRecentPosts(Long userId) {
            // 유명인은 타임라인 캐시에서가 아니라 최신 게시물들을 직접 조회
            return followService.findFollowings(userId).stream()
                    .map(ResponseFollowerDto::getId)
                    .filter(followService::isFamousUser) // 유명인만 필터링
                    .flatMap(followingId -> postService.getRecentPostsByUser(followingId).stream())
                    .collect(Collectors.toList());
        }
        ```
    
      </details>

  
  - **타임라인 병합**: 유저의 타임라인은 타임라인 캐시와 유명인 게시글을 직접 가져온 결과를 병합하여 제공, 이를 통해 조회 성능을 개선하고 데이터베이스 부하를 줄입니다.
    <details>
      <summary>자세히 보기</summary>
  
      유저의 팔로우 대상 게시물들을 병합하여 최신 게시글을 가져오는 방법은 다음과 같습니다:
      ```java
      public List<ResponsePostDto> getFollowingUsersRecentPosts(Long userId) {
          // 1. 유명인 팔로우 대상의 게시물 가져오기
          List<ResponsePostDto> famousPosts = getFamousFollowingsRecentPosts(userId);
  
          // 2. 비유명인 팔로우 대상의 게시물 가져오기
          List<ResponsePostDto> unFamousPosts = getUnFamousFollowingsRecentPosts(userId);
  
          // 3. 두 리스트를 합치고, 시간 기준으로 정렬하여 반환
          return Stream.concat(famousPosts.stream(), unFamousPosts.stream())
                  .sorted(Comparator.comparing(
                          post -> post.getProfile().getTime(),
                          getReverseOrderComparator()))
                  .collect(Collectors.toList());
      }
      ```
  
    </details>

  
  </details>

### 채팅

- **NoSQL**과 페이징을 통해 채팅 성능을 최적화하고 **Redis Pub/Sub**를 활용하여 분산 서버 환경 문제를 해결했습니다.

  <details>
    <summary>🔧 문제점 및 성능 개선 보기</summary>
  
    #### V1 문제점:
    - V1에서는 단일 서버와 RDBMS를 사용하여 모든 채팅 기록을 불러올 때 모든 내용을 한 번에 불러왔습니다. 이로 인해 데이터베이스의 부하가 증가하고, 서버와 클라이언트 간의 전송 데이터 양이 많아졌습니다.
    - 단일 서버에서는 웹소켓을 통해 실시간 채팅이 가능했지만, 분산 서버 환경에서는 클라이언트가 연결된 서버가 다를 수 있어 웹소켓 연결 문제가 발생했습니다.
  
    #### V2 해결점:
    - **데이터베이스 최적화**: NoSQL 데이터베이스를 사용하여 채팅 기록의 조회 속도를 개선하였습니다. 모든 채팅 기록을 불러오는 대신, 날짜를 기반으로 페이징 처리하여 10개씩 불러오도록 하여 서버와 클라이언트 간의 전송 데이터 양을 줄였습니다.
      <details>
        <summary>자세히 보기</summary>
        
        먼저 `Chat` 엔티티를 MongoDB의 Document로 전환합니다:
        ```java
        public interface ChatRepository extends MongoRepository<Chat, String> {
            List<Chat> findChatsBefore(Long user1Id, Long user2Id, LocalDateTime lastFetchedDateTime, Pageable pageable);
        }
        ```
    
        그리고 프론트에서 현재 가지고 있는 가장 오래된 채팅의 날짜를 기준으로 최대 10개의 채팅 데이터들만 가져오도록 합니다:
        ```java
        @Transactional(readOnly = true)
        public ResponseChatHistoriesDto getUserChats(GetUserChatsDto dto, String lastFetchedTime, Integer size) {
            Long user1Id = dto.getUser1Id();
            Long user2Id = dto.getUser2Id();
    
            // 마지막 조회 시간을 LocalDateTime 형식으로 변환
            LocalDateTime lastFetchedDateTime = getChatLocalDateTime(lastFetchedTime);
    
            // 페이지 정보 생성 (size + 1개의 메시지를 내림차순으로 가져옴)
            Pageable pageable = createPageable(size + 1);
    
            // 데이터베이스에서 size + 1개의 메시지 조회
            List<Chat> chats = chatRepository.findChatsBefore(user1Id, user2Id, lastFetchedDateTime, pageable);
    
            // size+1개 중에서 size개만 반환하고, size+1번째 데이터가 있으면 hasMoreChat을 true로 설정
            Boolean hasMoreChat = chats.size() > size;
            List<Chat> limitedChats = chats.subList(0, Math.min(chats.size(), size));
    
            // 메시지를 DTO로 변환
            List<ResponseChatHistoryDto> chatHistoryDtos = toChatHistoryDtos(limitedChats);
    
            return new ResponseChatHistoriesDto(chatHistoryDtos, hasMoreChat);
        }
        ```
    
        `ChatRepository`에서 `findChatsBefore` 메서드를 통해 페이징 처리된 채팅 기록을 조회하고, 프론트엔드에 필요한 데이터만 전송하여 성능을 최적화합니다.
    
      </details>

  
    - **웹소켓과 분산 서버**: 단일 서버에서는 웹소켓을 직접 사용하여 실시간 채팅을 처리하였지만, 분산 서버 환경에서는 Redis Pub/Sub를 사용하여 서로 다른 서버에 연결된 클라이언트 간의 메시지 전달 문제를 해결하였습니다.
      <details>
        <summary>자세히 보기</summary>
        
        각 서버는 서버 시작 시 Redis 채널을 구독합니다:
        ```java
        @PostConstruct
        public void init() {
            redisService.subscribeToPattern("chat.*.*", chatMessageListener);  // 채팅 메시지 구독
            // 추가 로직
        }
        ```
    
        메시지 리스너는 Redis에서 발행된 메시지를 수신하고, 웹소켓을 통해 클라이언트에 전송합니다:
        ```java
        public class RedisChatMessageListener implements MessageListener {
    
            private final RedisService redisService;
            private final SimpMessagingTemplate messagingTemplate;
    
            @Override
            public void onMessage(Message message, byte[] pattern) {
                NewChatDto chatMessage = redisService.deserializeMessage(message.getBody(), NewChatDto.class);
    
                long user1Id = Math.min(chatMessage.getFrom(), chatMessage.getTo());
                long user2Id = Math.max(chatMessage.getFrom(), chatMessage.getTo());
    
                // 채팅방 경로를 생성하고 메시지를 해당 경로로 전송
                String destination = "/topic/chat/" + user1Id + "/" + user2Id;
                messagingTemplate.convertAndSend(destination, chatMessage);
            }
        }
        ```
    
        이제 웹소켓으로 직접 메시지를 보내는 대신, Redis 채널에 메시지를 발행합니다:
        ```java
        private void processChatMessage(NewChatDto chatMessage, Long from, Long to) {
            // 메시지의 시간을 설정하고 저장
            chatMessage.setTime(getCurrentFormattedTime());
            chatService.save(chatMessage);
    
            // Redis 채널에 메시지 발행
            String channel = getChatChannelName(from, to);
            redisService.publishMessage(channel, chatMessage);
        }
        ```
    
        이 방식으로 메시지가 Redis를 통해 전달되고, 각 서버는 Redis를 통해 받은 메시지를 웹소켓으로 클라이언트에 전송하여 분산 서버 환경에서도 실시간 채팅을 처리합니다.
    
      </details>
  
  </details>

### 알림

- **비동기 처리**, **캐싱**, 및 **Batch 처리**를 도입하여 알림 작업의 효율성을 극대화하고, **Redis Pub/Sub**로 분산 서버 환경을 지원했습니다.

  <details>
    <summary>🔧 문제점 및 성능 개선 보기</summary>
  
    #### V1 문제점:
    - V1에서는 알림 작업을 처리하기 위해 별도의 비동기 처리 없이 직접 데이터베이스에 접근하였습니다. 이로 인해 자원을 많이 소모하고, 팔로워가 많은 유명인의 게시글 작성 시 동일한 알림 데이터가 대량으로 생성되어 데이터베이스에 과도한 부하가 발생했습니다.
  
    #### V2 해결점:
    - **비동기 및 병렬 처리**: 알림 전용 워커 스레드를 생성하고, `BRPOP`을 활용하여 메시지 큐에서 블로킹 방식으로 비동기적으로 알림 작업을 처리합니다. 이로 인해 자원을 최소화하면서 여러 서버에서 분산하여 알림 작업을 처리할 수 있게 됩니다.
      <details>
        <summary>자세히 보기</summary>
    
        알림 전송 워커를 비동기적으로 처리하기 위한 설정 방법은 다음과 같습니다:
        ```java
        /**
         * 애플리케이션이 준비되면 알림 전송 워커를 실행하는 메서드입니다.
         */
        @EventListener(ApplicationReadyEvent.class)
        public void runNotificationWorker() {
            ExecutorService executorService =
                    Executors.newFixedThreadPool(NOTIFICATION_WORKER_THREAD_NUM);
    
            for (int i = 0; i < NOTIFICATION_WORKER_THREAD_NUM; i++) {
                executorService.submit(notificationWorker::acceptTask);
            }
        }
    
        public void acceptTask() {
            while (true) {
                try {
                    // Redis 작업 큐에서 Blocking Pop 방식으로 작업을 가져옴
                    Object task = redisService.popTask(NOTIFICATION_QUEUE);
                    NotificationBatchDto batch = (NotificationBatchDto) task;
    
                    // 가져온 작업이 유효하다면 작업 수행
                    if (batch != null) {
                        work(batch);
                    }
                } catch (Exception e) {
                    // 예외 처리
                }
            }
        }
        ```
    
      </details>

    - **캐싱 및 데이터베이스 접근 최적화**: 알림 데이터를 `content`와 매핑 정보(수신인, 발신인)로 분리하여, 공통 정보인 `content`는 캐싱하고 매핑 정보만 데이터베이스에 저장하여 동일한 알림 내용이 중복 저장되지 않도록 합니다.
      <details>
        <summary>자세히 보기</summary>
    
        `NotificationV2`에서는 `content`를 별도의 엔티티로 분리하여 공통적인 알림 내용은 데이터베이스에 한번만 저장되도록 하고, 매핑 정보만 별도로 저장합니다:
        ```java
        public class NotificationV2 extends BaseTimeEntity { 
        
            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;
        
            @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "content_id")
            private NotificationContent content; // 알림의 상세 내용과 연관된 엔티티
        
            private Long recipientId;
            private Boolean isRead; // 알림의 읽음 여부 (true: 읽음, false: 안 읽음)
        
            public void setRead(Boolean read) {
                isRead = read;
            }
        }
        ```
    
        공통적인 `content`는 캐시를 활용하여 데이터베이스 접근을 최소화합니다:
        ```java
        @Cacheable(value = NOTIFICATION_CONTENT,
                   key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).NOTIFICATION_CONTENT_ID + '_' + #contentId")
        @Transactional(readOnly = true)
        public NotificationContent findContentById(Long contentId) {
            return notificationContentRepository.findById(contentId)
                    .orElseThrow(() -> new NotificationNotFoundException(NOTIFICATION_CONTENT_NOT_FOUND_ID));
        }
        ```
    
      </details>

  
    - **Batch 처리**: 알림 생성 시 `batch size`를 설정하여 일정 크기만큼 한 번에 알림을 저장함으로써 데이터베이스 접근을 최적화하였습니다. 적절한 `threshold`를 설정하여 과도한 부하를 방지하였습니다.
      <details>
        <summary>자세히 보기</summary>
    
        `NotificationSplitWorker`는 알림을 분할하는 작업을 수행하고, 분할된 알림 작업을 `NotificationWorker`에 위임합니다.
    
        애플리케이션이 준비되면 알림 분할 워커를 실행하는 메서드:
        ```java
        @EventListener(ApplicationReadyEvent.class)
        public void runNotificationSplitWorker() {
            ExecutorService executorService =
                    Executors.newFixedThreadPool(NOTIFICATION_SPLIT_WORKER_THREAD_NUM);
    
            for (int i = 0; i < NOTIFICATION_SPLIT_WORKER_THREAD_NUM; i++) {
                executorService.submit(notificationSplitWorker::acceptTask);
            }
        }
        ```
    
        알림 분할 작업을 처리하는 메서드:
        ```java
        @Transactional
        public void acceptTask() {
            while (true) {
                try {
                    // Local 작업큐에서 Blocking Pop 방식으로 가져옴
                    NotificationSplitDto beforeSplit = beforeSplitQueue.take();
    
                    // 가져온 작업이 유효하다면 알림을 BATCH_SIZE로 분할
                    work(beforeSplit);
                } catch (Exception e) {
                    log.error("알림 분할 작업 처리 중 예외가 발생했습니다.", e);
                }
            }
        }
        ```
    
        알림을 분할하고, 분할된 작업을 `NotificationWorker`에게 위임하는 메서드:
        ```java
        @Override
        public void work(Object task) {
            // 중간 로직 생략
            for (int i = 0; i < iterationMax; i++) {
                int leftIndex = i * BATCH_SIZE;
                int rightIndex = (i == iterationMax - 1) ? recipientIds.size() : (i + 1) * BATCH_SIZE;
                // 중간 로직 생략
                // BATCH_SIZE로 분할된 알림을 NotificationWorker에게 일을 위임한다
                notificationWorker.enqueue(notificationBatchDto);
            }
        }
        ```
    
        `NotificationWorker`는 분할된 알림을 한 번에 DB에 저장한 후 웹소켓을 통해 실시간으로 전송합니다:
        ```java
        notificationCrudService.saveAll(notifications);
        // 이후 notification을 publish
        ```
    
      </details>

  
    - **분산 서버 처리**: 쪼개진 알림은 알림 워커를 통해 Redis Pub/Sub를 사용하여 분산 서버에 웹소켓을 통해 전달하였습니다. 이를 통해 실시간으로 알림을 여러 서버에 분산하여 전달할 수 있습니다.
      <details>
        <summary>자세히 보기</summary>
    
        각 서버는 서버 시작 시 Redis 채널을 구독합니다:
        ```java
        @PostConstruct
        public void init() {
            redisService.subscribeToPattern("notification.*", notificationListener);  // 알림 구독
            // 추가 로직
        }
        ```
    
        메시지 리스너는 Redis에서 발행된 메시지를 수신하고, 웹소켓을 통해 클라이언트에 전송합니다:
        ```java
        public class RedisNotificationListener implements MessageListener {
    
            private final RedisService redisService;
            private final SimpMessagingTemplate messagingTemplate;
    
            @Override
            public void onMessage(Message message, byte[] pattern) {
                ResponseNotificationDto messageDto = redisService.deserializeMessage(message.getBody(), ResponseNotificationDto.class);
    
                // 알림을 보낼 경로를 설정하고 메시지를 해당 경로로 전송
                String destination = "/topic/notification/" + messageDto.getTargetUserId();
                messagingTemplate.convertAndSend(destination, messageDto);
            }
        }
        ```
    
        이제 웹소켓으로 직접 메시지를 보내는 대신, Redis 채널에 메시지를 발행합니다.
      
        이 방식으로 메시지가 Redis를 통해 전달되고, 각 서버는 Redis를 통해 받은 메시지를 웹소켓으로 클라이언트에 전송하여 분산 서버 환경에서도 실시간 알림을 처리합니다.
    
      </details>
  
  </details>



### 게시글 작성

- **비동기 및 병렬 처리**를 도입하여 이미지 업로드 시간을 단축하고, **즉시 포스트 ID 반환**으로 사용자 경험을 개선했습니다.

  <details>
    <summary>🔧 문제점 및 성능 개선 보기</summary>
  
    #### V1 문제점:
    - 게시글 작성 시 이미지가 여러 개인 경우, 클라이언트는 서버에 이미지를 업로드하고, **서버는 이미지 파일들을 S3에 저장하며**, 포스트 정보를 데이터베이스에 매핑하여 결과를 받아오는 데 시간이 걸렸습니다. 이로 인해 전체 게시글 작성 시간이 길어져 사용자 경험이 저하되었습니다.
  
    #### V2 해결점:
    - **이미지 업로드 비동기 처리**: 게시글 작성 시 이미지를 서버에 업로드할 때, 이미지는 비동기 및 병렬적으로 처리됩니다. 클라이언트에게는 즉시 포스트 ID만 반환되며, 이미지 업로드는 백그라운드에서 진행됩니다. 이를 통해 서버가 이미지를 S3에 저장하는 시간을 줄이게 됩니다.
      <details>
        <summary>자세히 보기</summary>
    
        ```java
        @PostMapping
        public Long createPosts(@LoginUser SessionUser loginUser,
                                @ModelAttribute RequestCreatePostDto request,
                                @RequestPart(value = "images", required = false) List<MultipartFile> imagesFile) {
            // 이미지 파일 저장 처리 (비동기)
            FileUploadResultDto savedFileDto = fileService.saveFiles(imagesFile);
    
            // 이미지 업로드가 진행중이지만, 이미지 이름만 가져와서 바로 다음 단계로 진행
            List<String> images = savedFileDto.getFileNames();
            String fieldKey = savedFileDto.getFieldKey();
            CreatePostDto dto = toCreatePostDto(request, loginUser, images, fieldKey);
    
            return postService.createPost(dto);
        }
    
        public FileUploadResultDto saveFiles(List<MultipartFile> files) {
            // 파일 리스트가 null인 경우 빈 결과를 반환
            if (files == null) return new FileUploadResultDto();
    
            // 고유한 필드 키 생성
            String fieldKey = generateFieldKey();
            int totalFiles = files.size();
            List<String> fileNames = new ArrayList<>();
    
            // 각 파일에 대해 비동기 업로드 처리
            for (MultipartFile file : files) {
                String fileName = getFilename(file);
                fileNames.add(fileName);
                // 비동기 방식으로 파일 업로드
                fileUploadWorker.uploadFileAsync(file, fileName, fieldKey, totalFiles);
            }
    
            // 업로드된 파일들의 이름과 필드 키를 포함한 결과 반환
            return new FileUploadResultDto(fileNames, fieldKey);
        }
    
        ```
    
        게시글 작성자는 이미지 업로드 중에는 자신의 로컬 이미지를 통해 임시로 프론트에서 게시글을 작성합니다. 이렇게 하면 작성자는 마치 서버에서 이미지를 받아와 게시글이 작성된 것처럼 빠르게 느낄 수 있으며, 동시에 ID를 알고 있기 때문에 임시 게시글에 댓글, 좋아요 등의 작업도 문제 없이 즉시 수행할 수 있습니다.
    
      </details>

    - **업로드중 접근 제한**: 다른 사용자는 게시글이 완전히 업로드된 후에만 볼 수 있도록 하여 업로드 중에 봐서 생기는 문제가 없도록 합니다.
      <details>
        <summary>자세히 보기</summary>
    
        이미지 파일을 업로드하는 워커는 처음에는 이미지 업로드를 수행하는 포스트 ID를 모르기 때문에 임시로 UUID를 통해 포스트 ID를 갖습니다:
        ```java
        String fieldKey = generateFieldKey();
        ```
    
        이후, `PostService`의 `createPost` 메서드에서는 post를 DB에 저장한 후 포스트 ID를 Redis 해시에 저장하여 워커가 UUID로 postId를 알 수 있게 합니다:
        ```java
        redisService.putValueInHash(UPLOADING_POSTS_HASH, dto.getFieldKey(), savedPost.getId(), 5 * ONE_MINUTE_TTL);
        ```
    
        DB에 저장이 끝났지만 이미지 업로드가 아직 진행 중일 경우(대부분의 경우), 업로드 중인 게시물 ID를 Redis Set에 추가하여 다른 사용자가 이 게시물을 조회하지 못하도록 합니다:
        ```java
        // 이미지 업로드가 끝나지 않았을 경우
        if (!fileUploadWorker.isUploadComplete(dto.getFieldKey())) {
            // 업로드 중인 게시물 ID를 Set에 추가
            redisService.addToSet(UPLOADING_POSTS_SET, savedPost.getId(), 5 * ONE_MINUTE_TTL);
        }
        ```
    
        이미지 파일 업로드 워커는 모든 파일이 업로드되었다면, Redis에서 `fieldKey`에 해당하는 포스트 ID를 가져와 업로드 중인 게시물 Set에서 제거합니다:
        ```java
        // 남은 파일이 0이면 모든 파일이 업로드된 것으로 간주
        if (remainingFiles == 0) {
            // Redis에서 fieldKey에 해당하는 postId를 가져옴
            Long postId = redisService.getValueFromHash(UPLOADING_POSTS_HASH, fieldKey, Long.class);
    
            if (postId != null) {
                // Redis의 업로드 진행 Set에서 해당 postId 제거
                redisService.removeFromSet(UPLOADING_POSTS_SET, postId);
            }
    
            // 모든 파일이 업로드 완료되었으면 진행 상황 맵에서 제거하고 완료 상태로 설정
            uploadProgressMap.remove(fieldKey);
            uploadCompletionMap.put(fieldKey, true);
        }
        ```
    
        이렇게 함으로써, 모든 파일의 업로드가 완료된 후에만 다른 사용자들이 해당 게시물을 조회할 수 있게 됩니다.
    
      </details>

  
  </details>

### 비밀번호 찾기

- **워커 쓰레드와 작업 메시지 큐**를 통해 이메일 전송을 비동기적으로 백그라운드에서 처리했습니다.
  
  <details>
    <summary>🔧 문제점 및 성능 개선 보기</summary>
  
    #### V1 문제점:
    - 비밀번호를 찾기 위해 SMTP를 통해 이메일을 직접 보내고, 이메일 전송 완료까지 기다린 후 메일 확인 창으로 이동하는 방식이었습니다. 이로 인해 사용자 경험이 저하되었고, 사용자에게는 이메일이 도착할 때까지 기다려야 하는 불편함이 있었습니다.
  
    #### V2 해결점:
    - **비동기 이메일 전송**: 이메일 전송 작업을 비동기적으로 처리하기 위해, 메일 전용 워커 스레드들이 Redis의 `BRPOP`을 사용하여 작업 큐에서 대기하게 하였습니다. `BRPOP`은 블로킹되기 때문에 자원을 거의 소모하지 않으면서도 이메일 보내는 작업이 즉시 처리될 수 있게 합니다.
      <details>
        <summary>자세히 보기</summary>
    
        **초기화**:
        ```java
        /**
         * 애플리케이션이 준비되면 메일 전송 워커를 실행하는 메서드입니다.
         */
        @EventListener(ApplicationReadyEvent.class)
        public void runMailSenderWorker() {
            ExecutorService executorService =
                    Executors.newFixedThreadPool(MAIL_SENDER_WORKER_THREAD_NUM);
    
            for (int i = 0; i < MAIL_SENDER_WORKER_THREAD_NUM; i++) {
                executorService.submit(mailSenderWorker::processTasks);
            }
        }
        ```
    
        **워커**:
        ```java
        /**
         * Redis 큐에서 메일 전송 작업을 지속적으로 처리하는 메서드입니다.
         * 이 메서드는 블로킹 팝을 사용해 Redis 작업 큐에서 메일 전송 작업을 가져오고,
         * 작업이 있으면 메일을 전송합니다. Redis 연결 문제 또는 타임아웃 발생 시 적절히 재시도합니다.
         */
        public void processTasks() {
            while (true) {
                try {
                    // Redis 큐에서 블로킹 방식으로 메일 전송 작업을 가져옴
                    MailTask task = (MailTask) redisService.popTask(MAIL_QUEUE);
                    if (task != null) {
                        // 가져온 작업이 유효하다면 메일을 전송
                        sendEmail(task);
                    }
                } catch (QueryTimeoutException e) {
                    // Lettuce 클라이언트의 기본 타임아웃(1분)에 도달하면 재연결 시도
                }
            }
        }
        ```
    
        이 설정을 통해 이메일 전송 작업을 비동기적으로 처리하며, 블로킹 방식으로 Redis 큐에서 작업을 대기함으로써 자원 소모를 최소화하고, 이메일 전송 작업을 효율적으로 처리할 수 있습니다.
    
      </details>

  
  </details>

### 검색

- **자동완성 개선**: 자동완성 성능을 개선하기 위해 디바운싱 기법을 적용해 HTTP 요청을 줄이고, 추천 키워드를 Sorted Set을 통해 처리하여 검색 완성도를 높였습니다.

  <details>
    <summary>🔧 문제점 및 성능 개선 보기</summary>
  
    #### V1 문제점:
    자동완성을 위해 너무 많은 HTTP 요청이 발생하였고, 자동완성을 위한 추천 키워드들을 모두 데이터베이스에서 저장하고 가져와야 했기 때문에 성능이 저하되고 비효율적이었습니다.

    #### V2 해결점:
   - **디바운싱 기법**: 프론트엔드에서 디바운싱 기법을 적용하여 사용자가 입력한 후 일정 시간 동안 입력이 없으면 HTTP 요청을 한 번만 보내게 하였습니다. 이를 통해 HTTP 요청 횟수를 줄이고 성능을 개선했습니다.
 
   - **Redis Sorted Set 사용**: 추천 키워드는 데이터베이스에서 가져오는 대신, Redis의 Sorted Set을 활용하여 처리합니다. 사용자가 검색할 때마다 해당 검색 키워드에 대해 모든 접두사(prefix)에게 점수를 추가합니다. 이렇게 하면 사용자의 검색 패턴에 따라 자동완성의 완성도가 점진적으로 높아지며, 추천 키워드의 성능이 개선됩니다.

      <details>
        <summary>자세히 보기</summary>

        **컨트롤러 (검색했을때)**:
        ```java
        /**
         * 검색어를 저장한 후, 사용자 검색 결과를 반환합니다.
         *
         * @param loginUser 현재 로그인한 사용자
         * @param query     검색어
         * @return 검색된 사용자 정보 리스트
         */
        @GetMapping
        public List<ResponseUserInfoDto> saveSearchInfosAndGetResults(@LoginUser SessionUser loginUser,
                                                                      @RequestParam String query) {
            Long userId = loginUser.getId();

            searchService.saveSearchHistory(buildSaveSearchHistoryDto(userId, query));

            return searchService.getUserSearchResults(query);
        }
        ```

        **서비스 (검색했을때)**:
        ```java
        @Transactional
        public void saveSearchHistory(SaveSearchHistoryDto dto) {
            // 중간 로직 생략
            // 검색어 점수 업데이트 (검색어 자동 완성을 위한 점수 증가)
            updateSearchTermScores(dto.getSearchQuery());
        }
    
        private void updateSearchTermScores(String term) {
            // 검색어의 접두사(prefix) 목록 생성
            List<String> prefixes = generatePrefixes(term);
    
            // 각 prefix를 기준으로 해당 검색어의 점수를 업데이트
            prefixes.forEach(prefix -> {
                // Redis에 저장할 캐시 키 생성
                String searchKey = cacheKeyGenerator(SEARCH_TERM_SCORES, prefix);
                // prefix를 키로 사용하고, 검색어를 값으로 하여 점수를 1 증가시킴
                redisService.incrementScoreInSortedSet(searchKey, term, 1.0);
            });
        }
        ```

        **자동완성 결과 가져오기**:
        이제 이 결과를 바탕으로 자동완성은 미완성 검색어 HTTP 요청이 왔을 때 score 값이 높은 상위 term들을 보내줍니다.
        
        ```java
        @GetMapping("/auto-complete")
        public List<ResponseTopSearchDto> getAutoCompleteResults(@LoginUser SessionUser loginUser,
                                                                 @RequestParam String prefix) {
            return searchService.getTopSearchTermsByPrefix(prefix);
        }
        ```

      </details>
  </details>

