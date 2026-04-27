# Summary — web (Codex, WO-DISCOVERY-004)
- 작성: Codex CLI
- 작성 시각: 2026-04-23
- 스캔 대상: `C:\Users\user\Dev\ollanvin\web`
- 비고: 워크오더는 clone을 지시했지만 동일한 로컬 경로가 이미 존재해 그 트리를 직접 읽음

## 1. 헌법성 진술

- `CONSTITUTION.md`는 모든 프로젝트 웹/프라이버시/리걸/랜딩 페이지를 `ollanvin/web` 한 저장소에서 GitHub Pages로 운영하라고 규정한다.
- 무료 DNS를 기본으로 하고 Route 53 Hosted Zone은 원칙적으로 금지한다.
- API는 초기에는 AWS 기본 URL을 쓰고, custom API domain은 필요가 증명되기 전까지 금지한다.
- AWS 고정비 서비스는 금지하고, Lambda/DynamoDB/Cognito/S3/API Gateway 같은 low-cost serverless만 허용한다.
- 코워크는 헌법 위반 작업을 임의로 수행하면 안 되고, work order 보존 의무가 있다.
- 헌법은 전 프로젝트 공통 적용이며 우회·꼼수·임시면제를 금지한다.

## 2. 도구·역할·워커 관련 진술

- 이 저장소는 코드 앱이 아니라 cross-project public web + constitution host 역할을 맡는다.
- MyPhoneCheck 같은 개별 프로젝트는 shared constitution은 여기 두고, applied project docs는 각자 repo에 둬야 한다.
- `callcheck/docs/00_governance/myphonecheck-global-core-common-principles-draft.md`는 web repo가 단순 랜딩 페이지 저장소가 아니라 shared rule source로도 쓰인다는 증거다.

## 3. 마일스톤·진행상태·결정

- 루트에는 `CONSTITUTION.md`와 `index.html`이 있다.
- `callcheck/`에는 privacy와 MyPhoneCheck 공통원칙 초안이 있다.
- `cardspend/`에는 landing/legal/privacy/subscription/refund/support/cookies/EULA/data-safety/AI disclosure/children/SDK disclosure 등 공개 웹 문서 묶음이 있다.
- 따라서 web repo는 "헌법 + 프로젝트별 정적 공개면" 두 층으로 구성된다.

## 4. 미해결·의사결정 대기·이슈

- HTML 일부는 title 추출이 일관되지 않아 문서 제목만으로는 세부 구조 파악이 제한된다.
- constitution은 v1.1인데, MyPhoneCheck 공통원칙 초안은 2026-04-15 기준 draft 상태다. 어느 항목이 이미 project governance에 반영되었는지 동기화 관리가 계속 필요하다.
- CallCheck public web에는 privacy 외의 product/legal surface가 많지 않아, 상용 public footprint는 CardSpend 쪽이 더 정리되어 보인다.

## 5. 사업 정보

- web repo는 각 프로젝트의 public legal/compliance surface를 수용한다.
- CardSpend는 privacy, terms, subscription, refund, support, children, AI disclosure, third-party SDKs, data safety를 갖춘 상태라 상용 배포 준비형 웹 구성이 보인다.
- MyPhoneCheck/CallCheck 쪽은 현재 privacy와 common principles 초안 중심으로 상대적으로 얇다.

## 6. 기술 정보

- 전반적으로 GitHub Pages용 정적 HTML 구조다.
- 웹서버/CMS/동적 백엔드보다 정적 파일 중심 운영을 강제하는 게 핵심 기술 원칙이다.
- 폴더 단위 분리는 `callcheck/`, `cardspend/`처럼 product surface를 나누되, 인프라는 한 repo로 몰아넣는 방식이다.

## 7. 파일별 1줄 요약

- `CONSTITUTION.md` — OllanVin 공통 웹/인프라 헌법
- `index.html` — OllanVin 루트 랜딩
- `callcheck/privacy.html` — CallCheck privacy policy
- `callcheck/docs/00_governance/myphonecheck-global-core-common-principles-draft.md` — MyPhoneCheck 공통원칙 초안
- `cardspend/index.html` — CardSpend 랜딩
- `cardspend/privacy.html` — CardSpend privacy policy
- `cardspend/terms.html` — CardSpend terms
- `cardspend/legal.html` — CardSpend legal
- `cardspend/subscription.html` — CardSpend subscription info
- `cardspend/refund.html` — CardSpend refund policy
- `cardspend/support.html` — CardSpend support page
- `cardspend/cookies.html` — cookies policy
- `cardspend/eula.html` — EULA
- `cardspend/data-safety.html` — Google Play style data safety disclosure
- `cardspend/ai-disclosure.html` — AI disclosure
- `cardspend/children.html` — children-related policy
- `cardspend/third-party-sdks.html` — third-party SDK disclosure
- `CNAME` — custom domain mapping artifact

## 8. 발견 + 예상 밖

- 헌법이 비용·호스팅·DNS·API 도메인까지 직접 규정하고 있어서 단순 문서가 아니라 운영체제 역할을 한다.
- MyPhoneCheck common principles draft가 web repo에 있다는 점이 "shared law는 web, applied implementation은 app repo"라는 분리 원칙을 실제로 보여준다.
- CardSpend 쪽 public/legal 페이지 세트가 가장 성숙하다.

## 9. 비전 정독 권장 TOP 5

1. `CONSTITUTION.md` — 모든 repo 위에 놓이는 운영 헌법
2. `callcheck/docs/00_governance/myphonecheck-global-core-common-principles-draft.md` — MyPhoneCheck shared rule source
3. `callcheck/privacy.html` — CallCheck public compliance surface
4. `cardspend/index.html` — product-facing landing reference
5. `cardspend/privacy.html` + `terms.html` + `subscription.html` — 상용 public/legal 패키지 기준선

한 줄 판정: `web`은 "정적 public site repo"이면서 동시에 "공통 헌법 저장소" 역할을 겸하고 있고, MyPhoneCheck는 여기서 shared principles만 가져와 각자 repo에 적용하는 구조다.
