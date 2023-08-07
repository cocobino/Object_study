# 객체 분해

※ 인지 과부하: 문제 해결에 필요한 요소의 수가 단기 기억량을 초과하는 순간 문제해결 능력이 급격히 떨어짐

※ 추상화: 불필요한 정보를 제거하거 문제 해결에 필요한 핵심만 남기는 작업

※ 분해: 큰문제를 해결 가능한 작은 문제로 나누는 작업

## 프로시저 추상화 데이터 추상화

- 프로시저 추상화
    - 소프트웨어가 무엇을 해야하는지 추상화
- 데이터 추상화
    - 소프트웨어가 무엇을 알아야하는지 추상화 해야함
- 추상데이터 타입
    - 시스템 분해를 결정후 데이터 중심으로 타입을 추상화
- 객체 지향
    - 시스템 분해를 결정후 데이터 중심으로 프로시저를 추상화

## 프로시저 추상화와 기능 분해

### 메인함수로서의 시스템

- 기능은 시스템을 분해하기 위한 기준으로 사용함
- 시스템 분해방식 = `알고리즘 분해`, `기능분해`
- 프로시저는 반복적으로 실행되거나 유사하게 실행되는 작업들을 하나의 장소에 모아 재사용하고 중복방지 및 추상화
- 잠재적 정보 은닉 가능성을 제시하지만 `효과적인 정보 은닉 체계`를 구축에 한계를 가짐

※ 하향식 접근법: 시스템을 구성하는 최상위 기능을 정의하고 최상위 기능을 좀더 작은 단계 하위 기능으로 분해 하는 방법,
정제된 기능은 자신의 바로 상위 기능보다 덜 추상적이여야함

### 급여 관리 시스템

급여 관리 시스템에 대한 추상적인 최상위 문장을 기술함으로 시작

충분히 저수준의 문장이 될때까지 기능을 분할하기

1. 직원의 급여를 계산한다.
    1. 사용자로부터 소득세율을 입력받는다
        1. 세율을 입력하세요 화면 출력
        2. 키보드를 통해 세율을 받음
    2. 직원의 급여를 계산한다.
        1. 전역변수에 저장된 직원의 기본급 정보 얻기
        2. 급여 예산
    3. 양식에 맞게 결과를 출력한다
        1. 이름, 급여, 형식에 따라 문자열 출력

책의 목차를 정리하고 그안에 내용을 채워 넣는것과 유사함

![img.png](img.png)

- 기능 분해방법에서는 기능 중심으로 데이터를 결정함
- 기능을 분해하고 정제하는 과정에서 필요한 데이터의 종류와 저장방식을 식별

→ 유지보수에 다양한 문제들이 발생함

### 급여 관리 시스템 구현

1. 직원의 급여를 계산한다

```Ruby
def main(name)
end
```

-------

1. 직원의 급여를 계산한다.
    1. 사용자로부터 소득세율을 입력받는다
    2. 직원의 급여를 계산한다.
    3. 양식에 맞게 결과를 출력한다

```Ruby
def main(name)
    taxRate = getTaxRate()
    pay = calculatePay(name, taxRate)
    puts(formatPay(name, pay))
end
```

---

1. 직원의 급여를 계산한다.
    1. 사용자로부터 소득세율을 입력받는다
        1. 세율을 입력하세요 화면 출력
        2. 키보드를 통해 세율을 받음

```Ruby
def getTaxRate()
  print("세율을 입력하세요: ")
  return gets().to_f() 
end
```

---

2. 직원의 급여를 계산한다.
    1. 전역변수에 저장된 직원의 기본급 정보 얻기
    2. 급여 예산

```Ruby
$employes = ["직원A", "직원B", "직원C"]
$basePays = [150, 200, 300]
```

```Ruby
def calculatePayFor(name, taxRate)
    index = $employes.index(name)
    basePay = $basePays[index]
    return basePay * (1 - taxRate)
```

---

3. 양식에 맞게 결과를 출력한다
    1. 이름, 급여, 형식에 따라 문자열 출력

```Ruby
def describeResult(name, pay)
    return "#{name}의 급여는 #{pay}입니다."
end
```

---
![img_1.png](img_1.png)

우리가 사는 세계는 체계적이지도 이상적이지도 않다는점, 불규칙하고 불완전한 인간과 만나는 지점에 혼란과 동요가 발생함

### 하향식 기능 분해의 문제점

- 시스템은 하나의 메인함수로 구성되어있지 않음
- 기능 추가, 요구사항 변경은 메인함수 빈번하게 수정
- 비지니스 로직이 사용자 인터페이스와 강하게 결함
- 하향식 분해는 이른시기에 함수들의 실행순서를 고정 시켜 유연성, 재사용성 저하
- 데이터 형식이 변경될 경우 파급효과 예측어려움

#### 하나의 메인 함수라는 비현실적인 아이디어

- 어떠한 시스템도 최초에 출시모습을 그대로 유지 하지 않음
- 메인함수는 중요한 여러 함수들중 하나로 전락함

#### 메인 함수의 빈번한 재설계

직원 급여 총액을 계산하는`sumOfBasePays` 구현시 main 함수와 동등한 수준의 작업함

메인 함수 로직을 `calculatePay` 로 이동

```Ruby
def calculatePay(name)
    taxRate = getTaxRate()
    pay = calculatePayFor(name, taxRate)
    return pay
end
```

---

```Ruby
def main(operation, args={})
    case (operation)
    when :pay then calculatePay(args[:name])
    when :basePays then sumOfBasePays()
    end
end
```

메인함수의 잦은 변경, 버그발생률 증가와 변경에 취약해짐

#### 비지니스 로직과 사용자 인터페이스 결합

초기 단계부터 입력과 출력 양식을 고민함 → 비지니스 로직과 사용자 인터페이스는 변경 빈도가 다르다
**근본적으로 변경에 불안정한 아키텍처를 낳게됨**

#### 성급하게 결정된 실행 순서

- 시스템이 무엇을 해야하는지아니라 어떻게 해야하는지에 집중하도록 만듬
- 처음부터 구현을 염두해두기 때문에 실행 순서를 정의하는 `시간 제약`을 강조함
- 기능 분해방식은 `중앙집중 제어 스타일` 형태를 띄움
- 함수가 재사용 가능하라면 상위 함수보다 더 일반적이여야함
- 모든 문제는 `결합도` 와 연관되게됨, 함수는 함께 절차를 구성하는 다른 함수들과 시간적으로 강하게 결합함

#### 데이터 변경으로 인한 파급효과

- 데이터 형식이 변경될 경우 파급효과 예측어려움
- `의존성`, `결합도`의 문제

---
아르바이트 직원 급여가 추가된경우

```Ruby
$employees = ["직원A", "직원B", "직원C", "아르바이트"]
$basePays = [400, 150, 200, 300]
$hourlys = [false, false, false, true]
```

업무 누적 시간이 필요

```Ruby
$timeCards = [0, 0, 0, 120, 120, 120]
```

정규 직원 정보를 관리하던 `$employees`, `$basePays` 는 `$hourlys` 로 변경됨
`$hourlys` 는 `$timeCards` 와 함께 사용됨 → 어플리케이션 데이터를 수정 하게 됨

아르바이트 직원 급여를 계산하는 함수

```Ruby
    def calculateHourlyPayFor(name, taxRate)
        index = $employees.index(name)
        basePay = $basePays[index]
        timeCard = $timeCards[index]
        return basePay * timeCard * (1 - taxRate)
    end
```

정규직원과 아르바이트 직원을 판단하는 `hourly?` 도 추가되면 아래와 같음

```Ruby
    def hourly?(name)
        index = $employees.index(name)
        return $hourlys[index]
    end
```

수정된 calulatePay 함수는 지원이 아프바이트 직원이면 `calculateHourlyPayFor` 호출, 직원이면 기존의 `calculatePayFor` 함수 실행

    ```Ruby
        def calculatePay(name)
            taxRate = getTaxRate()
            if hourly?(name)
                pay = calculateHourlyPayFor(name, taxRate)
            else
                pay = calculatePayFor(name, taxRate)
            end
            return pay
        end
    ```

시급로직에 문제가 있어 확인해보니 `basePay` 와 `employees` 에 정보를 추가하여 사이드이팩발생, `sumOfBasePays`도 수정해야함

    ```Ruby
        def sumOfBasePays()
            sum = 0
            for i in 0..$employees.length-1
                if hourly?($employees[i])
                    next
                end
                sum += $basePays[i]
            end
            return sum
        end
    ```

### 언제 하향식 분해가 유용한가?

안정화 된 후에는 설계의 다양한 측면을 논리적으로 설명하고 문서화 하기 용이함

## 모듈

### 정보은닉과 모듈

※정보은닉: 시스템에서 자주 변경되는 부분을 상대적으로 덜 변경되는 앉어적인 인터페이스 뒤로 감춰야 한다는 것이 핵심

- 외부에 감춰야하는 비밀에 따라 시스템 분할하는 모듈 분할의 원리
- 모듈은 변경될 가능성이 있는 비밀을 내부로 감추고 쉽게 변경되지 않을 퍼블릭 인터페이스를 외부에 제공, 모듈은 두가지를 감춰야함
    - 복잡성: 모듈이 너무 복잡한 경우 이해하고 사용하기 어려움
    - 변경가능성: 변경 가능한 설계 결정이 외부에 노출될 경우 파급효과가 커짐

전체 직원에 대한 처리를 Employee 모듈로 분리

```Ruby

$employees = ["직원A", "직원B", "직원C"]
$basePays = [400, 150, 200]
$hourlys= [false, false, false]
$timeCards = [0, 0, 0]

def Employees.calculatePay(name, taxRate)
    if(Employee.hourly?(name))
        pay = Employee.calculateHourlyPayFor(name, taxRate)
    else
        pay = Employee.calculatePayFor(name, taxRate)
    end
end

def Employees.hourly?(name)
    index = $employees.index(name)
    return $hourlys[index]
end

def Employees.calculateHourlyPayFor(name, taxRate)
    index = $employees.index(name)
    basePay = $basePays[index]
    timeCard = $timeCards[index]
    return basePay * timeCard * (1 - taxRate)
end

def Employyes.calculatePayFor(name, taxRate)
    return basePay * (1 - taxRate)
end

def Employees.sumOfBasePays()
    result = 0
    for name in $employees
        if !Employee.hourly?(name)
            result += Employee.calculatePayFor(name, 0)
        end
    end
    return result
    end
end
```

전역변수가 내부 모듈로 숨음, Employees 모듈이 제공하는 인터페이스에 포함된 함수들을 통해서만 내부 조작이 가능해짐

---

```Ruby
def main(operation, args={})
    case(operation)
    when :pay then calculatePay(args[:name])
    when :sum then sumOfBasePays()
    end
end

def calculatePay(name)
    taxRate = getTaxRate()
    pay = Employees.calculatePay(name, taxRate)
    puts(describeResult(name, pay)) 
end

def getTaxRate()
   print("세율을 입력하세요: ")
   return gets().to_f()
end
    
```

### 모듈의 장점과 한계

- 모듈 내부 변수가 변하더라도 내부에만 영향
- 비니지스 로직과 사용자 인터페이스에대한 관심사 분리
- 전역 변수와 전역 함수를 제거, 네임스페이스 오염을 방지

## 데이터 추상화와 추상 데이터 타입

### 추상 데이터 타입

- 타입 정의 선언 가능해야함
- 인스턴스를 다루기 위해 사용할 수 있는 오퍼레이션 집합을 정의
- 제공된 오퍼레이션 통해서 조작 할수 있도록 데이터를 외부로부터 보호 할수 있어야함
- 타입에 대해 여러개 인스턴스를 생성할 수 있어야함

---
추상 데이터 타입인 Struct 표현

```Ruby
Employee = Struct.new(:name, :basePay, :hourly, :timeCard)
End
```

외부에서 인자로 받던 값들은 Employee 타입 내부에 포함됨

```Ruby
Employee = Struct.new(:name, :basePay, :hourly, :timeCard)
    def calculatePay(employee, taxRate)
        if employee.hourly
            pay = calculateHourlyPayFor(employee, taxRate)
        else
            pay = calculatePayFor(employee, taxRate)
        end
        return pay
    end
    
    private
    def calculateHourlyPayFor(employee, taxRate)
        return employee.basePay * employee.timeCard * (1 - taxRate)
    end
    def calculateSalariedPayFor(employee, taxRate)
        return employee.basePay * (1 - taxRate)
    end
end
```

개별 직원의 기본급을 계산
```Ruby
    Employee = Struct.new(:name, :basePay, :hourly, :timeCard) do
    def monthlyBasePay()
        if(hourly)
            return basePay * timeCard
        else
            return basePay
        end
    end
```
직원 인스턴스 준비
```Ruby
    employees = [
        Employee.new("직원A", 400, false, 0),
        Employee.new("직원B", 150, false, 0),
        Employee.new("직원C", 200, false, 0)
    ]
```

```Ruby
    def calculatePay(name)
    taxRate = getTaxRate()
    for each in $employees
        if each.name == name
            then employee = each; break end
        pay = employee.calculatePay(taxRate)
        puts(describeResult(name, pay))
    end
```

정규 직원 전체에 대한 기본급 총합을 구하기 위해 Employee 인스턴스에 차레대로 `monthlyBasePay` 메서드를 호출

```Ruby
    def sumOfBasePays()
        result = 0
        for each in $employees
            result += each.monthlyBasePay()
        end
        puts(result)
    end
```

**리스코프가 이야기한 추상 데이터 타입 기본 의도는 언어가 제공하는 타입처럼 동작하는 사용자 정의타입을 추가 제공 해야함**

## 클래스
### 클래스는 추상 데이터 타입인가?
클래스는 추상 데이터 타입은 지원하지 못한다는 점, 다형성은 지원함

- 추상 데이터 타입은 타입을 추상화 한것
- 클래스는 절차를 추상화 한것

하나의 타입이 세부 타입을 추상화 하고 감추기 때문에 타입 추상화

![img_2.png](img_2.png)

---
```Ruby
    class Employee
    attr_reader :name, :basePay
    
    def initialize(name, basePay)
        @name = name
        @basePay = basePay
    end
    
    def calculatePay(taxRate)
        return basePay * (1 - taxRate)
    end
    
    def monthlyBasePay()
        raise NotImplementedError.new("서브클래스에서 처리")
    end
```

상속받은 샐러리맨

```Ruby
class SalariedEmployee < Employee
    def initialize(name, basePay)
        super(name, basePay)
    end
    
    def calculatePay(taxRate)
        return basePay * (basePay - taxRate)
    end

    def monthlyBasePay()
        return basePay
    end
end
```
![img_3.png](img_3.png)

---
기존 코드에 아무런 영향도 미치지 않고 새로운 객체 유형과 행위를 추가할 수 있는 객체지향의 특성을 `OCP(Open-Closed Principle)`라고 함


