`# 서브 클래싱과 서브 타이핑

- 상속은 두가지용도
    - 타입 계층
        - 구현 → 부모 자식 클래스를 일반화↔특수화함
    - 코드재사용
        - 간단한 선언만으로 부모 클래스 코드를 재사용 가능해짐
- 상속을 사용하는 일차적인 목표는 코드 재사용X → 타입계층을 구현하는 것을 지향해야함

## 타입

### 타입사이의 포함 관계

- 타입안에 포함된 객체들은 좀더 상세한 기준으로 묶어 새로운 타입을 정의하면 새로운 타입은 자연스럽게 기존 타입의 부분 집합이 됨
  ![img.png](img.png)
- 타입이 다른 타입에 포함될수도 있어서 하나이상의 타입으로 분류되는것도 가능해짐
  ![img_1.png](img_1.png)


- 일반화와 특수화 관계를 계층으로 표현할수 있고 일반화된 타입을 위쪽에
- 특수화된 타입을 아래쪽에 배치함

![img_2.png](img_2.png)

- 슈퍼타입: 두 타입 간의 관계에서 더 일반적인 타입
- 서브타입: 더 특수한 타입
- 내연과 외연의 관점에서 일반화와 특수화를 정의
    - 외연의 관점에서 일반적인 타입의 인스턴스 집합은 특수한 타입의 인스턴스 집합을 포함하는 슈퍼셋
    - 반대로 특수한 타입에 속한 인스턴스는 일반적인 타입의 집합에 포함된 서브셋

### 객체지향 프로그래밍과 타입 계층

- 슈퍼타입 : 서브타입이 정의한 퍼블릭 인터페이스를 일반화 시켜 상대적으로 범용적이고 넒은 의미로 정의
- 서브타입 : 슈퍼타입이 정의한 퍼블릭 인터페이스를 특수화 시켜 상대적으로 구체적이고 좁은 의미로 정의

## 서브 클래싱과 서브 타이핑

타입이 다른 타입의 서브타입이 되기위한 조건

### 언제 상속을 사용 해해야 하는가?

- 상속 관계가 is-a 모델링 하는가?
    - 애플리 케이션을 구성하는 어휘에 대한 관점에 기반함, [자식클래스]는 [부모클래스]다 를 만족하면 후보로 사용가능
- 클라이언트 입장에서 부모 클래스의 타입으로 자식클래스를 사용해도 무방한가?
    - 상속 계층을 사용하는 클라이언트 입장에서 부모 클래스와 자식 클래스 차이점 `행동 호환성`

### is-a 관계

- `is-a 관계` 를 모델링할때 상속을 사용해야함
    - 펭귄은 새다
    - 새는 날수있다

```java
public class Penguin extends Bird {
    public void fly() {
        throw new UnsupportedOperationException();
    }
}
```

- 펭귄은 새지만 날수없기에 어휘적인 정의가 아니라 `기대되는 행동에 따라 타입계층을 구성`해야하는 사실을 알려줌
- 타입계층의미는 문맥에따라 달라질 수 있음
- is-a를 단편적으로 받아들일 경우 어떤 혼란이 벌어질 수 있는지 보여주는 예시
- 두가지 후보 개념이 어떤 방식으로 사용 되고 협력하는지 살펴본 후에 상속의 적용 여부를 결정해도 늦지 않음

### 행동 호환성

- 펭귄이 새가 아니라는 사실이 받아들이기 위한 출발점은 `행동과 관련`
- is-a 관계로 묶고 싶을만큼 매혹적이지만 새와 펭귄이 다른 행동 박생은 동일한 타입으로 묶으면 안됨
- 행동의 호환 여부를 판단하는 기준은 `클라이언트 관점`

```java
public void fly(Bird bird){
        bird.fly();
        }
```

- 펭귄은 새의 자식클래스이기때문에 컴파일러는 업케스팅을 허용함
- 하지만 펭귄은 날수 없고 클라이언트는 모든 brid가 날수있기를 기대해서 flyBird 메서드로 전달해서는 안됨

- 첫번째 방법은 fly의 메서드를 오버라이딩으로 내부구현을 비워두기

```java
public class Penguin extends Bird {
    @Override
    public void fly() {
        // 아무것도 하지 않음
    }
}
```

- 펭귄에게 fly를 전달해도 아무일도 일어나지 않음, 펭귄은 날수 없게된
- 하지만 클라이언트가 bird가 모두 날수있다는 요건을 충족하지 못함

- 두번째 방법은 fly 메서드를 오버라이딩한 후 예외를 던지게 함

```java
public class Peguin extends Brid {
  ...

    @Override
    public void fly() {
        throw new UnsupportedOperationException();
    }
}
```

- 위경우 flyBrid 메서드에 전달되는 인자의 타입에 따라 메서드가 실패하거나 성공하게됨
- flyBrid 메서드는 모든 brid 가 날수있다고 가정한다는 사실에 주목해야함

- 세번째 방법은 flyBrid 메서드를 수정해서 인자로 전달된 bird 타입이 Penguin이 아닐 경우에만 fly 메시지를 전송하도록함

```java
public void flyBrid(Brid brid){
        if(brid instanceof Penguin){
        brid.fly();
        }
        }
```
- 이방법역시 문제가 있음 펭귄 이외 날수 없는 새가 상속계층에 추가되면 매번 추가해야함
- 개방 - 폐쇄 원칙을 위반하게됨

### 클라이언트의 기대에 따라 계층 분리하기
- flyBird 파라미터로 전달되는 모든 새가 날수있다고 가정하기때문에 fly에 올바르게 메시지 응답을 해야함
- 펭귄 인스턴스는 flyBrid에 전달되어서는 안됨, 펭귄과 협력하는 클라이언트는 날 수 없는 새와 협할 것이라고 가정함
- `날수있는 새와 없는새를 명확하게 구분`할 수 있게 상속 계층을 분리해야함

```java
public class Bird {
  ...
}

public class FlyingBird extends Bird {
    public void fly() {
        System.out.println("I'm flying");
    }
}

public class Penguin extends Brid {
  ...
}
```

- flyBird 메서드는 FlyingBird 타입을 이용해 날수 있는 새만 인자로 전ㄷ라해야함
- 날수 없는 새와 협력한다면 파라미터 타입을 Bird로 선언하면됨
```java
public void flyBird(FlyingBird bird) {
    bird.fly();
        }
```

![img_3.png](img_3.png)

- Flying 타입의 인스턴스만이 fly 메시지를 수신 가능함
- 기대했던 행동에 대해 수행되지 않거나 예외처리를 할 필요가 없어짐

- 다른 해별방법은 인스턴스를 분리하는 방법
![img_4.png](img_4.png)

- 만약 펭귄이 새의 코드를 재사용해야한다면 펭귄이 하나의 인터페이스만 구현하고 있기떄문에 문법적으론 새를 상속받아도 문제가 되지 않음
- 하지만 fly 오퍼레이션이 추가되어 방법을 사용할 수는 없음
- `더좋은 방법은 합성을 이용하는 방법`
- Bird 퍼블릭 인터페이스를 통해 재사용 가능하다는 전제를 만족 시켜야함
- Bird 퍼블릭 인터페이스를 통해 재사용 어려우면 Bird를 야각ㄴ 수정해야할 수도 있음
- 대부분의 경우 `불안정한 상속 계층을 계속 껴안고 가는것 보다 Bird를 재사용 가능하도록 수정`하는게 좋음

- 클라이언트에 따라 인터페이스르 분리하면 변경에 대한 영향을 더 세밀하게 제어
- 클라이언트의 요구가 바뀌더라도 영향의 파급효과를 효과적으로 제어할 수 있음
- Client1 의 기대가 바뀌어서 Flyer의 인터페이스라 변경되어야하면 Flyer에 의존하는 Bird가 영향을 받음
- 하지만 영향은 Bird에서 끝남
- 이러한 설계방법을 `인터페이스 분리 원칙(ISP)`

### 서브 클래싱과 서브 타이핑
- 사람들은 상속을 사용하는 두가지 목적에 특별한 이름을 붙임, `서브 클래싱`, `서브 타이핑`
- `서브클래싱`
  - 다른 클래스의 코드를 재사용할 목적으로 상속을 사용함
  - 부모클래스의 행동이 호환되지 않기에 자식클래스의 인스턴스가 부모클래스를 대신할수 없음
  - 구현상속, 클래스상속
- `서브 타이핑`
  - 타입 계층을 구성하기 위해 상속하는 경우
  - 영화 예매 시스템에서 구현한 DiscountPolicy 상속 계층이 서브타이핑에 해당

- 나쁜설계의 예로든 많은 부분이 서브 클래싱에 속함
- 슈퍼 타입과 서브 타입 사이의 관계에서 가장 중요한것은 퍼블릭 인터페이스
- 슈퍼 타입 인스턴스를 요구하는 모든곳에서 서브타입의 인스턴스를 사용하기 위해
- 서브타이핑 관계가 유지 되기 위해서는 서브타입이 슈퍼타입이 하는 모든 행동을 동일하게 할수 있어야함
- `행동 호환성`을 만족 시켜야함

- 자식 클래스가 부모 클래스를 대신 할수 있기 위해서는 자식 클래스가 부모 클래스가 사용되는 모든 문맥에서 자식클래스와 도일하게 행동 가능해야함
- 자식 클래스와 부모 클래스 사이의 행동 호환성은 부모 클래스에 대한 자식클래스의 대체 가능성을 포함함
- 행동 호환성과 대체 가능성은 올바른 상속관계를 구축하기 위해 따라가야할 지침

## 리스코프 치환 원칙
- 서브타입은 그것의 기반 타입에 대해 대체 가능해야함
```java
public class Rectangle {
    private int x,y,width,height;
    
    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
        
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
  ...
}
```

- Rec 에서 Square를 추가한 경우 개념적으로 정사각형은 직사각형의 특수한 경우
- is-a 관계가 성립하므로 상속으로 구현함
![img_5.png](img_5.png)

- 정사각형은 너비와 높이가 동일해야함
```java
public class Square extends Rectangle {
    public Square(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
    @Override
  public void setWidth(int width) {
        super.setWidth(width);
        super.setHeight(width);
    }
    
    @Override
  public void setHeight(int height) {
        super.setWidth(height);
        super.setHeight(height);
    }
}
```

- Square는 자식이기 때문에 Rectangle이 사용되는 모든 곳에서 Rectangle로 업캐스팅 될수있지만 문제가발생
- 직사각현의 너비와 높이가 다르다고 가정 → Square는 Rectangle의 대체 가능성을 만족하지 못함
- Rectangle은 is-a 라는 말이 얼마나 우리의 직관에서 벗어날 수 있는지 보여줌
- 클라이언트 관점에서 행동이 호환되는지 여부

### 클라이언트와 대체 가능성
- Square가 Rectangle을 대체할 수 없는 이유는 클라이언트 관점에서 Square와 Rectangle이 다르기 때문
- 클라이언트 입장에서 정사각형을 추상화한 Square는 직사각형을 추상화한 Rectangle과 동일하지 않음
- Rectagle을 사용하는 클라이언트는 Rectangle의 너비와 높이가 다를수 있다는 가정하게 코드를 개발
- Square는 너비와 높이가 항상 같음 대체 될경우 세워진 가정을 위반할 확률이 높음


### is-a 관계 다시 살펴보기
- 상속이 적합한지 판단하기 위해
  - is -a 관계를 모델링 한건가?
  - 클라이언트 입장에서 부모를 자식 클래스를 사용 할수 있는가?
- 어휘적으로 is-a 라고 할수있더라도 클라이언트 관점이아니면 의미가 없음

### 리스코프 치환 원칙은 유연한 설계 기반
- 새로운 자식 클래스를 추가하더라도 클라이언트 입장에서 동일하게 행동하기만 한다면 클라이언트를 수정하지 않고도 상속 계층을 확장
- 리스코프 치환 원칙을 따르는 설계는 유연할 뿐 아니라 확장성이 높음

- 의존성 역전 원칙
  - 구체 클래스인 Movie와 OverlappedDiscountPolicy 모두 추상 클래스인 DiscountPolicy에 의존함
  - 상위 수준 모듈인 Movie와 하위 수준 모듈인 OverlappedDiscountPolicy는 모두 추상 클래스인 DiscountPolicy 에 의존함
- 리스코프 치환원칙
  - DiscountPolicy와 협력하는 Movie 관점에서 DiscountPolicy 대신 자식 객체와 협력하더라도 문제가 없음
- 개방 폐쇄 원칙
  - 중복 할인이라는 새로운 정책이 추가되어도 확정성에 문제가 없음

![img_6.png](img_6.png)

## 계약에 의한 설께와 서브 타이핑
- 클라이언트와 서버사이 협력을 의무와 이익으로 구성된 계약 관점에서 표현한것을 `계약에 의한 설계` 라고 부름
- 클라이언트가 정상적으로 서버 메서드를 실행하기위해 `사전조건`과 메서드가 실행한 후 서버가 클라이언트에게 보장해야 하는 `사후조건`
- 메서드 실행 전과 실행 후에 인스턴스가 만족시켜야하는 `클래스 불변식` 
- 리스코프 치환 원칙은 어떤 타입이 서브 타입이 되기 위해서는 슈퍼타입의 인스턴스와 협락하는 클라이언트 관점에서 서브타입의 인스턴스가 슈퍼타입을 대체하더라도 협력에 지장 없어야 한다는것을 의미
- `서브 타입이 리스코프 치환 원칙을 만족하기 위해 클라이언트와 슈퍼타입 간에 체결된 계약을 준수해야함`

```java
public class Movie {
  ...
  public Money calculateMovieFee(Screening screening) {
    return discountPolicy.calculateDiscountAmount(screening);
  }
} 
```

- Moive는 DiscountPolicy의 인스턴스에게 calculateDiscountAmount 메시지를 전송하는 클라이언트
- DiscountPolicy는 Movie 의 메시지를 수신한 후 할인 가격을 계산해서 반환함

```java
public abstract class DiscountPolicy {
    public Money calculateDiscountAmount(Screening screening) {
     for(DiscountPolicy each : conditions) {
         if(each.isSatisfiedBy(screening)) {
            return calculateDiscountAmount(screening);
         }
     }
    return screening.getMovieFee();
    }
}
```

- 계약에 의한 설계에 따르면 협력하는 클라이언트와 슈퍼타입 인스턴스 사이에는 어떤 계약이 맺어져 있음
- 클라이언트와 슈퍼 타입은 이 계약을 준수할 때만 정상적으로 협력할수 있어야함
- 리스코프 치환 원칙은 서븥브타입은 그것의 슈퍼타입을 대체할수 있어야함, 클라이언트가 차이점을 인지하지 못한채, 슈퍼타입의 인터페이스를 이용해 서브 타입과 협력할수 있어야함


### 서브타입과 계약
- 모든 상황이 행복하지만 않음, 계약 관점에서 상속이 초래하는 가장 큰 문제는 `자식 클래스가 부모 클래스 메서드를 오버라이딩`
- `서브 타입ㅇ[] 더 강력한 사전 조건을 정의하면 안됨`
- `서브타입에 슈퍼타입과 같거나 더 강한 사후 조건을 명시하면 안됨`
- `서브 타입에 슈퍼타입과 같거나 더 약한 사후조건을 정의 할수 있음`

