# 상속과 코드 재사용
- 객체지향 프로그래밍 장점 중 하나는 코드를 재사용하기 용이함
- 재사용 관점에서 `상속`이란 클래스 안에 정의된 인스턴스 변수와 메서드를 자동으로 새로운 클래스에 추가하는 구현 기법
- 새로운 클래스의 인스턴스 안에 기존 클래스의 인스턴스를 포함시키는 방법을 `합성`

## 상속과 중복 코드

### DRY 원칙
- 중복코드는 변경을 방해함, 중복코드를 제거해야하는 이유중 하나
- 프로그램의 본질은 비즈니스와 관련된 지식을 코드로 변환하는것
- 중복 코드는 코드를 수정하는 데 필요한 노력을 몇 배로 증가시킨다

### 중복과 변경
#### 중복 코드 살펴보기
`요구사항`: 한달에 한번씩 가입자별로 전화 요금을 계산하는 간단한 애플리케이션

`규칙`: 통화시간을 단위 시간당 요금으로 나눔

```java
import java.time.Duration;
import java.time.LocalDateTime;

public class Call {
    private LocalDateTime from;
    private LocalDateTime to;

    public Call(LocalDateTime from, LocalDateTime to) {
        this.from = from;
        this.to = to;
    }

    public Duration getDuration() {
        return Duration.between(from, to);
    }

    public LocalDateTime getFrom() {
        return from;
    }
}
```

- Phone 인스턴스는 요금 계산에 필요한 세가지 인스턴스 변수 포함

```java
import java.time.Duration;

public class Phone {
    private Money amount;
    private Duration seconds;
    private List<Call> calls = new ArrayList<>();

    public Phone(Money amount, Duration seconds) {
        this.amount = amount;
        this.seconds = seconds;
    }

    public void call(Call call) {
        calls.add(call);
    }

    public List<Call> getCalls() {
        return calls;
    }

    public Money getAmount() {
        return amount;
    }

    public Duration getSeconds() {
        return seconds;
    }
    
    public Money calculateFee() {
        Money result = Money.ZERO;
        for(Call call : calls) {
            result = result.plus(amount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
        }
        return result;
    }
}
```
- 클라이언트는 아래와같은 방법으로 계산

```java
Phone phone = new Phone(Money.wons(5), Duration.ofSeconds(10));
phone.call(new Call(LocalDateTime.of(2020, 1, 1, 12, 10, 0),
        LocalDateTime.of(2020, 1, 1, 12, 11, 0)));

phone.calculateFee();
```

---

- 요구사항은 항상 변함 → 심야요금제가 밤 10시 이후 통화에 대해 할인 된다는 변경점이 생겼을 경우
- `Phone` 코드를 복사해서 `NightlyDiscountPhone` 클래스를 만들고 새 클래스를 수정하는방법

```java
public class NightlyDiscountPhone {
    private static final int LATE_NIGHT_HOUR = 22;
    
    private Money nightlyAmount;
    private Money regularAmount;
    private Duration seconds;
    private List<Call> calls = new ArrayList<>();
    
    public NightlyDiscountPhone(Money nightlyAmount, Money regularAmount, Duration seconds) {
        this.nightlyAmount = nightlyAmount;
        this.regularAmount = regularAmount;
        this.seconds = seconds;
    }
    
    //심야 요금을 계산함
    public Money calculateFee() {
        Money result = Money.ZERO;
        for(Call call : calls) {
            if(call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
                result = result.plus(nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
            } else {
                result = result.plus(regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
            }
        }
        return result;
    }
}
```

#### 중복 코드 수정하기
- 중복코드가 코드 수정에 미치는 영향을 살펴보기 위해 새로운 요구사항을 추가 → 통화 요금에 부과할 세금을 계산할경우
- `Phone`, `NightlyDiscountPhone` 양쪽에 모두 구현되어있음

```java
public class Phone {
    ...
    private double taxRate;
    public Phone(Money amount, Duration seconds, double taxRate) {
        ...
        this.taxRate = taxRate;
    }
    
    public Money calculateFee() {
        Money result = Money.ZERO;
        for(Call call : calls) {
            result = result.plus(amount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
        }
        return result.plus(result.times(taxRate));
    }
}
```

```java
public class NightlyDiscountPhone {
    ...
    private double taxRate;
    
    public NightlyDiscountPhone(Money nightlyAmount, Money regularAmount, Duration seconds, double taxRate) {
        ...
        this.taxRate = taxRate;
    }
    
    public Money calculateFee() {
        Money result = Money.ZERO;
        
        for(Call call : calls) {
            if(call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
                nightlyAmount = nightlyAmount.plus(nightlyAmount.times(taxRate));
            } else {
                regularAmount = regularAmount.plus(regularAmount.times(taxRate));
            }
        }
        return result.minus(result.times(taxRate));
    }
}
```


- 많은 코드 더미 속에서 어떤 코드가 중복인지를 파악하는 일은 어려움
- 함께 수정할때 하나라도 빠트리면 버그로 이어지는 경우가 존재
- `Phone` 은수정했지만`NightlyDiscountPhone` 은 수정하지 않았을 경우 장애가 발생함
- 코드의 일관성이 무너질 위험이 항상 도사리고 있음

#### 타입 코드 사용하기
- 클래스 중복을 제거하는 방법은 클래스를 하나로 합치는 방법
- 타입 코드를 추가하고 타입 코드 값에 따라 로직을 분기 시켜 `Phone` 과 `NightlyDiscountPhone` 을 하나의 클래스로 합침
- `타입 코드를 사용하는 클래스는 낮은 응집도와 높은 결합도를 가짐`

```java
public class Phone {
    private static final int LATE_NIGHT_HOUR = 22;
    enum PhoneType { REGULAR, NIGHTLY }
    
    private PhoneType type;
    
    private Money amount;
    private Money regularAmount;
    private Money nightlyAmount;
    private Duration seconds;
    private List<Call> calls = new ArrayList<>();
    
    public Phone(Money amount, Duration seconds) {
        this(PhoneType.REGULAR, amount, Money.ZERO, Money.ZERO, seconds);
    }
    
    public Phone(Money nightlyAmount, Money regularAmount, Duration seconds) {
        this(PhoneType.NIGHTLY, Money.ZERO, nightlyAmount, regularAmount, seconds);
    }
    
    public Phone(PhoneType type, Money amount, Money nightlyAmount, Money regularAmount, Duration seconds) {
        this.type = type;
        this.amount = amount;
        this.nightlyAmount = nightlyAmount;
        this.regularAmount = regularAmount;
        this.seconds = seconds;
    }
    
    public Moneny calculateFee() {
        Money result = Money.ZERO;
        
        for(Call call : calls) {
            if(type == PhoneType.REGULAR) {
                result = result.plus(amount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
            } else {
                if(call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
                    nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
                } else {
                    nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
                }
            }
        }
        
        return result;
    }
    
}

```

- 객체지향 프로그래밍 언어는 타입 코드를 사용하지 ㅇ낳고 중복 코드를 관리할 수 있는 효과적인 방법 제공 `상속`

### 상속을 이용해서 중복 코드 제거
- 이미 존재하는 클래스와 유사한 클래스가 필요하다면 코드를 복사하지 말고 상속을 이용해 재사용

```java
public class NightlyDiscountPhone extends Phone {
    private static final int LATE_NIGHT_HOUR = 22;
    
    private Money nightlyAmount;
    
    public NightlyDiscountPhone(Money nightlyAmount, Money regularAmount, Duration seconds) {
        super(regularAmount, seconds);
        this.nightlyAmount = nightlyAmount;
    }
    
    @Override
    public  Money calculateFee() {
        Money result = super.calculateFee();
        Money nightlyFee = Money.ZERO;
        
        for(Call call : getCalls()) {
            if(call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
                nightlyFee = nightlyFee.plus(getAmount().minus(nightlyAmount).times(call.getDuration().getSeconds() / getSeconds().getSeconds()));
            }
        }
        
        return result.minus(nightlyFee);
    }
}
```

- `super` 참조를 통해 부모 클래스인 `Phone` 의 `calculateFee` 메서드를 호출해서 일반 요금제에 따라 통화요금을 계산한 후 이값에서 통화 시작 시간이 10시이후인 통화요금을 뺴줌
- `Phone`의 코드를 재사용하기 위해 10시 이전의 요금을 `Phone`에서 처리
- `개발자의 가정을 이해하기 전에 코드 자체가 이해하기 어려움`
- 요구사항과 구현 사이의 차이가 크면 클수록 코드를 이해하는게 어려움
- 상속이 초래하는 부모 클래스와 자식 클래스 사이에 강한 결합이 코드를 수정하기 어렵게 만듬

### 강하게 결합된 Phone 과 NightlyDiscountPhone
- 부모 클래스와 자식 클래스의 결합이 문제인 이유 → `calculateFee`를 오버라이딩해서 사용
- 요구사항의 변경에서세금을 부과하는 요구사항이 추가되면 `calculateFee`사용할때 `taxRate`를 곱해야함

```java
public class Phone {
    ...
    private double taxRate;
    
    public Phone(Money amount, Duration seconds, double taxRate) {
        ...
        this.taxRate = taxRate;
    }
    
    public Money calculateFee() {
        Money result = Money.ZERO;
        for(Call call : calls) {
            result = result.plus(amount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
        }
        return result.plus(result.times(taxRate));
    }
    
    public double getTaxRate() {
        return taxRate;
    }
}
```
- `Phone`과 동일하게 값을 반환할때 `taxRate`을 이용해 세금을 부과해야함

```java
public class NightlyDiscountPhone extends Phone {
    public NightlyDiscountPhone(Money nightlyAmount, Money regularAmount, Duration seconds) {
        super(regularAmount, seconds);
        this.nightlyAmount = nightlyAmount;
    }
    
    @Override
    public Money calculateFee() {
        ...
        return result.minus(nightlyFee).plus(result.minus(nightlyFee).times(getTaxRate()));
    }
}
```

- 중복을 제거하기 위해 상속을 사용했지만, 요구사항 변경에의해 새로운 중복 코들르 만들어야함
```text
 자식 클래스의 메서드 안에서 super를 통해 부모클래스 메서드를 호출하는 경우 두 클래스는 강하게 결합함 super를 제거할수 있는 방법을 찾아야함
```

### 취약한 기반 클래스 문제
- `취약한 기반 클래스 문제`: 부모 클래스의 변경에 자식 클래스가 영향을 받는 현상
- 상속이라는 문맥 안에서 결합도가 초래하는 문제점을 가리키는 용어
- 상속은 자식 클래스가 부모 클래스의 구현 세부사항에 의존하도록 만들기 때문에 캡슐화를 약화시킴

### 불필요한 인터페이스 상속 문제
- 자바 초기 버전에 상속을 잘못 사용한 대표적 사례는 `java.,util.Properties`, `java.util.Stack`
- 부모의 상속 메서드를 사용할경우 자식 클래스의 규칙이 위반됨
- Vector를 상속받으면서 Stack 에서 FIFO 방식으로 동작 가능해짐

![img.png](img.png)


```text
상속받은 부모 클래스의 메서드가 자식 클래스의 내부 구조에 대한 규칙을 깨트릴 수있음
```

### 메서드 오버라이딩 오작용 문제
- `HashSet` 에서 강하게 결합된 `InstrumentedHashSet`

```java
public class InstrumentedHashSet<E> extends HasSet<E> {
    private int addCount = 0;
    
    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }
    
    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }
}
``` 

```java
InstrumentedHashSet<String> s = new InstrumentedHashSet<>();
s.addAll(Arrays.asList("Snap", "Crackle", "Pop"));
```

- addCount가 3이 될거라 생각하지만 6이됨
- 부모 클래스인 addAll 메서드 안에서 add를 호출함
- HashSet에서 각각의 요소를 추가하기 위해 내부적으로 add 메서드를 호출함
- `InstrumentedHashSet` 의 addAll 메서드를 제거하면 해결되지만 → `HashSet`의 `addAll` 메서드가 `add` 메시지를 전송하지 않도록 수정되면 `addAll` 메서드를 이용해 추가되는 요소들에 대한 카운트가 누락됨

```java
import java.util.HashSet;

public class InstrumentedHashSet<E> extends HashSet<E> {
    private int addCount = 0;
    
    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }
    
    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for(E e : c) {
            if(add(e)) {
                modified = true;
            }
        }
        return modified;
    }
}
```

- `InstrumentedHashSet`의 `addAll` 메서드를 오버라이딩 하고 추가 되는 각 요소에 대해 한번씩 add 메시지 호출
- 오버라이딩 된 `addAll` 메서드의 구현이 `HasSet`의 것과 동일하게됨, 미래에 발생할지 모르는 위험을 방지하기위해 코드를 중복함

```text
자식 클래스가 부모 클래스의 메서드를 오버라이딩 할경우 부모 클래스가 자신의 메서드를 사용하는 방법에 자식 클래스가 결합됨
```

### 부모 클래스와 자식 클래스 동시 수정 문제
- 음악 목록을 추가할 수 있는 플레이 리스트 구현

```java
public class Song {
    private String artist;
    private String title;
    
    public Song(String artist, String title) {
        this.artist = artist;
        this.title = title;
    }
    
    public String getArtist() {
        return artist;
    }
    
    public String getTitle() {
        return title;
    }
}
```

```java
public class Playlist {
    private Vector<Song> tracks = new Vector<>();
    
    public void append(Song song) {
        tracks.add(song);
    }
    
    public List<Song> getTracks() {
        return tracks;
    }
}
```

- Playlist 에서 노래 목록 뿐 아니라 가수별 노래의 제목도 함께 관리해야한다고 할때 → 노래를 추가한후, 가수 이름의 키로 노래 제목을 추가하도록 `Playlist` 의 `append` 메서드를 수정해야함

```java
public class Playlist {
    private Vector<Song> tracks = new Vector<>();
    private Map<String, String> singers = new HashMap<>();
    
    public void append(Song song) {
        tracks.add(song);
        singers.put(song.getArtist(), song.getTitle());
    }
    
    public List<Song> getTracks() {
        return tracks;
    }
    
    public Map<String, String> getSingers() {
        return singers;
    }
}
```

- `PersonalPlaylist`의 `remove` 메서드도 함꼐 수정해야 정상작동함
- `PersonalPlaylist`를 수정하지 않으면 `Playlist` 의 `tracks` 에서는 노래가 제거되지만 `singgers` 에서는 제거되지 않음

```java
public class PersonalPlaylist extends Playlist {
    public void remove(Song song) {
        getTracks().remove(song);
        getSingers().remove(song.getArtist());
    }
}
```
- 부모클래스를 수정할때 자식클래스를 함께 수정해야함

```text
클래스를 상속하면 결합도로 인해 자식 클래스와 부모 클래스의 구현을 영원히 변경하지 않거나 자식 클래스와 부모 클래스를 동시에 변경하거나 둘중하나를 선택해야함
```

## Phone 다시 살펴보기
- 상속으로 발생하는 취약한 기반 클래스 문제의 다양한 예를 살펴봄
- `Phone`  상속으로 인한 피해를 최소화 하는 방법을 구현

### 추상화에 의존하기
- `NightlyDiscountPhone` 의 큰문제는 `Phone`에 강결합 되어있음
- 코드 중복으 제거하기 위해 상속을 도입하는경우
    - 두 메서드가 유사하게 보인다면 차이점을 메서드로 추출, 추출된 메서드는 동일형태로 보이도록 만들수 있음
    - 부모 클래스의 코드를 하위 내리지 말고 자식 클래스를 상위로 올려야함

    
### 차이를 메서드로 추출하라
- 가장 먼저 할 일은 중복 코드 안에서 차이점을 별도의 메서드로 추출
- `변하는 것을부터 변하지 않는것을 분리`, `변하는 부분을 찾고 캡슐화`

```java
public class Phone {
    private Money amount;
    private Duration seconds;
    private List<Call> calls = new ArrayList<>();
    
    public Phone(Money amount, Duration seconds) {
        this.amount = amount;
        this.seconds = seconds;
    }
    
    public Money calculateFee() {
        Money result = Money.ZERO;
        for(Call call : calls) {
            result = result.plus(calculateCallFee(call));
        }
        return result;
    }
}
```

```java
public class NightlyDiscountPhone {
    private static final int LATE_NIGHT_HOUR = 22;
    
    private Money nightlyAmount;
    private Money regularAmount;
    private Duration seconds;
    private List<Call> calls = new ArrayList<>();
    
    public NightlyDiscountPhone(Money nightlyAmount, Money regularAmount, Duration seconds) {
        this.nightlyAmount = nightlyAmount;
        this.regularAmount = regularAmount;
        this.seconds = seconds;
    }
    
    public Money calculateFee() {
        Money result = Money.ZERO;
        for(Call call : calls) {
            if(call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
                result = result.plus(nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
            } else {
                result = result.plus(regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
            }
        }
        return result;
    }
}
```

- 두 클래스의 다른 부분을 별도의 메서드로 추출
- `calculateFee` for문 안에 구현된 계산로직이 다름 → 동일한 이름을 가진 메서드로 추출 `calculateCallFee`


```java
public class Phone {
    ...
    public Money calculateFee() {
        Money result = Money.ZERO;
        
        for(Call call : calls) {
            result = result.plus(calculateCallFee(call));
        }
        
        return result;
    }
    
    private Money calculateCallFee(Call call) {
        return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
    }
}
```

```java
    public class NightlyDiscountPhone {
    ...
    public Money calculateFee() {
        Money result = Money.ZERO;
        
        for(Call call : calls) {
            result = result.plus(calculateCallFee(call));
        }
        
        return reuslt
    }
    
    private Money calculateCallFee(Call call) {
        if(call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
            return nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        } else {
            return regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        }
    }
}
```

- 두클래스의 `calculateFee` 메서드는 동일해졌고 추출한 `calculateCallFee` 메서드 안에 서로 다른 부분을 격리함

### 중복 코드를 부모 클래스로 올려라

```java
public abstract class AbstractPhone{}

public class Phone extends AbstractPhone{ ... }

public class NightlyDiscountPhone extends AbstractPhone{ ... }
```

- `Phone` 과 `NightlyDiscountPhone` 공통 부분을 부모 클래스로 이동함
- 공통 코드를 옮길때 인스턴스 변수보다 메서드를 이동시키는게 편함
- 두클래스 사이에 완전히 동일한 코드 `calculateFee` → `AbstactPhone` 으로 이동

```java
public abstract class AbstractPhone {
    public Money calculateFee() {
        Money result = Money.ZERO;
        
        for(Call call : calls) {
            result = result.plus(calculateCallFee(call));
        }
        
        return result;
    }
}
```

```java
public abstract class AbstactPhone {
    private List<Call> calls = new ArrayList<>();
    
    public Money calculateFee() {
        Money result = Money.ZERO;
        
        for(Call call : calls) {
            result = result.plus(calculateCallFee(call));
        }
        
        return result;
    }
    
    abstract protected Money calculateCallFee(Call call);
}
```

```java
public class Phone extends AbstractPhone {
    private Money amount;
    private Duration seconds;
    
    public Phone(Money amount, Duration seconds) {
        this.amount = amount;
        this.seconds = seconds;
    }
    
    @Override
    protected Money calculateCallFee(Call call) {
        return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
    }
}
```

```java
public class NightlyDiscountPhone extends AbstractPhone {
    private static final int LATE_NIGHT_HOUR = 22;
    
    private Money nightlyAmount;
    private Money regularAmount;
    private Duration seconds;
    
    public NightlyDiscountPhone(Money nightlyAmount, Money regularAmount, Duration seconds) {
        this.nightlyAmount = nightlyAmount;
        this.regularAmount = regularAmount;
        this.seconds = seconds;
    }
    
    @Override
    protected Money calculateCallFee(Call call) {
        if(call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
            return nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        } else {
            return regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        }
    }
}
```

![img_1.png](img_1.png)

### 추상화가 핵심

- 공통 코드를 이동시킨 후 클래스는 서로다른 변경의 이유를 가짐
- 부모 클래스도 자신 내부에 구현된 추상메서드를 호출하기 때문에 추상화에 의존
- 새로운 요금제 추가하기도 쉬워짐

### 의도를 드러내는 이름 선택하기
- Phone 은 일반 요금제와 관련된 내용을 구현한다는 사실을 명시적으로 전달하지 못함
- `AbstractPhone` → `Phone`, `Phone` → `RegularPhone` 으로 변경

### 세금 추가하기

```java
public abstract class Phone {
    private double taxRate;
    private List<Call> calls = new ArrayList<>();
    
    public Phone(double taxRate) {
        this.taxRate = taxRate;
    }
    
    public Money calculateFee() {
        Money result = Money.ZERO;
        
        for(Call call : calls) {
            result = result.plus(calculateCallFee(call));
        }
        
        return result.plus(result.times(taxRate));
    }
    
    protected abstract Money calculateCallFee(Call call);
}
```

```java
public class RegularPhone extends Phone {
    private Money amount;
    private Duration seconds;
    
    public RegularPhone(Money amount, Duration seconds, double taxRate) {
        super(taxRate);
        this.amount = amount;
        this.seconds = seconds;
    }
    
    @Override
    protected Money calculateCallFee(Call call) {
        return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
    }
}
```

- 상속으로 인한 클래스 사이의 결합을 피할수 있는 방법은 없음
- 메서드 구현에 대한 결합은 추상 메서드를 추가함으로 완하는 가능하나, 잠재적인 결합을 제거할순 없다
