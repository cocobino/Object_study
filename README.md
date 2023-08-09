# 유연한 설계

## 개방 폐쇠 원칙
`소프트웨어 개체는 확장에대해 열려있어야 하고, 수정에 대해서는 닫혀 있어야한다.`

## 컴파일 타임 의존성을 고정시키고 런타임 의존성을 변경
- 개방 폐쇄 원칙은 컴파일타임 의존성에 관한 이야기
- 재사용 가능한 설게에서 런타임 의존성, 컴파일 의존성은 서로 다른 구조를 가짐

![img.png](img.png)

- 개방 폐쇄 원칙을 수용하는 코드는 컴파일타임 의존성을 수정하지 않고 런타임 의존성을 쉽게 변경 가능함

![img_1.png](img_1.png)

## 추상화가 핵심
- 개방 폐쇠 원칙을 따르는 가장 쉬운 방법은 추상화를 사용하는 것

```java
public abstract class DiscountPolicy {
    private List<DiscountCondition> conditions = new ArrayList<>();
    
    public DiscountPolicy(DiscountCondition ... conditions) {
        this.conditions = Arrays.asList(conditions);
    }
    
    public Money calculateDiscountAmount(Screening screening) {
        for(DiscountCondition each : conditions) {
            if(each.isSatisfiedBy(screening)) {
                return getDiscountAmount(screening);
            }
        }
        
        return screening.getMovieFee();
    }
    
    abstract protected Money getDiscountAmount(Screening screening);
}
```

- `DiscountPolicy` 는 `calculateDiscountAmount` 메서드와 조건을 만족할때 할인된 `getDiscountAmount`로 구성됨
- 변하지 않는 부분은 할인 여부를 판단, 변하는 붑누은 할인 요금을 게산하는 부분 → 상속은 생략된 부분을 구체화 하면서 정책을 확장할 수 있게 됨
- 개방폐쇄 원칙을 따르는 코드를 작성하는데 중요한 점은 **추상화**

## 생성 사용 분리
- `Movie`가 오직 `DiscountPolicy` 의존하기 위해선 구체 클래스의 인스턴스를 생성하면 안됨
- 결합도가 높아질수록 개방 폐쇠 원칙을 따르는 구조를 설계하기 어려워짐

```java

public class Movie {
    private DiscountPolicy discountPolicy;

    public Movie(String, title, Duration runningTime, Money fee, DiscountPolicy discountPolicy) {
        this.title = title;
        this.runningTime = runningTime;
        this.fee = fee;
        /**************************************************
        this.discountPolicy = new AmountDiscountPolicy()
         **************************************************/ 
    }
}
```
![img_2.png](img_2.png)

 - 생성시 사용책임을 함께 맡게됨
 - `생성과 사용을 분리`: 유연하고 재사용 가능한 설계를 원한다면 객체와 관련된 두가지 책임을 서로 다른 객체로 분리하는 작업
 - 적절한 방법은 클라이언트에게 객체를 생성할 책임을 옮기면됨

```java
public class Client {
    public Money getAvatarFee() {
        Movie avatar = new Movie("아바타",
                Duration.ofMinutes(120),
                Money.wons(10000),
                new AmountDiscountPolicy(Money.wons(800),
                        new SequenceCondition(1),
                        new SequenceCondition(10),
                        new PeriodCondition(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 59)),
                        new PeriodCondition(DayOfWeek.THURSDAY, LocalTime.of(10, 0), LocalTime.of(20, 59))
                ));
        return avatar.getFee();
    }
}
```

![img_3.png](img_3.png)

## FACTORY 추가하기
- 생성 책임을 `Client`로 옮긴 배경에는 `Movie`는 특정 컨텍스트에 묶여서는 안됨, `Client`는 묶여도 상관 없음
- 객체 생성과 관련된 책임만 전담하는 별도의 객체를 추가하기 → FACTORY 패턴

```java
public class Factory {
    public Movie createAvartarMovie() {
        return new Movie("아바타",
                Duration.ofMinutes(120),
                Money.wons(10000),
                new AmountDiscountPolicy(Money.wons(800),
                        new SequenceCondition(1),
                        new SequenceCondition(10),
                        new PeriodCondition(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 59)),
                        new PeriodCondition(DayOfWeek.THURSDAY, LocalTime.of(10, 0), LocalTime.of(20, 59))
                ));
    }
}
```

- `Client` 는 Factory를 사용해 생성된 Movie의 인스턴스를 반환받아 사용하기만 하면됨

```java
public class Client {
    public Factory factory;
    
    public Client(Factory factory) {
        this.factory = factory;
    }
    
    public Money getAvatarFee() {
        Movie avatar = factory.createAvartarMovie();
        return avatar.getFee();
    }
}
```

![img_4.png](img_4.png)

## 순수한 가공물에게 책임 할당하기
- 책임을 수행하는데 필요한 정보를 가장 많이 알고있는 INFORMATION EXPERT 에게 책임 할당
- `표현석 분해`: 도메인이 존재하는 사물, 개념을 표현하는 개체들을 이용해 시스템을 분해
  - 개념과 관계를 따르며 도메인과 소프트웨어 사이의 표현적 차이를 최소화 하는것을 목표
  - 도메인 모델은 설계를 위한 중요한 출발점 이지만 **단지 출발점**
- 책임을 할당하기 위해 창조되는 도메인과 무관한 인공적 객체를 `PURE FABRICATION` 이라고 함
- `행위적분해`: 어떤 행동을 추가하려고 하는데 이행동을 책임질 마땅한 도메인 개념이 존재하지 않을때 추가하고 사용
- 객체지향은 실세계의 모방이란 말은 옳지 않음 수많은 인공물들로 채워짐

## 의존성 주입
- 생성과 사용을 분리하면 `Movie` 에는 인스턴스를 사용하는 책임만 남게됨
- `의존성 주입`: 외부의 독립적인 객체가 인스턴스를 생성한후 이를 전달해서 의존성을 해결하는 방법
  - `생성자 주입`: 객체를 생성하는 시점에 생성자를 통한 의존성 해결
  - `setter 주입`: 객체 생성후 setter 메서드를 통한 의존성 해결
  - `메서드 주입`: 메서드를 호출하는 시점에 메서드 인자를 통한 의존성 해결

### 숨겨진 의존성은 나쁘다
의존성 주입 외에도 의존성을 해결할 수있는 방법
- `SERVICE LOCATOR 패턴` → 의존성 해결할 객체들을 보관하는 저장소 역할

```java
public class Movie {
    private DiscountPolicy discountPolicy;
    
    public Movie(String title, Duration runningTime, Money fee) {
        this.title = title;
        this.runningTime = runningTime;
        this.fee = fee;
      /**************************************************
      this.discountPolicy = ServiceLocator.discountPolicy();
       **************************************************/
    }
}
```

```java
public class ServiceLocator {
    private static ServiceLocator soleInstance = new ServiceLocator();
    private DiscountPolicy discountPolicy;

    public static DiscountPolicy discountPolicy() {
        return soleInstance.discountPolicy;
    }
}
``` 
---
- `Movie` 의 인스턴스가 `AmountDiscountPolicy` 인스턴스에 의존사기를 원하면 `ServiceLocator` 를 통해 `AmountDiscountPolicy` 인스턴스를 얻어야함
```java
ServiceLocator.provide = new AmountDiscountPolicy();
Movie avatar = new Movie("아바타",
                Duration.ofMinutes(120),
                Money.wons(10000));
```

- `ServiceLocator` 는 의존성을 숨기는 단점`이 존재
- `Movie` 는 `ServiceLocator` 가 제공하는 인스턴스가 어떤 클래스의 인스턴스인지 알 수 없음
- `ServiceLocator.provide = new AmountDiscountPolicy();` 코드가 없으면 할인정책이 없어 에러를 뱉고 런타임에서 에러를 확인할수 있게됨 (디버깅이 오래걸림)
